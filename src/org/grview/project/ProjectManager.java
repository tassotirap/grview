package org.grview.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JOptionPane;

import org.grview.actions.ActionContextHolder;
import org.grview.actions.AsinActionContext;
import org.grview.actions.AsinActionSet;
import org.grview.bsh.input.AbstractInputHandler;
import org.grview.bsh.input.DefaultInputHandlerProvider;
import org.grview.bsh.input.InputHandlerProvider;
import org.grview.bsh.input.ProjectManagerInputHandler;
import org.grview.canvas.Canvas;
import org.grview.editor.StandaloneTextArea;
import org.grview.editor.TextArea;
import org.grview.model.FileExtension;
import org.grview.model.ui.IconRepository;
import org.grview.project.tree.FileTree;
import org.grview.ui.DynamicView;
import org.grview.ui.MainWindow;
import org.grview.ui.Window;
import org.grview.ui.component.AdvancedTextAreaComponent;
import org.grview.ui.component.Component;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GramComponent;
import org.grview.ui.component.GrammarRepo;
import org.grview.ui.component.InputAdapterComponent;
import org.grview.ui.component.JavaComponent;
import org.grview.ui.component.LexComponent;
import org.grview.ui.component.SemComponent;
import org.grview.ui.component.TextAreaRepo;
import org.grview.ui.component.XMLComponent;
import org.grview.util.Log;


public class ProjectManager implements ActionContextHolder {

	//provides an action context, specially for user input
	private AsinActionContext<ProjectManagerBeanShellAction,AsinActionSet<ProjectManagerBeanShellAction>> actionContext;
	private InputHandlerProvider inputHandlerProvider;

	private Window window;
	private Project project;

	/* unsaved files (path) */
	private static ArrayList<String> unsavedFiles = new ArrayList<String>();

	public ProjectManager(Window window, Project project) {
		this.window = window;
		this.project = project;
		initInputHandler();
		DefaultActionSet das = new DefaultActionSet(this);
		das.load();
		das.initKeyBindings(); //not working yet
		addActionSet(das);
		window.getFrame().addKeyListener(inputHandlerProvider.getInputHandler().getKeyEventInterceptor());
		window.getFrame().addMouseListener(inputHandlerProvider.getInputHandler().getMouseEventInterceptor());
	}

	/**
	 * Marks a file as unsaved
	 * @param path the absolute path to the file
	 */
	public void setUnsaved(String path) {
		if (!unsavedFiles.contains(path)) {
			unsavedFiles.add(path);
		}
	}

	/**
	 * Used to find out if a file is unsaved
	 * @param path the absolute path to file
	 */
	public static boolean isUnsaved(String path) {
		return unsavedFiles.contains(path);
	}

	public static void saveFile(String st) {
		saveFileExt(st);
	}

	public static void saveFile(StandaloneTextArea sta) {
		saveFileExt(sta);
	}

	public static void saveFile(Canvas canvas) {
		saveFileExt(canvas);
	}

	public static void saveFileExt(Object... params) {
		String path = null;
		boolean componentSaved = false;
		if (params.length >= 1) {
			Object p1 = params[0];
			if (p1 instanceof TextArea || p1 instanceof FileComponent) {
				FileComponent fc;
				if (p1 instanceof TextArea) {
					fc = TextAreaRepo.getComponent((TextArea) p1);
				}
				else {
					fc = (FileComponent) p1;
				}
				if (fc != null) {
					fc.saveFile();
					path = fc.getPath();
				}
				componentSaved = true;
			}
			else if (p1 instanceof Canvas) {
				GramComponent gc = (GramComponent) GrammarRepo.getCompByCanvas((Canvas) p1);
				gc.saveFile();
				path = gc.getPath();
				componentSaved = true;
			}
			else if (p1 instanceof String) {
				path = (String) p1;
			}
		}
		Project p;
		File file;
		if (path != null) {
			if ((file = new File(path)).isFile()) {
				file = file.getParentFile();
			}
			if (Project.isProject(file)) {
				if ((p = Project.getProjectByPath(file.getAbsolutePath())) != null) {
					MainWindow mw = MainWindow.getInstance(p.getBaseDir().getAbsolutePath());
					if (mw != null && !componentSaved) {
						for (DynamicView dv : mw.getUnsavedViews()) {
							Component comp = dv.getComponentModel();
							if (comp instanceof FileComponent &&
									((FileComponent) comp).getPath().equals(path)) {
								((FileComponent) comp).saveFile();
							}
						}
					}
					if (mw != null) {
						mw.setSaved(path);
					}
					p.writeProject();
					FileTree.reload(p.getProjectsRootPath());
				}
			}
		}
	}

