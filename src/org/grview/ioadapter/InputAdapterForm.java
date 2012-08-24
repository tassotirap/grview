package org.grview.ioadapter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.grview.util.ExampleFileFilter;

public class InputAdapterForm extends JPanel
{

	private static final long serialVersionUID = 1L;
	private JLabel argsLabel = null;
	private JTextField argsTextField = null;
	private JButton browseButton = null;
	private JButton buildButton = null;
	private JLabel infoLabel = null;
	private InputAdapter inputAdapter;
	private InputAdapterForm instance;
	private JLabel jarLabel = null;
	private JTextField jarTextField = null;
	private JLabel jframeClass = null;
	private JLabel jframeLabel = null;
	private JLabel jframeName = null;

	private JTextField jframeNameTextField = null;

	private JTextField jframeTextField = null;
	private JTextField mainTextField = null;
	private JLabel sendLabel = null;
	private JTextField sentTextField = null;
	private JButton startButton = null;
	private JButton stopButton = null;

	public InputAdapterForm()
	{
		super();
		initialize();
	}

	/**
	 * This is the default constructor
	 */
	public InputAdapterForm(InputAdapter inputAdapter)
	{
		super();
		this.inputAdapter = inputAdapter;
		this.instance = this;
		initialize();
	}

	/**
	 * This method initializes browseButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBrowseButton()
	{
		if (browseButton == null)
		{
			browseButton = new JButton();
			browseButton.setText("...");
			browseButton.setPreferredSize(new Dimension(20, 19));
			browseButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					showBrowseDialog();
				}
			});
		}
		return browseButton;
	}

	/**
	 * This method initializes buildButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBuildButton()
	{
		if (buildButton == null)
		{
			buildButton = new JButton();
			buildButton.setText("Build");
			buildButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (inputAdapter.canBuild())
					{
						if (inputAdapter.build())
						{
							JOptionPane.showMessageDialog(instance, "Successfully built!", "Build", JOptionPane.INFORMATION_MESSAGE);
							getStartButton().setEnabled(true);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(instance, "Could not validate the form to build! See console for details.", "Validation Error", JOptionPane.ERROR_MESSAGE);
						getStartButton().setEnabled(false);
					}
				}
			});
		}
		return buildButton;
	}

	/**
	 * This method initializes startButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getStartButton()
	{
		if (startButton == null)
		{
			startButton = new JButton();
			startButton.setText("Start");
			startButton.setSize(getBrowseButton().getSize());
			startButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (inputAdapter.canStart())
					{
						if (inputAdapter.start())
						{
							JOptionPane.showMessageDialog(instance, "Successfully started!", "Start", JOptionPane.INFORMATION_MESSAGE);
							buildButton.setEnabled(false);
							startButton.setEnabled(false);
							stopButton.setEnabled(true);
							return;
						}
					}
					JOptionPane.showMessageDialog(instance, "Could not Start, see console for details", "Start error", JOptionPane.ERROR_MESSAGE);
				}
			});
			startButton.setEnabled(false);
		}
		return startButton;
	}

	/**
	 * This method initializes stopButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getStopButton()
	{
		if (stopButton == null)
		{
			stopButton = new JButton();
			stopButton.setText("Stop");
			stopButton.setEnabled(false);
			stopButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (inputAdapter.stop())
					{
						stopButton.setEnabled(false);
						buildButton.setEnabled(true);
					}
				}
			});
		}
		return stopButton;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 5;
		gridBagConstraints3.gridy = 8;
		gridBagConstraints3.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints3.gridwidth = 2;
		gridBagConstraints3.insets = new Insets(10, 0, 5, 10);
		GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
		gridBagConstraints29.fill = GridBagConstraints.BOTH;
		gridBagConstraints29.gridy = 6;
		gridBagConstraints29.weightx = 1.0;
		gridBagConstraints29.gridx = 1;
		gridBagConstraints29.gridwidth = 4;
		gridBagConstraints29.insets = new Insets(5, 0, 5, 5);
		GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
		gridBagConstraints28.gridx = 0;
		gridBagConstraints28.gridy = 6;
		gridBagConstraints28.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints28.insets = new Insets(5, 0, 5, 5);
		jframeName = new JLabel();
		jframeName.setText("JFrame name (Optinal): ");
		GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
		gridBagConstraints27.fill = GridBagConstraints.BOTH;
		gridBagConstraints27.gridy = 4;
		gridBagConstraints27.weightx = 1.0;
		gridBagConstraints27.gridx = 1;
		gridBagConstraints27.gridwidth = 4;
		gridBagConstraints27.insets = new Insets(5, 0, 5, 5);
		GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
		gridBagConstraints26.gridx = 0;
		gridBagConstraints26.gridy = 4;
		gridBagConstraints26.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints26.insets = new Insets(5, 0, 5, 5);
		argsLabel = new JLabel();
		argsLabel.setText("Main arguments: ");
		GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
		gridBagConstraints25.gridx = 0;
		gridBagConstraints25.gridy = 0;
		gridBagConstraints25.gridwidth = 3;
		gridBagConstraints25.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints25.insets = new Insets(10, 10, 10, 10);
		infoLabel = new JLabel();
		infoLabel.setText("Fill this form to create a new input adapter.");
		GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
		gridBagConstraints24.gridx = 3;
		gridBagConstraints24.gridwidth = 2;
		gridBagConstraints24.gridy = 8;
		gridBagConstraints24.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints24.insets = new Insets(10, 0, 5, 5);
		GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
		gridBagConstraints23.gridx = 1;
		gridBagConstraints23.gridy = 8;
		gridBagConstraints23.weightx = 1.0;
		gridBagConstraints23.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints23.insets = new Insets(10, 0, 5, 5);
		GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
		gridBagConstraints22.fill = GridBagConstraints.BOTH;
		gridBagConstraints22.gridy = 7;
		gridBagConstraints22.weightx = 1.0;
		gridBagConstraints22.gridx = 1;
		gridBagConstraints22.gridwidth = 4;
		gridBagConstraints22.insets = new Insets(10, 0, 5, 5);
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.gridy = 7;
		gridBagConstraints21.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints21.insets = new Insets(5, 10, 5, 5);
		sendLabel = new JLabel();
		sendLabel.setText("Send frame to Output Adapter: ");
		GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
		gridBagConstraints20.fill = GridBagConstraints.BOTH;
		gridBagConstraints20.gridy = 5;
		gridBagConstraints20.weightx = 1.0;
		gridBagConstraints20.gridx = 1;
		gridBagConstraints20.gridwidth = 4;
		gridBagConstraints20.insets = new Insets(5, 0, 5, 5);
		GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
		gridBagConstraints19.gridx = 0;
		gridBagConstraints19.gridy = 5;
		gridBagConstraints19.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints19.insets = new Insets(5, 0, 5, 5);
		jframeClass = new JLabel();
		jframeClass.setText("JFrame class (Optional): ");
		GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
		gridBagConstraints18.fill = GridBagConstraints.BOTH;
		gridBagConstraints18.gridy = 3;
		gridBagConstraints18.weightx = 1.0;
		gridBagConstraints18.gridx = 1;
		gridBagConstraints18.gridwidth = 4;
		gridBagConstraints18.insets = new Insets(5, 0, 5, 5);
		GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
		gridBagConstraints17.gridx = 0;
		gridBagConstraints17.gridy = 3;
		gridBagConstraints17.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints17.insets = new Insets(5, 0, 5, 5);
		jframeLabel = new JLabel();
		jframeLabel.setText("Main class: ");
		GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
		gridBagConstraints16.gridx = 5;
		gridBagConstraints16.gridwidth = 1;
		gridBagConstraints16.gridy = 1;
		gridBagConstraints16.insets = new Insets(5, 0, 0, 0);
		GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
		gridBagConstraints15.fill = GridBagConstraints.BOTH;
		gridBagConstraints15.gridy = 1;
		gridBagConstraints15.weightx = 1.0;
		gridBagConstraints15.gridx = 1;
		gridBagConstraints15.gridwidth = 4;
		gridBagConstraints15.insets = new Insets(10, 0, 5, 5);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints.insets = new Insets(10, 0, 5, 5);
		jarLabel = new JLabel();
		jarLabel.setText("Application .jar file: ");
		this.setPreferredSize(new Dimension(605, 271));
		this.setLayout(new GridBagLayout());
		this.add(jarLabel, gridBagConstraints);
		this.add(getJarTextField(), gridBagConstraints15);
		this.add(getBrowseButton(), gridBagConstraints16);
		this.add(jframeLabel, gridBagConstraints17);
		this.add(getMainTextField(), gridBagConstraints18);
		this.add(jframeClass, gridBagConstraints19);
		this.add(getJframeTextField(), gridBagConstraints20);
		this.add(sendLabel, gridBagConstraints21);
		this.add(getSentTextField(), gridBagConstraints22);
		this.add(getBuildButton(), gridBagConstraints23);
		this.add(getStartButton(), gridBagConstraints24);
		this.add(infoLabel, gridBagConstraints25);
		this.add(argsLabel, gridBagConstraints26);
		this.add(getArgsTextField(), gridBagConstraints27);
		this.add(jframeName, gridBagConstraints28);
		this.add(getJframeNameTextField(), gridBagConstraints29);
		this.add(getStopButton(), gridBagConstraints3);
	}

	private void showBrowseDialog()
	{
		JFileChooser jfc = new JFileChooser();
		FileFilter jarFilter = new ExampleFileFilter("jar", "A compressed jar file");
		jfc.setFileFilter(jarFilter);
		int rVal = jfc.showOpenDialog(this);
		if (rVal == JFileChooser.APPROVE_OPTION)
		{
			getJarTextField().setText(jfc.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * This method initializes argsTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getArgsTextField()
	{
		if (argsTextField == null)
		{
			argsTextField = new JTextField();
		}
		return argsTextField;
	}

	/**
	 * This method initializes jarTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getJarTextField()
	{
		if (jarTextField == null)
		{
			jarTextField = new JTextField();
		}
		return jarTextField;
	}

	/**
	 * This method initializes jframeNameTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getJframeNameTextField()
	{
		if (jframeNameTextField == null)
		{
			jframeNameTextField = new JTextField();
		}
		return jframeNameTextField;
	}

	/**
	 * This method initializes jframeTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getJframeTextField()
	{
		if (jframeTextField == null)
		{
			jframeTextField = new JTextField();
		}
		return jframeTextField;
	}

	/**
	 * This method initializes mainTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getMainTextField()
	{
		if (mainTextField == null)
		{
			mainTextField = new JTextField();
		}
		return mainTextField;
	}

	/**
	 * This method initializes sentTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getSentTextField()
	{
		if (sentTextField == null)
		{
			sentTextField = new JTextField();
		}
		return sentTextField;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
