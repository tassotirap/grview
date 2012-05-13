package org.grview.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
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
import org.grview.ui.component.AdapterComponent;
import org.grview.ui.component.BadParameterException;
import org.grview.ui.component.Component;
import org.grview.ui.component.DullComponent;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GramComponent;
import org.grview.ui.component.TextAreaRepo;

/** An abstract, top-level windows with docking **/
public abstract class Window implements PropertyChangeListener{
	
	public Window() {
		this(DEFAULT_TITLE);
		windowAdapter = new WindowAdapter(this);
	}
	
	public Window(String title) {
		frame = new JFrame(title);
		frame.setName(DEFAULT_NAME);
	}
	
	/**
	 * Initializes the frame and shows it.
	 */
	protected abstract void showFrame();
	
	public abstract ProjectManager getProjectManager();
	public abstract Project getProject();
	public abstract DynamicView addComponent(java.awt.Component component, org.grview.ui.component.Component componentModel, String title, String fileName, Icon icon, int place);
	public abstract TabWindow[] getTabPage();
	public abstract void removeFileFromProject(String fileName);
	public abstract void renameFile(String oldName, String newName);
	public abstract RootWindow getRootWindow();
	/**
	 * Creates the frame menu bar.
	 *
	 * @return the frame menu bar
	 */
	protected <E extends ActionContextHolder> JMenuBar createMenuBar(final E context, MenuModel model) {
		if (context == null) {
			if (defMenuBar == null) {
				defMenuBar = createMenuBarExt(null, model);
				return defMenuBar;
			}
		}
		if (!menuBars.containsKey(context)) {
			menuBars.put(context, createMenuBarExt(context, model));
		}
		return menuBars.get(context);
	}
	
	/**
	 * Creates always a new toolbar
	 * @param <E> the class of the context, used to invoke actions
	 * @param context the context instance
	 * @return a new menubar
	 */
	@SuppressWarnings("unchecked")
	public <E extends ActionContextHolder> JMenuBar createMenuBarExt(E context, MenuModel model) {
		Menu<E> menu;
		menu = new Menu<E>(new String[] {Menu.FILE, Menu.EDIT,
				Menu.OPTIONS, Menu.PROJECT, 
				Menu.WINDOW, Menu.HELP}, this, getProjectManager(), context, model);

		menu.build();
		return menu;
	}
	
	public void updateFocusedComponent(Component comp) {
		//TODO should identify the parser in a more elegant way 
		if (comp == null) {
			View view = getRootWindow().getFocusedView();
			if (view instanceof DynamicView) {
				if (((DynamicView) view).getTitle().equals("Parser")) {
					TextArea ta = ParsingEditor.getInstance().getTextArea();
					addToolBar(createToolBar(ta, true, false), true, true);
					MenuModel model = new MenuModel();
					addMenuBar(createMenuBar(ta, model), true, true);
				}
				else {
					addToolBar(createToolBar(null, false, false), true, true);
					addMenuBar(createMenuBar(null, new MenuModel()), true, true);
				}
			}
			else {
				addToolBar(createToolBar(null, false, false), true, true);
				addMenuBar(createMenuBar(null, new MenuModel()), true, true);
			}
			return;
		}
		if (comp instanceof FileComponent) {
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
			//see if it is a text area
			TextArea ta;
			if ((ta = TextAreaRepo.getTextArea(comp)) != null) {
				addToolBar(createToolBar(ta, true, false), true, true);
				addMenuBar(createMenuBar(ta, model), true, true);
			}
			else {
				if (comp instanceof GramComponent) {
					model.zoomIn = true;
					model.zoomOut = true;
					addToolBar(createToolBar(activeScene, true, true), true, true);
					addMenuBar(createMenuBar(activeScene, model), true, true);
				}
			}
		}
	}
	
	/**
	 * Add a menu bar to the frame
	 * @param menuBar the JMenuBar that will be added
	 * @param replace, whether it should replace the existing toolbar or not, not used for now
	 * @param repaint, whether the frame should be repainted or not
	 */
	protected void addMenuBar(JMenuBar menuBar, boolean replace, boolean repaint) {
		if (curMenuBar != null) {
			frame.setMenuBar(null);
		}
		curMenuBar = menuBar;
		frame.setJMenuBar(menuBar);
		if (repaint) {
			frame.validate();
			frame.repaint();
		}
	}
	
	/**
	 * Add a tool bar to the frame
	 * @param toolBar the JComponent instance for the tool bar
	 * @param replace, whether the new tool bar should override completely the old one or not
	 * @param repaint, whether the frame should be repainted or not
	 */
	protected void addToolBar(JComponent toolBar, boolean replace, boolean repaint) {
		if (replace && curToolBar != null) {
			frame.getContentPane().remove(curToolBar);
		}
		curToolBar = toolBar;
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		if (repaint) {
			frame.validate();
			frame.repaint();
		}
	}
	
	/**
	 * Creates the frame tool bar.
	 *
	 * @return the frame tool bar
	 */
	protected <T extends ActionContextHolder> JComponent createToolBar(final T ref, boolean tb1, boolean tb2) {
		if (ref == null) {
			if (defToolBar == null) {
				defToolBar = createToolBarExt(ref, false, false);
				return defToolBar;
			}
		}
		if (!toolBars.containsKey(ref) || ref instanceof AdapterComponent) {
			toolBars.put(ref, createToolBarExt(ref, tb1, tb2));
		}
		return toolBars.get(ref);
	}
	
