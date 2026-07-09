package com.darkwell.rorh;

import com.darkwell.rorh.config.PluginConfig;
import com.darkwell.rorh.config.PluginData;
import com.darkwell.utils.*;
import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@PluginDescriptor(
		name = "Ring of Recoil Helper"
)
public class RingOfRecoilPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private ClientUI window;

	@Inject
	private ClientThread clientThread;

	@Inject
	private PluginConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager items;

	@Inject
	private Gson gson;

	@Inject
	private InfoBoxManager info;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlays;

	@Inject
	private ChatHelper chat;

	@Inject
	private EquipmentHelper equipment;

	@Inject
	private PlayerHelper player;


	private PluginData data;
	private ChargesInfoBox infoBox;
	private TextOverlay overlay;
	private BankTextOverlay bankTextOverlay;
	private FlashOverlay flashOverlay;
	private boolean isActive, isBankOverlayActive;
	private int lastImmuneNpcId = -1, lastHitTook = -1;
	private int overrideItemId = -1;

	@Override
	protected void startUp() {
		isActive = true;
		log.debug("Ring of Recoil Helper started!");
		data = loadPluginData();
		clientThread.invokeLater(this::update);

		bankTextOverlay = new BankTextOverlay(this, client);
		toggleBankOverlay(config.displayChargesInBank());
	}

	@Override
	protected void shutDown() {
		isActive = false;
		log.debug("Ring of Recoil Helper stopped!");

		resetPluginData();
		savePluginData();

		if (infoBox != null) {
			info.removeInfoBox(infoBox);
		}

		if (overlay != null) {
			overlays.remove(overlay);
		}

		toggleBankOverlay(false);
		toggleFlashOverlay(false);
	}

	//region API
	public int getCharges() {
		return data.ringCharges.getOrDefault(equipment.getEquippedRingId(), -1);
	}

	public int getCharges(int ringId) {
		return data.ringCharges.getOrDefault(ringId, -1);
	}

	public String getChargesText(int id, boolean total) {
		String text;
		var charges = total ? getTotalChargesOnPlayer() : getCharges(id);
		if (charges == -1) {
			text = "?";
		} else if (charges < 1000) {
			text = String.format("%,d", charges);
		} else {
			text = String.format("%,.1fK", charges / 1000f);
		}
		return text;
	}

	public int getTotalChargesOnPlayer() {
		var total = getCharges();

		var inv = client.getItemContainer(Constants.INVENTORY_CONTAINER);
		if (inv == null) {
			return total;
		}

		var items = inv.getItems();
		for (var item : items) {
			if (item != null && item.getId() == Constants.RING_OF_RECOIL_ID) {
				total += 40;
			}
		}

		if (!isRecoilRingEquipped()) {
			total -= 40;
		}

		return total;
	}

	protected void setCharges(int ringId, int amount) {
		if(data.ringCharges.getOrDefault(ringId, -1) == amount) {
			return;
		}

		if(amount == 0 && ringId == Constants.RING_OF_RECOIL_ID) {
			amount = 40;
		}

		data.ringCharges.put(ringId, amount);
		update();
		log.debug("Charges for ring {} set to {}", ringId, amount);
	}

	protected void decreaseChargeByOne(int ringId) {
		if (!data.ringCharges.containsKey(ringId)) {
			log.debug("No recorded ring charge for {}", ringId);
			return;
		}
		var charges = getCharges(ringId);
		if (charges > -1) {
			setCharges(ringId, charges - 1);
		}
	}

	public boolean isRecoilRing(int ring) {
		return ring == Constants.RING_OF_RECOIL_ID || Constants.RING_OF_SUFFERING_IDS.contains(ring);
	}

	public boolean isNotImmune(int npcId) {
		return !Constants.IMMUNE_NPCS.contains(npcId);
	}

	public boolean isRecoilRingEquipped() {
		var ring = equipment.getEquippedRingId();
		return isRecoilRing(ring);
	}

	public WorldPoint getClosestWarningPoint() {
		var playerPos = player.getPosition();

		AtomicReference<Integer> closestDist = new AtomicReference<>();
		closestDist.set(Integer.MAX_VALUE);
		AtomicReference<WorldPoint> closestPoint = new AtomicReference<>();
		closestPoint.set(new WorldPoint(0, 0, -100));
		Constants.WARNING_TILES.entrySet().stream()
				.filter(entry ->
						configManager.getConfiguration(Constants.PLUGIN_GROUP, entry.getKey(), Boolean.class))
				.forEach(entry -> {
					var dist = entry.getValue().distanceTo2D(playerPos);
					if (dist < closestDist.get()) {
						closestPoint.set(entry.getValue());
						closestDist.set(dist);
					}
				});
		return closestPoint.get();
	}

	public boolean inActiveRegion(boolean overrideGlobal) {
		if (config.areaGlobal() && !overrideGlobal) {
			return true;
		}

		var region = player.getCurrentRegion();
		if (Constants.AREA_REGIONS.containsKey(region) && (boolean) configManager.getConfiguration(
				Constants.PLUGIN_GROUP, Constants.AREA_REGIONS.get(region), Boolean.class)) {
			return true;
		}

		return getClosestWarningPoint().distanceTo2D(player.getPosition()) < 30;
	}
	//endregion

	//region Event Callbacks
	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event) {
		clientThread.invokeAtTickEnd(() -> handleWidgetLoaded(event));
	}

	private void handleWidgetLoaded(WidgetLoaded event) {
		if (event.getGroupId() != Constants.RECOIL_BREAK_CHAT_WIDGET) {
			return;
		}

		var parent = client.getWidget(Constants.RECOIL_BREAK_CHAT_WIDGET, 1);
		if (parent == null) {
			log.debug("Parent none");
			return;
		}

		var child = parent.getChild(0);
		if(child == null) {
			log.debug("Child none");
			return;
		}

		var matcher = Constants.RECOIL_BREAK_CHARGES.matcher(child.getText());
		if(matcher.find()) {
			setCharges(Constants.RING_OF_RECOIL_ID, Integer.parseInt(matcher.group(1)));
		} else {
			log.debug("No charge found in text: {}", child.getText());
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		var option = event.getMenuOption();
		var itemId = event.getWidget() != null ? event.getWidget().getItemId() : -1;
		if ((option.equals("Check") || option.equals("Break")) && isRecoilRing(itemId)) {
			overrideItemId = itemId;
		}

		var target = Text.removeTags(event.getMenuTarget());
		if ((option.equals("Use")) && isRecoilRing(itemId) && target.startsWith("Ring of recoil -> Ring of suffering")) {
			overrideItemId = itemId;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		overrideItemId = -1;
		clientThread.invokeLater(this::update);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		var state = event.getGameState();
		switch (state) {
			case LOADING:
				clientThread.invokeLater(this::update);
				break;

			case LOGIN_SCREEN:
			case CONNECTION_LOST:
			case HOPPING:
				savePluginData();
				break;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getKey().equalsIgnoreCase("displayChargesInBank")) {
			toggleBankOverlay(Boolean.getBoolean(event.getNewValue()));
			return;
		}

		clientThread.invokeLater(this::update);
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event) {
		var player = client.getLocalPlayer();
		var isPlayerTarget = event.getTarget() == player;
		if (!isPlayerTarget && event.getSource() != player) {
			return;
		}

		update();

		var entity = isPlayerTarget ? event.getSource() : event.getTarget();
		if (!(entity instanceof NPC)) {
			return;
		}

		var npc = (NPC) entity;
		checkImmunity(npc);
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hit) {
		if (!isRecoilRingEquipped()) {
			return;
		}

		var target = hit.getActor();
		var player = client.getLocalPlayer();
		var tick = client.getTickCount();
		var splat = hit.getHitsplat();
		var type = splat.getHitsplatType();
		var dmg = splat.getAmount();
		if (player == target && dmg > 0 && (type == HitsplatID.DAMAGE_MAX_ME || type == HitsplatID.DAMAGE_ME)) {
			lastHitTook = tick;
			log.debug("something hit player");
		} else if (tick - lastHitTook < 2 && dmg == 1 && target.getInteracting() == player && type == HitsplatID.DAMAGE_ME) {
			lastHitTook = -1;
			decreaseChargeByOne(equipment.getEquippedRingId());
			log.debug("{} took 1 damage from recoil", target.getName());
		}
	}

	@Subscribe
	public void onFocusChanged(FocusChanged event) {
		clientThread.invokeLater(this::update);
	}

	@Subscribe
	public void onChatMessage(ChatMessage msg) {
		if (msg == null) {
			return;
		}

		var type = msg.getType();
		if (type != ChatMessageType.GAMEMESSAGE && type != ChatMessageType.MESBOX) {
			return;
		}

		var charges = getChargesFromChatMessage(msg);
		if (charges < 0) {
			return;
		}

		var ring = overrideItemId != -1 ? overrideItemId : equipment.getEquippedRingId();
		if (ring < 0) {
			ring = Constants.RING_OF_RECOIL_ID;
		}
		overrideItemId = -1;

		setCharges(ring, charges);
	}

	private int getChargesFromChatMessage(ChatMessage chatMessage) {
		var msg = chatMessage.getMessage();
		var matcher = Constants.RECOIL_CHARGE_REGEX.matcher(msg);
		if (matcher.find()) {
			return Integer.parseUnsignedInt(matcher.group(1));
		}

		matcher = Constants.SUFFERING_CHARGE_REGEX.matcher(msg);
		if (matcher.find()) {
			return Integer.parseUnsignedInt(matcher.group(1).replace(",", ""));
		}

		matcher = Constants.RECOIL_DEGRADE_REGEX.matcher(msg);
		if (matcher.find()) {
			return Integer.parseUnsignedInt(matcher.group(1));
		}

		matcher = Constants.RECOIL_ONE_CHARGE_REGEX.matcher(msg);
		if (matcher.find()) {
			return 1;
		}

		matcher = Constants.RECOIL_FULLY_CHARGED_REGEX.matcher(msg);
		if (matcher.find()) {
			return Constants.RECOIL_FULL_RECHARGE;
		}

		matcher = Constants.SUFFERING_RECHARGE_REGEX.matcher(msg);
		if (matcher.find()) {
			data.ringCharges.put(Constants.RING_OF_RECOIL_ID, Constants.RECOIL_FULL_RECHARGE);
			return Integer.parseUnsignedInt(matcher.group(1).replace(",", ""));
		}

		matcher = Constants.RECOIL_SHATTER_REGEX.matcher(msg);
		if (matcher.find()) {
			if (config.ringBreakAlert()) {
				notifier.notify(Text.removeTags(chatMessage.getMessage()));
			}
			if (config.doSoundFx()) {
				var sfx = config.soundFx();
				client.playSoundEffect(sfx);
			}
			return Constants.RECOIL_FULL_RECHARGE;
		}

		return -1;
	}

	public void checkImmunity(NPC npc) {
		var npcId = npc.getId();
		if (npcId == lastImmuneNpcId || !config.warnWhenImmune()) {
			return;
		}

		if (!isRecoilRingEquipped()) {
			return;
		}

		if (isNotImmune(npcId)) {
			return;
		}

		lastImmuneNpcId = npcId;
		chat.sendConsoleMessage(String.format(Constants.WARNING_IMMUNE,
				npc.getName(), items.getItemComposition(equipment.getEquippedRingId()).getName()));
	}
	//endregion

	//region State Updates
	private void toggleFlashOverlay(boolean state) {
		if (flashOverlay != null) {
			overlays.remove(flashOverlay);
		}

		if (!state || isRecoilRingEquipped()) {
			return;
		}

		switch (config.overlayType()) {
			case SOLID:
				flashOverlay = new FlashOverlay(client, Constants.FLASH_COLOR, 1, 0);
				break;

			case FLASH_ALWAYS:
				flashOverlay = new FlashOverlay(client, Constants.FLASH_COLOR, 1, 1);
				break;

			case FLASH_UNFOCUSED:
				if (window.isFocused()) {
					return;
				}
				flashOverlay = new FlashOverlay(client, Constants.FLASH_COLOR, 1, 1);
				break;

			case NONE:
				return;
		}

		switch (config.overlayTarget()) {
			case IN_COMBAT:
				if (player.isHealthBarVisible()) {
					overlays.add(flashOverlay);
				}
				break;

			case IN_AREA:
				if (inActiveRegion(true)) {
					overlays.add(flashOverlay);
				}
				break;

			case ALWAYS:
				overlays.add(flashOverlay);
				break;
		}
	}

	private void updateInfoBox() {
		if (infoBox != null) {
			info.removeInfoBox(infoBox);
		}

		if (!isActive) {
			return;
		}

		if (canDisplayInfoBox()) {
			var ring = equipment.getEquippedRingId();
			var charges = config.useTotalCharges() ? getTotalChargesOnPlayer() : getCharges(ring);
			infoBox = new ChargesInfoBox(this, ring, items, getChargesText(ring, config.useTotalCharges()), charges);
			info.addInfoBox(infoBox);
		}
	}

	private void updateOverlay() {
		if (overlay != null) {
			overlays.remove(overlay);
		}

		if (!isActive) {
			return;
		}

		overlay = new TextOverlay(this, client, "", getClosestWarningPoint());
		updateOverlayText();
		overlays.add(overlay);
	}

	private void updateOverlayText() {
		if (!isActive) {
			return;
		}

		if (!isRecoilRingEquipped()) {
			overlay.text = Constants.WARNING_NO_RING;
			overlay.color = Color.RED;
			return;
		}

		var charges = getTotalChargesOnPlayer();
		overlay.color = Color.GREEN;
		overlay.text = String.format(Constants.WARNING_RING,
				items.getItemComposition(equipment.getEquippedRingId()).getName(), charges);
	}

	private void updateCharges() {
		if (!isActive) {
			return;
		}

		toggleFlashOverlay(true);
		if (!isRecoilRingEquipped()) {
			return;
		}

		var ring = equipment.getEquippedRingId();

		// Send low-charge alert if charges meet threshold
		var charges = getCharges();
		if (config.sendLowChargeAlert() && charges == config.lowChargeThreshold()) {
			var msg = charges == 1 ? Constants.NOTIFY_LOW_CHARGE_SINGLE : Constants.NOTIFY_LOW_CHARGE;
			notifier.notify(String.format(msg, charges, items.getItemComposition(ring).getName()));
		}
	}

	private void update() {
		if (!isActive) {
			return;
		}

		updateCharges();
		updateInfoBox();
		updateOverlay();
	}

	private boolean canDisplayInfoBox() {
		return isActive && isRecoilRingEquipped() && config.displayInfoBox() && inActiveRegion(false);
	}

	private void toggleBankOverlay(boolean state) {
		if (bankTextOverlay == null || isBankOverlayActive == state) {
			return;
		}
		isBankOverlayActive = state;
		if (state) {
			overlays.add(bankTextOverlay);
		} else {
			overlays.remove(bankTextOverlay);
		}
	}
	//endregion

	//region Config Data
	@Provides
	PluginConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(PluginConfig.class);
	}

	public PluginData loadPluginData() {
		var json = config.internalSaveData();
		if (json == null || json.isEmpty()) {
			return new PluginData();
		}
		return gson.fromJson(json, PluginData.class);
	}

	public void savePluginData() {
		if (data == null) {
			return;
		}

		configManager.setConfiguration(Constants.PLUGIN_GROUP, Constants.PLUGIN_CONFIG_DATA_KEY, gson.toJson(data));
	}

	public void resetPluginData() {
		data = new PluginData();
	}
	//endregion
}
