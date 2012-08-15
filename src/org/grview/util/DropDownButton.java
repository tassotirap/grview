package org.grview.util;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

// from http://www.jroller.com/santhosh/date/20050528
//@author  santhosh kumar - santhosh@in.fiorano.com, com modificações minhas
public abstract class DropDownButton extends JButton implements ChangeListener, PopupMenuListener, ActionListener, PropertyChangeListener
{
	private final JButton mainButton = this;
	private final JButton arrowButton = new JButton(new ImageIcon(getClass().getResource("/org/grview/images/dropdown.gif")));

	private boolean popupVisible = false;

	public DropDownButton()
	{
		mainButton.getModel().addChangeListener(this);
		arrowButton.getModel().addChangeListener(this);
		arrowButton.addActionListener(this);
		arrowButton.setMargin(new Insets(3, 0, 3, 0));
		arrowButton.setBorder(new EmptyBorder(12, 0, 12, 5));
		arrowButton.setOpaque(false);
		mainButton.addPropertyChangeListener("enabled", this); // NOI18N
	}

	/*------------------------------[ PropertyChangeListener ]---------------------------------------------------*/

	protected abstract JPopupMenu getPopupMenu();

	/*------------------------------[ ChangeListener ]---------------------------------------------------*/

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		JPopupMenu popup = getPopupMenu();
		popup.addPopupMenuListener(this);
		popup.show(mainButton, 0, mainButton.getHeight());
	}

	/*------------------------------[ ActionListener ]---------------------------------------------------*/

	public JButton addToToolBar(JToolBar toolbar)
	{
		arrowButton.setBackground(toolbar.getBackground());
		toolbar.add(mainButton);
		toolbar.add(arrowButton);
		return mainButton;
	}

	/*------------------------------[ PopupMenuListener ]---------------------------------------------------*/

	@Override
	public void popupMenuCanceled(PopupMenuEvent e)
	{
		popupVisible = false;
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
	{
		popupVisible = false;

		mainButton.getModel().setRollover(false);
		arrowButton.getModel().setSelected(false);
		((JPopupMenu) e.getSource()).removePopupMenuListener(this); // act as
																	// good
																	// programmer
																	// :)
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e)
	{
		popupVisible = true;
		mainButton.getModel().setRollover(true);
		arrowButton.getModel().setSelected(true);
	}

	/*------------------------------[ Other Methods ]---------------------------------------------------*/

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		arrowButton.setEnabled(mainButton.isEnabled());
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == mainButton.getModel())
		{
			if (popupVisible && !mainButton.getModel().isRollover())
			{
				mainButton.getModel().setRollover(true);
				return;
			}
			arrowButton.getModel().setRollover(mainButton.getModel().isRollover());
			arrowButton.setSelected(mainButton.getModel().isArmed() && mainButton.getModel().isPressed());
		}
		else
		{
			if (popupVisible && !arrowButton.getModel().isSelected())
			{
				arrowButton.getModel().setSelected(true);
				return;
			}
			mainButton.getModel().setRollover(arrowButton.getModel().isRollover());
		}
	}
}