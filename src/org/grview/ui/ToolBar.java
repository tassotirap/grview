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

	// definitions for tool bars nicknames. Note that is a nice thing that they
	// all are 7 carac. long
	private final static String MAIN_TB_GENERAL = "m_general";
	private final static String CANVAS_TB_CANVAS = "canv_tb";
	private final static String MAIN_TB_CANVAS = "mcan_tb";
	private final static String MAIN_TB_TEXTAREA = "mtex_tb";
	private final static String BUILD_TB_CANVAS = "bcan_tb";
	private final static String BUILD_TB_TEXTAREA = "btex_tb";
	private final static String NAVI_TB_CANVAS = "navi_tb";
	private final static String SPLIT_TB_CANVAS = "scan_tb";
	private final static String SPLIT_TB_TEXTAREA = "stex_tb";
	private final static String OPTIONS_TB_CANVAS = "ocan_tb";
	private final static String OPTIONS_TB_TEXTAREA = "otex_tb";

	public final static String CANVAS_CONTEXT = "canvasContext";
	public final static String TEXTAREA_CONTEXT = "textAreaContext";

	private static ToolBar instance;

	private final String img_path = "/org/grview/images/";

	// t0 icons
	private final URL _new = getClass().getResource(img_path + "document-new.png");
	private final URL _open = getClass().getResource(img_path + "document-open.png");
	private final URL newFile = getClass().getResource(img_path + "file-new.png");
	private final URL newProject = getClass().getResource(img_path + "project-new.png");
	private final URL openFile = getClass().getResource(img_path + "file-open.png");
	private final URL openProject = getClass().getResource(img_path + "project-open.png");

	// t1 icons
	private final URL save = getClass().getResource(img_path + "document-save.png");
	private final URL saveAll = getClass().getResource(img_path + "document-save-all.png");
	private final URL print = getClass().getResource(img_path + "document-print.png");
	private final URL copy = getClass().getResource(img_path + "edit-copy.png");
	private final URL cut = getClass().getResource(img_path + "edit-cut.png");
	private final URL paste = getClass().getResource(img_path + "edit-paste.png");
	private final URL undo = getClass().getResource(img_path + "edit-undo.png");
	private final URL redo = getClass().getResource(img_path + "edit-redo.png");

	// t2 icons
	private final URL run = getClass().getResource(img_path + "application-run.png");
	private final URL zoom_in = getClass().getResource(img_path + "zoom-in.png");
	private final URL zoom_out = getClass().getResource(img_path + "zoom-out.png");

	// t6 icons
	private final URL select = getClass().getResource(img_path + "select.png");
	private final URL successor = getClass().getResource(img_path + "successor.png");
	private final URL alternative = getClass().getResource(img_path + "alternative.png");
	private final URL terminal = getClass().getResource(img_path + "icon_t.png");
	private final URL n_terminal = getClass().getResource(img_path + "icon_nt.png");
	private final URL left_hand = getClass().getResource(img_path + "left_hand.png");
	private final URL lambda_alternative = getClass().getResource(img_path + "icon_l.png");
	private final URL label = getClass().getResource(img_path + "label.png");
	private final URL start = getClass().getResource(img_path + "icon_s.png");

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

	public abstract class CommandBar<E> extends JToolBar implements PropertyChangeListener
	{

		protected E context;

		public abstract AbstractEditAction<E> getAction(String action);

		public abstract String getNickname();

		public abstract void initActions();

		public abstract void initLayout();

		public abstract HashMap<String, String[]> getContextEnabledMap();

		public String getContextType()
		{
			if (context instanceof Canvas)
				return CANVAS_CONTEXT;
			else if (context instanceof TextArea)
				return TEXTAREA_CONTEXT;
			return null;
		}
	}

	public abstract class T0<E> extends CommandBar<E>
	{

		JMenuItem openFile = new JMenuItem(LangHelper.open_file);
		JMenuItem openProject = new JMenuItem(LangHelper.open_project);
		JMenuItem newFile = new JMenuItem(LangHelper.new_file);
		JMenuItem newProject = new JMenuItem(LangHelper.new_project);

		JMenuItem[] mItems = new JMenuItem[]
		{ openFile, openProject, newFile, newProject };
		HashMap<JMenuItem, String> actionNameByItem = new HashMap<JMenuItem, String>();

		DropDownButton _b1 = new DropDownButton()
		{
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
		DropDownButton _b2 = new DropDownButton()
		{
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

		@Override
		public String getNickname()
		{
			return MAIN_TB_GENERAL;
		}

		public T0(E context)
		{
			this.context = context;
			_b1.setName(LangHelper._new);
			_b2.setName(LangHelper._open);
			_b1.setToolTipText(LangHelper._new);
			_b2.setToolTipText(LangHelper._open);
			actionNameByItem.put(openFile, LangHelper.open_file);
			actionNameByItem.put(openProject, LangHelper.open_project);
			actionNameByItem.put(newFile, LangHelper.new_file);
			actionNameByItem.put(newProject, LangHelper.new_project);
			contextEnabledMap.put(MAIN_TB_CANVAS, new String[]
			{ _b1.getName(), _b2.getName() });
			_b1.setIcon(new ImageIcon(_new));
			_b2.setIcon(new ImageIcon(_open));
			_b1.addToToolBar(this);
			_b2.addToToolBar(this);
		}

		@Override
		public void initActions()
		{
			_b1.addActionListener(new ActionListener()
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
			_b2.addActionListener(new ActionListener()
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
			JButton[] buttons = new JButton[]
			{ _b1, _b2 };
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
			}
		}

		public void propertyChange(PropertyChangeEvent event)
		{
		}

		@Override
		public HashMap<String, String[]> getContextEnabledMap()
		{
			return this.contextEnabledMap;
		}

		public DropDownButton get_b1()
		{
			return _b1;
		}

		public DropDownButton get_b2()
		{
			return _b2;
		}

	}

	public abstract class T1<E> extends CommandBar<E>
	{
		JButton b1 = new JButton(new ImageIcon(save));
		JButton b2 = new JButton(new ImageIcon(saveAll));
		JButton b3 = new JButton(new ImageIcon(print));
		JButton b4 = new JButton(new ImageIcon(copy));
		JButton b5 = new JButton(new ImageIcon(cut));
		JButton b6 = new JButton(new ImageIcon(paste));
		JButton b7 = new JButton(new ImageIcon(undo));
		JButton b8 = new JButton(new ImageIcon(redo));
		JButton[] buttons = new JButton[]
		{ b1, b2, b3, b4, b5, b6, b7, b8 };
		String[] names = new String[]
		{ LangHelper.save, LangHelper.save_all, LangHelper.print, LangHelper.copy, LangHelper.cut, LangHelper.paste, LangHelper.undo, LangHelper.redo };
		HashMap<String, String[]> contextEnabledMap = new HashMap<String, String[]>();

		E context;

		@Override
		public String getNickname()
		{
			if (context instanceof Canvas)
				return MAIN_TB_CANVAS;
			else if (context instanceof TextArea)
				return MAIN_TB_TEXTAREA;
			return null;
		}

		public T1(E context)
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
			contextEnabledMap.put(MAIN_TB_CANVAS, new String[]
			{ b1.getName(), b2.getName(), b3.getName(), b4.getName(), b5.getName(), b6.getName(), b7.getName(), b8.getName() });
			contextEnabledMap.put(MAIN_TB_TEXTAREA, new String[]
			{ b1.getName(), b2.getName(), b3.getName(), b4.getName(), b5.getName(), b6.getName(), b7.getName(), b8.getName() });
			this.add(b1);
			b1.setEnabled(true);
			this.add(b2);
			b2.setEnabled(true);
			this.add(b3);
			b3.setEnabled(true);
			JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
			sep1.setMaximumSize(new Dimension(6, 100));
			this.add(sep1);
			this.add(b4);
			b4.setEnabled(true);
			this.add(b5);
			b5.setEnabled(true);
			this.add(b6);
			b6.setEnabled(true);
			JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
			sep2.setMaximumSize(new Dimension(6, 100));
			this.add(sep2);
			this.add(b7);
			b7.setEnabled(true);
			this.add(b8);
			b8.setEnabled(true);
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
						JButton button = (JButton)evt.getSource();		
						getAction(button.getName().replaceAll(" ", "").toLowerCase()).invoke(context);
					}

				});
			}
		}

		public void propertyChange(PropertyChangeEvent event)
		{
			if (event.getSource() instanceof Canvas)
			{
				VolatileStateManager vsm = CanvasFactory.getVolatileStateManager(((Canvas) context).getID());
				if (event.getPropertyName().equals("undoable"))
				{
					b7.setEnabled(true);
					b7.setToolTipText("Undo " + vsm.getNextUndoable());
				}
				if (event.getPropertyName().equals("object_state"))
				{
					if (vsm.hasNextRedo())
					{
						b8.setEnabled(true);
						b8.setToolTipText("Redo " + vsm.getNextRedoable());
					}
				}
			}
		}

		@Override
		public HashMap<String, String[]> getContextEnabledMap()
		{
			return this.contextEnabledMap;
		}

		public JButton getB1()
		{
			return b1;
		}

		public JButton getB2()
		{
			return b2;
		}

		public JButton getB3()
		{
			return b3;
		}

		public JButton getB4()
		{
			return b4;
		}

		public JButton getB5()
		{
			return b5;
		}

		public JButton getB6()
		{
			return b6;
		}

		public JButton getB7()
		{
			return b7;
		}

		public JButton getB8()
		{
			return b8;
		}

		public JButton[] getButtons()
		{
			return buttons;
		}

		public String[] getNames()
		{
			return names;
		}

	}

	public abstract class T2<E> extends CommandBar<E>
	{
		JButton b1 = new JButton(new ImageIcon(run));
		JButton b2 = new JButton(new ImageIcon(zoom_in));
		JButton b3 = new JButton(new ImageIcon(zoom_out));
		JButton[] buttons = new JButton[]
		{ b1, b2, b3 };
		String[] names = new String[]
		{ LangHelper.build, LangHelper.zoom_plus, LangHelper.zoom_minus };

		E context;

		@Override
		public String getNickname()
		{
			if (context instanceof Canvas)
				return CANVAS_TB_CANVAS;
			return null;
		}

		public T2(E context)
		{
			this.context = context;
			for (int i = 0; i < buttons.length; i++)
			{
				buttons[i].setName(names[i]);
			}
			this.add(b1);
			JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
			sep1.setMaximumSize(new Dimension(6, 100));
			this.add(sep1);
			this.add(b2);
			this.add(b3);
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

		public void propertyChange(PropertyChangeEvent event)
		{
			System.out.println("ok2");
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

		public JButton[] getButtons()
		{
			return buttons;
		}

		public JButton getB1()
		{
			return b1;
		}

		public JButton getB2()
		{
			return b2;
		}

		public JButton getB3()
		{
			return b3;
		}

	}

	public abstract class T6<E> extends CommandBar<E>
	{
		JButton b1 = new JButton(new ImageIcon(select));
		JButton b2 = new JButton(new ImageIcon(successor));
		JButton b3 = new JButton(new ImageIcon(alternative));
		JButton b4 = new JButton(new ImageIcon(left_hand));
		JButton b5 = new JButton(new ImageIcon(n_terminal));
		JButton b6 = new JButton(new ImageIcon(terminal));
		JButton b7 = new JButton(new ImageIcon(lambda_alternative));
		JButton b8 = new JButton(new ImageIcon(label));
		JButton b9 = new JButton(new ImageIcon(start));
		JButton[] buttons = new JButton[]
		{ b1, b2, b3, b4, b5, b6, b7, b8, b9 };
		String[] names = new String[]
		{ LangHelper.select, LangHelper.successor, LangHelper.alternative, LangHelper.left_hand, LangHelper.n_terminal, LangHelper.terminal, LangHelper.lambda_alternative, LangHelper.label, LangHelper.start };

		E context;

		@Override
		public String getNickname()
		{
			if (context instanceof Canvas)
				return CANVAS_TB_CANVAS;
			return null;
		}

		public T6(E context)
		{
			this.context = context;
			for (int i = 0; i < buttons.length; i++)
			{
				buttons[i].setName(names[i]);
			}
			this.add(b1);
			JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
			sep1.setMaximumSize(new Dimension(100, 6));
			this.add(sep1);
			this.add(b2);
			this.add(b3);
			JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
			sep2.setMaximumSize(new Dimension(100, 6));
			this.add(sep2);
			this.add(b9);
			this.add(b4);
			this.add(b5);
			this.add(b6);
			this.add(b7);
			JSeparator sep3 = new JSeparator(SwingConstants.HORIZONTAL);
			sep3.setMaximumSize(new Dimension(100, 6));
			this.add(sep3);
			this.add(b8);
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

		public void propertyChange(PropertyChangeEvent event)
		{
			System.out.println("ok2");
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

		public JButton[] getButtons()
		{
			return buttons;
		}

		public JButton getB1()
		{
			return b1;
		}

		public JButton getB2()
		{
			return b2;
		}

		public JButton getB3()
		{
			return b3;
		}

		public JButton getB4()
		{
			return b4;
		}

		public JButton getB5()
		{
			return b5;
		}

		public JButton getB6()
		{
			return b6;
		}

		public JButton getB7()
		{
			return b7;
		}

		public JButton getB8()
		{
			return b8;
		}

	}

}
