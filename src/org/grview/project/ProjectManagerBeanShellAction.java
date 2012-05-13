package org.grview.project;

import org.grview.actions.AbstractEditAction;
import org.grview.actions.AsinActionSet;
import org.grview.actions.BeanShellFacade;
import org.grview.bsh.BshMethod;
import org.grview.bsh.NameSpace;
import org.grview.bsh.UtilEvalError;
import org.grview.util.Log;


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
public class ProjectManagerBeanShellAction extends AbstractEditAction<ProjectManager> {

	private String sanitizedName;
	private String code;

	private BshMethod cachedCode;
	private static final BeanShellFacade<ProjectManager> bsh = new MyBeanShellFacade();
	
	public ProjectManagerBeanShellAction(String name, String code) {
		super(name);
		this.code = code;
		/* Some characters that we like to use in action names
		 * ('.', '-') are not allowed in BeanShell identifiers. */
		sanitizedName = name.replace('.','_').replace('-','_').replace(" ", "_");
	}
	
	@Override
	public void invoke(ProjectManager pManager) {
		try
		{
			if(cachedCode == null)
			{
				String cachedCodeName = "action_" + sanitizedName;
				cachedCode = bsh.cacheBlock(cachedCodeName,code,true);
			}

			bsh.runCachedBlock(cachedCode,pManager,
				new NameSpace(bsh.getNameSpace(),
				"BeanShellAction.invoke()"));
		}
		catch(Throwable e)
		{
			Log.log(Log.ERROR,this,e);
		}
	}

	private static class MyBeanShellFacade extends BeanShellFacade<ProjectManager>
	{
		@Override
		public void init() {
			global.importClass("org.grview.project.ProjectManager");
			global.importClass("org.grview.ui.wizard.NewFileWizard");
		}
		@Override
		protected void setupDefaultVariables(NameSpace namespace, ProjectManager pManager) throws UtilEvalError 
		{
			if(pManager != null)
			{
				namespace.setVariable("pManager",pManager, false);
			}
		}

		@Override
		protected void resetDefaultVariables(NameSpace namespace) throws UtilEvalError
		{
			namespace.setVariable("pManager",null, false);
		}

		@Override
		protected void handleException(ProjectManager canvas, String path, Throwable t)
		{
			Log.log(Log.ERROR,this, t, t);
		}
	}
}
