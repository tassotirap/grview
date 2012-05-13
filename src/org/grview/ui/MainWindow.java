package org.grview.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.grview.actions.AbstractEditAction;
import org.grview.canvas.CanvasFactory;
import org.grview.model.FileExtension;
import org.grview.model.ui.IconRepository;
import org.grview.project.Project;
import org.grview.project.ProjectManager;
import org.grview.ui.Menu.MenuModel;
import org.grview.ui.component.ComponentListener;
import org.grview.ui.component.ConsoleComponent;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GeneratedGrammarComponent;
import org.grview.ui.component.GramComponent;
import org.grview.ui.component.InputAdapterComponent;
import org.grview.ui.component.JavaComponent;
import org.grview.ui.component.LexComponent;
import org.grview.ui.component.OutlineComponent;
import org.grview.ui.component.OutputComponent;
import org.grview.ui.component.ParserComponent;
import org.grview.ui.component.ProjectsComponent;
import org.grview.ui.component.SemComponent;
import org.grview.ui.component.SemanticStackComponent;
import org.grview.ui.component.SimpleTextAreaComponent;
import org.grview.ui.component.SyntaxStackComponent;
import org.grview.ui.component.TextAreaRepo;
import org.grview.ui.component.XMLComponent;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;
import net.infonode.docking.WindowBar;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.MixedViewHandler;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

public class MainWindow extends Window implements ComponentListener {

	/** constructor 
	 * sets all project paths, create a new default window, 
	 * gets an id, and says hello on the volatile state manager
	 * **/
	private MainWindow(String projectsRootPath) {
		this.projectsRootPath = projectsRootPath;
		this.project = Project.restoreProject(projectsRootPath);
		this.projectManager = new ProjectManager(this, project);
		createRootWindow();
		setDefaultLayout();
		this.id = getNewID();
		CanvasFactory.getVolatileStateManager(id).getMonitor().addPropertyChangeListener(this);
		instances = new HashMap<String, MainWindow>();
		instances.put(projectsRootPath, this);
		showFrame();
	}

