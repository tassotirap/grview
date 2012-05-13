package org.grview.ui.component;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class DullComponent2 extends Component {

	@Override
	public JComponent create(Object param) throws BadParameterException {
		JPanel panel = new JPanel(new BorderLayout());
		return panel;
	}

	@Override
	public void fireContentChanged() {}
}
