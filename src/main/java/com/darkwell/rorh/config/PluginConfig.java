package com.darkwell.rorh.config;

import com.darkwell.rorh.Constants;
import com.darkwell.rorh.FlashOverlayTarget;
import com.darkwell.utils.FlashOverlayType;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(Constants.PLUGIN_GROUP)
public interface PluginConfig extends Config
{
	//region General
	@ConfigItem(
			keyName = "displayInfoBox",
			name = "Display Info Box",
			description = "Whether the info box should be displayed when a ring of recoil or ring of suffering is equipped.",
			position = 0
	)
	default boolean displayInfoBox() {
		return true;
	}

	@ConfigItem(
			keyName = "useTotalCharges",
			name = "Display Total Charges",
			description = "Whether the info box should display total charges, or only charges on the currently equipped ring.",
			position = 1
	)
	default boolean useTotalCharges() {
		return false;
	}

	@ConfigItem(
			keyName = "warnWhenImmune",
			name = "Warn When Immune",
			description = "Warns when you are facing an enemy who's immune to the recoil effect.",
			position = 2
	)
	default boolean warnWhenImmune() {
		return true;
	}

	@ConfigItem(
			keyName = "displayChargesInBank",
			name = "Display Charges in Bank",
			description = "Displays your current charges on the rings within your bank.",
			position = 3
	)
	default boolean displayChargesInBank() {
		return true;
	}

	@ConfigItem(
			keyName = "overlayType",
			name = "Unequipped Overlay",
			description = "What type of overlay to render when you don't have a recoiling ring equipped.",
			position = 4,
			section = notificationSection
	)
	default FlashOverlayType overlayType() {
		return FlashOverlayType.FLASH_UNFOCUSED;
	}

	@ConfigItem(
			keyName = "overlayTarget",
			name = "Unequipped Overlay Condition",
			description = "When should the unequipped overlay display, if at all?",
			position = 5,
			section = notificationSection
	)
	default FlashOverlayTarget overlayTarget() {
		return FlashOverlayTarget.NEVER;
	}
	//endregion

	//region Notifications
	@ConfigSection(
			name = "Notifications",
			description = "Options for managing charge settings.",
			position = 6,
			closedByDefault = true
	)
	String notificationSection = "Notifications";

	@ConfigItem(
		keyName = "sendLowChargeAlert",
		name = "Low Charge Alert",
		description = "Send a notification when your ring of recoil's charges hit a threshold.",
		section = notificationSection
	)
	default boolean sendLowChargeAlert() {
		return true;
	}

	@ConfigItem(
		keyName = "lowChargeThreshold",
		name = "Low Charge Alert Threshold",
		description = "If \"Low Charge Alert\" is enabled, this defines the amount of charges before an alert is sent.",
		section = notificationSection
	)
	default int lowChargeThreshold() {
		return 2;
	}

	@ConfigItem(
		keyName = "ringBreakAlert",
		name = "Ring Break Alert",
		description = "Send a notification when your ring of recoil breaks.",
		section = notificationSection
	)
	default boolean ringBreakAlert() {
		return true;
	}
	//endregion

	//region Areas
	@ConfigSection(
			name = "Areas",
			description = "Options for managing active areas.",
			position = 5
	)
	String areaSection = "Areas";

	@ConfigItem(keyName="areaGlobal", name = "Global", description = "Enable the info box and notifications to appear wherever you are in Gielinor.", section = areaSection, position = -100)
	default boolean areaGlobal() { return true; }

	@ConfigItem(keyName="areaBarbAssault", name = "Barbarian Assault", description = "Enable the info box and notifications to appear at Barbarian Assault.", section = areaSection)
	default boolean areaBarbAssault() { return true; }

	@ConfigItem(keyName="areaCerberus", name = "Cerberus' Lair", description = "Enable the info box and notifications to appear at Cerberus' Lair.", section = areaSection)
	default boolean areaCerberus() { return true; }

	@ConfigItem(keyName="areaCorpBeast", name = "Corporeal Beast", description = "Enable the info box and notifications to appear at Corporeal Beast.", section = areaSection)
	default boolean areaCorpBeast() { return true; }

	@ConfigItem(keyName="areaDrakes", name = "Drakes", description = "Enable the info box and notifications to appear at Drakes.", section = areaSection)
	default boolean areaDrakes() { return true; }

