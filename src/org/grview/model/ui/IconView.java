package org.grview.model.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

/**
 * Custom view icon.
 */
public class IconView extends Icon {
	
    public int getIconHeight() {
	      return ICON_SIZE;
	    }

	    public int getIconWidth() {
	      return ICON_SIZE;
	    }

	    public void paintIcon(Component c, Graphics g, int x, int y) {
	      Color oldColor = g.getColor();

	      g.setColor(new Color(70, 70, 70));
	      g.fillRect(x, y, ICON_SIZE, ICON_SIZE);

	      g.setColor(new Color(100, 230, 100));
	      g.fillRect(x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE - 2);

	      g.setColor(oldColor);
	    }

}
