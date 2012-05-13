package org.grview.ioadapter;

import javax.swing.JComponent;

public abstract class IOAdapter {

	public abstract JComponent getFormView();
	
	public abstract JComponent getCodeView();
	
	public abstract JComponent getFrameView();
}
