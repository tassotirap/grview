package org.grview.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.grview.actions.AbstractEditAction;
import org.grview.actions.ActionContextHolder;
import org.grview.canvas.Canvas;
import org.grview.editor.TextArea;
import org.grview.project.ProjectManager;
import org.grview.util.LangHelper;
import org.grview.util.Log;

public class Menu<E extends ActionContextHolder> extends JMenuBar
{
	private static final long serialVersionUID = 1L;

	ArrayList<String> menus;
	Window window;
	E context;
	ProjectManager projectManager;
	int contextDesc;
	MenuModel model; 

	public final static int DEFAULT_CONTEXT = 0;
	public final static int CANVAS_CONTEXT = 1;
	public final static int TEXTAREA_CONTEXT = 2;

	public final static String DOTS = "...";

	public final static String FILE = "File";
	public final static String EDIT = "Edit";
	public final static String OPTIONS = "Options";
	public final static String PROJECT = "Project";
	public final static String WINDOW = "Window";
	public final static String HELP = "Help";

	public Menu(String[] menus, Window window, ProjectManager projectManager, E context, MenuModel model)
	{
		this.window = window;
		this.menus = new ArrayList<String>();
		this.context = context;
		this.projectManager = projectManager;
		this.model = model;
		if (context instanceof Canvas)
		{
			contextDesc = CANVAS_CONTEXT;
		}
		else if (context instanceof TextArea)
		{
			contextDesc = TEXTAREA_CONTEXT;
		}
		else
		{
			contextDesc = DEFAULT_CONTEXT;
		}
		for (String m : menus)
		{
			this.menus.add(m);
		}
	}

	public void build()
	{
		for (int i = 0; i < menus.size(); i++)
		{
			String m = menus.get(i);
			if (m.equals(EDIT))
			{
				this.add(createEditMenu());
			}
			else if (m.equals(FILE))
			{
				this.add(createFileMenu());
			}
			else if (m.equals(OPTIONS))
			{
				this.add(createOptionsMenu());
			}
			else if (m.equals(PROJECT))
			{
				this.add(createProjectMenu());
			}
			else if (m.equals(HELP))
			{
				this.add(createHelpMenu());
			}
			else if (m.equals(WINDOW))
			{
				this.add(createWindowMenu());
			}
		}
	}

