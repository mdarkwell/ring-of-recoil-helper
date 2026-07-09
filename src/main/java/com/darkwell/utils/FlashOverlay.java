package com.darkwell.utils;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;

import java.awt.*;

public class FlashOverlay extends Overlay {
	private static final Color TRANSPARENT = new Color(1f, 1f, 1f, 0f);

	private final Client client;
	public Color color;
	public int interval, length;

	public FlashOverlay(Client client, Color color, int length, int interval) {
		this.client = client;
		this.color = color;
		this.interval = interval;
		this.length = length;
	}

	@Override
	public Dimension render(Graphics2D graphics2D) {
		var length = this.length + this.interval;
		var tick = client.getTickCount();
		if(tick % length >= this.length) {
			graphics2D.setColor(TRANSPARENT);
			return null;
		}

		graphics2D.setColor(color);
		graphics2D.fillRect(-512, -512, client.getCanvasWidth() + 1024, client.getCanvasHeight() + 1024);
		return null;
	}
}
