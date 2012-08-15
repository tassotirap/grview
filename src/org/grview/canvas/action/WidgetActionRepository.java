package org.grview.canvas.action;

import java.util.Observer;

import org.grview.canvas.Canvas;
import org.netbeans.api.visual.action.WidgetAction;

public interface WidgetActionRepository extends Observer
{

	public final static String CREATE = "Create";
	public final static String SELECT = "Select";
	public final static String MULTI_SELECT = "Multi Select";
	public final static String NODE_HOVER = "NodeHover";
	public final static String ALTERNATIVE = "Alternative";
	public final static String SUCCESSOR = "Successor";
	public final static String RECONNECT = "Reconnect";
	public final static String EDITOR = "Editor";
	public final static String MOVE = "Move";
	public final static String MOVE_FREE = "MoveFree";
	public final static String MOVE_SNAP = "MoveSnap";
	public final static String MOVE_ALIGN = "MoveAlign";
	public final static String MOVE_LINES = "MoveLines";
	public final static String POPUP_MENU_MAIN = "PopupMenuMain";
	public final static String RECTANGULAR_SELECT = "RectangularSelect";
	public final static String MOUSE_CENTERED_ZOOM = "MouseCenteredZoom";
	public final static String PAN = "Pan";
	public final static String CONN_SELECT = "ConnSelect";
	public final static String FREE_MOVE_CP = "FreeMoveCP";
	public final static String ADD_REMOVE_CP = "AddRemoveCP";
	public final static String SELECT_LABEL = "SelectLabel";
	public final static String LABEL_HOVER = "LabelHover";
	public final static String STATIC_MOVE_FREE = "StaticMoveFree";
	public final static String DELETE = "Delete";
	public final static String COPY_PASTE = "CopyPaste";

	public abstract void clearAction(String action);

	public abstract WidgetAction getAction(String action, Canvas canvas);

}
