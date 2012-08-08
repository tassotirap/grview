package org.grview.ui.component;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EmptyComponent extends AbstractComponent
{

	@Override
	public JComponent create(Object param) throws BadParameterException
	{
		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Empty page, files will be opened here.");
		panel.add(label, BorderLayout.CENTER);
		return panel;
	}

	@Override
	public void fireContentChanged()
	{
	}

}
