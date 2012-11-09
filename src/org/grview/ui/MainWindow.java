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
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
import org.grview.model.FileNames;
import org.grview.model.ui.IconRepository;
import org.grview.project.ProjectManager;
import org.grview.ui.Menu.MenuModel;
import org.grview.ui.ThemeManager.Theme;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.BadParameterException;
import org.grview.ui.component.ComponentListener;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GeneratedGrammarComponent;
import org.grview.ui.component.GrammarComponent;
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
import org.grview.ui.toolbar.BaseToolBar;
import org.grview.ui.toolbar.ToolBarFile;

public class MainWindow extends Window implements ComponentListener
{
	private Vector<DynamicView> defaultLayout[];

	/**
	 * Contains all the static views
	 */
	private ViewMap perspectiveMap = new ViewMap();

	/**
	 * The one and only root window
	 */
	private RootWindow rootWindow;

	/**
	 * In this properties object the modified property values for close buttons
	 * etc. are stored. This object is cleared when the theme is changed.
	 */
	private RootWindowProperties rootWindowProperties;

	protected WindowAdapter windowAdapter;

	private TabWindow tabPage[];

	/**
	 * constructor sets all project paths, create a new default window, gets an
	 * id, and says hello on the volatile state manager
	 * **/
	private MainWindow(String projectsRootPath)
	{
		projectMediator = new ProjectManager(this, projectsRootPath);
		windowAdapter = new WindowAdapter(this, projectMediator);
		rootWindowProperties = new RootWindowProperties();
		defaultLayout = new Vector[6];
		tabPage = new TabWindow[6];

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

	public static void main(String[] args) throws Exception
	{
		UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
		final String projectRootPath = (args.length >= 1) ? args[0] : ".";
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new MainWindow(projectRootPath);
			}
		});
	}

	/**
	 * Creates the default views
	 */
	private void createDefaultViews()
	{
		activeScene = CanvasFactory.createCanvas(projectMediator.getProject().getGrammarFile());
		try
		{
			ArrayList<TabItem> tabItems = createTabs();

			for (int i = 0; i < defaultLayout.length; i++)
				defaultLayout[i] = new Vector<DynamicView>();

			for (int i = 0; i < tabItems.size(); i++)
			{
				int nextId = getDynamicViewId();
				DynamicView view = new DynamicView(tabItems.get(i).getTitle(), tabItems.get(i).getViewIcon(), tabItems.get(i).getComponent(), null, null, nextId);
				defaultLayout[tabItems.get(i).getLayoutOrder()].add(view);
				perspectiveMap.addView(i, view);
				windowAdapter.updateViews(view, true);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private org.grview.ui.component.AbstractComponent createFileComponent(String type)
	{
		if (type.equalsIgnoreCase(FileNames.GRAM_EXTENSION))
			return new GrammarComponent();
		if (type.equalsIgnoreCase(FileNames.LEX_EXTENSION))
			return new LexComponent();
		if (type.equalsIgnoreCase(FileNames.SEM_EXTENSION))
			return new SemComponent();
		if (type.equalsIgnoreCase(FileNames.XML_EXTENSION))
			return new XMLComponent();
		if (type.equalsIgnoreCase(FileNames.TXT_EXTENSION))
			return new SimpleTextAreaComponent();
		if (type.equalsIgnoreCase(FileNames.JAVA_EXTENSION))
			return new JavaComponent();
		if (type.equalsIgnoreCase(FileNames.IN_EXTENSION))
			return new InputAdapterComponent();
		return null;
	}

	private void createMenuModel(String name, org.grview.ui.component.AbstractComponent component)
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
			addToolBar(createToolBar(activeScene, true, true), false, false);
			addMenuBar(createMenuBar(activeScene, model), false, false);
		}
		else
		{
			addToolBar(createToolBar(TextAreaRepo.getTextArea(component), true, false), false, false);
			addMenuBar(createMenuBar(TextAreaRepo.getTextArea(component), model), false, false);
		}
	}

	/**
	 * Creates the root window and the views.
	 */
	private void createRootWindow()
	{

		MixedViewHandler handler = new MixedViewHandler(perspectiveMap, new ViewSerializer()
		{
			@Override
			public View readView(ObjectInputStream in) throws IOException
			{
				return getDynamicView(in.readInt());
			}

			@Override
			public void writeView(View view, ObjectOutputStream out) throws IOException
			{
				out.writeInt(((DynamicView) view).getId());
			}
		});

		rootWindow = DockingUtil.createRootWindow(perspectiveMap, handler, true);
		rootWindowProperties.addSuperObject(ThemeManager.getCurrentTheme().getRootWindowProperties());
		rootWindow.getRootWindowProperties().addSuperObject(rootWindowProperties);
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
		rootWindow.addListener(new WindowAdapter(this, projectMediator));
		rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
	}

	private ArrayList<TabItem> createTabs() throws BadParameterException
	{
		ArrayList<TabItem> tabItems = new ArrayList<TabItem>();
		tabItems.add(new TabItem("Project", new ProjectsComponent().create(projectMediator.getProject()), RIGHT_BOTTOM_TABS, IconRepository.getInstance().PROJECT_ICON));
		tabItems.add(new TabItem("Outline", new OutlineComponent().create(activeScene), RIGHT_TOP_TABS, IconRepository.getInstance().OVERVIEW_CON));
		tabItems.add(new TabItem("Grammar", new GeneratedGrammarComponent().create(activeScene), BOTTOM_LEFT_TABS, IconRepository.getInstance().GRAMMAR_ICON));
		tabItems.add(new TabItem("Syntax Stack", new SyntaxStackComponent().create(activeScene), BOTTOM_LEFT_TABS, IconRepository.getInstance().SYNTACTIC_STACK_ICON));
		tabItems.add(new TabItem("Sem. Stack", new SemanticStackComponent().create(activeScene), BOTTOM_LEFT_TABS, IconRepository.getInstance().SEMANTIC_STACK_ICON));
		tabItems.add(new TabItem("Output", new OutputComponent().create(activeScene), BOTTOM_LEFT_TABS, IconRepository.getInstance().ACTIVE_OUTPUT_ICON));
		tabItems.add(new TabItem("Parser", new ParserComponent().create(projectMediator.getProject().getProjectsRootPath()), BOTTOM_RIGHT_TABS, IconRepository.getInstance().PARSER_ICON));
		return tabItems;
	}

	private void openFiles() throws BadParameterException
	{
		List<File> filesToOpen = projectMediator.getProject().getOpenedFiles();
		if (filesToOpen.size() == 0)
		{
			projectMediator.getProject().getOpenedFiles().add(projectMediator.getProject().getGrammarFile());
		}
		for (int i = 0; i < filesToOpen.size(); i++)
		{
			String name = filesToOpen.get(i).getName();
			AbstractComponent component = createFileComponent(name.substring(name.lastIndexOf(".")));

			if (component != null)
			{
				component.addComponentListener(this);

				Icon icon = IconRepository.getIconByFileName(name);
				addComponent(component.create(filesToOpen.get(i).getAbsolutePath()), component, name, filesToOpen.get(i).getAbsolutePath(), icon, CENTER_TABS);
				if (i == filesToOpen.size() - 1)
				{
					createMenuModel(name, component);
				}
			}
		}
	}

	/**
	 * Sets the default window layout.
	 */
	private void setDefaultLayout()
	{
		DynamicView views[];
		for (int i = 0; i < tabPage.length; i++)
		{
			if (defaultLayout.length > i)
			{
				views = new DynamicView[defaultLayout[i].size()];
				tabPage[i] = new TabWindow(defaultLayout[i].toArray(views));
				tabPage[i].getTabWindowProperties().getCloseButtonProperties().setVisible(false);
			}
		}
		rootWindow.setWindow(new SplitWindow(false, 0.75f, new SplitWindow(true, 0.8f, tabPage[CENTER_TABS], new SplitWindow(false, 0.5f, tabPage[RIGHT_TOP_TABS], tabPage[RIGHT_BOTTOM_TABS])), new SplitWindow(true, 0.7f, tabPage[BOTTOM_LEFT_TABS], tabPage[BOTTOM_RIGHT_TABS])));

		WindowBar windowBar = rootWindow.getWindowBar(Direction.DOWN);

		while (windowBar.getChildWindowCount() > 0)
			windowBar.getChildWindow(0).close();
	}

	@Override
	protected BaseToolBar<ProjectManager> getNewFileToolBar()
	{
		ToolBarFile<ProjectManager> toolBarNewFile = new ToolBarFile<ProjectManager>(projectMediator);
		toolBarNewFile.setLayout(new BoxLayout(toolBarNewFile, BoxLayout.LINE_AXIS));
		return toolBarNewFile;
	}

	/**
	 * Initializes the frame and shows it.
	 */
	@Override
	protected void showFrame()
	{
		frame.getContentPane().add(rootWindow, BorderLayout.CENTER);
		frame.setSize(900, 700);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenDim.width - frame.getWidth()) / 2, (screenDim.height - frame.getHeight()) / 2);
		frame.setVisible(true);
		frame.addWindowListener(new FrameAdapter());
	}

	@Override
	public DynamicView addComponent(Component component, org.grview.ui.component.AbstractComponent componentModel, String title, String fileName, Icon icon, int place)
	{
		DynamicView view = new DynamicView(title, icon, component, componentModel, fileName, getDynamicViewId());
		if (componentModel instanceof GrammarComponent)
		{
			activeScene = CanvasFactory.getCanvasFromFile(fileName);
		}
		if (componentModel != null)
			componentModel.addComponentListener(this);
		tabPage[place].addTab(view);
		if (componentModel != null)
			updateFocusedComponent(componentModel);
		return view;
	}

	public void changeTheme(Theme theme)
	{
		ThemeManager.changeTheme(rootWindowProperties, theme);
	}

	@Override
	public void ContentChanged(org.grview.ui.component.AbstractComponent source, Object oldValue, Object newValue)
	{
		if (dynamicViewsByComponent.containsKey(source))
		{
			DynamicView view = dynamicViewsByComponent.get(source);
			if (!view.getTitle().startsWith(UNSAVED_PREFIX))
				view.getViewProperties().setTitle(UNSAVED_PREFIX + view.getTitle());
			projectMediator.setUnsavedView(((FileComponent) source).getPath(), view);
		}

	}

	@Override
	public RootWindow getRootWindow()
	{
		return rootWindow;
	}

	@Override
	public TabWindow[] getTabPage()
	{
		return tabPage;
	}

	@Override
	public void removeFileFromProject(String fileName)
	{
		projectMediator.closeFile(fileName);
	}

	public void setSaved(String path)
	{
		if (dynamicViewsByPath.containsKey(path))
		{
			DynamicView dynamicView = dynamicViewsByPath.get(path);

			if (projectMediator.hasUnsavedView(dynamicView))
			{
				if (dynamicView.getTitle().startsWith(UNSAVED_PREFIX))
				{
					dynamicView.getViewProperties().setTitle(dynamicView.getTitle().replace(UNSAVED_PREFIX, ""));
				}
			}

			while (projectMediator.hasUnsavedView(dynamicView))
			{
				projectMediator.removeUnsavedView(path);
			}
		}
	}
}
