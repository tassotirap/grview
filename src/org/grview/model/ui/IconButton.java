package org.grview.model.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

/**
 * Custom view button icon.
 */
public class IconButton extends Icon {
	
    public int getIconHeight() {
	      return ICON_SIZE;
	    }

	    public int getIconWidth() {
	      return ICON_SIZE;
	    }

	    public void paintIcon(Component c, Graphics g, int x, int y) {
	      Color oldColor = g.getColor();

	      g.setColor(Color.BLACK);
	      g.fillOval(x, y, ICON_SIZE, ICON_SIZE);

	      g.setColor(oldColor);
	    }
}
