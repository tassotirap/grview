package org.grview.canvas.action;

import org.grview.actions.AbstractEditAction;
import org.grview.actions.AsinActionSet;
import org.grview.actions.BeanShellFacade;
import org.grview.canvas.Canvas;
import org.grview.util.Log;

import bsh.BshMethod;
import bsh.NameSpace;
import bsh.UtilEvalError;

/**
 * An action that evaluates BeanShell code when invoked. BeanShell actions are
 * usually loaded from <code>actions.xml</code> and
 * <code>browser.actions.xml</code> files; see {@link AsinActionSet} for syntax
 * information.
 * 
 * @see jEdit#getAction(String)
 * @see jEdit#getActionNames()
 * @see AsinActionSet
 * @author Gustavo Braga
 */
public class CanvasBeanShellAction extends AbstractEditAction<Canvas>
{

	// {{{ MyBeanShellFacade class
	private static class MyBeanShellFacade extends BeanShellFacade<Canvas>
	{
		@Override
		protected void handleException(Canvas canvas, String path, Throwable t)
		{
			Log.log(Log.ERROR, this, t, t);
		}

		@Override
		protected void resetDefaultVariables(NameSpace namespace) throws UtilEvalError
		{
			namespace.setVariable("canvas", null, false);
		}

		@Override
		protected void setupDefaultVariables(NameSpace namespace, Canvas canvas) throws UtilEvalError
		{
			if (canvas != null)
			{
				namespace.setVariable("canvas", canvas, false);
			}
		}

		@Override
		public void init()
		{
			global.importClass("org.grview.canvas.Canvas");
			global.importClass("org.grview.canvas.CanvasFactory");
			global.importClass("org.grview.canvas.state.VolatileStateManager");
			global.importClass("org.grview.canvas.state.StaticStateManager");
			global.importClass("org.grview.syntax.grammar.Controller");
			global.importClass("org.grview.canvas.action.WidgetCopyPasteProvider");
			global.importClass("org.grview.canvas.action.WidgetDeleteProvider");
			global.importClass("org.grview.project.ProjectManager");
			global.importPackage("org.grview.util");
		}
	} // }}}

	private static final BeanShellFacade<Canvas> bsh = new MyBeanShellFacade();

	private BshMethod cachedCode;

	private String code;

	private String sanitizedName;

	public CanvasBeanShellAction(String name, String code)
	{
		super(name);
		this.code = code;
		/*
		 * Some characters that we like to use in action names ('.', '-') are
		 * not allowed in BeanShell identifiers.
		 */
		sanitizedName = name.replace('.', '_').replace('-', '_');
	}

	// }}}

	@Override
	public void invoke(Canvas canvas)
	{
		try
		{
			if (cachedCode == null)
			{
				String cachedCodeName = "action_" + sanitizedName;
				cachedCode = bsh.cacheBlock(cachedCodeName, code, true);
			}

			bsh.runCachedBlock(cachedCode, canvas, new NameSpace(bsh.getNameSpace(), "BeanShellAction.invoke()"));
		}
		catch (Throwable e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}

}
