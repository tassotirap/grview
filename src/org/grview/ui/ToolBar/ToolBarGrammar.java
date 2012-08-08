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
import org.grview.util.LangHelper;

import com.jidesoft.icons.ColorFilter;

public class ToolBarGrammar extends CommandBar<Canvas>
{
	private static final long serialVersionUID = 1L;

	private final URL selectURL = getClass().getResource(imgPath + "select.png");
	private final URL successorURL = getClass().getResource(imgPath + "successor.png");
	private final URL alternativeURL = getClass().getResource(imgPath + "alternative.png");
	private final URL terminalURL = getClass().getResource(imgPath + "icon_t.png");
	private final URL nTerminalURL = getClass().getResource(imgPath + "icon_nt.png");
	private final URL leftHandURL = getClass().getResource(imgPath + "left_hand.png");
	private final URL lambdaAlternativeURL = getClass().getResource(imgPath + "icon_l.png");
	private final URL labelURL = getClass().getResource(imgPath + "label.png");
	private final URL startURL = getClass().getResource(imgPath + "icon_s.png");

	JButton btnSelect = new JButton(new ImageIcon(selectURL));
	JButton btnSucessor = new JButton(new ImageIcon(successorURL));
	JButton btnAlternative = new JButton(new ImageIcon(alternativeURL));
	JButton btnLeftHand = new JButton(new ImageIcon(leftHandURL));
	JButton btnNTerminal = new JButton(new ImageIcon(nTerminalURL));
	JButton btnTerminal = new JButton(new ImageIcon(terminalURL));
	JButton btnLambdaAlternative = new JButton(new ImageIcon(lambdaAlternativeURL));
	JButton btnLabel = new JButton(new ImageIcon(labelURL));
	JButton btnStart = new JButton(new ImageIcon(startURL));
	JButton[] buttons = new JButton[]
	{ btnSelect, btnSucessor, btnAlternative, btnLeftHand, btnNTerminal, btnTerminal, btnLambdaAlternative, btnLabel, btnStart };
	String[] names = new String[]
	{ LangHelper.select, LangHelper.successor, LangHelper.alternative, LangHelper.left_hand, LangHelper.n_terminal, LangHelper.terminal, LangHelper.lambda_alternative, LangHelper.label, LangHelper.start };

	Canvas canvas;

	public ToolBarGrammar(Canvas canvas)
	{
		this.canvas = canvas;
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i].setName(names[i]);
		}
		this.add(btnSelect);
		JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
		sep1.setMaximumSize(new Dimension(100, 6));
		this.add(sep1);
		this.add(btnSucessor);
		this.add(btnAlternative);
		JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
		sep2.setMaximumSize(new Dimension(100, 6));
		this.add(sep2);
		this.add(btnStart);
		this.add(btnLeftHand);
		this.add(btnNTerminal);
		this.add(btnTerminal);
		this.add(btnLambdaAlternative);
		JSeparator sep3 = new JSeparator(SwingConstants.HORIZONTAL);
		sep3.setMaximumSize(new Dimension(100, 6));
		this.add(sep3);
		this.add(btnLabel);
	}

	public JButton getBtnAlternative()
	{
		return btnAlternative;
	}

	public JButton getBtnLabel()
	{
		return btnLabel;
	}

	public JButton getBtnLambdaAlternative()
	{
		return btnLambdaAlternative;
	}

	public JButton getBtnLeftHand()
	{
		return btnLeftHand;
	}

	public JButton getBtnNTerminal()
	{
		return btnNTerminal;
	}

	public JButton getBtnSelect()
	{
		return btnSelect;
	}

	public JButton getBtnSucessor()
	{
		return btnSucessor;
	}

	public JButton getBtnTerminal()
	{
		return btnTerminal;
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

		btnSelect.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				canvas.setActiveTool(Canvas.SELECT);
			}

		});
		btnSucessor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				canvas.setActiveTool(Canvas.SUCCESSOR);
			}

		});
		btnAlternative.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				canvas.setActiveTool(Canvas.ALTERNATIVE);
			}

		});
		btnLeftHand.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				canvas.setActiveTool(Canvas.LEFT_SIDE);
			}

		});
		btnNTerminal.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				canvas.setActiveTool(Canvas.N_TERMINAL);
			}

		});
		btnTerminal.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				canvas.setActiveTool(Canvas.TERMINAL);
			}

		});
		btnLambdaAlternative.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				canvas.setActiveTool(Canvas.LAMBDA);
			}

		});
		btnLabel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				canvas.setActiveTool(Canvas.LABEL);
			}

		});
		btnStart.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				canvas.setActiveTool(Canvas.START);
			}

		});
	}

	@Override
	public void initLayout()
	{
		for (int i = 0; i < buttons.length; i++)
		{
			JButton bt = buttons[i];
			bt.setOpaque(false);
			bt.setBorder(new EmptyBorder(1, 1, 1, 1));
			bt.setRolloverEnabled(true);
			bt.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon) bt.getIcon()).getImage())));
			bt.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon) bt.getIcon()).getImage())));
			bt.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon) bt.getIcon()).getImage())));
			bt.setBackground(this.getBackground());
			bt.setToolTipText(names[i]);
		}
	}

	public void propertyChange(PropertyChangeEvent event)
	{

	}
}