	/**
	 * gets an instance depending on the project path
	 * @param projectsRootPath the root directory containing the projects and possibly others
	 * @param projectPath the path exclusively to the current active project
	 * @return
	 */
	public static MainWindow getInstance(String projectsRootPath)
	{
		if (instances == null)
		{
			instances = new HashMap<String, MainWindow>();
			instances.put(projectsRootPath, new MainWindow(projectsRootPath));
		}
		else if (!instances.keySet().contains(projectsRootPath)){
			instances.put(projectsRootPath, new MainWindow(projectsRootPath));
		}
		return instances.get(projectsRootPath);
	}

	
	/**
	 * Creates the default views
	 */
	private void createDefaultViews() {
		int nextId;
		activeScene = CanvasFactory.createCanvas(project.getGrammarFile().get(project.getVersion()));
		try {
			String titles[] = new String[] { "Project",
					"Outline",
					"Console",
					"Grammar",
					"Syntax Stack",
					"Sem. Stack",
					"Output",
					"Parser"};
			JComponent components[] = new JComponent[] {
					new ProjectsComponent().create(project),
					new OutlineComponent().create(activeScene),
					new ConsoleComponent().create(null),
					new GeneratedGrammarComponent().create(activeScene),
					new SyntaxStackComponent().create(activeScene),
					new SemanticStackComponent().create(activeScene),
					new OutputComponent().create(activeScene),
					new ParserComponent().create(project.getProjectsRootPath())};
			
			int layoutOrder[] = new int[] {RIGHT_BOTTOM_TABS,
											RIGHT_TOP_TABS,
											BOTTOM_LEFT_TABS,
											BOTTOM_LEFT_TABS,
											BOTTOM_LEFT_TABS,
											BOTTOM_LEFT_TABS,
											BOTTOM_LEFT_TABS,
											BOTTOM_RIGHT_TABS};

			Icon viewIcon[] = new Icon[] { IconRepository.getInstance().PROJECT_ICON,
											IconRepository.getInstance().OVERVIEW_CON,
											IconRepository.getInstance().CONSOLE_ICON,
											IconRepository.getInstance().GRAMMAR_ICON,
											IconRepository.getInstance().SYNTACTIC_STACK_ICON,
											IconRepository.getInstance().SEMANTIC_STACK_ICON,
											IconRepository.getInstance().ACTIVE_OUTPUT_ICON,
											IconRepository.getInstance().PARSER_ICON};
			
			for (int i = 0; i < defaultLayout.length; i++) 
				defaultLayout[i] = new Vector<DynamicView>();
			
			for (int i = 0; i < components.length; i++) {
				nextId = getDynamicViewId();
				DynamicView view = new DynamicView(titles[i], viewIcon[i], components[i], null, null, nextId);
				defaultLayout[layoutOrder[i]].add(view);
				perspectiveMap.addView(i, view);
				windowAdapter.updateViews(view, true);
			}
			
			HashMap<String, org.grview.ui.component.Component> fileComponents = new HashMap<String, org.grview.ui.component.Component>();
			fileComponents.put(FileExtension.GRAM_FILE, new GramComponent());
			fileComponents.put(FileExtension.LEX_FILE, new LexComponent());
			fileComponents.put(FileExtension.SEM_FILE, new SemComponent());
			fileComponents.put(FileExtension.XML_FILE, new XMLComponent());
			fileComponents.put(FileExtension.TXT_FILE, new SimpleTextAreaComponent());
			fileComponents.put(FileExtension.JAVA_FILE, new JavaComponent());
			fileComponents.put(FileExtension.IN_FILE, new InputAdapterComponent());
			
			ArrayList<File> files = project.getOpenedFiles();
			if (files.size() == 0) {
				project.getOpenedFiles().add(project.getGrammarFile().get(project.getVersion()));
			}
			for (int i = 0; i < files.size(); i++) {
				String name = files.get(i).getName();
				org.grview.ui.component.Component component = fileComponents.get(name.substring(name.lastIndexOf(".")));
				nextId = getDynamicViewId();
				Icon icon = IconRepository.getIconByFileName(name);
				DynamicView view = new DynamicView(name, icon, component.create(files.get(i).getAbsolutePath()), component, files.get(i).getAbsolutePath(), nextId);
				component.addComponentListener(this);
				defaultLayout[CENTER_TABS].add(view);
				perspectiveMap.addView(i + perspectiveMap.getViewCount(), view);
				windowAdapter.updateViews(view, true);
				if (i == files.size() - 1) {
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
					if (name.endsWith(FileExtension.GRAM_FILE)) {
						model.zoomIn = true;
						model.zoomOut = true;
						addToolBar(createToolBar(activeScene, true, true), false, false);
						addMenuBar(createMenuBar(activeScene, model), false, false);
					}
					else {
						addToolBar(createToolBar(TextAreaRepo.getTextArea(component), true, false), false, false);
						addMenuBar(createMenuBar(TextAreaRepo.getTextArea(component), model), false, false);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Creates the root window and the views.
	 */
	private void createRootWindow() {
		
		// The mixed view map makes it easy to mix static and dynamic views inside the same root window
		MixedViewHandler handler = new MixedViewHandler(perspectiveMap, new ViewSerializer() {
			public void writeView(View view, ObjectOutputStream out) throws IOException {
				out.writeInt(((DynamicView) view).getId());
			}

			public View readView(ObjectInputStream in) throws IOException {
				return getDynamicView(in.readInt());
			}
		});

		//rootWindow = DockingUtil.createHeavyweightSupportedRootWindow(viewMap, handler, true);
		rootWindow = DockingUtil.createRootWindow(perspectiveMap, handler, true);

		// Set gradient theme. The theme properties object is the super object of our properties object, which
		// means our property value settings will override the theme values
		properties.addSuperObject(currentTheme.getRootWindowProperties());

		// Our properties object is the super object of the root window properties object, so all property values of the
		// theme and in our property object will be used by the root window
		rootWindow.getRootWindowProperties().addSuperObject(properties);

		// Enable the bottom window bar
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);

		// Add a listener which shows dialogs when a window is closing or closed.
		rootWindow.addListener(new WindowAdapter(this));

		// Add a mouse button listener that closes a window when it's clicked with the middle mouse button.
		rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);

		////////////////////// CREATE THE VIEWS ////////////////////////////////
		createDefaultViews();
		////////////////////////////////////////////////////////////////////////////
	}

	/**
	 * Sets the default window layout.
	 */
	private void setDefaultLayout() {
		DynamicView views[];
		for (int i = 0; i < tabPage.length; i++) {
			if (defaultLayout.length > i) {
				views = new DynamicView[defaultLayout[i].size()];
				tabPage[i] = new TabWindow(defaultLayout[i].toArray(views));
				tabPage[i].getTabWindowProperties().getCloseButtonProperties().setVisible(false);
			}
		}
		rootWindow.setWindow(new SplitWindow(false,
											0.75f,
											new SplitWindow(true, 
													0.8f,
													tabPage[CENTER_TABS],
													new SplitWindow(false,
															0.5f,
															tabPage[RIGHT_TOP_TABS],
															tabPage[RIGHT_BOTTOM_TABS])),
											new SplitWindow(true,
															0.7f,
															tabPage[BOTTOM_LEFT_TABS], 
															tabPage[BOTTOM_RIGHT_TABS])));

		WindowBar windowBar = rootWindow.getWindowBar(Direction.DOWN);


		while (windowBar.getChildWindowCount() > 0)
			windowBar.getChildWindow(0).close();
	}

	
	@Override
	public DynamicView addComponent(Component component, org.grview.ui.component.Component componentModel, String title, String fileName, Icon icon, int place) {
		DynamicView view = new DynamicView(title, icon, component, componentModel, fileName, getDynamicViewId());
		if (componentModel != null) componentModel.addComponentListener(this);
		tabPage[place].addTab(view);
		if (componentModel != null) updateFocusedComponent(componentModel);
		return view;
		// not necessary for now project.getOpenedFiles().add(new File(fileName));
	}
	
	
	public void setSaved(String path) {
		if (dynamicViewsByPath.containsKey(path)) {
			DynamicView dv = dynamicViewsByPath.get(path);
			if (unsavedViews.contains(dv)) {
				unsavedViews.remove(dv);
				if (dv.getTitle().startsWith(UNSAVED_PREFIX)) {
					dv.getViewProperties().setTitle(dv.getTitle().substring(UNSAVED_PREFIX.length()));
				}
			}
		}
	}
	
	@Override
	public void removeFileFromProject(String fileName) {
		projectManager.closeFile(fileName);
	}
	
	@Override
	public void renameFile(String oldName, String newName) {
		String oldTitle = oldName.replace(projectsRootPath,"").replace("\\", "").replace("/", "");
		String newTitle = newName.replace(projectsRootPath, "").replace("\\", "").replace("/", "");
		for (DynamicView view : dynamicViewsById.values()) {
			if (view.getTitle().equals(oldTitle)) {
				view.getViewProperties().setTitle(newTitle);
				break;
			}
		}
		projectManager.renameFile(oldName, newName);
	}
	
	/**
	 * Initializes the frame and shows it.
	 */
	@Override
	protected void showFrame() {
		frame.getContentPane().add(rootWindow, BorderLayout.CENTER);
		frame.setSize(900, 700);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(
				(screenDim.width - 900) / 2,
				(screenDim.height - 700) / 2
		);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	protected ToolBar.CommandBar<ProjectManager> getProjectToolBar() {
		ToolBar tb = ToolBar.getInstance();
		ToolBar.T0<ProjectManager> t0 = tb.new T0<ProjectManager>(projectManager) {
			private static final long serialVersionUID = 1L;

			@Override
			public AbstractEditAction<ProjectManager> getAction(String action)  {
				return projectManager.getActionContext().getAction(action);
			}
		};
		t0.initLayout();
		t0.initActions();
		t0.setLayout(new BoxLayout(t0, BoxLayout.LINE_AXIS));
		return t0;
	}
	
	@Override
	public RootWindow getRootWindow() {
		return rootWindow;
	}

	@Override
	public void ContentChanged(org.grview.ui.component.Component source,
			Object oldValue, Object newValue) {
		if (dynamicViewsByComponent.containsKey(source)) {
			DynamicView view = dynamicViewsByComponent.get(source);
			if (!view.getTitle().startsWith(UNSAVED_PREFIX))
				view.getViewProperties().setTitle(UNSAVED_PREFIX + view.getTitle());
			unsavedViews.add(view);
			if (source instanceof FileComponent) {
				projectManager.setUnsaved(((FileComponent) source).getPath());
			}
		}
		
	}
	
	public void propertyChange(PropertyChangeEvent event) {		

	}

	private synchronized String getNewID() {
		return String.valueOf(lastID++);
	}
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
		final String projectRootPath = (args.length >= 1)?args[0]:".";
		final ArrayList<String> projectsPath;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Project project = Project.restoreProject(projectRootPath);
				new MainWindow(projectRootPath);
				//MainWindow.getInstance(projectsPath);
			}
		});
	}
	
