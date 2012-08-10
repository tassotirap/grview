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
import org.grview.util.LangHelper;

import com.jidesoft.icons.ColorFilter;

public class ToolBarGrammar extends BaseToolBar<Canvas>
{
	private static final long serialVersionUID = 1L;

	private JButton btnSelect, btnSucessor, btnAlternative, btnLeftHand, btnLabel;
	private JButton btnNTerminal, btnTerminal, btnLambdaAlternative, btnStart;
	JButton[] buttons;
	String[] names;
	
	public ToolBarGrammar(Canvas canvas)
	{
		super(canvas);
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i].setName(names[i]);
		}
		this.add(btnSelect);
		this.add(createJSeparator());
		this.add(btnSucessor);
		this.add(btnAlternative);
		this.add(createJSeparator());
		this.add(btnStart);
		this.add(btnLeftHand);
		this.add(btnNTerminal);
		this.add(btnTerminal);
		this.add(btnLambdaAlternative);
		this.add(createJSeparator());
		this.add(btnLabel);
	}

	private JSeparator createJSeparator()
	{
		JSeparator jSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		jSeparator.setMaximumSize(new Dimension(100, 6));
		return jSeparator;
	}

	@Override
	protected void initActions()
	{

		btnSelect.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				context.setActiveTool(Canvas.SELECT);
			}

		});
		btnSucessor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				context.setActiveTool(Canvas.SUCCESSOR);
			}

		});
		btnAlternative.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				context.setActiveTool(Canvas.ALTERNATIVE);
			}

		});
		btnLeftHand.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				context.setActiveTool(Canvas.LEFT_SIDE);
			}

		});
		btnNTerminal.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				context.setActiveTool(Canvas.N_TERMINAL);
			}

		});
		btnTerminal.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				context.setActiveTool(Canvas.TERMINAL);
			}

		});
		btnLambdaAlternative.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				context.setActiveTool(Canvas.LAMBDA);
			}

		});
		btnLabel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				context.setActiveTool(Canvas.LABEL);
			}

		});
		btnStart.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				context.setActiveTool(Canvas.START);
			}

		});
	}

	@Override
	protected void initComponets()
	{	
		btnSelect = new JButton(new ImageIcon(getClass().getResource(imgPath + "select.png")));
		btnSucessor = new JButton(new ImageIcon(getClass().getResource(imgPath + "successor.png")));
		btnAlternative = new JButton(new ImageIcon(getClass().getResource(imgPath + "alternative.png")));
		btnLeftHand = new JButton(new ImageIcon(getClass().getResource(imgPath + "left_hand.png")));
		btnNTerminal = new JButton(new ImageIcon(getClass().getResource(imgPath + "icon_nt.png")));
		btnTerminal = new JButton(new ImageIcon(getClass().getResource(imgPath + "icon_t.png")));
		btnLambdaAlternative = new JButton(new ImageIcon(getClass().getResource(imgPath + "icon_l.png")));
		btnLabel = new JButton(new ImageIcon(getClass().getResource(imgPath + "label.png")));
		btnStart = new JButton(new ImageIcon(getClass().getResource(imgPath + "icon_s.png")));

		buttons = new JButton[]{ btnSelect, btnSucessor, btnAlternative, btnLeftHand, btnNTerminal, btnTerminal, btnLambdaAlternative, btnLabel, btnStart };
		names = new String[]{LangHelper.select , LangHelper.successor, LangHelper.alternative, LangHelper.left_hand, LangHelper.n_terminal, LangHelper.terminal, LangHelper.lambda_alternative, LangHelper.label, LangHelper.start };
	}

	@Override
	protected void initLayout()
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
}