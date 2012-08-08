package org.grview.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import org.grview.actions.AbstractEditAction;
import org.grview.actions.ActionContextHolder;
import org.grview.canvas.Canvas;
import org.grview.editor.TextArea;
import org.grview.model.ui.IconView;
import org.grview.parser.ParsingEditor;
import org.grview.project.Project;
import org.grview.project.ProjectManager;
import org.grview.ui.Menu.MenuModel;
import org.grview.ui.ToolBar.CommandBar;
import org.grview.ui.ToolBar.ToolBarCanvas;
import org.grview.ui.ToolBar.ToolBarFile;
import org.grview.ui.component.AdapterComponent;
import org.grview.ui.component.BadParameterException;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.EmptyComponent;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GramComponent;
import org.grview.ui.component.TextAreaRepo;

/** An abstract, top-level windows with docking **/
public abstract class Window implements PropertyChangeListener
{

	public final static int LEFT_TOP_TABS = 0;
	public final static int RIGHT_BOTTOM_TABS = 1;
	public final static int RIGHT_TOP_TABS = 2;
	public final static int BOTTOM_LEFT_TABS = 3;
	public final static int CENTER_TABS = 4;
	public final static int BOTTOM_RIGHT_TABS = 5;
	public final static Icon VIEW_ICON = new IconView();
	public final static String DEFAULT_TITLE = "GrView";
	public final static String DEFAULT_NAME = "GrView Window";
	public final static String UNSAVED_PREFIX = "* ";

	/**
	 * Contains the dynamic views that have been added to the root window,
	 * mapped by id
	 */
	protected HashMap<Integer, DynamicView> dynamicViewsById = new HashMap<Integer, DynamicView>();

	/**
	 * Contains the dynamic views that have been added to the root window,
	 * mapped by its component
	 */
	protected HashMap<AbstractComponent, DynamicView> dynamicViewsByComponent = new HashMap<AbstractComponent, DynamicView>();

	/**
	 * Contains the dynamic views that have been added to the root window, the
	 * filename of it's component, when there is one
	 */
	protected HashMap<String, DynamicView> dynamicViewsByPath = new HashMap<String, DynamicView>();

	/**
	 * The application frame
	 */
	protected JFrame frame;

	/**
	 * The focused canvas, where graphs can be drawn
	 */
	protected Canvas activeScene;

	private HashMap<Integer, DynamicView> dummyViews = new HashMap<Integer, DynamicView>();

	protected WindowAdapter windowAdapter;

	private HashMap<Object, JComponent> toolBars = new HashMap<Object, JComponent>();

	private JComponent defaultToolBar;

	private JComponent currentToolBar;

	private HashMap<Object, JMenuBar> menuBars = new HashMap<Object, JMenuBar>();

	private JMenuBar defaultMenuBar;
	private JMenuBar currentMenuBar;

	public Window()
	{
		this(DEFAULT_TITLE);
		windowAdapter = new WindowAdapter(this);
	}

	public Window(String title)
	{
		frame = new JFrame(title);
		frame.setName(DEFAULT_NAME);
	}

	/**
	 * Create Drawable Canvas Toolbar
	 * @param ref
	 * @param toolBar
	 * @return
	 */
	private <T extends ActionContextHolder> ToolBarCanvas createToolBarCanvas(final T ref)
	{
		ToolBarCanvas toolBarCanvas = new ToolBarCanvas((Canvas)ref);
		toolBarCanvas.initLayout();
		toolBarCanvas.initActions();
		toolBarCanvas.setLayout(new BoxLayout(toolBarCanvas, BoxLayout.LINE_AXIS));
		return toolBarCanvas;
	}

	/**
	 * Always create a new tool bar
	 * 
	 * @param <T>
	 *            the context, in witch the tool bar will be created
	 * @param ref
	 *            an instance of the same class of context
	 * @param enableToolBarFile
	 *            indicates whether the toolbar 1 will be shown
	 * @param enableToolBarCanvas
	 *            indicates whether the toolbar 2 will be shown
	 * @return a new toolbar
	 */
	@SuppressWarnings("rawtypes")
	private <T extends ActionContextHolder> JComponent createToolBarExt(final T ref, boolean enableToolBarFile, boolean enableToolBarCanvas)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(getNewFileToolBar());

