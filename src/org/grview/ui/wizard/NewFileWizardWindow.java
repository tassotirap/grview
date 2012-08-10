package org.grview.ui.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.grview.ui.wizard.FileEntry;

public class NewFileWizardWindow extends JFrame
{

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JList jList = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JTextArea jTextArea = null;
	private JPanel jPanel2 = null;
	private JLabel jLabel2 = null;

	private Object[] fileEntries;
	private HashMap<String, String> descByName;
	private JTextField jTextField = null;

	/**
	 * This is the default constructor
	 */
	public NewFileWizardWindow(Object[] fileEntries, HashMap<String, String> descByName)
	{
		super();
		this.fileEntries = fileEntries;
		this.descByName = descByName;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(320, 418);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenDim.width - 320) / 2, (screenDim.height - 418) / 2);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setContentPane(getJContentPane());
		this.setTitle("File Wizard");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane()
	{
		if (jContentPane == null)
		{
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.BOTH;
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints12.gridx = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Name:");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints5.anchor = GridBagConstraints.LINE_START;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 7;
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 4;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 0.2;
			gridBagConstraints2.insets = new Insets(5, 10, 0, 10);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 3;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 0.8;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new Insets(5, 10, 5, 10);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new Insets(10, 10, 5, 10);
			gridBagConstraints.anchor = GridBagConstraints.LINE_START;
			GridBagConstraints gridBagConstraintsB = new GridBagConstraints();
			gridBagConstraintsB.gridx = 0;
			gridBagConstraintsB.gridy = 0;
			gridBagConstraintsB.insets = new Insets(10, 0, 10, 0);
			gridBagConstraintsB.anchor = GridBagConstraints.LINE_START;
			GridBagConstraints gridBagConstraintsB1 = new GridBagConstraints();
			gridBagConstraintsB1.gridx = 1;
			gridBagConstraintsB1.gridy = 0;
			gridBagConstraintsB1.weightx = 1.0;
			gridBagConstraintsB1.weighty = 1.0;
			gridBagConstraintsB1.insets = new Insets(10, 0, 10, 0);
			jLabel = new JLabel();
			jLabel.setText("Choose below the type of file you want to create.");
			JScrollPane jScrollPane = new JScrollPane(getJList());
			JScrollPane jScrollPane2 = new JScrollPane(getJTextArea());
			getJPanel2().add(jLabel2, gridBagConstraintsB);
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(jLabel, gridBagConstraints);
			jContentPane.add(jScrollPane, gridBagConstraints1);
			jContentPane.add(getJPanel1(), gridBagConstraints11);
			jContentPane.add(jScrollPane2, gridBagConstraints2);
			jContentPane.add(getJPanel2(), gridBagConstraints5);
			jContentPane.add(getJTextField(), gridBagConstraints12);
		}
		return jContentPane;
	}

	public JTextArea getJTextArea()
	{
		if (jTextArea == null)
		{
			jTextArea = new JTextArea();
			jTextArea.setLineWrap(true);
		}
		return jTextArea;
	}

	/**
	 * This method initializes jList
	 * 
	 * @return javax.swing.JList
	 */
	public JList getJList()
	{
		if (jList == null)
		{
			jList = new JList(fileEntries);
			jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jList.setCellRenderer(new FileCellRenderer());
		}
		return jList;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel()
	{
		if (jPanel == null)
		{
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setPreferredSize(new Dimension(0, 100));
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1()
	{
		if (jPanel1 == null)
		{
			jPanel1 = new JPanel();
			jPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
			jPanel1.add(getOkButton());
			jPanel1.add(getCancelButton());
		}
		return jPanel1;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getOkButton()
	{
		if (jButton == null)
		{
			jButton = new JButton();
			jButton.setText("Ok");
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getCancelButton()
	{
		if (jButton1 == null)
		{
			jButton1 = new JButton();
			jButton1.setText("Cancel");
			jButton1.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					setVisible(false);
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2()
	{
		if (jPanel2 == null)
		{
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
		}
		return jPanel2;
	}

	class FileCellRenderer extends JLabel implements ListCellRenderer
	{
		private final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

		public FileCellRenderer()
		{
			setOpaque(true);
			setIconTextGap(12);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			FileEntry entry = (FileEntry) value;
			setText(entry.getTitle());
			setIcon(entry.getImage());
			if (isSelected)
			{
				getJTextArea().setText(descByName.get(getText()));
				setBackground(HIGHLIGHT_COLOR);
				setForeground(Color.white);
			}
			else
			{
				setBackground(Color.white);
				setForeground(Color.black);
			}
			return this;
		}
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getJTextField()
	{
		if (jTextField == null)
		{
			jTextField = new JTextField();
		}
		return jTextField;
	}
} // @jve:decl-index=0:visual-constraint="10,10"
