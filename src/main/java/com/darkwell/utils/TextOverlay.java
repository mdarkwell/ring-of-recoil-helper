package com.darkwell.utils;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;

public class TextOverlay extends Overlay {

	protected final Plugin plugin;
	protected final Client client;

	public String text;
	public WorldPoint target;
	public Color color = Color.WHITE;

	public TextOverlay(Plugin plugin, Client client, String text, WorldPoint target) {
		this.plugin = plugin;
		this.client = client;
		this.text = text;
		this.target = target;
	}

	@Override
	public Dimension render(Graphics2D graphics2D) {
		var point = LocalPoint.fromWorld(client, target);
		if(point == null) {
			return null;
		}

		var screenPoint = Perspective.getCanvasTextLocation(client, graphics2D, point, text, 500);
		if(screenPoint == null) {
			return null;
		}

		OverlayUtil.renderTextLocation(graphics2D, screenPoint, text, color);
		return null;
	}
}
