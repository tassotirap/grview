package org.grview.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import org.grview.canvas.Canvas;
import org.grview.editor.TextArea;
import org.grview.model.ui.IconView;
import org.grview.parser.ParsingEditor;
import org.grview.project.ProjectManager;
import org.grview.ui.Menu.MenuModel;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.BadParameterException;
import org.grview.ui.component.EmptyComponent;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GrammarComponent;
import org.grview.ui.component.TextAreaRepo;
import org.grview.ui.menubar.MenuBarFactory;
import org.grview.ui.toolbar.ToolBarFactory;

/** An abstract, top-level windows with docking **/
public abstract class Window
{

	public final static int BOTTOM_LEFT_TABS = 3;
	public final static int BOTTOM_RIGHT_TABS = 5;
	public final static int CENTER_TABS = 4;
	public final static String DEFAULT_NAME = "GrView Window";
	public final static String DEFAULT_TITLE = "GrView";
	public final static int LEFT_TOP_TABS = 0;
	public final static int RIGHT_BOTTOM_TABS = 1;
	public final static int RIGHT_TOP_TABS = 2;
	public final static String UNSAVED_PREFIX = "* ";
	public final static Icon VIEW_ICON = new IconView();

	private JMenuBar currentMenuBar;
	private JComponent currentToolBar;
	
	

	private HashMap<Integer, DynamicView> dummyViews = new HashMap<Integer, DynamicView>();
	
	

	protected Canvas activeScene;

	protected HashMap<AbstractComponent, DynamicView> dynamicViewsByComponent = new HashMap<AbstractComponent, DynamicView>();

	protected HashMap<Integer, DynamicView> dynamicViewsById = new HashMap<Integer, DynamicView>();

	protected HashMap<String, DynamicView> dynamicViewsByPath = new HashMap<String, DynamicView>();

	protected JFrame frame;
	
	protected WindowAdapter windowAdapter;
	
	protected ProjectManager projectManager;
	protected MenuBarFactory menuBarFactory;
	protected ToolBarFactory toolBarFactory;

	public Window()
	{
		this(DEFAULT_TITLE);
	}

	public Window(String title)
	{
		frame = new JFrame(title);
		frame.setName(DEFAULT_NAME);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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


	/**
	 * Creates the frame tool bar.
	 * 
	 * @return the frame tool bar
	 */
	

	/**
	 * Initializes the frame and shows it.
	 */
	protected abstract void showFrame();

	public abstract DynamicView addComponent(Component component, AbstractComponent componentModel, String title, String fileName, Icon icon, int place);

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
}
