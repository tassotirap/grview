package org.grview.ui.toolbar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.grview.canvas.Canvas;
import org.grview.syntax.grammar.Controller;
import org.grview.util.LangHelper;

import com.jidesoft.icons.ColorFilter;

public class ToolBarCanvas extends BaseToolBar<Canvas>
{
	private static final long serialVersionUID = 1L;

	JButton[] buttons;

	String[] names;
	private JButton btnRun, btnZoomIn, btnZoomOut;

	public ToolBarCanvas(Canvas canvas)
	{
		super(canvas);
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i].setName(names[i]);
		}
		this.add(btnRun);
		this.add(createJSeparator());
		this.add(btnZoomIn);
		this.add(btnZoomOut);
	}

	private JSeparator createJSeparator()
	{
		JSeparator jSeparator = new JSeparator(SwingConstants.VERTICAL);
		jSeparator.setMaximumSize(new Dimension(6, 100));
		return jSeparator;
	}

	@Override
	protected void initActions()
	{
		btnZoomIn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				if (context.canZoomIn())
				{
					context.setZoomFactor(context.getZoomFactor() * 1.1);
					context.validate();
				}
			}

		});

		btnZoomOut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				if (context.canZoomOut())
				{
					context.setZoomFactor(context.getZoomFactor() * 0.9);
					context.validate();
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
	protected void initComponets()
	{
		btnRun = new JButton(new ImageIcon(getClass().getResource(imgPath + "application-run.png")));
		btnZoomIn = new JButton(new ImageIcon(getClass().getResource(imgPath + "zoom-in.png")));
		btnZoomOut = new JButton(new ImageIcon(getClass().getResource(imgPath + "zoom-out.png")));
		buttons = new JButton[]{ btnRun, btnZoomIn, btnZoomOut };
		names = new String[]{ LangHelper.build, LangHelper.zoom_plus, LangHelper.zoom_minus };
	}

	@Override
	protected void initLayout()
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

	public String[] getNames()
	{
		return names;
	}
}
