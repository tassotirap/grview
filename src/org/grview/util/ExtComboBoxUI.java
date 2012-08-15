package org.grview.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

public class ExtComboBoxUI extends MetalComboBoxUI
{
	private int padding = 10;

	@Override
	protected ComboPopup createPopup()
	{
		BasicComboPopup popup = new BasicComboPopup(comboBox)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void show()
			{
				// Need to compute width of text
				int widest = getWidestItemWidth();

				// Get the box's size
				Dimension popupSize = comboBox.getSize();

				// Set the size of the popup
				popupSize.setSize(widest + (2 * padding), getPopupHeightForRowCount(comboBox.getMaximumRowCount()));

				// Compute the complete bounds
				Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height, popupSize.width, popupSize.height);

				// Set the size of the scroll pane
				scroller.setMaximumSize(popupBounds.getSize());
				scroller.setPreferredSize(popupBounds.getSize());
				scroller.setMinimumSize(popupBounds.getSize());

				// Cause it to re-layout
				list.invalidate();

				// Handle selection of proper item
				int selectedIndex = comboBox.getSelectedIndex();
				if (selectedIndex == -1)
					list.clearSelection();
				else
					list.setSelectedIndex(selectedIndex);

				// Make sure the selected item is visible
				list.ensureIndexIsVisible(list.getSelectedIndex());

				// Use lightweight if asked for
				setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());

				// Show the popup
				show(comboBox, popupBounds.x, popupBounds.y);
			}
		};

		popup.getAccessibleContext().setAccessibleParent(comboBox);

		return popup;
	}

	public int getWidestItemWidth()
	{
		// Items, font
		int numItems = comboBox.getItemCount();
		Font font = comboBox.getFont();
		FontMetrics metrics = comboBox.getFontMetrics(font);

		// The widest width
		int widest = 0;

		for (int i = 0; i < numItems; i++)
		{
			// Get the item
			Object item = comboBox.getItemAt(i);

			// Calculate the width of this line
			int lineWidth = metrics.stringWidth(item.toString());

			// Use whatever's widest
			widest = Math.max(widest, lineWidth);
		}

		return widest;
	}
}