package org.grview.ui.ToolBar;

import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.JToolBar;

import org.grview.actions.AbstractEditAction;
import org.grview.canvas.Canvas;
import org.grview.editor.TextArea;

public abstract class CommandBar<E> extends JToolBar implements PropertyChangeListener
{
	protected final String imgPath = "/org/grview/images/";
	protected final static String MAIN_TB_GENERAL = "m_general";
	protected final static String CANVAS_TB_CANVAS = "canv_tb";
	protected final static String MAIN_TB_CANVAS = "mcan_tb";
	protected final static String MAIN_TB_TEXTAREA = "mtex_tb";

	public final static String CANVAS_CONTEXT = "canvasContext";
	public final static String TEXTAREA_CONTEXT = "textAreaContext";

	private static final long serialVersionUID = 1L;
	protected E context;

	public abstract AbstractEditAction<E> getAction(String action);

	public abstract HashMap<String, String[]> getContextEnabledMap();

	public String getContextType()
	{
		if (context instanceof Canvas)
			return CANVAS_CONTEXT;
		else if (context instanceof TextArea)
			return TEXTAREA_CONTEXT;
		return null;
	}

	public abstract String getNickname();

	public abstract void initActions();

	public abstract void initLayout();
}
