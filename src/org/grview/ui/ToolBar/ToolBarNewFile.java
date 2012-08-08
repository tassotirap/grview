package org.grview.ui.ToolBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.HashMap;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import org.grview.actions.AbstractEditAction;
import org.grview.util.DropDownButton;
import org.grview.util.LangHelper;
import org.grview.util.Log;

import com.jidesoft.icons.ColorFilter;

public class ToolBarNewFile<E> extends CommandBar<E>
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
//		ddbNewFile.addActionListener(new ActionListener()
//		{
//
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				AbstractEditAction<E> action = getAction(LangHelper.new_file.toLowerCase());
//				if (action != null)
//					action.invoke(context);
//				else
//					Log.log(Log.ERROR, this, "Could not invoke action. -> " + LangHelper.new_file);
//			}
//		});
//		ddbOpenFile.addActionListener(new ActionListener()
//		{
//
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				AbstractEditAction<E> action = getAction(LangHelper.open_file.toLowerCase());
//				if (action != null)
//					action.invoke(context);
//				else
//					Log.log(Log.ERROR, this, "Could not invoke action. -> " + LangHelper.open_file);
//			}
//		});
//		for (final JMenuItem item : mItems)
//		{
//			item.addActionListener(new ActionListener()
//			{
//
//				@Override
//				public void actionPerformed(ActionEvent e)
//				{
//					AbstractEditAction<E> action = getAction(actionNameByItem.get(item).replace(" ", "_").toLowerCase());
//					if (action != null)
//						action.invoke(context);
//					else
//						Log.log(Log.ERROR, this, "Could not invoke action -> " + actionNameByItem.get(item));
//				}
//			});
//		}
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
