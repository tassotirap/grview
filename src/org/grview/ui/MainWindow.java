package org.grview.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;
import net.infonode.docking.WindowBar;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.MixedViewHandler;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

import org.grview.canvas.CanvasFactory;
import org.grview.editor.TextArea;
import org.grview.model.FileNames;
import org.grview.model.ui.IconFactory;
import org.grview.model.ui.IconFactory.IconType;
import org.grview.model.ui.IconView;
import org.grview.parser.ParsingEditor;
import org.grview.project.ProjectManager;
import org.grview.ui.Menu.MenuModel;
import org.grview.ui.TabWindowList.TabPlace;
import org.grview.ui.ThemeManager.Theme;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.BadParameterException;
import org.grview.ui.component.ComponentListener;
import org.grview.ui.component.ComponetFactory;
import org.grview.ui.component.EmptyComponent;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GeneratedGrammarComponent;
import org.grview.ui.component.GrammarComponent;
import org.grview.ui.component.OutlineComponent;
import org.grview.ui.component.OutputComponent;
import org.grview.ui.component.ParserComponent;
import org.grview.ui.component.ProjectsComponent;
import org.grview.ui.component.SemanticStackComponent;
import org.grview.ui.component.SyntaxStackComponent;
import org.grview.ui.component.TextAreaRepo;
import org.grview.ui.dynamicview.DynamicView;
import org.grview.ui.dynamicview.DynamicViewRepository;
import org.grview.ui.menubar.MenuBarFactory;
import org.grview.ui.toolbar.ToolBarFactory;

public class MainWindow implements ComponentListener
{
	private ViewMap perspectiveMap = new ViewMap();
	private RootWindowProperties rootWindowProperties;

	public final static String DEFAULT_NAME = "GrView Window";
	public final static String DEFAULT_TITLE = "GrView";
	public final static String UNSAVED_PREFIX = "* ";
	public final static Icon VIEW_ICON = new IconView();

	private DynamicView emptyDynamicView = null;
	private JMenuBar currentMenuBar;
	private JComponent currentToolBar;
	private TabWindowList tabWindow;

	private JFrame frame;
	
	private RootWindow rootWindow;
	private ProjectManager projectManager;
	private MenuBarFactory menuBarFactory;
	private ToolBarFactory toolBarFactory;
	private DynamicViewRepository dynamicaViewRepository;

	public MainWindow(String projectsPath)
	{
		init();
		setLookAndFeel();
		this.projectManager = new ProjectManager(this, projectsPath);
		new WindowAdapter(this, projectManager);
		this.menuBarFactory = new MenuBarFactory(projectManager, this);
		this.toolBarFactory = new ToolBarFactory(projectManager);
		this.rootWindowProperties = new RootWindowProperties();

		createRootWindow();
		createDefaultViews();
		setDefaultLayout();
		try
		{
			openFiles();
		}
		catch (BadParameterException e)
		{
			e.printStackTrace();
		}
		showFrame();
	}

	private ArrayList<TabItem> createDefaultTabs() throws BadParameterException
	{
		IconFactory iconFactory = new IconFactory();
		ArrayList<TabItem> tabItems = new ArrayList<TabItem>();
		tabItems.add(new TabItem("Project", new ProjectsComponent().create(projectManager.getProject()), TabPlace.RIGHT_BOTTOM_TABS, iconFactory.getIcon(IconType.PROJECT_ICON)));
		tabItems.add(new TabItem("Outline", new OutlineComponent().create(projectManager.getActiveScene()), TabPlace.RIGHT_TOP_TABS, iconFactory.getIcon(IconType.OVERVIEW_CON)));
		tabItems.add(new TabItem("Grammar", new GeneratedGrammarComponent().create(projectManager.getActiveScene()), TabPlace.BOTTOM_LEFT_TABS, iconFactory.getIcon(IconType.GRAMMAR_ICON)));
		tabItems.add(new TabItem("Syntax Stack", new SyntaxStackComponent().create(projectManager.getActiveScene()), TabPlace.BOTTOM_LEFT_TABS, iconFactory.getIcon(IconType.SYNTACTIC_STACK_ICON)));
		tabItems.add(new TabItem("Sem. Stack", new SemanticStackComponent().create(projectManager.getActiveScene()), TabPlace.BOTTOM_LEFT_TABS, iconFactory.getIcon(IconType.SEMANTIC_STACK_ICON)));
		tabItems.add(new TabItem("Output", new OutputComponent().create(projectManager.getActiveScene()), TabPlace.BOTTOM_LEFT_TABS, iconFactory.getIcon(IconType.ACTIVE_OUTPUT_ICON)));
		tabItems.add(new TabItem("Parser", new ParserComponent().create(projectManager.getProject().getProjectsRootPath()), TabPlace.BOTTOM_RIGHT_TABS, iconFactory.getIcon(IconType.PARSER_ICON)));
		return tabItems;
	}

