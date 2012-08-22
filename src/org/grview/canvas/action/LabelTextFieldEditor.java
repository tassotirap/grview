package org.grview.canvas.action;

import java.beans.PropertyChangeSupport;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.syntax.command.CommandFactory;
import org.grview.syntax.command.RenameCommand;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;

public class LabelTextFieldEditor implements TextFieldInplaceEditor
{

	private Canvas canvas;
	private PropertyChangeSupport monitor;

	public LabelTextFieldEditor(Canvas canvas)
	{
		this.canvas = canvas;
		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager(canvas.getID()));
	}

	@Override
	public String getText(Widget widget)
	{
		return ((LabelWidget) widget).getLabel();
	}

	@Override
	public boolean isEnabled(Widget widget)
	{
		if (canvas.getActiveTool().equals(Canvas.SELECT))
		{
			return true;
		}
		return false;
	}

	@Override
	public void setText(Widget widget, String text)
	{
		((LabelWidget) widget).setLabel(text);
		RenameCommand rc = CommandFactory.createRenameCommand();
		monitor.firePropertyChange("undoable", null, rc);
	}
}