package org.grview.ui.component;

import javax.swing.JComponent;

import bsh.BshConsole;


public class ConsoleComponent extends Component {

	@Override
	public JComponent create(Object param){
		BshConsole bc = new BshConsole();
		//System.setOut(bc.getOut());
		//System.setErr(bc.getErr());
		new Thread(bc.getInterpreter()).start();
		return bc;
	}

	@Override
	public void fireContentChanged() {}

}
