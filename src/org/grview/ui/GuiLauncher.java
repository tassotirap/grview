package org.grview.ui;

import org.grview.ui.lib.SplashWindow;

/**
 * 
 * @author Tasso Tirapani Silva Pinto
 * 
 *         GuiLancher class is the Starter class of GrView First is loaded
 *         WorkspaceChooser followed by MainWindow
 * 
 */
public class GuiLauncher
{
	private final static String MAIN_WINDOW = "org.grview.ui.MainWindow";
	private final static String SPLASH_SCREEN_PNG = "splash_screen.png";
	private final static String WORKSPACE_CHOOSER = "org.grview.ui.WorkspaceChooser";
	private final String[] args;

	private GuiLauncher(String[] args)
	{
		this.args = args;
	}

	public static void main(String[] args)
	{
		GuiLauncher guiLauncher = new GuiLauncher(args);
		guiLauncher.startWorkspaceChooser();
	}

	private String[] createNewArgs(String firstArgs)
	{
		String[] nargs = new String[args.length + 1];
		nargs[0] = firstArgs;
		for (int i = 0; i < args.length; i++)
		{
			nargs[i + 1] = args[i];
		}
		return nargs;
	}

	/**
	 * Start a WorkspaceChooser Class
	 */
	private void startWorkspaceChooser()
	{
		WorkspaceChooser workspaceChooser = WorkspaceChooser.getInstance();

		SplashWindow.invokeMain(WORKSPACE_CHOOSER, args);
		while (!workspaceChooser.isCanceled() && !workspaceChooser.isDone())
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		if (workspaceChooser.isDone())
		{
			SplashWindow.splash(GuiLauncher.class.getResource(SPLASH_SCREEN_PNG));
			String[] nargs = createNewArgs(workspaceChooser.getWorkspaceDir());
			SplashWindow.invokeMain(MAIN_WINDOW, nargs);
			SplashWindow.disposeSplash();
		}
	}
}
