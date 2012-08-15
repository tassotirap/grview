package org.grview.ui.component;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.grview.canvas.Canvas;
import org.grview.output.SemanticStack;

public class SemanticStackComponent extends AbstractComponent
{

	@Override
	public JComponent create(Object param) throws BadParameterException
	{
		if (param instanceof Canvas)
		{
			JScrollPane jsp = new JScrollPane(SemanticStack.getInstance().getView((Canvas) param));
			return jsp;
		}
		else
		{
			throw new BadParameterException("Was Expecting a canvas as parameter.");
		}
	}

	@Override
	public void fireContentChanged()
	{
	}

}
