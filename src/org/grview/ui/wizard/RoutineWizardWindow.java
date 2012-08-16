package org.grview.ui.wizard;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.grview.actions.Mode;
import org.grview.editor.StandaloneTextArea;
import org.grview.editor.TextArea;
import org.grview.editor.syntax.ModeProvider;

public class RoutineWizardWindow extends JFrame
{

	private static final String INSERT_CODE_HERE = "/* insert code here */";
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel nameLabel = null;
	private JTextField nameTextField = null;
	private TextArea codeTextArea = null;
	private JPanel buttonsPanel = null;
	private JButton insertButton = null; // @jve:decl-index=0:visual-constraint="371,512"
	private JButton cancelButton = null; // @jve:decl-index=0:visual-constraint="512,525"

	/**
	 * This is the default constructor
	 */
	public RoutineWizardWindow()
	{
		super();
		initialize();
	}

	/**
	 * This method initializes buttonsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonsPanel()
	{
		if (buttonsPanel == null)
		{
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
			buttonsPanel.add(getCancelButton());
			buttonsPanel.add(getInsertButton());
		}
		return buttonsPanel;
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
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.insets = new Insets(10, 10, 10, 0);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.insets = new Insets(10, 10, 0, 10);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 0.9;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new Insets(10, 10, 0, 10);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new Insets(10, 10, 0, 0);
			nameLabel = new JLabel();
			nameLabel.setText("Name:");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(nameLabel, gridBagConstraints);
			jContentPane.add(getNameTextField(), gridBagConstraints1);
			jContentPane.add(getCodeTextArea(), gridBagConstraints2);
			jContentPane.add(getButtonsPanel(), gridBagConstraints4);
		}
		return jContentPane;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(550, 500);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenDim.width - 550) / 2, (screenDim.height - 500) / 2);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getCancelButton()
	{
		if (cancelButton == null)
		{
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
		}
		return cancelButton;
	}

	/**
	 * This method initializes codeTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	protected TextArea getCodeTextArea()
	{
		if (codeTextArea == null)
		{
			codeTextArea = StandaloneTextArea.createTextArea();
			Mode mode = new Mode("groovy");
			mode.setProperty("file", "modes/groovy.xml");
			ModeProvider.instance.addMode(mode);
			codeTextArea.getBuffer().setMode(mode);
			codeTextArea.setText(INSERT_CODE_HERE);

			codeTextArea.addFocusListener(new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent arg0)
				{
					if (codeTextArea.getText().equals(""))
					{
						codeTextArea.setText(INSERT_CODE_HERE);
					}
				}

				@Override
				public void focusGained(FocusEvent arg0)
				{
					if (codeTextArea.getText().equals(INSERT_CODE_HERE))
					{
						codeTextArea.setText("");
					}
				}
			});
		}
		return codeTextArea;
	}

	/**
	 * This method initializes insertButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getInsertButton()
	{
		if (insertButton == null)
		{
			insertButton = new JButton();
			insertButton.setText("Insert");
		}
		return insertButton;
	}

	/**
	 * This method initializes nameTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	protected JTextField getNameTextField()
	{
		if (nameTextField == null)
		{
			nameTextField = new JTextField();
		}
		return nameTextField;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
