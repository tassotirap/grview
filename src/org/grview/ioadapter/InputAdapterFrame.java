package org.grview.ioadapter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class InputAdapterFrame extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JDesktopPane jDesktopPane;
	private JFrame frame;

	/** this is the default constructor **/
	public InputAdapterFrame(JFrame frame)
	{
		super();
		initialize(frame);
	}

	private void initialize(JFrame frame)
	{
		setLayout(new BorderLayout());
		add(getJDesktopPane(frame), BorderLayout.CENTER);
	}

	public JDesktopPane getJDesktopPane(JFrame frame)
	{
		if (jDesktopPane == null || this.frame != frame)
		{
			this.frame = frame;
			jDesktopPane = new JDesktopPane();
			jDesktopPane.setBackground(new Color(128, 128, 255));
			transformToInternalFrame(frame, jDesktopPane);
		}
		return jDesktopPane;
	}

	/**
	 * Creates a new JInternalFrame based on the contents of an existing JFrame
	 * 
	 * @param f
	 *            the JFrame that will be used to create the JInternalFrame
	 * @param desktop
	 *            the JDesktopPane that will receive the JInternalFrame, if null
	 *            it only retunrs the internal frame
	 * @return a new JInternalFrame
	 */
	public JInternalFrame transformToInternalFrame(JFrame f, JDesktopPane desktop)
	{
		JInternalFrame jif = new JInternalFrame(f.getTitle(), true, true, true, true);
		if (f.getIconImage() != null)
			jif.setFrameIcon(new ImageIcon(f.getIconImage()));
		if (desktop != null)
			desktop.add(jif);
		jif.setSize(f.getSize());
		jif.setContentPane(f.getContentPane());
		if (desktop != null)
			SwingUtilities.convertPointFromScreen(f.getLocation(), desktop);
		jif.setLocation(new Point(0, 0));
		jif.setBounds(f.getBounds());
		f.dispose();
		jif.setVisible(true);
		return (jif);
	}

}
