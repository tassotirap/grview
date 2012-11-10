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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
import org.grview.model.ui.IconFactory;
import org.grview.model.ui.IconFactory.IconType;
import org.grview.project.ProjectManager;
import org.grview.ui.Menu.MenuModel;
import org.grview.ui.TabWindowList.TabPlace;
import org.grview.ui.ThemeManager.Theme;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.BadParameterException;
import org.grview.ui.component.ComponentListener;
import org.grview.ui.component.ComponetFactory;
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
import org.grview.ui.menubar.MenuBarFactory;
import org.grview.ui.toolbar.ToolBarFactory;

public class MainWindow extends Window implements ComponentListener
{
	private ViewMap perspectiveMap = new ViewMap();
	private RootWindowProperties rootWindowProperties;

	public MainWindow(String projectsRootPath)
	{
		setLookAndFeel();
		this.projectManager = new ProjectManager(this, projectsRootPath);
		this.windowAdapter = new WindowAdapter(this, projectManager);
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

	private void createDefaultViews()
	{
		try
		{
			activeScene = CanvasFactory.createCanvas(this.projectManager.getProject().getGrammarFile());
			ArrayList<TabItem> tabItems = createTabs();
			dynamicaViewRepository.createDefaultViews(tabItems, perspectiveMap);
		}
		catch (BadParameterException e)
		{
			e.printStackTrace();
		}
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
			addToolBar(toolBarFactory.createToolBar(activeScene, true, true), false, false);
			addMenuBar(menuBarFactory.createMenuBar(activeScene, model), false, false);
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

	private ArrayList<TabItem> createTabs() throws BadParameterException
	{
		IconFactory iconFactory = new IconFactory();
		ArrayList<TabItem> tabItems = new ArrayList<TabItem>();
		tabItems.add(new TabItem("Project", new ProjectsComponent().create(projectManager.getProject()), TabPlace.RIGHT_BOTTOM_TABS, iconFactory.getIcon(IconType.PROJECT_ICON)));
		tabItems.add(new TabItem("Outline", new OutlineComponent().create(activeScene), TabPlace.RIGHT_TOP_TABS, iconFactory.getIcon(IconType.OVERVIEW_CON)));
		tabItems.add(new TabItem("Grammar", new GeneratedGrammarComponent().create(activeScene), TabPlace.BOTTOM_LEFT_TABS, iconFactory.getIcon(IconType.GRAMMAR_ICON)));
		tabItems.add(new TabItem("Syntax Stack", new SyntaxStackComponent().create(activeScene), TabPlace.BOTTOM_LEFT_TABS, iconFactory.getIcon(IconType.SYNTACTIC_STACK_ICON)));
		tabItems.add(new TabItem("Sem. Stack", new SemanticStackComponent().create(activeScene), TabPlace.BOTTOM_LEFT_TABS, iconFactory.getIcon(IconType.SEMANTIC_STACK_ICON)));
		tabItems.add(new TabItem("Output", new OutputComponent().create(activeScene), TabPlace.BOTTOM_LEFT_TABS, iconFactory.getIcon(IconType.ACTIVE_OUTPUT_ICON)));
		tabItems.add(new TabItem("Parser", new ParserComponent().create(projectManager.getProject().getProjectsRootPath()), TabPlace.BOTTOM_RIGHT_TABS, iconFactory.getIcon(IconType.PARSER_ICON)));
		return tabItems;
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
	public DynamicView addComponent(Component component, org.grview.ui.component.AbstractComponent componentModel, String title, String fileName, Icon icon, TabPlace place)
	{
		DynamicView view = new DynamicView(title, icon, component, componentModel, fileName, dynamicaViewRepository.getDynamicViewId());
		if (componentModel instanceof GrammarComponent)
		{
			activeScene = CanvasFactory.getCanvasFromFile(fileName);
		}
		if (componentModel != null)
			componentModel.addComponentListener(this);
		getTabWindowList().getTabWindow(place).addTab(view);
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
		if (dynamicaViewRepository.containsDynamicView(source))
		{
			DynamicView view = dynamicaViewRepository.getDynamicView(source);
			if (!view.getTitle().startsWith(UNSAVED_PREFIX))
				view.getViewProperties().setTitle(UNSAVED_PREFIX + view.getTitle());
			projectManager.setUnsavedView(((FileComponent) source).getPath(), view);
		}

	}

	@Override
	public RootWindow getRootWindow()
	{
		return rootWindow;
	}



	@Override
	public void removeFileFromProject(String fileName)
	{
		projectManager.closeFile(fileName);
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
}
