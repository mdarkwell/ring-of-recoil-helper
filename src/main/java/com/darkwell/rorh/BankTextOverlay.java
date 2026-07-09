package com.darkwell.rorh;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import java.awt.*;

@Slf4j
public class BankTextOverlay extends Overlay {
	private static final int BANK_WIDGET_ID = 786444;
	private static final int BANK_BOTTOM_WIDGET_ID = 786454;
	private static final int BANK_TOP_WIDGET_ID = 786442;

	private final RingOfRecoilPlugin plugin;
	private final Client client;

	public BankTextOverlay(RingOfRecoilPlugin plugin, Client client) {
		this.plugin = plugin;
		this.client = client;

		setPriority(0.75f);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		drawAfterLayer(BANK_WIDGET_ID);
	}

	@Override
	public Dimension render(Graphics2D graphics2D) {
		var bank = client.getWidget(BANK_WIDGET_ID);
		if (bank == null || bank.isHidden()) {
			return null;
		}

		// Used for clipping text out of the bank widget bounds
		var bankBottom = client.getWidget(BANK_BOTTOM_WIDGET_ID);
		var bankTop = client.getWidget(BANK_TOP_WIDGET_ID);
		var bottomBounds = bankBottom != null ? bankBottom.getBounds() : null;
		var bottomY = bottomBounds != null ? bottomBounds.getY() : -1024;
		var topBounds = bankTop != null ? bankTop.getBounds() : null;
		var topY = topBounds != null ? topBounds.getY() + topBounds.getHeight() : -1024;

		var items = bank.getDynamicChildren();
		for (var slot : items) {
			if (slot == null || slot.isHidden()) {
				continue;
			}
			var id = slot.getItemId();
			if (id <= 0 || !plugin.isRecoilRing(id)) {
				continue;
			}

			var bounds = slot.getBounds();
			if(bounds == null) {
				continue;
			}

			var y = (int)bounds.getCenterY() + 16;
			if(y < topY || y > bottomY) {
				continue;
			}

			String text = plugin.getChargesText(id, false);

			var color = text.equals("?") ? Color.WHITE : Color.CYAN;
			graphics2D.setColor(color);
			var width = graphics2D.getFontMetrics().stringWidth(text);
			var point = new Point((int)bounds.getCenterX() - width / 2, y);
			OverlayUtil.renderTextLocation(graphics2D, point, text, color);
		}

		return null;
	}
}