	public void createFile(String name, FileExtension extension) throws IOException {
		if (extension.getExtension().equals(FileExtension.GRAM_FILE)) {
			if (project.getGrammarFile().get(project.getVersion()) != null) {
				JOptionPane.showMessageDialog(null, "Only one grammar file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileExtension.SEM_FILE)) {
			if (project.getSemFile().get(project.getVersion()) != null) {
				JOptionPane.showMessageDialog(null, "Only one semantic routines file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileExtension.LEX_FILE)) {
			if (project.getLexFile().get(project.getVersion()) != null) {
				JOptionPane.showMessageDialog(null, "Only one lexical scanner file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileExtension.OUT_FILE)) {
			if (project.getLexFile().get(project.getVersion()) != null) {
				JOptionPane.showMessageDialog(null, "This kind of file is not supported yet", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else {
			File baseDir = project.getBaseDir();
			File newFile = null;
			if (extension != null) {
				if (!name.endsWith(extension.getExtension())) {
					name += extension.getExtension();
				}
			}
			if (!name.startsWith(baseDir.getAbsolutePath())) {
				newFile = new File(baseDir, name);
			}
			else {
				newFile = new File(name);
			}
			try {
				newFile.createNewFile();
				openFile(newFile.getAbsolutePath());
			} catch (SecurityException e) {
				JOptionPane.showMessageDialog(null, "Could not create file. Probably you do not have permission to write on disk.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
				Log.log(Log.WARNING, this, "A security exception was thrown while trying to create a new file.", e);
			}
			FileTree.reload(baseDir.getAbsolutePath());
		}
	}

	/**
	 * Open a file
	 * @param path the absolute path of the file.
	 */
	public void openFile(String path) {
		File file = new File(path);
		if (!isFileOpen(path)) {
			try {
				if (file.getName().toLowerCase().endsWith(FileExtension.LEX_FILE.toLowerCase())) {
					LexComponent lc = new LexComponent();
					window.addComponent(lc.create(path), lc, file.getName(), path, IconRepository.getInstance().LEX_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.SEM_FILE.toLowerCase())) {
					SemComponent sc = new SemComponent();
					window.addComponent(sc.create(path), sc, file.getName(), path, IconRepository.getInstance().SEM_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.GRAM_FILE.toLowerCase())) {
					GramComponent gc = new GramComponent();
					window.addComponent(gc.create(path), gc, file.getName(),path, IconRepository.getInstance().GRAM_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.XML_FILE.toLowerCase())) {
					XMLComponent xc = new XMLComponent();
					window.addComponent(xc.create(path), xc, file.getName(), path, IconRepository.getInstance().XML_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.JAVA_FILE.toLowerCase())) {
					JavaComponent jc = new JavaComponent();
					window.addComponent(jc.create(path), jc, file.getName(), path, IconRepository.getInstance().JAVA_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.IN_FILE.toLowerCase())) {
					InputAdapterComponent iac = new InputAdapterComponent();
					window.addComponent(iac.create(path), iac, file.getName(), path, IconRepository.getInstance().IN_ICON, Window.CENTER_TABS);
				}
				else {
					AdvancedTextAreaComponent atac = new AdvancedTextAreaComponent(null);
					window.addComponent(atac.create(path), atac, file.getName(), path, IconRepository.getInstance().TXT_ICON, Window.CENTER_TABS);
				}
				project.getOpenedFiles().add(new File(path));
				project.writeProject();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


	/**
	 * Close an opened file from the perspective of the current project
	 * @param fileName, the absolute file name of the file being closed.
	 */
	public void closeFile(String fileName) {
		File file;
		for (File f : project.getOpenedFiles()) {
			if (f.getAbsolutePath().equals(fileName)) {
				project.getOpenedFiles().remove(f);
				file = f;
				break;
			}
		}
		project.writeProject();
	}

	/** Informs whether a file is currently opened or not.
	 * @param fileName the full path of the file
	 * @return true if the file is open
	 */
	public boolean isFileOpen(String fileName) {
		for (File f : project.getOpenedFiles()) {
			if (f.getAbsolutePath().equals(fileName))
				return true;
		}
		return false;
	}

	public void renameFile(String oldName, String newName) {
		File fr = null;
		for (File f : project.getOpenedFiles()) {
			if (f.getAbsolutePath().equals(oldName)) {
				fr = f;
				break;
			}
		}
		if (fr != null && project.getOpenedFiles().contains(fr)) {
			project.getOpenedFiles().remove(fr);
			project.getOpenedFiles().add(new File(newName));
		}
		if (oldName.endsWith(FileExtension.GRAM_FILE)) {
			project.getGrammarFile().put(project.getVersion(), new File(newName));
		}
		else if (oldName.endsWith(FileExtension.SEM_FILE)) {
			project.getSemFile().put(project.getVersion(), new File(newName));
		}
		else if (oldName.endsWith(FileExtension.LEX_FILE)) {
			project.getLexFile().put(project.getVersion(), new File(newName));
		}
		project.writeProject();
	}
	/**
	 * Creates an actionContext and initializes the input handler for this canvas.
	 * When compared to TextArea, Canvas has an mixed approach to input event handling,
	 * for it can deal with input through invocation of action beans or through direct
	 * , usual, invocation, mainly when the input comes from some widget.
	 */
	public void initInputHandler()
	{
		actionContext = new AsinActionContext<ProjectManagerBeanShellAction, AsinActionSet<ProjectManagerBeanShellAction>>()
		{
			@Override
			public void invokeAction(EventObject evt, ProjectManagerBeanShellAction action)
			{
				action.invoke(ProjectManager.this);
			}
		};

		inputHandlerProvider =  new DefaultInputHandlerProvider(new ProjectManagerInputHandler(this)
		{
			@Override
			protected ProjectManagerBeanShellAction getAction(String action)
			{
				return actionContext.getAction(action);
			}
		});
	}

	/**
	 * Adds a new action set to the canvas's list of ActionSets.
	 *
	 * @param actionSet the actionSet to add
	 */
	public void addActionSet(AsinActionSet<ProjectManagerBeanShellAction> actionSet)
	{
		actionContext.addActionSet(actionSet);
	}

	public AsinActionContext<ProjectManagerBeanShellAction, AsinActionSet<ProjectManagerBeanShellAction>> getActionContext() {
		return actionContext;
	}

	/**
	 * The default action set for canvas
	 */
	protected static class DefaultActionSet extends AsinActionSet<ProjectManagerBeanShellAction>
	{
		private final ProjectManager pManager;

		DefaultActionSet(ProjectManager pManager)
		{
			super(null, ProjectManager.class.getResource("/org/grview/project/project.actions.xml"));
			this.pManager = pManager;
		}

		@Override
		protected ProjectManagerBeanShellAction[] getArray(int size)
		{
			return new ProjectManagerBeanShellAction[size];
		}

		@Override
		protected String getProperty(String name)
		{
			return null;
		}

		@SuppressWarnings("unchecked")
		public AbstractInputHandler getInputHandler()
		{
			return pManager.inputHandlerProvider.getInputHandler();
		}

		@Override
		protected ProjectManagerBeanShellAction createBeanShellAction(String actionName,
				String code,
				String selected,
				boolean noRepeat,
				boolean noRecord,
				boolean noRememberLast)
		{
			return new ProjectManagerBeanShellAction(actionName,code);
		}
	}
}
