package org.grview.canvas.action;

import java.util.HashMap;
import java.util.Observable;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasData;
import org.grview.canvas.CanvasPopupMenu;
import org.grview.canvas.strategy.MoveStrategy;
import org.grview.canvas.widget.GridWidget;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.modules.visual.action.SingleLayerAlignWithWidgetCollector;

public class WidgetActionRepositoryFactory
{

	private static class WAR implements WidgetActionRepository
	{

		HashMap<String, WidgetAction> actions = new HashMap<String, WidgetAction>();

		private String activeMoveAction = null;

		public WAR()
		{
			actions.put(CREATE, null);
			actions.put(SELECT, null);
			actions.put(MULTI_SELECT, null);
			actions.put(NODE_HOVER, null);
			actions.put(ALTERNATIVE, null);
			actions.put(SUCCESSOR, null);
			actions.put(RECONNECT, null);
			actions.put(EDITOR, null);
			actions.put(MOVE, null);
			actions.put(MOVE_FREE, null);
			actions.put(MOVE_SNAP, null);
			actions.put(MOVE_ALIGN, null);
			actions.put(MOVE_LINES, null);
			actions.put(POPUP_MENU_MAIN, null);
			actions.put(RECTANGULAR_SELECT, null);
			actions.put(MOUSE_CENTERED_ZOOM, null);
			actions.put(PAN, null);
			actions.put(CONN_SELECT, null);
			actions.put(FREE_MOVE_CP, null);
			actions.put(ADD_REMOVE_CP, null);
			actions.put(SELECT_LABEL, null);
			actions.put(LABEL_HOVER, null);
			actions.put(STATIC_MOVE_FREE, null);
			actions.put(DELETE, null);
			actions.put(COPY_PASTE, null);
		}

		@Override
		public void clearAction(String action)
		{
			actions.put(action, null);
		}