	private void createDefaultViews()
	{
		try
		{
			projectManager.setActiveScene(CanvasFactory.createCanvas(this.projectManager.getProject().getGrammarFile()));
			ArrayList<TabItem> tabItems = createDefaultTabs();
			dynamicaViewRepository.createDefaultViews(tabItems, perspectiveMap);
		}
		catch (BadParameterException e)
		{
			e.printStackTrace();
		}
	}

	private void createDynamicViewMenu(View view)
	{
		MenuModel model = new MenuModel();
		if (((DynamicView) view).getTitle().equals("Parser"))
		{
			TextArea textArea = ParsingEditor.getInstance().getTextArea();
			addToolBar(toolBarFactory.createToolBar(textArea, true, false), true, true);
			addMenuBar(menuBarFactory.createMenuBar(textArea, model), true, true);
		}
		else
		{
			addToolBar(toolBarFactory.createToolBar(null, false, false), true, true);
			addMenuBar(menuBarFactory.createMenuBar(null, model), true, true);
		}
	}

	private void createMenuModel(String name, AbstractComponent component)
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
		if (name.endsWith(FileNames.GRAM_EXTENSION))
		{
			model.zoomIn = true;
			model.zoomOut = true;
			addToolBar(toolBarFactory.createToolBar(projectManager.getActiveScene(), true, true), false, false);
			addMenuBar(menuBarFactory.createMenuBar(projectManager.getActiveScene(), model), false, false);
		}
		else
		{
			addToolBar(toolBarFactory.createToolBar(TextAreaRepo.getTextArea(component), true, false), false, false);
			addMenuBar(menuBarFactory.createMenuBar(TextAreaRepo.getTextArea(component), model), false, false);
		}
	}

	private void createRootWindow()
	{

		MixedViewHandler handler = new MixedViewHandler(perspectiveMap, new ViewSerializer()
		{
			@Override
			public View readView(ObjectInputStream in) throws IOException
			{
				return dynamicaViewRepository.getDynamicView(in.readInt());
			}

			@Override
			public void writeView(View view, ObjectOutputStream out) throws IOException
			{
				out.writeInt(((DynamicView) view).getId());
			}
		});

		rootWindowProperties.addSuperObject(ThemeManager.getCurrentTheme().getRootWindowProperties());

		rootWindow = DockingUtil.createRootWindow(perspectiveMap, handler, true);
		rootWindow.getRootWindowProperties().addSuperObject(rootWindowProperties);
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
		rootWindow.addListener(new WindowAdapter(this, projectManager));
		rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
	}

	private void init()
	{
		init(DEFAULT_TITLE);
	}

	private void init(String title)
	{
		frame = new JFrame(title);
		frame.setName(DEFAULT_NAME);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.tabWindow = new TabWindowList();
		this.dynamicaViewRepository = new DynamicViewRepository();
	}

	private void openFiles() throws BadParameterException
	{
		List<File> filesToOpen = projectManager.getOpenedFiles();

		for (int i = 0; i < filesToOpen.size(); i++)
		{
			String name = filesToOpen.get(i).getName();
			AbstractComponent component = ComponetFactory.createFileComponent(name.substring(name.lastIndexOf(".")));

			if (component != null)
			{
				component.addComponentListener(this);
				IconFactory iconFactory = new IconFactory();
				Icon icon = iconFactory.getIcon(name);
				addComponent(component.create(filesToOpen.get(i).getAbsolutePath()), component, name, filesToOpen.get(i).getAbsolutePath(), icon, TabPlace.CENTER_TABS);
				if (i == filesToOpen.size() - 1)
				{
					createMenuModel(name, component);
				}
			}
		}
	}

	private void setDefaultLayout()
	{
		for (int i = 0; i < TabWindowList.TAB_SIZE; i++)
		{
			if (dynamicaViewRepository.getDefaultLayout().size() > i)
			{
				TabWindow tabWindow = new TabWindow(dynamicaViewRepository.getDefaultLayout().get(i).toArray());
				getTabWindowList().add(tabWindow);
				getTabWindowList().getTabWindow(i).getTabWindowProperties().getCloseButtonProperties().setVisible(false);
			}
		}
		rootWindow.setWindow(new SplitWindow(false, 0.75f, new SplitWindow(true, 0.8f, getTabWindowList().getCenterTab(), new SplitWindow(false, 0.5f, getTabWindowList().getRightTopTab(), getTabWindowList().getRightBottonTab())), new SplitWindow(true, 0.7f, getTabWindowList().getBottonLeftTab(), getTabWindowList().getBottonRightTab())));

		WindowBar windowBar = rootWindow.getWindowBar(Direction.DOWN);

		while (windowBar.getChildWindowCount() > 0)
			windowBar.getChildWindow(0).close();
	}

	private void setLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
		}
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
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

	protected void showFrame()
	{
		frame.getContentPane().add(rootWindow, BorderLayout.CENTER);
		frame.setSize(900, 700);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenDim.width - frame.getWidth()) / 2, (screenDim.height - frame.getHeight()) / 2);
		frame.setVisible(true);
		frame.addWindowListener(new FrameAdapter());
	}

	public DynamicView addComponent(Component component, AbstractComponent componentModel, String title, String fileName, Icon icon, TabPlace place)
	{
		if (componentModel != null)
		{
			DynamicView view = new DynamicView(title, icon, component, componentModel, fileName, dynamicaViewRepository.getDynamicViewId());
			if (componentModel instanceof GrammarComponent)
			{
				projectManager.setActiveScene(CanvasFactory.getCanvasFromFile(fileName));
			}

			componentModel.addComponentListener(this);
			getTabWindowList().getTabWindow(place).addTab(view);
			updateFocusedComponent(componentModel);

			return view;
		}
		return null;
	}

	public void addEmptyDynamicView() throws BadParameterException
	{
		EmptyComponent emptyComponent = new EmptyComponent();
		emptyDynamicView = addComponent(emptyComponent.create(null), emptyComponent, "Empty Page", null, VIEW_ICON, TabPlace.CENTER_TABS);
	}

	public void changeTheme(Theme theme)
	{
		ThemeManager.changeTheme(rootWindowProperties, theme);
	}

	@Override
	public void ContentChanged(AbstractComponent source, Object oldValue, Object newValue)
	{
		if (dynamicaViewRepository.containsDynamicView(source))
		{
			DynamicView view = dynamicaViewRepository.getDynamicView(source);
			if (!view.getTitle().startsWith(UNSAVED_PREFIX))
				view.getViewProperties().setTitle(UNSAVED_PREFIX + view.getTitle());
			projectManager.setUnsavedView(((FileComponent) source).getPath(), view);
		}

	}

	public JFrame getFrame()
	{
		return frame;
	}

	public TabWindowList getTabs()
	{
		return tabWindow;
	}

	public TabWindowList getTabWindowList()
	{
		return tabWindow;
	}

	public void removeEmptyDynamicView()
	{
		if (emptyDynamicView != null)
		{
			emptyDynamicView.close();
			emptyDynamicView = null;
		}
	}

	public void setSaved(String path)
	{
		if (dynamicaViewRepository.containsDynamicView(path))
		{
			DynamicView dynamicView = dynamicaViewRepository.getDynamicView(path);

			if (projectManager.hasUnsavedView(dynamicView))
			{
				if (dynamicView.getTitle().startsWith(UNSAVED_PREFIX))
				{
					dynamicView.getViewProperties().setTitle(dynamicView.getTitle().replace(UNSAVED_PREFIX, ""));
				}
			}

			while (projectManager.hasUnsavedView(dynamicView))
			{
				projectManager.removeUnsavedView(path);
			}
		}
	}

	public void updateFocusedComponent(AbstractComponent component)
	{
		if (component == null)
		{
			View view = rootWindow.getFocusedView();
			if (view instanceof DynamicView)
			{
				createDynamicViewMenu(view);
			}
			else
			{
				MenuModel model = new MenuModel();
				addToolBar(toolBarFactory.createToolBar(null, false, false), true, true);
				addMenuBar(menuBarFactory.createMenuBar(null, model), true, true);
			}
			return;
		}
		if (component instanceof FileComponent)
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
			TextArea textArea = TextAreaRepo.getTextArea(component);
			if (textArea != null)
			{
				addToolBar(toolBarFactory.createToolBar(textArea, true, false), true, true);
				addMenuBar(menuBarFactory.createMenuBar(textArea, model), true, true);
			}
			else
			{
				if (component instanceof GrammarComponent)
				{
					model.zoomIn = true;
					model.zoomOut = true;
					addToolBar(toolBarFactory.createToolBar(projectManager.getActiveScene(), true, true), true, true);
					addMenuBar(menuBarFactory.createMenuBar(projectManager.getActiveScene(), model), true, true);
				}
			}
		}
	}

	public void updateWindow(DockingWindow window, boolean added)
	{
		dynamicaViewRepository.updateViews(window, added);
	}
}
