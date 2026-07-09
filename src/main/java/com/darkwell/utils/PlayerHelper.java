package com.darkwell.utils;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerHelper {
	@Inject
	private Client client;

	public WorldPoint getPosition() {
		var player = client.getLocalPlayer();
		if(player == null) {
			return new WorldPoint(0, 0, -100);
		}
		return player.getWorldLocation();
	}

	public boolean isHealthBarVisible() {
		var player = client.getLocalPlayer();
		return player != null && player.getHealthRatio() != -1;
	}

	public int getCurrentRegion() {
		var player = client.getLocalPlayer();
		if(player == null) {
			return -1;
		}

		var location = player.getWorldLocation();
		if(location == null) {
			return -1;
		}

		return location.getRegionID();
	}
}
