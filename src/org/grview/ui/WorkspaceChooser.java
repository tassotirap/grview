package org.grview.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.grview.project.Project;
import org.grview.util.Log;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

/**
 * 
 * @author Gusga
 * @author Tasso
 */
public class WorkspaceChooser extends JFrame
{
	private static final String PROJECTS_SCREEN_PNG = "projects_screen.png";
	private static final long serialVersionUID = 1L;
	private static final String LIST_FILE = "workspace";

	private static WorkspaceChooser instance;

	private String workspaceDir;
	private boolean canceled;
	private boolean done;

	private JButton btnBrowse;
	private JButton btnOk;
	private JButton btnCancel;
	private JLabel imgWorkspace;
	private JLabel lblWorkspace;
	private JComboBox<String> ckbWorkspace;

	public static WorkspaceChooser getInstance()
	{
		if (instance == null)
		{
			instance = new WorkspaceChooser();
		}
		return instance;
	}

	public static void main(String args[])
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				getInstance().setVisible(true);
			}
		});
	}

	private WorkspaceChooser()
	{
		setPanelProperties();
		setPanelLocation();
		initComponents();
		readDirsFromList();
	}

	private void addDirToList(String filename)
	{
		File file = new File(System.getProperty("java.io.tmpdir"), LIST_FILE);
		StringBuffer oldText = new StringBuffer();
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = "";
			while ((line = bufferedReader.readLine()) != null)
				oldText.append(line + "\n");
			bufferedReader.close();
			PrintWriter pw = new PrintWriter(file);
			pw.print(oldText.toString());
			pw.print(filename);
			pw.close();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Could not load workspace list!", e);
		}
	}

	private void btnBrowseActionPerformed()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int dialogReturn = fileChooser.showOpenDialog(this);
		if (dialogReturn == JFileChooser.APPROVE_OPTION)
		{
			ckbWorkspace.setSelectedItem(fileChooser.getSelectedFile().getAbsolutePath());
		}
		if (dialogReturn == JFileChooser.CANCEL_OPTION)
		{
			ckbWorkspace.setSelectedItem("");
		}
	}

	private void btnCancelActionPerformed()
	{
		this.dispose();
		canceled = true;
	}

	private void btnOkActionPerformed()
	{
		String dir = ckbWorkspace.getSelectedItem().toString();
		File file = new File(dir);
		boolean newFile = false;
		if (!dir.equals(""))
		{
			boolean exists = false;
			if (file.exists())
			{
				workspaceDir = dir;
			}
			else
			{
				int option = JOptionPane.showConfirmDialog(this, "This directory does not exist.\nIf you continue this directory will be created and a new project will be created on this directory.\nProceed?", "Directory not found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (option == JOptionPane.NO_OPTION)
				{
					return;
				}
				if (file.mkdir())
				{
					newFile = true;
					workspaceDir = dir;
				}
				else
				{
					JOptionPane.showMessageDialog(this, "Could not find or create this directory in disk!", "Workspace Loader", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			try
			{
				if (!Project.isProject(file))
				{
					if (file.listFiles().length > 0)
					{
						JOptionPane.showMessageDialog(this, "Must be a new, empty, or existing project directory!", "Workspace Loader", JOptionPane.ERROR_MESSAGE);
						return;
					}
					Project.createProject(file, null, null).writeProject();
				}
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, "Could not create a new project!", "Worksapce Loader", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				if (newFile && !file.delete())
				{
					JOptionPane.showMessageDialog(this, "Could not delete the created directory!", "Workspace Loader", JOptionPane.ERROR_MESSAGE);
				}
				return;
			}
			for (int i = 0; i < ckbWorkspace.getItemCount(); i++)
			{
				if (ckbWorkspace.getItemAt(i).toString().equals(dir))
				{
					exists = true;
					break;
				}
			}
			if (!exists)
			{
				addDirToList(dir);
			}
			this.setVisible(false);
			done = true;
		}
	}

	private void initComponents()
	{
		imgWorkspace = new JLabel();
		lblWorkspace = new JLabel();
		ckbWorkspace = new JComboBox<String>();
		btnBrowse = new JButton();
		btnCancel = new JButton();
		btnOk = new javax.swing.JButton();

		imgWorkspace.setIcon(new ImageIcon(getClass().getResource(PROJECTS_SCREEN_PNG))); // NOI18N
		lblWorkspace.setText("Please inform a workspace to continue:");
		btnBrowse.setText("Browse");
		
		btnBrowse.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnBrowseActionPerformed();
			}
		});

		btnCancel.setText("OK");
		btnCancel.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnOkActionPerformed();
			}
		});

		btnOk.setText("Cancel");
		btnOk.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btnCancelActionPerformed();
			}
		});

		ckbWorkspace.setEditable(true);

		GroupLayout layout = new GroupLayout(getContentPane());
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(GroupLayout.TRAILING).add(ckbWorkspace, GroupLayout.PREFERRED_SIZE, 305, GroupLayout.PREFERRED_SIZE).add(btnCancel, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.RELATED).add(layout.createParallelGroup(GroupLayout.TRAILING).add(btnOk, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE).add(btnBrowse, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)).addContainerGap()).add(imgWorkspace).add(layout.createSequentialGroup().addContainerGap().add(lblWorkspace)));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING).add(layout.createSequentialGroup().add(imgWorkspace).addPreferredGap(LayoutStyle.UNRELATED).add(lblWorkspace).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(layout.createParallelGroup(GroupLayout.BASELINE).add(ckbWorkspace, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).add(btnBrowse)).addPreferredGap(LayoutStyle.RELATED).add(layout.createParallelGroup(GroupLayout.BASELINE).add(btnOk).add(btnCancel)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		getContentPane().setLayout(layout);
		pack();
	}

	private void readDirsFromList()
	{
		File file = new File(System.getProperty("java.io.tmpdir"), LIST_FILE);
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = "";
			while ((line = bufferedReader.readLine()) != null)
			{
				if (!line.equals(""))
					ckbWorkspace.addItem(line);
			}
			bufferedReader.close();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Could not load workspace list!", e);
		}
	}

	private void setPanelLocation()
	{
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDim.width - 428) / 2, (screenDim.height - 230) / 2);
		setResizable(false);
	}

	private void setPanelProperties()
	{
		setTitle("grView 2.0");
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		setSize(428, 230);
	}

	public String getWorkspaceDir()
	{
		return this.workspaceDir;
	}

	public boolean isCanceled()
	{
		return canceled;
	}

	public boolean isDone()
	{
		return done;
	}

}
