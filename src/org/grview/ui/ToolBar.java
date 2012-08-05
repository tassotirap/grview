package org.grview.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.HashMap;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.grview.actions.AbstractEditAction;
import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.canvas.state.VolatileStateManager;
import org.grview.editor.TextArea;
import org.grview.util.DropDownButton;
import org.grview.util.LangHelper;
import org.grview.util.Log;

import com.jidesoft.icons.ColorFilter;

public class ToolBar
{
	public abstract class CommandBar<E> extends JToolBar implements PropertyChangeListener
	{
		private static final long serialVersionUID = 1L;
		protected E context;

		public abstract AbstractEditAction<E> getAction(String action);

		public abstract HashMap<String, String[]> getContextEnabledMap();

		public String getContextType()
		{
			if (context instanceof Canvas)
				return CANVAS_CONTEXT;
			else if (context instanceof TextArea)
				return TEXTAREA_CONTEXT;
			return null;
		}

		public abstract String getNickname();

		public abstract void initActions();

		public abstract void initLayout();
	}

	public abstract class ToolBarCanvas<E> extends CommandBar<E>
	{
		private static final long serialVersionUID = 1L;

		private final URL runURL = getClass().getResource(imgPath + "application-run.png");
		private final URL zoomInURL = getClass().getResource(imgPath + "zoom-in.png");
		private final URL zoomOutURL = getClass().getResource(imgPath + "zoom-out.png");

		JButton btnRun = new JButton(new ImageIcon(runURL));
		JButton btnZoomIn = new JButton(new ImageIcon(zoomInURL));
		JButton btnZoomOut = new JButton(new ImageIcon(zoomOutURL));
		JButton[] buttons = new JButton[] { btnRun, btnZoomIn, btnZoomOut };
		String[] names = new String[] { LangHelper.build, LangHelper.zoom_plus, LangHelper.zoom_minus };

		E context;

		public ToolBarCanvas(E context)
		{
			this.context = context;
			for (int i = 0; i < buttons.length; i++)
			{
				buttons[i].setName(names[i]);
			}
			this.add(btnRun);
			JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
			sep1.setMaximumSize(new Dimension(6, 100));
			this.add(sep1);
			this.add(btnZoomIn);
			this.add(btnZoomOut);
		}

		public JButton getBtnRun()
		{
			return btnRun;
		}

		public JButton getBtnZoomIn()
		{
			return btnZoomIn;
		}

		public JButton getBtnZoomOut()
		{
			return btnZoomOut;
		}

		public JButton[] getButtons()
		{
			return buttons;
		}

		@Override
		public HashMap<String, String[]> getContextEnabledMap()
		{
			return null;
		}

		public String[] getNames()
		{
			return names;
		}

		@Override
		public String getNickname()
		{
			if (context instanceof Canvas)
				return CANVAS_TB_CANVAS;
			return null;
		}

