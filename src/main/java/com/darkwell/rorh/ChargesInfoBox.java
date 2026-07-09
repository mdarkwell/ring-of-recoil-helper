package com.darkwell.rorh;

import net.runelite.api.Item;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.*;

import java.awt.*;

public class ChargesInfoBox extends InfoBox {

	private final String text;
	private final int amount;

	public ChargesInfoBox(RingOfRecoilPlugin plugin, int itemId, ItemManager items, String text, int amount) {
		super(items.getImage(itemId), plugin);
		this.text = text;
		this.amount = amount;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Color getTextColor() {
		if(amount < 0) {
			return Color.CYAN;
		}
		if(amount > 5) {
			return Color.WHITE;
		}
		if(amount > 2) {
			return Color.YELLOW;
		}
		return Color.RED;
	}
}
