package org.grview.ui;

public class GuiLauncher
{
	private final String SPLASH_SCREEN_PNG = "splash_screen.png";
	private final String WORKSPACE_CHOOSER = "org.grview.ui.WorkspaceChooser";
	private final String MAIN_WINDOW = "org.grview.ui.MainWindow";
	private final String[] args;

	private GuiLauncher(String[] args)
	{
		this.args = args;
	}

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

	public static void main(String[] args)
	{
		GuiLauncher guiLauncher = new GuiLauncher(args);
		guiLauncher.startWorkspaceChooser();
	}
}