	protected abstract ToolBar.CommandBar<ProjectManager> getProjectToolBar();
	
	/**
	 * Always create a new tool bar
	 * @param <T> the context, in witch the tool bar will be created
	 * @param ref an instance of the same class of context
	 * @param tb1 indicates whether the toolbar 1 will be shown
	 * @param tb2 indicates whether the toolbar 2 will be shown 
	 * @return a new toolbar
	 */
	@SuppressWarnings("unchecked")
	private <T extends ActionContextHolder> JComponent createToolBarExt(final T ref, boolean tb1, boolean tb2) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		ToolBar tb = ToolBar.getInstance();
		ToolBar.T1<T> t1 = tb.new T1<T>(ref) {
			private static final long serialVersionUID = 1L;

			@Override
			public AbstractEditAction<T> getAction(String action) {
				return ref.getActionContext().getAction(action);
			}

		};
		t1.initLayout();
		t1.initActions();
		t1.setLayout(new BoxLayout(t1, BoxLayout.LINE_AXIS));
		ToolBar.T2<T> t2 = tb.new T2<T>(ref) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public AbstractEditAction<T> getAction(String action) {
				return ref.getActionContext().getAction(action);
			}
		};
		t2.initLayout();
		t2.initActions();
		t2.setLayout(new BoxLayout(t2, BoxLayout.LINE_AXIS));
		p.add(getProjectToolBar());
		if (tb1)
			p.add(t1);
		if (tb2)
			p.add(t2);
		return p;

	}
	/**
	 * Add a view based on a dummy component.
	 * @param tb a tab window where the dummy view will be inserted into
	 */
	public void addDummyView(int place) {
		try {
			DullComponent dc = new DullComponent();
			dummyViews.put(place, addComponent(dc.create(null), dc, "Empty Page", null, Window.VIEW_ICON, place));
		} catch (BadParameterException e) {}
	}
	
	public void removeDummyView(int place) {
		if (dummyViews.containsKey(place)) {
			dummyViews.get(place).close();
		}
	}
	/**
	 * Returns a dynamic view with specified id, reusing an existing view if possible.
	 *
	 * @param id the dynamic view id
	 * @return the dynamic view
	 */
	public View getDynamicView(int id) {
		View view = dynamicViewsById.get(new Integer(id));

		//hope it never gets here
		if (view == null)
			view = new DynamicView("Untitled", VIEW_ICON, new JPanel(), null, null, id);

		return view;
	}

	/**
	 * Returns the next available dynamic view id.
	 *
	 * @return the next available dynamic view id
	 */
	public int getDynamicViewId() {
		int id = 0;

		while (dynamicViewsById.containsKey(new Integer(id)))
			id++;

		return id;
	}
	
	//---------------- GETTERS AND SETTERS -------------------------------------
	
	public final static int LEFT_TOP_TABS = 0;
	public final static int RIGHT_BOTTOM_TABS = 1;
	public final static int RIGHT_TOP_TABS = 2;
	public final static int BOTTOM_LEFT_TABS = 3;
	public final static int CENTER_TABS = 4;
	public final static int BOTTOM_RIGHT_TABS = 5;
	
	public HashMap<Integer, DynamicView> getDynamicViewsById(){
		return dynamicViewsById;
	}
	
	public HashMap<Component, DynamicView> getDynamicViewByComponent() {
		return dynamicViewsByComponent;
	}
	
	public HashMap<String, DynamicView> getDynamicViewByPath() {
		return dynamicViewsByPath;
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public ArrayList<DynamicView> getUnsavedViews() {
		return unsavedViews;
	}
	public Canvas getActiveScene(){
		return activeScene;
	}
	
	//---------------- DEFs AND CONs -------------------------------------------
	
	public static final Icon VIEW_ICON = new IconView();
	
	public final static String DEFAULT_TITLE = "GrView";
	public final static String DEFAULT_NAME = "GrView Window";
	
	public final static String UNSAVED_PREFIX = "* ";
	/**
	 * Contains the dynamic views that have been added to the root window, mapped by id
	 */
	protected HashMap<Integer, DynamicView> dynamicViewsById = new HashMap<Integer, DynamicView>();
	/**
	 * Contains the dynamic views that have been added to the root window, mapped by its component
	 */
	protected HashMap<Component, DynamicView> dynamicViewsByComponent = new HashMap<Component, DynamicView>();
	/**
	 * Contains the dynamic views that have been added to the root window, the filename of it's component, when there is one
	 */
	protected HashMap<String, DynamicView> dynamicViewsByPath = new HashMap<String, DynamicView>();
	/**
	 * Views that need to be saved
	 */
	protected ArrayList<DynamicView> unsavedViews = new ArrayList<DynamicView>();
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
	
	/* tool bars dictionary, the default toolbar, and the current toolbar */
	private HashMap<Object, JComponent> toolBars = new HashMap<Object, JComponent>();
	private JComponent defToolBar; //default toolbar
	private JComponent curToolBar; //current toolbar
	
	private HashMap<Object, JMenuBar> menuBars = new HashMap<Object, JMenuBar>();
	private JMenuBar defMenuBar;
	private JMenuBar curMenuBar;
}
