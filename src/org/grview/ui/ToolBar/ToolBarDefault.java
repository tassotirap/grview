package org.grview.ui.toolbar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.grview.actions.Registers;
import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.canvas.action.WidgetCopyPasteProvider;
import org.grview.canvas.action.WidgetDeleteProvider;
import org.grview.canvas.state.VolatileStateManager;
import org.grview.editor.StandaloneTextArea;
import org.grview.editor.buffer.JEditBuffer;
import org.grview.project.ProjectMediator;
import org.grview.util.LangHelper;

import com.jidesoft.icons.ColorFilter;

public class ToolBarDefault<E> extends BaseToolBar<E> implements PropertyChangeListener
{
	private static final long serialVersionUID = 1L;

	JButton[] buttons;
	String[] names;

	private JButton btnCut, btnPaste, btnUndo, btnRedo;
	private JButton btnSave, btnSaveAll, btnPrint, btnCopy;

	public ToolBarDefault(E context)
	{
		super(context);
		if (context instanceof Canvas)
		{
			((Canvas) context).getMonitor().addPropertyChangeListener("undoable", this);
			((Canvas) context).getMonitor().addPropertyChangeListener("object_state", this);
		}
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i].setName(names[i]);
		}
		this.add(btnSave);
		this.add(btnSaveAll);
		this.add(btnPrint);
		this.add(createJSeparator());
		this.add(btnCopy);
		this.add(btnCut);
		this.add(btnPaste);
		this.add(createJSeparator());
		this.add(btnUndo);
		this.add(btnRedo);
	}

	private JSeparator createJSeparator()
	{
		JSeparator jSeparator = new JSeparator(SwingConstants.VERTICAL);
		jSeparator.setMaximumSize(new Dimension(6, 100));
		return jSeparator;
	}

	private void setBaseActions()
	{
		btnSave.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				ProjectMediator.saveFile(context);
			}

		});
		btnSaveAll.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				ProjectMediator.saveAllFiles();
			}

		});
		btnPrint.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				ProjectMediator.print(context);
			}
		});
	}

	private void setCanvasActions(final Canvas canvas)
	{
		btnCopy.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				WidgetCopyPasteProvider wcpp = new WidgetCopyPasteProvider(canvas);
				wcpp.copySelected();
			}

		});
		btnCut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				WidgetCopyPasteProvider wcpp = new WidgetCopyPasteProvider(canvas);
				WidgetDeleteProvider wdp = new WidgetDeleteProvider(canvas);
				wcpp.cutSelected(wdp);
			}

		});
		btnPaste.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				WidgetCopyPasteProvider wcpp = new WidgetCopyPasteProvider(canvas);
				wcpp.paste(null);
			}

		});
		btnUndo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				VolatileStateManager volatileStateManager = canvas.getVolatileStateManager();
				if (volatileStateManager.hasNextUndo())
				{
					volatileStateManager.undo();
				}
			}

		});
		btnRedo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				VolatileStateManager vsm = canvas.getVolatileStateManager();
				if (vsm.hasNextRedo())
				{
					vsm.redo();
				}
			}

		});
	}

	private void setTextActions(final StandaloneTextArea textArea)
	{
		btnCopy.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				Registers.copy(textArea, '$');
			}

		});
		btnCut.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				Registers.cut(textArea, '$');
			}

		});
		btnPaste.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				Registers.paste(textArea, '$', false);
			}

		});
		btnUndo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				JEditBuffer buffer = textArea.getBuffer();
				buffer.undo(textArea);
			}

		});
		btnRedo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				JEditBuffer buffer = textArea.getBuffer();
				buffer.redo(textArea);
			}

		});
	}

	@Override
	protected void initActions()
	{
		setBaseActions();

		if (context instanceof Canvas)
		{
			setCanvasActions((Canvas) context);
		}
		else if (context instanceof StandaloneTextArea)
		{
			setTextActions((StandaloneTextArea) context);
		}
	}

	@Override
	protected void initComponets()
	{
		btnSave = new JButton(new ImageIcon(getClass().getResource(imgPath + "document-save.png")));
		btnSaveAll = new JButton(new ImageIcon(getClass().getResource(imgPath + "document-save-all.png")));
		btnPrint = new JButton(new ImageIcon(getClass().getResource(imgPath + "document-print.png")));
		btnCopy = new JButton(new ImageIcon(getClass().getResource(imgPath + "edit-copy.png")));
		btnCut = new JButton(new ImageIcon(getClass().getResource(imgPath + "edit-cut.png")));
		btnPaste = new JButton(new ImageIcon(getClass().getResource(imgPath + "edit-paste.png")));
		btnUndo = new JButton(new ImageIcon(getClass().getResource(imgPath + "edit-undo.png")));
		btnRedo = new JButton(new ImageIcon(getClass().getResource(imgPath + "edit-redo.png")));
		buttons = new JButton[]{ btnSave, btnSaveAll, btnPrint, btnCopy, btnCut, btnPaste, btnUndo, btnRedo };
		names = new String[]{ LangHelper.save, LangHelper.save_all, LangHelper.print, LangHelper.copy, LangHelper.cut, LangHelper.paste, LangHelper.undo, LangHelper.redo };
	}

	@Override
	protected void initLayout()
	{
		for (int i = 0; i < buttons.length; i++)
		{
			JButton btn = buttons[i];
			btn.setOpaque(false);
			btn.setBorder(new EmptyBorder(5, 5, 5, 5));
			btn.setRolloverEnabled(true);
			btn.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon) btn.getIcon()).getImage())));
			btn.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon) btn.getIcon()).getImage())));
			btn.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon) btn.getIcon()).getImage())));
			btn.setBackground(this.getBackground());
			btn.setToolTipText(names[i]);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource() instanceof Canvas)
		{
			VolatileStateManager vsm = CanvasFactory.getVolatileStateManager();
			if (event.getPropertyName().equals("undoable"))
			{
				btnUndo.setEnabled(true);
				btnUndo.setToolTipText("Undo " + vsm.getNextUndoable());
			}
			if (event.getPropertyName().equals("object_state"))
			{
				if (vsm.hasNextRedo())
				{
					btnRedo.setEnabled(true);
					btnRedo.setToolTipText("Redo " + vsm.getNextRedoable());
				}
			}
		}
	}

}