package com.darkwell.utils;

import net.runelite.api.*;

import javax.inject.*;

@Singleton
public class EquipmentHelper {
	@Inject
	private Client client;

	public Item getEquippedRing() {
		var equipment = client.getItemContainer(94);
		if (equipment != null) {
			return equipment.getItem(EquipmentInventorySlot.RING.getSlotIdx());
		}
		return null;
	}

	public int getEquippedRingId() {
		var item = getEquippedRing();
		return item == null ? -1 : item.getId();
	}
}
