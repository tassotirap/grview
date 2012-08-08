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

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.canvas.state.VolatileStateManager;
import org.grview.editor.TextArea;
import org.grview.util.LangHelper;

import com.jidesoft.icons.ColorFilter;

public abstract class ToolBarFile<E> extends CommandBar<E>
{
	private static final long serialVersionUID = 1L;

	private final URL saveURL = getClass().getResource(imgPath + "document-save.png");
	private final URL saveAllURL = getClass().getResource(imgPath + "document-save-all.png");
	private final URL printURL = getClass().getResource(imgPath + "document-print.png");
	private final URL copyURL = getClass().getResource(imgPath + "edit-copy.png");
	private final URL cutURL = getClass().getResource(imgPath + "edit-cut.png");
	private final URL pasteURL = getClass().getResource(imgPath + "edit-paste.png");
	private final URL undoURL = getClass().getResource(imgPath + "edit-undo.png");
	private final URL redoURL = getClass().getResource(imgPath + "edit-redo.png");

	JButton btnSave = new JButton(new ImageIcon(saveURL));
	JButton btnSaveAll = new JButton(new ImageIcon(saveAllURL));
	JButton btnPrint = new JButton(new ImageIcon(printURL));
	JButton btnCopy = new JButton(new ImageIcon(copyURL));
	JButton btnCut = new JButton(new ImageIcon(cutURL));
	JButton btnPaste = new JButton(new ImageIcon(pasteURL));
	JButton btnUndo = new JButton(new ImageIcon(undoURL));
	JButton btnRedo = new JButton(new ImageIcon(redoURL));
	JButton[] buttons = new JButton[] { btnSave, btnSaveAll, btnPrint, btnCopy, btnCut, btnPaste, btnUndo, btnRedo };
	String[] names = new String[] { LangHelper.save, LangHelper.save_all, LangHelper.print, LangHelper.copy, LangHelper.cut, LangHelper.paste, LangHelper.undo, LangHelper.redo };
	HashMap<String, String[]> contextEnabledMap = new HashMap<String, String[]>();

	E context;

	public ToolBarFile(E context)
	{
		this.context = context;
		if (context instanceof Canvas)
		{
			((Canvas) context).getMonitor().addPropertyChangeListener("undoable", this);
			((Canvas) context).getMonitor().addPropertyChangeListener("object_state", this);
		}
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i].setName(names[i]);
		}
		contextEnabledMap.put(MAIN_TB_CANVAS, new String[] { btnSave.getName(), btnSaveAll.getName(), btnPrint.getName(), btnCopy.getName(), btnCut.getName(), btnPaste.getName(), btnUndo.getName(), btnRedo.getName() });
		contextEnabledMap.put(MAIN_TB_TEXTAREA, new String[] { btnSave.getName(), btnSaveAll.getName(), btnPrint.getName(), btnCopy.getName(), btnCut.getName(), btnPaste.getName(), btnUndo.getName(), btnRedo.getName() });
		this.add(btnSave);
		btnSave.setEnabled(true);
		this.add(btnSaveAll);
		btnSaveAll.setEnabled(true);
		this.add(btnPrint);
		btnPrint.setEnabled(true);
		JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
		sep1.setMaximumSize(new Dimension(6, 100));
		this.add(sep1);
		this.add(btnCopy);
		btnCopy.setEnabled(true);
		this.add(btnCut);
		btnCut.setEnabled(true);
		this.add(btnPaste);
		btnPaste.setEnabled(true);
		JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
		sep2.setMaximumSize(new Dimension(6, 100));
		this.add(sep2);
		this.add(btnUndo);
		btnUndo.setEnabled(true);
		this.add(btnRedo);
		btnRedo.setEnabled(true);
	}

	public JButton getBtnCopy()
	{
		return btnCopy;
	}

	public JButton getBtnCut()
	{
		return btnCut;
	}

	public JButton getBtnPaste()
	{
		return btnPaste;
	}

	public JButton getBtnPrint()
	{
		return btnPrint;
	}

	public JButton getBtnRedo()
	{
		return btnRedo;
	}

	public JButton getBtnSave()
	{
		return btnSave;
	}

	public JButton getBtnSaveAll()
	{
		return btnSaveAll;
	}

	public JButton getBtnUndo()
	{
		return btnUndo;
	}

	public JButton[] getButtons()
	{
		return buttons;
	}

	@Override
	public HashMap<String, String[]> getContextEnabledMap()
	{
		return this.contextEnabledMap;
	}

	public String[] getNames()
	{
		return names;
	}

	@Override
	public String getNickname()
	{
		if (context instanceof Canvas)
			return MAIN_TB_CANVAS;
		else if (context instanceof TextArea)
			return MAIN_TB_TEXTAREA;
		return null;
	}

	@Override
	public void initActions()
	{
		for (final JButton button : buttons)
		{
			button.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					JButton button = (JButton) evt.getSource();
					getAction(button.getName().replaceAll(" ", "").toLowerCase()).invoke(context);
				}

			});
		}
	}

	@Override
	public void initLayout()
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

	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource() instanceof Canvas)
		{
			VolatileStateManager vsm = CanvasFactory.getVolatileStateManager(((Canvas) context).getID());
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