package org.grview.ui.debug;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Insets;

public class ErrorDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel errorLabel = null;
	private JList errorList = null;
	private JTextArea taErrorDescription = null;
	private JButton btOk = null;
	private JLabel labelDesc = null;

	/**
	 * @param owner
	 */
	public ErrorDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 400);
		this.setTitle("Error");
		this.setContentPane(getJContentPane());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(0, 5, 5, 0);
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridy = 2;
			labelDesc = new JLabel();
			labelDesc.setText("Description:");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.ipadx = 16;
			gridBagConstraints3.insets = new Insets(10, 0, 10, 0);
			gridBagConstraints3.gridy = 4;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 3;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.insets = new Insets(0, 5, 15, 5);
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(5, 5, 5, 0);
			gridBagConstraints.gridy = 0;
			errorLabel = new JLabel();
			errorLabel.setText("One or more errors have occurred. See the list below:");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(errorLabel, gridBagConstraints);
			jContentPane.add(getErrorList(), gridBagConstraints1);
			jContentPane.add(getTaErrorDescription(), gridBagConstraints2);
			jContentPane.add(getBtOk(), gridBagConstraints3);
			jContentPane.add(labelDesc, gridBagConstraints4);
		}
		return jContentPane;
	}

	/**
	 * This method initializes errorList	
	 * 	
	 * @return javax.swing.JList	
	 */
	public JList getErrorList() {
		if (errorList == null) {
			errorList = new JList();
		}
		return errorList;
	}

	/**
	 * This method initializes taErrorDescription	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	public JTextArea getTaErrorDescription() {
		if (taErrorDescription == null) {
			taErrorDescription = new JTextArea();
		}
		return taErrorDescription;
	}

	/**
	 * This method initializes btOk	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtOk() {
		if (btOk == null) {
			btOk = new JButton("Ok");
			btOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return btOk;
	}

}
