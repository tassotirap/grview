package org.grview.canvas;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import org.grview.canvas.action.GridProvider;
import org.grview.canvas.action.LineProvider;
import org.grview.canvas.action.WidgetCopyPasteProvider;
import org.grview.canvas.action.WidgetDeleteProvider;
import org.grview.canvas.state.VolatileStateManager;
import org.grview.canvas.widget.MarkedWidget;
import org.grview.project.Project;
import org.grview.project.ProjectManager;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.command.AddRoutineCommand;
import org.grview.syntax.command.CommandFactory;
import org.grview.syntax.command.RemoveRoutineCommand;
import org.grview.syntax.grammar.Controller;
import org.grview.ui.wizard.RoutineWizard;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

public class CanvasPopupMenu extends WidgetAction.Adapter implements PopupMenuProvider
{

	private Canvas canvas;
	private Widget widget;
	private GridProvider gridProvider;
	private LineProvider lineProvider;
	private PropertyChangeSupport monitor;

	final JPopupMenu popup = new JPopupMenu();

	public CanvasPopupMenu(Canvas canvas)
	{
		this.canvas = canvas;
		gridProvider = GridProvider.getInstance(canvas);
		lineProvider = LineProvider.getInstance(canvas);
		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager(canvas.getID()));
	}

	private JMenuItem createCopyMenu()
	{
		JMenuItem copyMenu = new JMenuItem("Copy");
		copyMenu.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				WidgetCopyPasteProvider wcpp = new WidgetCopyPasteProvider(canvas);
				if (widget.getState().isSelected())
				{
					wcpp.copySelected();
				}
				else
				{
					wcpp.copyThese(widget);
				}
			}
		});
		return copyMenu;
	}

	private JMenuItem createCutMenu()
	{
		JMenuItem cutMenu = new JMenuItem("Cut");
		cutMenu.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				WidgetCopyPasteProvider wcpp = new WidgetCopyPasteProvider(canvas);
				WidgetDeleteProvider wdp = new WidgetDeleteProvider(canvas);
				if (widget.getState().isSelected() && wdp.isDeletionAllowed())
				{
					wcpp.cutSelected(wdp);
				}
				else if (wdp.isDeletionAllowed(widget))
				{
					wcpp.cutThese(wdp, widget);
				}
			}
		});
		return cutMenu;
	}

	private JMenuItem createDeleteMenu()
	{
		JMenuItem deleteMenu = new JMenuItem("Delete");
		deleteMenu.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				WidgetDeleteProvider wdp = new WidgetDeleteProvider(canvas);
				if (widget.getState().isSelected() && wdp.isDeletionAllowed())
				{
					wdp.deleteSelected();
				}
				else if (wdp.isDeletionAllowed(widget))
				{
					wdp.deleteThese(widget);
				}
			}
		});
		return deleteMenu;
	}

	private JMenuItem createBuildAndParseMenu()
	{
		JMenuItem grammarMenu = new JMenuItem("Build and Parse");
		grammarMenu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Cursor oldCursor = canvas.getView().getCursor();
				canvas.getView().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Controller.generateAndParseCurrentGrammar(false);
				canvas.getView().setCursor(oldCursor);
			}
		});
		return grammarMenu;
	}

	private JMenuItem createBuildAndExport()
	{
		JMenuItem grammarMenu = new JMenuItem("Build and Export");
		grammarMenu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Cursor oldCursor = canvas.getView().getCursor();
				canvas.getView().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Controller.generateAndParseCurrentGrammar(true);
				canvas.getView().setCursor(oldCursor);
			}
		});
		return grammarMenu;
	}

	private JMenu createMovingMenu()
	{
		JMenu movingMenu = new JMenu("Move Policy");

		ButtonGroup group = new ButtonGroup();

		JRadioButtonMenuItem free = new JRadioButtonMenuItem("Free Move");
		group.add(free);
		movingMenu.add(free).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				gridProvider.setVisible(false);
				lineProvider.removeAllLines();
				canvas.setMoveStrategy(Canvas.M_FREE);
			}
		});
		free.setSelected(canvas.getCanvasState().getPreferences().getMoveStrategy().equals(Canvas.M_FREE));

		JRadioButtonMenuItem snap = new JRadioButtonMenuItem("Snap To Grid");
		group.add(snap);
		movingMenu.add(snap).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				gridProvider.setVisible(true);
				lineProvider.removeAllLines();
				canvas.setMoveStrategy(Canvas.M_SNAP);
			}
		});
		snap.setSelected(canvas.getCanvasState().getPreferences().getMoveStrategy().equals(Canvas.M_SNAP));

		JRadioButtonMenuItem align = new JRadioButtonMenuItem("Auto Align");
		group.add(align);
		movingMenu.add(align).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				gridProvider.setVisible(false);
				lineProvider.removeAllLines();
				canvas.setMoveStrategy(Canvas.M_ALIGN);
			}
		});
		align.setSelected(canvas.getCanvasState().getPreferences().getMoveStrategy().equals(Canvas.M_ALIGN));

		JRadioButtonMenuItem lines = new JRadioButtonMenuItem("Snap To Lines");
		group.add(lines);
		movingMenu.add(lines).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				gridProvider.setVisible(false);
				lineProvider.populateCanvas();
				canvas.setMoveStrategy(Canvas.M_LINES);
			}
		});
		lines.setSelected(canvas.getCanvasState().getPreferences().getMoveStrategy().equals(Canvas.M_LINES));

		return movingMenu;
	}

	private JMenuItem createPasteMenu(final Point localLocation)
	{
		JMenuItem pasteMenu = new JMenuItem("Paste");
		pasteMenu.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				WidgetCopyPasteProvider wcpp = new WidgetCopyPasteProvider(canvas);
				wcpp.paste(localLocation);
			}
		});
		return pasteMenu;
	}

	private JMenuItem createRedoMenu()
	{
		final VolatileStateManager vsm = CanvasFactory.getVolatileStateManager(canvas.getID());
		JMenuItem redoMenu = new JMenuItem();
		redoMenu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (vsm.hasNextRedo())
				{
					vsm.redo();
				}
			}
		});
		if (!vsm.hasNextRedo())
		{
			redoMenu.setEnabled(false);
			redoMenu.setText("Redo");
		}
		else
		{
			redoMenu.setText("Redo " + vsm.getNextRedoable());
		}
		return redoMenu;
	}

	private JMenu createRoutingMenu()
	{
		JMenu routingMenu = new JMenu("Routing Policy");

		ButtonGroup group = new ButtonGroup();

		JRadioButtonMenuItem orto = new JRadioButtonMenuItem("Ortogonal");
		group.add(orto);
		routingMenu.add(orto).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				canvas.setConnStrategy(Canvas.R_ORTHOGONAL);
			}
		});
		orto.setSelected(canvas.getCanvasState().getPreferences().getConnectionStrategy().equals(Canvas.R_ORTHOGONAL));

		JRadioButtonMenuItem direct = new JRadioButtonMenuItem("Direct");
		group.add(direct);
		routingMenu.add(direct).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				canvas.setConnStrategy(Canvas.R_DIRECT);
			}
		});
		direct.setSelected(canvas.getCanvasState().getPreferences().getConnectionStrategy().equals(Canvas.R_DIRECT));

		JRadioButtonMenuItem free = new JRadioButtonMenuItem("Free");
		group.add(free);
		routingMenu.add(free).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				canvas.setConnStrategy(Canvas.R_FREE);
			}
		});
		free.setSelected(canvas.getCanvasState().getPreferences().getConnectionStrategy().equals(Canvas.R_FREE));

		return routingMenu;
	}

	private JMenu createShowMenu()
	{
		JMenu showMenu = new JMenu("Show");

		ButtonGroup group = new ButtonGroup();

		JRadioButtonMenuItem nothing = new JRadioButtonMenuItem("Nothing");
		group.add(nothing);
		showMenu.add(nothing).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				gridProvider.setVisible(false);
				lineProvider.removeAllLines();
				lineProvider.setGuideVisible(false);
			}
		});
		nothing.setSelected((!canvas.isShowingGrid() && !canvas.isShowingGuide() && !canvas.isShowingLines()));
		JRadioButtonMenuItem grid = new JRadioButtonMenuItem("Grid");
		group.add(grid);
		showMenu.add(grid).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				gridProvider.setVisible(true);
				lineProvider.removeAllLines();
				lineProvider.setGuideVisible(false);
			}
		});
		grid.setEnabled(canvas.getCanvasState().getPreferences().getMoveStrategy().equals(Canvas.M_FREE));
		grid.setSelected(canvas.isShowingGrid());
		JRadioButtonMenuItem line = new JRadioButtonMenuItem("Lines");
		group.add(line);
		showMenu.add(line).addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gridProvider.setVisible(false);
				lineProvider.populateCanvas();
				lineProvider.setGuideVisible(false);
			}
		});
		line.setEnabled(canvas.getCanvasState().getPreferences().getMoveStrategy().equals(Canvas.M_FREE));
		line.setSelected(canvas.isShowingLines());
		showMenu.add(new JSeparator());
		JCheckBoxMenuItem guide = new JCheckBoxMenuItem("Guide Line");
		showMenu.add(guide).addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				lineProvider.setGuideVisible(!lineProvider.isGuideVisible());
			}
		});
		guide.setSelected(canvas.isShowingGuide());
		return showMenu;
	}

	private JMenuItem createSMMenu()
	{
		boolean hasSR = false;
		boolean isMarkedWidget = false;
		String mark = null;
		if ((widget instanceof MarkedWidget))
		{
			isMarkedWidget = true;
			if ((mark = ((MarkedWidget) widget).getMark()) != null && !mark.equals(""))
			{
				hasSR = true;
			}
		}
		JMenu SMMenu = new JMenu("Semantic Routines");
		if (!isMarkedWidget)
		{
			SMMenu.setEnabled(false);
		}
		JMenuItem createSR = new JMenuItem("Create New...");
		createSR.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Project p = ProjectManager.getProject();
				if (p != null)
				{
					String semFile = null;
					if (p.getSemFile() != null && p.getSemFile().get(p.getVersion()) != null)
					{
						semFile = p.getSemFile().get(p.getVersion()).getAbsolutePath();
					}
					if (semFile != null && ProjectManager.hasUnsavedView(semFile))
					{
						int option = JOptionPane.showConfirmDialog(popup, "A new semantic routine can not be created while the semantic routines file remains unsaved.\nWould you like to save it now?", "Can not create a new routine", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if (option == JOptionPane.YES_OPTION)
						{
							ProjectManager.saveFile(semFile);
						}
						else
						{
							return;
						}
					}
					if (semFile != null)
					{
						new RoutineWizard(((String) canvas.findObject(widget)), (MarkedWidget) widget, null, p, monitor);
					}
				}
			}
		});
		SMMenu.add(createSR);
		JMenuItem removeSR;
		if (hasSR)
		{
			removeSR = new JMenuItem("Remove " + mark);
			removeSR.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					RemoveRoutineCommand command = CommandFactory.createRemoveRoutineCommand();
					if (command.addObject(canvas.findObject(widget), null) && command.execute())
					{
						((MarkedWidget) widget).setMark(null);
						monitor.firePropertyChange("undoable", null, command);
					}
				}
			});
		}
		else
		{
			removeSR = new JMenuItem("Remove");
			removeSR.setEnabled(false);
		}
		SMMenu.add(removeSR);
		JMenuItem editSR;
		if (hasSR)
		{
			editSR = new JMenuItem("Edit " + mark + "...");
			editSR.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					Project project = ProjectManager.getProject();
					if (project != null)
					{
						String semFile = null;
						if (project.getSemFile() != null && project.getSemFile().get(project.getVersion()) != null)
						{
							semFile = project.getSemFile().get(project.getVersion()).getAbsolutePath();
						}
						if (semFile != null && ProjectManager.hasUnsavedView(semFile))
						{
							int option = JOptionPane.showConfirmDialog(popup, "A semantic routine can not be edited while the semantic routines file remains unsaved.\nWould you like to save it now?", "Can not create a new routine", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
							if (option == JOptionPane.YES_OPTION)
							{
								ProjectManager.saveFile(semFile);
							}
							else
							{
								return;
							}
						}
						if (semFile != null)
						{
							new RoutineWizard(((String) canvas.findObject(widget)), (MarkedWidget) widget, ((MarkedWidget) widget).getMark(), project, monitor);
						}
					}
				}
			});
		}
		else
		{
			editSR = new JMenuItem("Edit...");
			editSR.setEnabled(false);
		}
		SMMenu.add(editSR);
		SMMenu.add(new JSeparator());
		final String[] SRs = new String[SemanticRoutinesRepo.getRegRoutines().size()];
		SemanticRoutinesRepo.getRegRoutines().toArray(SRs);
		JMenuItem routineM;
		for (int i = 0; i < SRs.length; i++)
		{
			routineM = new JMenuItem("Use " + SRs[i]);
			final String name = SRs[i];
			routineM.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					AddRoutineCommand command = CommandFactory.createAddRoutineCommand();
					if (command.addObject(canvas.findObject(widget), name) && command.execute())
					{
						((MarkedWidget) widget).setMark(name);
						monitor.firePropertyChange("undoable", null, command);
					}
				}
			});
			SMMenu.add(routineM);
		}
		return SMMenu;
	}

	private JMenuItem createUndoMenu()
	{
		final VolatileStateManager vsm = CanvasFactory.getVolatileStateManager(canvas.getID());
		JMenuItem undoMenu = new JMenuItem();
		undoMenu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (vsm.hasNextUndo())
				{
					vsm.undo();
				}
			}
		});
		if (!vsm.hasNextUndo())
		{
			undoMenu.setEnabled(false);
			undoMenu.setText("Undo");
		}
		else
		{
			undoMenu.setText("Undo " + vsm.getNextUndoable());
		}
		return undoMenu;
	}

	public JPopupMenu getPopupMenu(Widget widget, Point localLocation)
	{
		Object oWidget = canvas.getFocusedObject();
		Widget activeWidget = canvas.findWidget((oWidget != widget) ? oWidget : null);
		if (activeWidget == null)
		{
			for (String string : canvas.getNodes())
			{
				
				Widget tempWidget = canvas.findWidget(string);
				if (tempWidget.getPreferredLocation() != null && tempWidget.getPreferredBounds() != null)
				{
					Rectangle area = new Rectangle();
					area.x = tempWidget.getPreferredLocation().x;
					area.y = tempWidget.getPreferredLocation().y;
					area.height = tempWidget.getPreferredBounds().height;
					area.width = tempWidget.getPreferredBounds().width;
					if (tempWidget.getBorder() != null)
					{
						area.height += tempWidget.getBorder().getInsets().bottom + tempWidget.getBorder().getInsets().top;
						area.width += tempWidget.getBorder().getInsets().left + tempWidget.getBorder().getInsets().right;
					}
					if (area.x <= localLocation.x && area.y >= localLocation.y && area.x + area.width >= localLocation.x && area.y - area.height <= localLocation.y)
					{
						activeWidget = tempWidget;
						oWidget = string;
						break;
					}
				}
			}
		}
		popup.removeAll();
		if (activeWidget != null && !canvas.isLabel(oWidget))
		{
			this.widget = activeWidget;
			popup.add(createCopyMenu());
			popup.add(createCutMenu());
			popup.add(new JSeparator());
			popup.add(createDeleteMenu());
			popup.add(new JSeparator());
			popup.add(createSMMenu());
			popup.add(new JSeparator());
		}
		popup.add(createUndoMenu());
		popup.add(createRedoMenu());
		popup.add(new JSeparator());
		popup.add(createPasteMenu(localLocation));
		popup.add(new JSeparator());
		popup.add(createBuildAndParseMenu());
		popup.add(createBuildAndExport());
		popup.add(new JSeparator());
		popup.add(createShowMenu());
		popup.add(createRoutingMenu());
		popup.add(createMovingMenu());

		return popup;
	}

}