		@Override
		public WidgetAction getAction(String action, Canvas canvas)
		{
			if (action.equals(MOVE))
			{
				if (activeMoveAction != null)
				{
					WidgetAction moveAction = null;
					if (activeMoveAction.equals(CanvasData.M_FREE))
					{
						moveAction = getAction(MOVE_FREE, canvas);
					}
					else if (activeMoveAction.equals(CanvasData.M_SNAP))
					{
						moveAction = getAction(MOVE_SNAP, canvas);
					}
					else if (activeMoveAction.equals(CanvasData.M_ALIGN))
					{
						moveAction = getAction(MOVE_ALIGN, canvas);
					}
					else if (activeMoveAction.equals(CanvasData.M_LINES))
					{
						moveAction = getAction(MOVE_LINES, canvas);
					}
					return moveAction;
				}
				if (actions.get(MOVE) == null)
				{
					// in case no move strategy is defined, free move is used by
					// default
					actions.put(MOVE, getAction(MOVE_FREE, canvas));
				}
				return actions.get(MOVE);
			}
			else if (action.equals(MOVE_FREE))
			{
				if (actions.get(MOVE_FREE) == null)
				{
					actions.put(MOVE_FREE, ActionFactory.createMoveAction(ActionFactory.createFreeMoveStrategy(), new MultiMoveProvider(canvas)));
				}
				return actions.get(MOVE_FREE);
			}
			else if (action.equals(MOVE_SNAP))
			{
				if (actions.get(MOVE_SNAP) == null)
				{
					actions.put(MOVE_SNAP, ActionFactory.createMoveAction(ActionFactory.createSnapToGridMoveStrategy(GridWidget.GRID_SIZE, GridWidget.GRID_SIZE), new MultiMoveProvider(canvas)));
				}
				return actions.get(MOVE_SNAP);
			}
			else if (action.equals(MOVE_ALIGN))
			{
				if (actions.get(MOVE_ALIGN) == null)
				{
					actions.put(MOVE_ALIGN, ActionFactory.createAlignWithMultiMoveAction(canvas, new SingleLayerAlignWithWidgetCollector(canvas.getMainLayer(), true), canvas.getInterractionLayer(), ActionFactory.createDefaultAlignWithMoveDecorator(), true));
				}
				return actions.get(MOVE_ALIGN);
			}
			else if (action.equals(MOVE_LINES))
			{
				if (actions.get(MOVE_LINES) == null)
				{
					actions.put(MOVE_LINES, ActionFactory.createMoveAction(ActionFactory.createSnapToLineMoveStrategy(canvas), new MultiMoveProvider(canvas)));																																				// true));
				}
				return actions.get(MOVE_LINES);
			}
			else if (action.equals(CREATE))
			{
				if (actions.get(CREATE) == null)
				{
					actions.put(CREATE, new NodeCreateAction(canvas));
				}
				return actions.get(CREATE);
			}
			else if (action.equals(SELECT))
			{
				if (actions.get(SELECT) == null)
				{
					actions.put(SELECT, ActionFactory.createSelectAction(new NodeSelectProvider(canvas)));
				}
				return actions.get(SELECT);
			}
			else if (action.equals(MULTI_SELECT))
			{
				if (actions.get(MULTI_SELECT) == null)
				{
					actions.put(MULTI_SELECT, ActionFactory.createSelectAction(new NodeMultiSelectProvider(canvas)));
				}
				return actions.get(MULTI_SELECT);
			}
			else if (action.equals(NODE_HOVER))
			{
				if (actions.get(NODE_HOVER) == null)
				{
					actions.put(NODE_HOVER, ActionFactory.createHoverAction(new NodeHoverProvider(canvas)));
				}
				return actions.get(NODE_HOVER);
			}
			else if (action.equals(ALTERNATIVE))
			{
				if (actions.get(ALTERNATIVE) == null)
				{
					actions.put(ALTERNATIVE, ActionFactory.createConnectAction(canvas.getCanvasDecorator().getConnDecoratorAlt(), canvas.getInterractionLayer(), new NodeConnectProvider(canvas)));
				}
				return actions.get(ALTERNATIVE);
			}
			else if (action.equals(SUCCESSOR))
			{
				if (actions.get(SUCCESSOR) == null)
				{
					actions.put(SUCCESSOR, ActionFactory.createConnectAction(canvas.getCanvasDecorator().getConnDecoratorSuc(), canvas.getInterractionLayer(), new NodeConnectProvider(canvas)));
				}
				return actions.get(SUCCESSOR);
			}
			else if (action.equals(RECONNECT))
			{
				if (actions.get(RECONNECT) == null)
				{
					actions.put(RECONNECT, ActionFactory.createReconnectAction(new NodeReconnectProvider(canvas)));
				}
				return actions.get(RECONNECT);
			}
			else if (action.equals(EDITOR))
			{
				if (actions.get(EDITOR) == null)
				{
					actions.put(EDITOR, ActionFactory.createInplaceEditorAction(new LabelTextFieldEditor(canvas)));
				}
				return actions.get(EDITOR);
			}
			else if (action.equals(POPUP_MENU_MAIN))
			{
				if (actions.get(POPUP_MENU_MAIN) == null)
				{
					actions.put(POPUP_MENU_MAIN, ActionFactory.createPopupMenuAction(new CanvasPopupMenu(canvas)));
				}
				return actions.get(POPUP_MENU_MAIN);
			}
			else if (action.equals(RECTANGULAR_SELECT))
			{
				if (actions.get(RECTANGULAR_SELECT) == null)
				{
					actions.put(RECTANGULAR_SELECT, ActionFactory.createRectangularSelectAction(ActionFactory.createDefaultRectangularSelectDecorator(canvas), canvas.getBackgroundLayer(), new CanvasRectangularSelectProvider(canvas)));
				}
				return actions.get(RECTANGULAR_SELECT);
			}
			else if (action.equals(MOUSE_CENTERED_ZOOM))
			{
				if (actions.get(MOUSE_CENTERED_ZOOM) == null)
				{
					actions.put(MOUSE_CENTERED_ZOOM, ActionFactory.createMouseCenteredZoomAction(1.05));
				}
				return actions.get(MOUSE_CENTERED_ZOOM);
			}
			else if (action.equals(PAN))
			{
				if (actions.get(PAN) == null)
				{
					actions.put(PAN, ActionFactory.createPanAction());
				}
				return actions.get(PAN);
			}
			else if (action.equals(CONN_SELECT))
			{
				if (actions.get(CONN_SELECT) == null)
				{
					actions.put(CONN_SELECT, canvas.createSelectAction());
				}
				return actions.get(CONN_SELECT);
			}
			else if (action.equals(FREE_MOVE_CP))
			{
				if (actions.get(FREE_MOVE_CP) == null)
				{
					actions.put(FREE_MOVE_CP, ActionFactory.createFreeMoveControlPointAction());
				}
				return actions.get(FREE_MOVE_CP);
			}
			else if (action.equals(ADD_REMOVE_CP))
			{
				if (actions.get(ADD_REMOVE_CP) == null)
				{
					actions.put(ADD_REMOVE_CP, ActionFactory.createAddRemoveControlPointAction());
				}
				return actions.get(ADD_REMOVE_CP);
			}
			else if (action.equals(SELECT_LABEL))
			{
				if (actions.get(SELECT_LABEL) == null)
				{
					actions.put(SELECT_LABEL, ActionFactory.createSelectAction(new LabelSelectProvider(canvas)));
				}
				return actions.get(SELECT_LABEL);
			}
			else if (action.equals(LABEL_HOVER))
			{
				if (actions.get(LABEL_HOVER) == null)
				{
					actions.put(LABEL_HOVER, ActionFactory.createHoverAction(new LabelHoverProvider(canvas)));
				}
				return actions.get(LABEL_HOVER);
			}
			else if (action.equals(STATIC_MOVE_FREE))
			{
				if (actions.get(STATIC_MOVE_FREE) == null)
				{
					actions.put(STATIC_MOVE_FREE, ActionFactory.createMoveAction());
				}
				return actions.get(STATIC_MOVE_FREE);
			}
			else if (action.equals(DELETE))
			{
				if (actions.get(DELETE) == null)
				{
					actions.put(DELETE, ActionFactory.createDeleteAction(new WidgetDeleteProvider(canvas)));
				}
				return actions.get(DELETE);
			}
			else if (action.equals(COPY_PASTE))
			{
				if (actions.get(COPY_PASTE) == null)
				{
					actions.put(COPY_PASTE, ActionFactory.createCopyPasteAction(new WidgetCopyPasteProvider(canvas)));
				}
				return actions.get(COPY_PASTE);
			}
			return null;
		}

		@Override
		public void update(Observable obs, Object obj)
		{
			if (obs instanceof MoveStrategy)
			{
				activeMoveAction = (String) obj;
			}
		}
	}

	private static WidgetActionRepository war = new WAR();

	public static WidgetActionRepository createRepository()
	{
		war = new WAR();
		return war;
	}

	public static WidgetActionRepository getDefaultRepository()
	{
		return war;
	}
}