	@Override
	public ProjectManager getProjectManager(){
		return projectManager;
	}

	@Override
	public Project getProject() {
		return project;
	}
	
	@Override
	public TabWindow[] getTabPage() {
		return tabPage;
	}
	//############################# VARS ##################################
	
	private static int lastID;
	
	private TabWindow tabPage[] = new TabWindow[6];
	
	private String id;
	/**
	 * Sets the path to the root directory of the current projects
	 */
	private String projectsRootPath = "Projects";

	/**
	 * The one and only root window
	 */
	private RootWindow rootWindow;

	/**
	 * An array of the static views
	 */
	private View[] views = new View[7];

	/**
	 * Contains all the static views
	 */
	private ViewMap perspectiveMap = new ViewMap();

	/**
	 * The view menu items
	 */
	private JMenuItem[] viewItems = new JMenuItem[views.length];

	/**
	 * The currently applied docking windows theme
	 */
	private DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();

	private Vector<DynamicView> defaultLayout[] = new Vector[6];
	/**
	 * In this properties object the modified property values for close buttons etc. are stored. This object is cleared
	 * when the theme is changed.
	 */
	private RootWindowProperties properties = new RootWindowProperties();

	/**
	 * Where the layouts are stored.
	 */
	private byte[][] layouts = new byte[3][];


	private static HashMap<String,MainWindow> instances;
	
	/** the current project **/
	private Project project;
	
	/** the current project manager **/
	private ProjectManager projectManager;
}