	@ConfigItem(keyName="areaFightCave", name = "TzHaar Fight Cave", description = "Enable the info box and notifications to appear at Fight Cave.", section = areaSection)
	default boolean areaFightCave() { return true; }

	@ConfigItem(keyName="areaGg", name = "Grotesque Guardians", description = "Enable the info box and notifications to appear at Grotesque Guardians.", section = areaSection)
	default boolean areaGg() { return true; }

	@ConfigItem(keyName="areaGwd", name = "God Wars Dungeon", description = "Enable the info box and notifications to appear at God Wars Dungeon.", section = areaSection)
	default boolean areaGodWars() { return true; }

	@ConfigItem(keyName="areaInferno", name = "Inferno", description = "Enable the info box and notifications to appear at Inferno.", section = areaSection)
	default boolean areaInferno() { return true; }

	@ConfigItem(keyName="areaKq", name = "Kalphite Queen", description = "Enable the info box and notifications to appear at Kalphite Queen.", section = areaSection)
	default boolean areaKq() { return true; }

	@ConfigItem(keyName="areaLithkren", name = "Lithkren Vault", description = "Enable the info box and notifications to appear at Lithkren Vault.", section = areaSection)
	default boolean areaLithkren() { return true; }

	@ConfigItem(keyName="areaMimic", name = "The Mimic", description = "Enable the info box and notifications to appear at The Mimic.", section = areaSection)
	default boolean areaMimic() { return true; }

	@ConfigItem(keyName="areaMithrilDragons", name = "Mithril Dragons", description = "Enable the info box and notifications to appear at Mithril Dragons.", section = areaSection)
	default boolean areaMithrilDragons() { return true; }

	@ConfigItem(keyName="areaMoons", name = "Moons of Peril", description = "Enable the info box and notifications to appear at Moons of Peril.", section = areaSection)
	default boolean areaMoons() { return true; }

	@ConfigItem(keyName="areaMuspah", name = "Phantom Muspah", description = "Enable the info box and notifications to appear at Phantom Muspah.", section = areaSection)
	default boolean areaMuspah() { return true; }

	@ConfigItem(keyName="areaSarachnis", name = "Sarachnis", description = "Enable the info box and notifications to appear at Sarachnis.", section = areaSection)
	default boolean areaSarachnis() { return true; }

	@ConfigItem(keyName="areaScorpia", name = "Scorpia", description = "Enable the info box and notifications to appear at Scorpia.", section = areaSection)
	default boolean areaScorpia() { return true; }

	@ConfigItem(keyName="areaSmoke", name = "Smoke Dungeon", description = "Enable the info box and notifications to appear at Smoke Dungeon.", section = areaSection)
	default boolean areaSmoke() { return true; }

	@ConfigItem(keyName="areaSucc", name = "Duke Sucellus", description = "Enable the info box and notifications to appear at Duke Sucellus.", section = areaSection)
	default boolean areaSucc() { return true; }

	@ConfigItem(keyName="areaVardorvis", name = "Vardorvis", description = "Enable the info box and notifications to appear at Vardorvis.", section = areaSection)
	default boolean areaVardorvis() { return true; }

	@ConfigItem(keyName="areaYama", name = "Yama", description = "Enable the info box and notifications to appear at Yama.", section = areaSection)
	default boolean areaYama() { return true; }

	@ConfigItem(keyName="areaZulrah", name = "Zulrah", description = "Enable the info box and notifications to appear at Zulrah.", section = areaSection)
	default boolean areaZulrah() { return true; }
	//endregion

	//region Sounds
	@ConfigSection(
			name = "Sounds",
			description = "Options for managing sounds.",
			position = 7,
			closedByDefault = true
	)
	String soundSection = "Sounds";

	@ConfigItem(
			keyName = "doSoundFx",
			name = "Play Sound FX",
			description = "Whether to play sound FX.",
			position = 399,
			section = soundSection
	)
	default boolean doSoundFx() {
		return true;
	}

	@ConfigItem(
			keyName = "soundFx",
			name = "Breaking Sound",
			description = "Which breaking sound to play when a ring of recoil breaks. These sound IDs can be found on the oldschool wiki.",
			position = 400,
			section = soundSection
	)
	default int soundFx() {
		return 2381; // destroy_object
	}
	//endregion

	@ConfigItem(
		keyName = Constants.PLUGIN_CONFIG_DATA_KEY,
		name = "Plugin Save Data",
		description = "",
		hidden = true
	)
	default String internalSaveData() {
		return "";
	}
}