	private JMenu createFileMenu()
	{
		JMenu mFile = new JMenu(FILE);
		final ArrayList<String> PMbuttons = new ArrayList<String>();
		final ArrayList<String> Ebuttons = new ArrayList<String>();
		JMenuItem nFile = new JMenuItem(LangHelper.new_file + DOTS);
		nFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		JMenuItem nProject = new JMenuItem(LangHelper.new_project);
		nProject.setEnabled(false);
		nProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		JMenuItem oFile = new JMenuItem(LangHelper.open_file + DOTS);
		oFile.setEnabled(false);
		oFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		JMenuItem oProject = new JMenuItem(LangHelper.open_project + DOTS);
		oProject.setEnabled(false);
		oProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		JMenuItem save = new JMenuItem(LangHelper.save);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		JMenuItem saveAll = new JMenuItem(LangHelper.save_all);
		saveAll.setEnabled(false);
		saveAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		JMenuItem saveAs = new JMenuItem(LangHelper.save_as + DOTS);
		saveAs.setEnabled(false);
		JMenuItem print = new JMenuItem(LangHelper.print + DOTS);
		print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		JMenu exportAs = new JMenu("Export As");
		JMenuItem png = new JMenuItem("PNG File" + DOTS);
		png.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, ActionEvent.CTRL_MASK));
		JMenuItem ebnf = new JMenuItem("Extended BNF" + DOTS);
		ebnf.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, ActionEvent.CTRL_MASK));
		exportAs.add(png);
		exportAs.add(ebnf);
		png.setEnabled(model.pngExport);
		ebnf.setEnabled(model.ebnfExport);
		if (!model.pngExport && !model.ebnfExport)
		{
			exportAs.setEnabled(false);
		}
		save.setEnabled(model.save);
		print.setEnabled(model.print);
		JMenuItem quit = new JMenuItem("Quit");
		quit.setEnabled(true);
		PMbuttons.add(nFile.getText());
		PMbuttons.add(oFile.getText());
		PMbuttons.add(nProject.getText());
		PMbuttons.add(oProject.getText());
		PMbuttons.add(saveAll.getText());
		PMbuttons.add(quit.getText());
		Ebuttons.add(saveAs.getText());
		Ebuttons.add(save.getText());
		Ebuttons.add(print.getText());
		Ebuttons.add(png.getText());
		Ebuttons.add(ebnf.getText());

		MenuListener menuListener = new MenuListener(PMbuttons, Ebuttons);

		nFile.addActionListener(menuListener);
		nProject.addActionListener(menuListener);
		oFile.addActionListener(menuListener);
		oProject.addActionListener(menuListener);
		save.addActionListener(menuListener);
		saveAll.addActionListener(menuListener);
		saveAs.addActionListener(menuListener);
		print.addActionListener(menuListener);
		png.addActionListener(menuListener);
		ebnf.addActionListener(menuListener);
		quit.addActionListener(menuListener);
		mFile.add(nFile);
		mFile.add(nProject);
		mFile.add(new JSeparator());
		mFile.add(oFile);
		mFile.add(oProject);
		mFile.add(new JSeparator());
		mFile.add(save);
		mFile.add(saveAll);
		mFile.add(saveAs);
		mFile.add(new JSeparator());
		mFile.add(print);
		mFile.add(new JSeparator());
		mFile.add(exportAs);
		mFile.add(new JSeparator());
		mFile.add(quit);
		return mFile;
	}

	private JMenu createOptionsMenu()
	{
		JMenu optionsMenu = new JMenu(OPTIONS);
		optionsMenu.setEnabled(false);
		return optionsMenu;
	}

	private JMenu createProjectMenu()
	{
		JMenu projectMenu = new JMenu(PROJECT);
		projectMenu.setEnabled(false);
		return projectMenu;
	}

	private JMenu createWindowMenu()
	{
		JMenu windowMenu = new JMenu(WINDOW);
		windowMenu.setEnabled(false);
		return windowMenu;
	}

	private JMenu createHelpMenu()
	{
		JMenu helpMenu = new JMenu(HELP);
		helpMenu.setEnabled(false);
		return helpMenu;
	}

	private JMenu createEditMenu()
	{
		JMenu edit = new JMenu("Edit");
		final ArrayList<String> PMbuttons = new ArrayList<String>();
		final ArrayList<String> Ebuttons = new ArrayList<String>();
		edit.setMnemonic(KeyEvent.VK_E);
		JMenuItem undo = new JMenuItem("Undo");
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		JMenuItem redo = new JMenuItem("Redo");
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		JMenuItem copy = new JMenuItem("Copy");
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		JMenuItem cut = new JMenuItem("Cut");
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		JMenuItem paste = new JMenuItem("Paste");
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		JMenuItem zoomIn = new JMenuItem("Zoom In");
		zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		JMenuItem zoomOut = new JMenuItem("Zoom Out");
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
		JMenuItem findReplace = new JMenuItem("Find/Replace...");
		findReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		JMenuItem preferences = new JMenuItem("Preferences...");
		Ebuttons.add(undo.getText());
		Ebuttons.add(redo.getText());
		Ebuttons.add(copy.getText());
		Ebuttons.add(cut.getText());
		Ebuttons.add(paste.getText());
		Ebuttons.add(zoomIn.getText());
		Ebuttons.add(zoomOut.getText());
		JMenuItem[] items = new JMenuItem[] { undo, redo, copy, cut, paste, zoomIn, zoomOut };

		MenuListener menuListener = new MenuListener(PMbuttons, Ebuttons);

		for (JMenuItem item : items)
		{
			item.addActionListener(menuListener);
		}

		undo.setEnabled(model.undo);
		redo.setEnabled(model.redo);
		copy.setEnabled(model.copy);
		paste.setEnabled(model.paste);
		cut.setEnabled(model.cut);
		zoomIn.setEnabled(model.zoomIn);
		zoomOut.setEnabled(model.zoomOut);

		edit.add(undo);
		edit.add(redo);
		edit.add(new JSeparator());
		edit.add(copy);
		edit.add(cut);
		edit.add(paste);
		edit.add(new JSeparator());
		edit.add(zoomIn);
		edit.add(zoomOut);
		edit.add(new JSeparator());
		edit.add(preferences);
		edit.setEnabled(false);
		return edit;
	}

	private class MenuListener implements ActionListener
	{

		ArrayList<String> PMbuttons;
		ArrayList<String> Ebuttons;

		MenuListener(ArrayList<String> PMbuttons, ArrayList<String> Ebuttons)
		{
			this.PMbuttons = PMbuttons;
			this.Ebuttons = Ebuttons;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String sanitizedName = e.getActionCommand().toLowerCase().replace(DOTS, "").replace('.', '_').replace('-', '_').replace(" ", "_");
			System.out.println(e.getActionCommand());
			if (PMbuttons.contains(e.getActionCommand()))
			{
				AbstractEditAction<ProjectManager> actionpm = projectManager.getActionContext().getAction(sanitizedName);
				if (actionpm != null)
				{
					actionpm.invoke(projectManager);
				}
			}
			else if (Ebuttons.contains(e.getActionCommand()))
			{
				AbstractEditAction<E> actione = context.getActionContext().getAction(sanitizedName);
				if (actione != null)
				{
					actione.invoke(context);
				}
				else
					Log.log(Log.ERROR, this, "Could not invoke action. -> " + sanitizedName);
			}
		}
	};

	public static class MenuModel
	{
		public boolean save;
		public boolean saveAll;
		public boolean saveAs;
		public boolean print;
		public boolean pngExport;
		public boolean ebnfExport;
		public boolean copy;
		public boolean cut;
		public boolean paste;
		public boolean undo;
		public boolean redo;
		public boolean zoomIn;
		public boolean zoomOut;
		public boolean find;
	}
}
