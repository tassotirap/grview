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
import org.grview.project.ProjectManager;
import org.grview.ui.wizard.NewFileWizard;
import org.grview.util.DropDownButton;
import org.grview.util.LangHelper;
import org.grview.util.Log;

import com.jidesoft.icons.ColorFilter;

public class ToolBarNewFile<E> extends CommandBar<E>
{
	private static final long serialVersionUID = 1L;

	private final URL newURL = getClass().getResource(imgPath + "document-new.png");
	private final URL openURL = getClass().getResource(imgPath + "document-open.png");

	JButton openFile = new JButton(new ImageIcon(openURL));
	JButton newFile = new JButton(new ImageIcon(newURL));

	HashMap<JButton, String> actionNameByItem = new HashMap<JButton, String>();

	E context;

	public ToolBarNewFile(E context)
	{
		this.context = context;

		this.add(newFile);
		this.add(openFile);

		actionNameByItem.put(openFile, LangHelper.open_file);
		actionNameByItem.put(newFile, LangHelper.new_file);
	}

	@Override
	public String getNickname()
	{
		return MAIN_TB_GENERAL;
	}

	@Override
	public void initActions()
	{
		newFile.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("New File");
				
			}
		});
		
		openFile.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Open File");
				
			}
		});
	}

	@Override
	public void initLayout()
	{
		this.setFloatable(false);
		this.setAlignmentX(0.5f);
		JButton[] buttons = new JButton[]
		{ newFile, openFile };
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

	@Override
	public HashMap<String, String[]> getContextEnabledMap()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