		@Override
		public void initActions()
		{
			for (final JButton bt : buttons)
			{
				bt.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent evt)
					{
						getAction(bt.getName().replaceAll(" ", "").toLowerCase()).invoke(context);
					}

				});
			}
		}

		@Override
		public void initLayout()
		{
			for (int i = 0; i < buttons.length; i++)
			{
				JButton bt = buttons[i];
				bt.setOpaque(false);
				bt.setBorder(new EmptyBorder(5, 5, 5, 5));
				bt.setRolloverEnabled(true);
				bt.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon) bt.getIcon()).getImage())));
				bt.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon) bt.getIcon()).getImage())));
				bt.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon) bt.getIcon()).getImage())));
				bt.setBackground(this.getBackground());
				bt.setToolTipText(names[i]);
			}
		}

		public void propertyChange(PropertyChangeEvent event)
		{

		}

	}

	public abstract class ToolBarFile<E> extends CommandBar<E>
	{
		private static final long serialVersionUID = 1L;

		private final URL saveURL = getClass().getResource(imgPath + "document-save.png");
		private final URL saveAllURL = getClass().getResource(imgPath + "document-save-all.png");
		private final URL printURL = getClass().getResource(imgPath + "document-print.png");
		private final URL copyURL = getClass().getResource(imgPath + "edit-copy.png");
		private final URL cutURL = getClass().getResource(imgPath + "edit-cut.png");
		private final URL pasteURL = getClass().getResource(imgPath + "edit-paste.png");
		private final URL undoURL = getClass().getResource(imgPath + "edit-undo.png");
		private final URL redoURL = getClass().getResource(imgPath + "edit-redo.png");

		JButton btnSave = new JButton(new ImageIcon(saveURL));
		JButton btnSaveAll = new JButton(new ImageIcon(saveAllURL));
		JButton btnPrint = new JButton(new ImageIcon(printURL));
		JButton btnCopy = new JButton(new ImageIcon(copyURL));
		JButton btnCut = new JButton(new ImageIcon(cutURL));
		JButton btnPaste = new JButton(new ImageIcon(pasteURL));
		JButton btnUndo = new JButton(new ImageIcon(undoURL));
		JButton btnRedo = new JButton(new ImageIcon(redoURL));
		JButton[] buttons = new JButton[] { btnSave, btnSaveAll, btnPrint, btnCopy, btnCut, btnPaste, btnUndo, btnRedo };
		String[] names = new String[] { LangHelper.save, LangHelper.save_all, LangHelper.print, LangHelper.copy, LangHelper.cut, LangHelper.paste, LangHelper.undo, LangHelper.redo };
		HashMap<String, String[]> contextEnabledMap = new HashMap<String, String[]>();

		E context;

		public ToolBarFile(E context)
		{
			this.context = context;
			if (context instanceof Canvas)
			{
				((Canvas) context).getMonitor().addPropertyChangeListener("undoable", this);
				((Canvas) context).getMonitor().addPropertyChangeListener("object_state", this);
			}
			for (int i = 0; i < buttons.length; i++)
			{
				buttons[i].setName(names[i]);
			}
			contextEnabledMap.put(MAIN_TB_CANVAS, new String[] { btnSave.getName(), btnSaveAll.getName(), btnPrint.getName(), btnCopy.getName(), btnCut.getName(), btnPaste.getName(), btnUndo.getName(), btnRedo.getName() });
			contextEnabledMap.put(MAIN_TB_TEXTAREA, new String[] { btnSave.getName(), btnSaveAll.getName(), btnPrint.getName(), btnCopy.getName(), btnCut.getName(), btnPaste.getName(), btnUndo.getName(), btnRedo.getName() });
			this.add(btnSave);
			btnSave.setEnabled(true);
			this.add(btnSaveAll);
			btnSaveAll.setEnabled(true);
			this.add(btnPrint);
			btnPrint.setEnabled(true);
			JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
			sep1.setMaximumSize(new Dimension(6, 100));
			this.add(sep1);
			this.add(btnCopy);
			btnCopy.setEnabled(true);
			this.add(btnCut);
			btnCut.setEnabled(true);
			this.add(btnPaste);
			btnPaste.setEnabled(true);
			JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
			sep2.setMaximumSize(new Dimension(6, 100));
			this.add(sep2);
			this.add(btnUndo);
			btnUndo.setEnabled(true);
			this.add(btnRedo);
			btnRedo.setEnabled(true);
		}

		public JButton getBtnCopy()
		{
			return btnCopy;
		}

		public JButton getBtnCut()
		{
			return btnCut;
		}

		public JButton getBtnPaste()
		{
			return btnPaste;
		}

		public JButton getBtnPrint()
		{
			return btnPrint;
		}

		public JButton getBtnRedo()
		{
			return btnRedo;
		}

		public JButton getBtnSave()
		{
			return btnSave;
		}

		public JButton getBtnSaveAll()
		{
			return btnSaveAll;
		}

		public JButton getBtnUndo()
		{
			return btnUndo;
		}

		public JButton[] getButtons()
		{
			return buttons;
		}

		@Override
		public HashMap<String, String[]> getContextEnabledMap()
		{
			return this.contextEnabledMap;
		}

		public String[] getNames()
		{
			return names;
		}

		@Override
		public String getNickname()
		{
			if (context instanceof Canvas)
				return MAIN_TB_CANVAS;
			else if (context instanceof TextArea)
				return MAIN_TB_TEXTAREA;
			return null;
		}

		@Override
		public void initActions()
		{
			for (final JButton button : buttons)
			{
				button.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent evt)
					{
						JButton button = (JButton) evt.getSource();
						getAction(button.getName().replaceAll(" ", "").toLowerCase()).invoke(context);
					}

				});
			}
		}

		@Override
		public void initLayout()
		{
			for (int i = 0; i < buttons.length; i++)
			{
				JButton btn = buttons[i];
				btn.setOpaque(false);
				btn.setBorder(new EmptyBorder(5, 5, 5, 5));
				btn.setRolloverEnabled(true);
				btn.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon) btn.getIcon()).getImage())));
				btn.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon) btn.getIcon()).getImage())));
				btn.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon) btn.getIcon()).getImage())));
				btn.setBackground(this.getBackground());
				btn.setToolTipText(names[i]);
			}
		}

		public void propertyChange(PropertyChangeEvent event)
		{
			if (event.getSource() instanceof Canvas)
			{
				VolatileStateManager vsm = CanvasFactory.getVolatileStateManager(((Canvas) context).getID());
				if (event.getPropertyName().equals("undoable"))
				{
					btnUndo.setEnabled(true);
					btnUndo.setToolTipText("Undo " + vsm.getNextUndoable());
				}
				if (event.getPropertyName().equals("object_state"))
				{
					if (vsm.hasNextRedo())
					{
						btnRedo.setEnabled(true);
						btnRedo.setToolTipText("Redo " + vsm.getNextRedoable());
					}
				}
			}
		}

	}

	public abstract class ToolBarGrammar<E> extends CommandBar<E>
	{
		private static final long serialVersionUID = 1L;

		private final URL selectURL = getClass().getResource(imgPath + "select.png");
		private final URL successorURL = getClass().getResource(imgPath + "successor.png");
		private final URL alternativeURL = getClass().getResource(imgPath + "alternative.png");
		private final URL terminalURL = getClass().getResource(imgPath + "icon_t.png");
		private final URL nTerminalURL = getClass().getResource(imgPath + "icon_nt.png");
		private final URL leftHandURL = getClass().getResource(imgPath + "left_hand.png");
		private final URL lambdaAlternativeURL = getClass().getResource(imgPath + "icon_l.png");
		private final URL labelURL = getClass().getResource(imgPath + "label.png");
		private final URL startURL = getClass().getResource(imgPath + "icon_s.png");

		JButton btnSelect = new JButton(new ImageIcon(selectURL));
		JButton btnSucessor = new JButton(new ImageIcon(successorURL));
		JButton btnAlternative = new JButton(new ImageIcon(alternativeURL));
		JButton btnLeftHand = new JButton(new ImageIcon(leftHandURL));
		JButton btnNTerminal = new JButton(new ImageIcon(nTerminalURL));
		JButton btnTerminal = new JButton(new ImageIcon(terminalURL));
		JButton btnLambdaAlternative = new JButton(new ImageIcon(lambdaAlternativeURL));
		JButton btnLabel = new JButton(new ImageIcon(labelURL));
		JButton btnStart = new JButton(new ImageIcon(startURL));
		JButton[] buttons = new JButton[] { btnSelect, btnSucessor, btnAlternative, btnLeftHand, btnNTerminal, btnTerminal, btnLambdaAlternative, btnLabel, btnStart };
		String[] names = new String[] { LangHelper.select, LangHelper.successor, LangHelper.alternative, LangHelper.left_hand, LangHelper.n_terminal, LangHelper.terminal, LangHelper.lambda_alternative, LangHelper.label, LangHelper.start };

		E context;

		public ToolBarGrammar(E context)
		{
			this.context = context;
			for (int i = 0; i < buttons.length; i++)
			{
				buttons[i].setName(names[i]);
			}
			this.add(btnSelect);
			JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
			sep1.setMaximumSize(new Dimension(100, 6));
			this.add(sep1);
			this.add(btnSucessor);
			this.add(btnAlternative);
			JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
			sep2.setMaximumSize(new Dimension(100, 6));
			this.add(sep2);
			this.add(btnStart);
			this.add(btnLeftHand);
			this.add(btnNTerminal);
			this.add(btnTerminal);
			this.add(btnLambdaAlternative);
			JSeparator sep3 = new JSeparator(SwingConstants.HORIZONTAL);
			sep3.setMaximumSize(new Dimension(100, 6));
			this.add(sep3);
			this.add(btnLabel);
		}

		public JButton getBtnAlternative()
		{
			return btnAlternative;
		}

		public JButton getBtnLabel()
		{
			return btnLabel;
		}

		public JButton getBtnLambdaAlternative()
		{
			return btnLambdaAlternative;
		}

		public JButton getBtnLeftHand()
		{
			return btnLeftHand;
		}

		public JButton getBtnNTerminal()
		{
			return btnNTerminal;
		}

		public JButton getBtnSelect()
		{
			return btnSelect;
		}

		public JButton getBtnSucessor()
		{
			return btnSucessor;
		}

		public JButton getBtnTerminal()
		{
			return btnTerminal;
		}

		public JButton[] getButtons()
		{
			return buttons;
		}

		@Override
		public HashMap<String, String[]> getContextEnabledMap()
		{
			return null;
		}

		public String[] getNames()
		{
			return names;
		}

		@Override
		public String getNickname()
		{
			if (context instanceof Canvas)
				return CANVAS_TB_CANVAS;
			return null;
		}

		@Override
		public void initActions()
		{
			for (final JButton bt : buttons)
			{
				bt.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent evt)
					{
						getAction(bt.getName().replaceAll(" ", "").toLowerCase()).invoke(context);
					}

				});
			}
		}

		@Override
		public void initLayout()
		{
			for (int i = 0; i < buttons.length; i++)
			{
				JButton bt = buttons[i];
				bt.setOpaque(false);
				bt.setBorder(new EmptyBorder(1, 1, 1, 1));
				bt.setRolloverEnabled(true);
				bt.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon) bt.getIcon()).getImage())));
				bt.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon) bt.getIcon()).getImage())));
				bt.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon) bt.getIcon()).getImage())));
				bt.setBackground(this.getBackground());
				bt.setToolTipText(names[i]);
			}
		}

		public void propertyChange(PropertyChangeEvent event)
		{

		}
	}

	public abstract class ToolBarNewFile<E> extends CommandBar<E>
	{
		private static final long serialVersionUID = 1L;

		private final URL newURL = getClass().getResource(imgPath + "document-new.png");
		private final URL openURL = getClass().getResource(imgPath + "document-open.png");

		JMenuItem openFile = new JMenuItem(LangHelper.open_file);
		JMenuItem openProject = new JMenuItem(LangHelper.open_project);
		JMenuItem newFile = new JMenuItem(LangHelper.new_file);
		JMenuItem newProject = new JMenuItem(LangHelper.new_project);

		JMenuItem[] mItems = new JMenuItem[] { openFile, openProject, newFile, newProject };
		HashMap<JMenuItem, String> actionNameByItem = new HashMap<JMenuItem, String>();

		DropDownButton ddbNewFile = new DropDownButton()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected JPopupMenu getPopupMenu()
			{

				JPopupMenu jpm = new JPopupMenu();
				jpm.add(newFile);
				jpm.add(newProject);
				newProject.setEnabled(false);
				return jpm;
			}

		};
		DropDownButton ddbOpenFile = new DropDownButton()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected JPopupMenu getPopupMenu()
			{

				JPopupMenu jpm = new JPopupMenu();
				jpm.add(openFile);
				jpm.add(openProject);
				openProject.setEnabled(false);
				openFile.setEnabled(false);
				return jpm;
			}

		};

		E context;
		HashMap<String, String[]> contextEnabledMap = new HashMap<String, String[]>();

		public ToolBarNewFile(E context)
		{
			this.context = context;
			ddbNewFile.setName(LangHelper._new);
			ddbOpenFile.setName(LangHelper._open);
			ddbNewFile.setToolTipText(LangHelper._new);
			ddbOpenFile.setToolTipText(LangHelper._open);
			actionNameByItem.put(openFile, LangHelper.open_file);
			actionNameByItem.put(openProject, LangHelper.open_project);
			actionNameByItem.put(newFile, LangHelper.new_file);
			actionNameByItem.put(newProject, LangHelper.new_project);
			contextEnabledMap.put(MAIN_TB_CANVAS, new String[] { ddbNewFile.getName(), ddbOpenFile.getName() });
			ddbNewFile.setIcon(new ImageIcon(newURL));
			ddbOpenFile.setIcon(new ImageIcon(openURL));
			ddbNewFile.addToToolBar(this);
			ddbOpenFile.addToToolBar(this);
		}

		@Override
		public HashMap<String, String[]> getContextEnabledMap()
		{
			return this.contextEnabledMap;
		}

		public DropDownButton getDdbNewFile()
		{
			return ddbNewFile;
		}

		public DropDownButton getDdbOpenFile()
		{
			return ddbOpenFile;
		}

		@Override
		public String getNickname()
		{
			return MAIN_TB_GENERAL;
		}

		@Override
		public void initActions()
		{
			ddbNewFile.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					AbstractEditAction<E> action = getAction(LangHelper.new_file.toLowerCase());
					if (action != null)
						action.invoke(context);
					else
						Log.log(Log.ERROR, this, "Could not invoke action. -> " + LangHelper.new_file);
				}
			});
			ddbOpenFile.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					AbstractEditAction<E> action = getAction(LangHelper.open_file.toLowerCase());
					if (action != null)
						action.invoke(context);
					else
						Log.log(Log.ERROR, this, "Could not invoke action. -> " + LangHelper.open_file);
				}
			});
			for (final JMenuItem item : mItems)
			{
				item.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						AbstractEditAction<E> action = getAction(actionNameByItem.get(item).replace(" ", "_").toLowerCase());
						if (action != null)
							action.invoke(context);
						else
							Log.log(Log.ERROR, this, "Could not invoke action -> " + actionNameByItem.get(item));
					}
				});
			}
		}

		@Override
		public void initLayout()
		{
			this.setFloatable(false);
			this.setAlignmentX(0.5f);
			JButton[] buttons = new JButton[] { ddbNewFile, ddbOpenFile };
			for (int i = 0; i < buttons.length; i++)
			{
				JButton btn = buttons[i];
				btn.setOpaque(false);
				btn.setBorder(new EmptyBorder(5, 5, 5, 5));
				btn.setRolloverEnabled(true);
				btn.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon) btn.getIcon()).getImage())));
				btn.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon) btn.getIcon()).getImage())));
				btn.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon) btn.getIcon()).getImage())));
				btn.setBackground(this.getBackground());
			}
		}

		public void propertyChange(PropertyChangeEvent event)
		{
		}

	}

	private final static String MAIN_TB_GENERAL = "m_general";
	private final static String CANVAS_TB_CANVAS = "canv_tb";
	private final static String MAIN_TB_CANVAS = "mcan_tb";
	private final static String MAIN_TB_TEXTAREA = "mtex_tb";

	public final static String CANVAS_CONTEXT = "canvasContext";
	public final static String TEXTAREA_CONTEXT = "textAreaContext";
	private static ToolBar instance;
	private final String imgPath = "/org/grview/images/";

	// Private singleton constructor
	private ToolBar()
	{
	}

	// Singleton getInstance
	public static ToolBar getInstance()
	{
		if (instance == null)
		{
			instance = new ToolBar();
		}
		return instance;
	}

}