		if (enableToolBarFile)
		{
			ToolBarFile<T> toolBarFile = createToolBarFile(ref);
			panel.add(toolBarFile);
		}
		if (enableToolBarCanvas)
		{
			ToolBarCanvas toolBarCanvas = createToolBarCanvas(ref);
			panel.add(toolBarCanvas);
		}
		return panel;

	}

	@SuppressWarnings("rawtypes")
	private <T extends ActionContextHolder> ToolBarFile<T> createToolBarFile(final T ref)
	{
		ToolBarFile<T> toolBarFile = new ToolBarFile<T>(ref);
		toolBarFile.initLayout();
		toolBarFile.initActions();
		toolBarFile.setLayout(new BoxLayout(toolBarFile, BoxLayout.LINE_AXIS));
		return toolBarFile;
	}

	/**
	 * Add a menu bar to the frame
	 * 
	 * @param menuBar
	 *            the JMenuBar that will be added
	 * @param replace
	 *            , whether it should replace the existing toolbar or not, not
	 *            used for now
	 * @param repaint
	 *            , whether the frame should be repainted or not
	 */
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

	/**
	 * Add a tool bar to the frame
	 * 
	 * @param toolBar
	 *            the JComponent instance for the tool bar
	 * @param replace
	 *            , whether the new tool bar should override completely the old
	 *            one or not
	 * @param repaint
	 *            , whether the frame should be repainted or not
	 */
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

	/**
	 * Creates the frame menu bar.
	 * 
	 * @return the frame menu bar
	 */
	@SuppressWarnings("rawtypes")
	protected <E extends ActionContextHolder> JMenuBar createMenuBar(final E context, MenuModel model)
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

	/**
	 * Creates the frame tool bar.
	 * 
	 * @return the frame tool bar
	 */
	@SuppressWarnings("rawtypes")
	protected <T extends ActionContextHolder> JComponent createToolBar(final T ref, boolean enableToolBarFile, boolean enableToolBarCanvas)
	{
		if (ref == null)
		{
			if (defaultToolBar == null)
			{
				defaultToolBar = createToolBarExt(ref, false, false);
				return defaultToolBar;
			}
		}
		if (!toolBars.containsKey(ref) || ref instanceof AdapterComponent)
		{
			toolBars.put(ref, createToolBarExt(ref, enableToolBarFile, enableToolBarCanvas));
		}
		return toolBars.get(ref);
	}

	protected abstract CommandBar<ProjectManager> getNewFileToolBar();

	/**
	 * Initializes the frame and shows it.
	 */
	protected abstract void showFrame();

	public abstract DynamicView addComponent(java.awt.Component component, org.grview.ui.component.AbstractComponent componentModel, String title, String fileName, Icon icon, int place);

	// ---------------- DEFs AND CONs
	// -------------------------------------------

	/**
	 * Add a view based on a dummy component.
	 * 
	 * @param tb
	 *            a tab window where the dummy view will be inserted into
	 */
	public void addDummyView(int place)
	{
		try
		{
			EmptyComponent emptyComponent = new EmptyComponent();
			dummyViews.put(place, addComponent(emptyComponent.create(null), emptyComponent, "Empty Page", null, Window.VIEW_ICON, place));
		}
		catch (BadParameterException e)
		{
		}
	}

	/**
	 * Creates always a new toolbar
	 * 
	 * @param <E>
	 *            the class of the context, used to invoke actions
	 * @param context
	 *            the context instance
	 * @return a new menubar
	 */

	@SuppressWarnings("rawtypes")
	public <E extends ActionContextHolder> JMenuBar createMenuBarExt(E context, MenuModel model)
	{
		Menu<E> menu = new Menu<E>(new String[]
		{ Menu.FILE, Menu.EDIT, Menu.OPTIONS, Menu.PROJECT, Menu.WINDOW, Menu.HELP }, this, getProjectManager(), context, model);
		menu.build();
		return menu;
	}

	public Canvas getActiveScene()
	{
		return activeScene;
	}

	/**
	 * Returns a dynamic view with specified id, reusing an existing view if
	 * possible.
	 * 
	 * @param id
	 *            the dynamic view id
	 * @return the dynamic view
	 */
	public View getDynamicView(int id)
	{
		View view = dynamicViewsById.get(new Integer(id));

		// hope it never gets here
		if (view == null)
			view = new DynamicView("Untitled", VIEW_ICON, new JPanel(), null, null, id);

		return view;
	}

	public HashMap<AbstractComponent, DynamicView> getDynamicViewByComponent()
	{
		return dynamicViewsByComponent;
	}

	public HashMap<String, DynamicView> getDynamicViewByPath()
	{
		return dynamicViewsByPath;
	}

	/**
	 * Returns the next available dynamic view id.
	 * 
	 * @return the next available dynamic view id
	 */
	public int getDynamicViewId()
	{
		int id = 0;

		while (dynamicViewsById.containsKey(new Integer(id)))
			id++;

		return id;
	}

	public HashMap<Integer, DynamicView> getDynamicViewsById()
	{
		return dynamicViewsById;
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public abstract Project getProject();

	public abstract ProjectManager getProjectManager();

	public abstract RootWindow getRootWindow();

	public abstract TabWindow[] getTabPage();

	public void removeDummyView(int place)
	{
		if (dummyViews.containsKey(place))
		{
			dummyViews.get(place).close();
		}
	}

	public abstract void removeFileFromProject(String fileName);

	public abstract void renameFile(String oldName, String newName);

	public void updateFocusedComponent(AbstractComponent comp)
	{
		// TODO should identify the parser in a more elegant way
		if (comp == null)
		{
			View view = getRootWindow().getFocusedView();
			if (view instanceof DynamicView)
			{
				if (((DynamicView) view).getTitle().equals("Parser"))
				{
					TextArea ta = ParsingEditor.getInstance().getTextArea();
					addToolBar(createToolBar(ta, true, false), true, true);
					MenuModel model = new MenuModel();
					addMenuBar(createMenuBar(ta, model), true, true);
				}
				else
				{
					addToolBar(createToolBar(null, false, false), true, true);
					addMenuBar(createMenuBar(null, new MenuModel()), true, true);
				}
			}
			else
			{
				addToolBar(createToolBar(null, false, false), true, true);
				addMenuBar(createMenuBar(null, new MenuModel()), true, true);
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
				addToolBar(createToolBar(ta, true, false), true, true);
				addMenuBar(createMenuBar(ta, model), true, true);
			}
			else
			{
				if (comp instanceof GramComponent)
				{
					model.zoomIn = true;
					model.zoomOut = true;
					addToolBar(createToolBar(activeScene, true, true), true, true);
					addMenuBar(createMenuBar(activeScene, model), true, true);
				}
			}
		}
	}
}
