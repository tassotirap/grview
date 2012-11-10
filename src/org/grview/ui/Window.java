package org.grview.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

import org.grview.canvas.Canvas;
import org.grview.editor.TextArea;
import org.grview.model.ui.IconView;
import org.grview.parser.ParsingEditor;
import org.grview.project.ProjectManager;
import org.grview.ui.Menu.MenuModel;
import org.grview.ui.TabWindowList.TabPlace;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.BadParameterException;
import org.grview.ui.component.EmptyComponent;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GrammarComponent;
import org.grview.ui.component.TextAreaRepo;
import org.grview.ui.dynamicview.DynamicView;
import org.grview.ui.dynamicview.DynamicViewRepository;
import org.grview.ui.menubar.MenuBarFactory;
import org.grview.ui.toolbar.ToolBarFactory;

/** An abstract, top-level windows with docking **/
public abstract class Window
{	
	public final static String DEFAULT_NAME = "GrView Window";
	public final static String DEFAULT_TITLE = "GrView";
	
	
	public final static String UNSAVED_PREFIX = "* ";
	public final static Icon VIEW_ICON = new IconView();

	private DynamicView emptyDynamicView = null;
	private JMenuBar currentMenuBar;
	private JComponent currentToolBar;
	
	private TabWindowList tabWindowList;
	protected JFrame frame;
	protected Canvas activeScene;
	protected RootWindow rootWindow;
	protected WindowAdapter windowAdapter;	
	protected ProjectManager projectManager;
	protected MenuBarFactory menuBarFactory;
	protected ToolBarFactory toolBarFactory;	
	protected DynamicViewRepository dynamicaViewRepository;

	public Window()
	{
		this(DEFAULT_TITLE);
	}

	public Window(String title)
	{
		frame = new JFrame(title);
		frame.setName(DEFAULT_NAME);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.tabWindowList = new TabWindowList();
		this.dynamicaViewRepository = new DynamicViewRepository();
	}

	protected void addMenuBar(JMenuBar menuBar, boolean replace, boolean repaint)
	{
		if (currentMenuBar != null)
		{
			frame.setMenuBar(null);
		}
		currentMenuBar = menuBar;
		frame.setJMenuBar(menuBar);
		if (repaint)
		{
			frame.validate();
			frame.repaint();
		}
	}

	protected void addToolBar(JComponent toolBar, boolean replace, boolean repaint)
	{
		if (replace && currentToolBar != null)
		{
			frame.getContentPane().remove(currentToolBar);
		}
		currentToolBar = toolBar;
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		if (repaint)
		{
			frame.validate();
			frame.repaint();
		}
	}

	protected abstract void showFrame();

	public abstract DynamicView addComponent(Component component, AbstractComponent componentModel, String title, String fileName, Icon icon, TabPlace place);

	public void addEmptyDynamicView()
	{
		try
		{
			EmptyComponent emptyComponent = new EmptyComponent();
			emptyDynamicView = addComponent(emptyComponent.create(null), emptyComponent, "Empty Page", null, Window.VIEW_ICON, TabPlace.CENTER_TABS);
		}
		catch (BadParameterException e)
		{
		}
	}

	public Canvas getActiveScene()
	{
		return activeScene;
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public abstract RootWindow getRootWindow();

	public TabWindowList getTabPage()
	{
		return tabWindowList;
	}

	public void removeEmptyDynamicView()
	{
		if (emptyDynamicView != null)
		{
			emptyDynamicView.close();
			emptyDynamicView = null;
		}
	}

	public abstract void removeFileFromProject(String fileName);

	public void update(DockingWindow window, boolean added)
	{
		dynamicaViewRepository.updateViews(window, added);
	}

	public void updateFocusedComponent(AbstractComponent comp)
	{
		if (comp == null)
		{
			View view = getRootWindow().getFocusedView();
			if (view instanceof DynamicView)
			{
				if (((DynamicView) view).getTitle().equals("Parser"))
				{
					TextArea ta = ParsingEditor.getInstance().getTextArea();
					addToolBar(toolBarFactory.createToolBar(ta, true, false), true, true);
					MenuModel model = new MenuModel();
					addMenuBar(menuBarFactory.createMenuBar(ta, model), true, true);
				}
				else
				{
					addToolBar(toolBarFactory.createToolBar(null, false, false), true, true);
					addMenuBar(menuBarFactory.createMenuBar(null, new MenuModel()), true, true);
				}
			}
			else
			{
				addToolBar(toolBarFactory.createToolBar(null, false, false), true, true);
				addMenuBar(menuBarFactory.createMenuBar(null, new MenuModel()), true, true);
			}
			return;
		}
		if (comp instanceof FileComponent)
		{
			MenuModel model = new MenuModel();
			model.save = true;
			model.saveAs = true;
			model.saveAll = true;
			model.print = true;
			model.copy = true;
			model.cut = true;
			model.paste = true;
			model.undo = true;
			model.redo = true;
			model.find = true;
			// see if it is a text area
			TextArea ta;
			if ((ta = TextAreaRepo.getTextArea(comp)) != null)
			{
				addToolBar(toolBarFactory.createToolBar(ta, true, false), true, true);
				addMenuBar(menuBarFactory.createMenuBar(ta, model), true, true);
			}
			else
			{
				if (comp instanceof GrammarComponent)
				{
					model.zoomIn = true;
					model.zoomOut = true;
					addToolBar(toolBarFactory.createToolBar(activeScene, true, true), true, true);
					addMenuBar(menuBarFactory.createMenuBar(activeScene, model), true, true);
				}
			}
		}
	}

	public TabWindowList getTabWindowList()
	{
		return tabWindowList;
	}
}
