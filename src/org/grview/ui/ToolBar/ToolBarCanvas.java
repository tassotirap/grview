package org.grview.ui.ToolBar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.HashMap;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.grview.actions.AbstractEditAction;
import org.grview.canvas.Canvas;
import org.grview.syntax.grammar.Controller;
import org.grview.util.LangHelper;

import com.jidesoft.icons.ColorFilter;

public class ToolBarCanvas extends CommandBar<Canvas>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final URL runURL = getClass().getResource(imgPath + "application-run.png");
	private final URL zoomInURL = getClass().getResource(imgPath + "zoom-in.png");
	private final URL zoomOutURL = getClass().getResource(imgPath + "zoom-out.png");

	JButton btnRun = new JButton(new ImageIcon(runURL));
	JButton btnZoomIn = new JButton(new ImageIcon(zoomInURL));
	JButton btnZoomOut = new JButton(new ImageIcon(zoomOutURL));
	JButton[] buttons = new JButton[]
	{ btnRun, btnZoomIn, btnZoomOut };
	String[] names = new String[]
	{ LangHelper.build, LangHelper.zoom_plus, LangHelper.zoom_minus };

	Canvas canvas;

	public ToolBarCanvas(Canvas canvas)
	{
		this.canvas = canvas;
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i].setName(names[i]);
		}
		this.add(btnRun);
		JSeparator jSeparator1 = new JSeparator(SwingConstants.VERTICAL);
		jSeparator1.setMaximumSize(new Dimension(6, 100));
		this.add(jSeparator1);
		this.add(btnZoomIn);
		this.add(btnZoomOut);
	}

	public JButton getBtnRun()
	{
		return btnRun;
	}

	public JButton getBtnZoomIn()
	{
		return btnZoomIn;
	}

	public JButton getBtnZoomOut()
	{
		return btnZoomOut;
	}

	public JButton[] getButtons()
	{
		return buttons;
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

	@Override
	public String getNickname()
	{
		if (canvas instanceof Canvas)
			return CANVAS_TB_CANVAS;
		return null;
	}

	@Override
	public void initActions()
	{
		btnZoomIn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				if (canvas.canZoomIn())
				{
					canvas.setZoomFactor(canvas.getZoomFactor() * 1.1);
					canvas.validate();
				}
			}

		});

		btnZoomOut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				if (canvas.canZoomOut())
				{
					canvas.setZoomFactor(canvas.getZoomFactor() * 0.9);
					canvas.validate();
				}
			}

		});

		btnRun.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				Controller.generateAndParseCurrentGrammar(false);
			}

		});

	}

	@Override
	public void initLayout()
	{
		for (int i = 0; i < buttons.length; i++)
		{
			JButton button = buttons[i];
			button.setOpaque(false);
			button.setBorder(new EmptyBorder(5, 5, 5, 5));
			button.setRolloverEnabled(true);
			button.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon) button.getIcon()).getImage())));
			button.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon) button.getIcon()).getImage())));
			button.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon) button.getIcon()).getImage())));
			button.setBackground(this.getBackground());
			button.setToolTipText(names[i]);
		}
	}

	public void propertyChange(PropertyChangeEvent event)
	{

	}
}
