package org.grview.ui.ToolBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import org.grview.project.ProjectManager;
import org.grview.ui.wizard.NewFileWizard;

import com.jidesoft.icons.ColorFilter;

public class ToolBarNewFile<E> extends BaseToolBar<E>
{
	private static final long serialVersionUID = 1L;

	JButton btnNewFile;

	public ToolBarNewFile(E context)
	{
		super(context);
		this.add(btnNewFile);
	}

	@Override
	protected void initActions()
	{
		btnNewFile.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				new NewFileWizard(ProjectManager.getProjectManager());
				System.out.println("New File");

			}
		});
	}
	
	@Override
	protected void initComponets()
	{
		btnNewFile = new JButton(new ImageIcon(getClass().getResource(imgPath + "document-new.png")));
	}

	@Override
	protected void initLayout()
	{
		this.setFloatable(false);
		this.setAlignmentX(0.5f);
		btnNewFile.setOpaque(false);
		btnNewFile.setBorder(new EmptyBorder(5, 5, 5, 5));
		btnNewFile.setRolloverEnabled(true);
		btnNewFile.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon) btnNewFile.getIcon()).getImage())));
		btnNewFile.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon) btnNewFile.getIcon()).getImage())));
		btnNewFile.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon) btnNewFile.getIcon()).getImage())));
		btnNewFile.setBackground(this.getBackground());
	}
}
