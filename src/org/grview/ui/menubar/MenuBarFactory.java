package org.grview.ui.menubar;

import java.util.HashMap;

import javax.swing.JMenuBar;

import org.grview.actions.ActionContextHolder;
import org.grview.project.ProjectManager;
import org.grview.ui.MainWindow;
import org.grview.ui.Menu;
import org.grview.ui.Menu.MenuModel;

public class MenuBarFactory
{
	private JMenuBar defaultMenuBar;
	private HashMap<Object, JMenuBar> menuBars = new HashMap<Object, JMenuBar>();
	private ProjectManager projectManager;
	private MainWindow window;

	public MenuBarFactory(ProjectManager projectManager, MainWindow window)
	{
		this.projectManager = projectManager;	
		this.window = window;
	}
	
	@SuppressWarnings("rawtypes")
	public JMenuBar createMenuBarExt(ActionContextHolder context, MenuModel model)
	{
		Menu menu = new Menu(new String[]{ Menu.FILE, Menu.EDIT, Menu.OPTIONS, Menu.PROJECT, Menu.WINDOW, Menu.HELP }, window, context, model, projectManager);
		menu.build();
		return menu;
	}
	
	@SuppressWarnings("rawtypes")
	public JMenuBar createMenuBar(final ActionContextHolder context, MenuModel model)
	{
		if (context == null)
		{
			if (defaultMenuBar == null)
			{
				defaultMenuBar = createMenuBarExt(null, model);
				return defaultMenuBar;
			}
		}
		if (!menuBars.containsKey(context))
		{
			menuBars.put(context, createMenuBarExt(context, model));
		}
		return menuBars.get(context);
	}

}
