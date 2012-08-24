/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.grview.canvas.action;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.util.EnumSet;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;

import org.grview.canvas.Canvas;
import org.grview.canvas.strategy.SnapToLineMoveStrategy;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.AlignWithMoveDecorator;
import org.netbeans.api.visual.action.AlignWithWidgetCollector;
import org.netbeans.api.visual.action.ConnectDecorator;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.CycleFocusProvider;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.action.HoverProvider;
import org.netbeans.api.visual.action.InplaceEditorProvider;
import org.netbeans.api.visual.action.MoveControlPointProvider;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.ReconnectDecorator;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.action.RectangularSelectDecorator;
import org.netbeans.api.visual.action.RectangularSelectProvider;
import org.netbeans.api.visual.action.ResizeControlPointResolver;
import org.netbeans.api.visual.action.ResizeProvider;
import org.netbeans.api.visual.action.ResizeStrategy;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.visual.action.AcceptAction;
import org.netbeans.modules.visual.action.ActionMapAction;
import org.netbeans.modules.visual.action.AddRemoveControlPointAction;
import org.netbeans.modules.visual.action.AlignWithMoveStrategyProvider;
import org.netbeans.modules.visual.action.AlignWithResizeStrategyProvider;
import org.netbeans.modules.visual.action.CenteredZoomAction;
import org.netbeans.modules.visual.action.ConnectAction;
import org.netbeans.modules.visual.action.CycleFocusAction;
import org.netbeans.modules.visual.action.CycleObjectSceneFocusProvider;
import org.netbeans.modules.visual.action.DefaultRectangularSelectDecorator;
import org.netbeans.modules.visual.action.EditAction;
import org.netbeans.modules.visual.action.ExtendedConnectAction;
import org.netbeans.modules.visual.action.ForwardKeyEventsAction;
import org.netbeans.modules.visual.action.FreeMoveControlPointProvider;
import org.netbeans.modules.visual.action.InplaceEditorAction;
import org.netbeans.modules.visual.action.MouseCenteredZoomAction;
import org.netbeans.modules.visual.action.MouseHoverAction;
import org.netbeans.modules.visual.action.MoveAction;
import org.netbeans.modules.visual.action.MoveControlPointAction;
import org.netbeans.modules.visual.action.ObjectSceneRectangularSelectProvider;
import org.netbeans.modules.visual.action.OrthogonalMoveControlPointProvider;
import org.netbeans.modules.visual.action.PanAction;
import org.netbeans.modules.visual.action.PopupMenuAction;
import org.netbeans.modules.visual.action.ReconnectAction;
import org.netbeans.modules.visual.action.RectangularSelectAction;
import org.netbeans.modules.visual.action.ResizeAction;
import org.netbeans.modules.visual.action.ResizeCornersControlPointResolver;
import org.netbeans.modules.visual.action.SelectAction;
import org.netbeans.modules.visual.action.SingleLayerAlignWithWidgetCollector;
import org.netbeans.modules.visual.action.SnapToGridMoveStrategy;
import org.netbeans.modules.visual.action.SwitchCardProvider;
import org.netbeans.modules.visual.action.TextFieldInplaceEditorProvider;
import org.netbeans.modules.visual.action.TwoStatedMouseHoverAction;
import org.netbeans.modules.visual.action.WheelPanAction;
import org.netbeans.modules.visual.action.ZoomAction;

/**
 * The factory class of all built-in actions. Action creation usually requires
 * some parameter like decorator (cares about the visualization) and provider
 * (cares about the logic of an action).
 * <p>
 * Instances of the built-in actions could be shared by multiple widgets.
 * 
 * @author David Kaspar
 */
public final class ActionFactory
{

	private static final ActionMapAction ACTION_MAP_ACTION = new ActionMapAction(null, null);

	private static final AlignWithMoveDecorator ALIGN_WITH_MOVE_DECORATOR_DEFAULT = new AlignWithMoveDecorator()
	{
		@Override
		public ConnectionWidget createLineWidget(Scene scene)
		{
			ConnectionWidget widget = new ConnectionWidget(scene);
			widget.setStroke(STROKE);
			widget.setForeground(Color.BLUE);
			return widget;
		}
	};

	private static final ConnectDecorator CONNECT_DECORATOR_DEFAULT = new ConnectDecorator()
	{
		@Override
		public ConnectionWidget createConnectionWidget(Scene scene)
		{
			ConnectionWidget widget = new ConnectionWidget(scene);
			widget.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
			return widget;
		}

		@Override
		public Anchor createFloatAnchor(Point location)
		{
			return AnchorFactory.createFixedAnchor(location);
		}

		@Override
		public Anchor createSourceAnchor(Widget sourceWidget)
		{
			return AnchorFactory.createCenterAnchor(sourceWidget);
		}

		@Override
		public Anchor createTargetAnchor(Widget targetWidget)
		{
			return AnchorFactory.createCenterAnchor(targetWidget);
		}
	};

	private static WidgetCopyPasteAction COPY_PASTE_ACTION;

	private static final WidgetAction CYCLE_FOCUS_OBJECT_SCENE = createCycleFocusAction(new CycleObjectSceneFocusProvider());

	private static WidgetDeleteAction DELETE_ACTION;

	private static final WidgetAction MOVE_ACTION = createMoveAction(null, null);

	private static final WidgetAction MOVE_CONTROL_POINT_ACTION_FREE = createMoveControlPointAction(createFreeMoveControlPointProvider());

	private static final WidgetAction MOVE_CONTROL_POINT_ACTION_ORTHOGONAL = createMoveControlPointAction(createOrthogonalMoveControlPointProvider());

	private static final MoveControlPointProvider MOVE_CONTROL_POINT_PROVIDER_FREE = new FreeMoveControlPointProvider();

	private static final MoveControlPointProvider MOVE_CONTROL_POINT_PROVIDER_ORTHOGONAL = new OrthogonalMoveControlPointProvider();

	private static final MoveProvider MOVE_PROVIDER_DEFAULT = new MoveProvider()
	{
		@Override
		public Point getOriginalLocation(Widget widget)
		{
			return widget.getPreferredLocation();
		}

		@Override
		public void movementFinished(Widget widget)
		{
		}

		@Override
		public void movementStarted(Widget widget)
		{
		}

		@Override
		public void setNewLocation(Widget widget, Point location)
		{
			widget.setPreferredLocation(location);
		}
	};

	private static final MoveStrategy MOVE_STRATEGY_FREE = new MoveStrategy()
	{
		@Override
		public Point locationSuggested(Widget widget, Point originalLocation, Point suggestedLocation)
		{
			return suggestedLocation;
		}
	};

	private static final PanAction PAN_ACTION = new PanAction();

	private static final ReconnectDecorator RECONNECT_DECORATOR_DEFAULT = new ReconnectDecorator()
	{
		@Override
		public Anchor createFloatAnchor(Point location)
		{
			return AnchorFactory.createFixedAnchor(location);
		}

		@Override
		public Anchor createReplacementWidgetAnchor(Widget replacementWidget)
		{
			return AnchorFactory.createCenterAnchor(replacementWidget);
		}
	};

	private static final WidgetAction RESIZE_ACTION = createResizeAction(null, null);

	private static final ResizeControlPointResolver RESIZE_CONTROL_POINT_RESOLVER_DEFAULT = new ResizeCornersControlPointResolver();

	private static final ResizeProvider RESIZE_PROVIDER_DEFAULT = new ResizeProvider()
	{
		@Override
		public void resizingFinished(Widget widget)
		{
		}

		@Override
		public void resizingStarted(Widget widget)
		{
		}
	};

	private static final ResizeStrategy RESIZE_STRATEGY_FREE = new ResizeStrategy()
	{
		@Override
		public Rectangle boundsSuggested(Widget widget, Rectangle originalBounds, Rectangle suggestedBounds, ResizeProvider.ControlPoint controlPoint)
		{
			return suggestedBounds;
		}
	};

	private static final BasicStroke STROKE = new BasicStroke(1.0f, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_BUTT, 5.0f, new float[]{ 6.0f, 3.0f }, 0.0f);

	private static final WheelPanAction WHEEL_PAN_ACTION = new WheelPanAction();

	private ActionFactory()
	{
	}

	/**
	 * Creates a accept action with a specified accept logic provider.
	 * 
	 * @param provider
	 *            the accept logic provider
	 * @return the accept action
	 */
	public static WidgetAction createAcceptAction(AcceptProvider provider)
	{
		assert provider != null;
		return new AcceptAction(provider);
	}

	/**
	 * Creates an action which handles keys and popup menu. The key-to-action
	 * binding is obtained from the InputMap of a view JComponent of a scene.
	 * The actions for popup menu are obtained from the ActionMap of a view
	 * JComponent of a scene.
	 * 
	 * @return the action-map action
	 */
	public static WidgetAction createActionMapAction()
	{
		return ACTION_MAP_ACTION;
	}

	/**
	 * Creates an action which handles keys and popup menu. The key-to-action
	 * binding and popup menu items are obtained from specified ActionMap and
	 * InputMap.
	 * 
	 * @param inputMap
	 *            the input map
	 * @param actionMap
	 *            the action map
	 * @return the action-map action
	 */
	public static WidgetAction createActionMapAction(InputMap inputMap, ActionMap actionMap)
	{
		assert inputMap != null && actionMap != null;
		return new ActionMapAction(inputMap, actionMap);
	}

	/**
	 * Creates a add-remove control point action with a default sensitivity. The
	 * action is assigned to a FreeConnectionWidget.
	 * 
	 * @return the add-remove control point action
	 */
	public static WidgetAction createAddRemoveControlPointAction()
	{
		return createAddRemoveControlPointAction(3.0, 5.0);
	}

	/**
	 * Creates a add-remove control point action with a specified sensitivity.
	 * 
	 * @param createSensitivity
	 *            the create sensitivity
	 * @param deleteSensitivity
	 *            the delete sensitivity
	 * @return the add-remove control point action
	 */
	public static WidgetAction createAddRemoveControlPointAction(double createSensitivity, double deleteSensitivity)
	{
		return createAddRemoveControlPointAction(createSensitivity, deleteSensitivity, null);
	}

	/**
	 * Creates a add-remove control point action with a specified sensitivity.
	 * 
	 * @param createSensitivity
	 *            the create sensitivity
	 * @param deleteSensitivity
	 *            the delete sensitivity
	 * @param routingPolicy
	 *            the routing policy that is automatically set to a connection
	 *            widget with control points modified by this action; if null,
	 *            then routing policy is not set
	 * @return the add-remove control point action
	 * @since 2.9
	 */
	public static WidgetAction createAddRemoveControlPointAction(double createSensitivity, double deleteSensitivity, ConnectionWidget.RoutingPolicy routingPolicy)
	{
		return new AddRemoveControlPointAction(createSensitivity, deleteSensitivity, routingPolicy);
	}

	/**
	 * Creates a align-with move action.
	 * 
	 * @param collector
	 *            the collector of objects that the alignment is checked
	 *            against.
	 * @param interractionLayer
	 *            the interraction layer where the align-with hint lines are
	 *            placed
	 * @param decorator
	 *            the align-with move decorator
	 * @return the align-with move action
	 */
	public static WidgetAction createAlignWithMoveAction(AlignWithWidgetCollector collector, LayerWidget interractionLayer, AlignWithMoveDecorator decorator)
	{
		return createAlignWithMoveAction(collector, interractionLayer, decorator, true);
	}

	/**
	 * Creates a align-with move action.
	 * 
	 * @param collector
	 *            the collector of objects that the alignment is checked
	 *            against.
	 * @param interractionLayer
	 *            the interraction layer where the align-with hint lines are
	 *            placed
	 * @param decorator
	 *            the align-with move decorator
	 * @param outerBounds
	 *            if true, then the align-with is check against whole bounds of
	 *            widgets in collection layer; if false, then the align-with is
	 *            check against client area (widget bounds without border insets
	 * @return the align-with move action
	 * @since 2.7
	 */
	public static WidgetAction createAlignWithMoveAction(AlignWithWidgetCollector collector, LayerWidget interractionLayer, AlignWithMoveDecorator decorator, boolean outerBounds)
	{
		assert collector != null && interractionLayer != null && decorator != null;
		AlignWithMoveStrategyProvider sp = new AlignWithMoveStrategyProvider(collector, interractionLayer, decorator, outerBounds);
		return createMoveAction(sp, sp);
	}

	/**
	 * Creates a align-with move action.
	 * 
	 * @param collectionLayer
	 *            the layer with objects that the alignment is checked against.
	 * @param interractionLayer
	 *            the interraction layer where the align-with hint lines are
	 *            placed
	 * @param decorator
	 *            the align-with move decorator
	 * @return the align-with move action
	 */
	public static WidgetAction createAlignWithMoveAction(LayerWidget collectionLayer, LayerWidget interractionLayer, AlignWithMoveDecorator decorator)
	{
		return createAlignWithMoveAction(collectionLayer, interractionLayer, decorator, true);
	}

	/**
	 * Creates a align-with move action.
	 * 
	 * @param collectionLayer
	 *            the layer with objects that the alignment is checked against.
	 * @param interractionLayer
	 *            the interraction layer where the align-with hint lines are
	 *            placed
	 * @param decorator
	 *            the align-with move decorator
	 * @param outerBounds
	 *            if true, then the align-with is check against whole bounds of
	 *            widgets in collection layer; if false, then the align-with is
	 *            check against client area (widget bounds without border insets
	 * @return the align-with move action
	 * @since 2.7
	 */
	public static WidgetAction createAlignWithMoveAction(LayerWidget collectionLayer, LayerWidget interractionLayer, AlignWithMoveDecorator decorator, boolean outerBounds)
	{
		assert collectionLayer != null;
		return createAlignWithMoveAction(new SingleLayerAlignWithWidgetCollector(collectionLayer, outerBounds), interractionLayer, decorator != null ? decorator : ALIGN_WITH_MOVE_DECORATOR_DEFAULT, outerBounds);
	}

	public static WidgetAction createAlignWithMultiMoveAction(Canvas canvas, AlignWithWidgetCollector collector, LayerWidget interractionLayer, AlignWithMoveDecorator decorator, boolean outerBounds)
	{
		assert collector != null && interractionLayer != null && decorator != null;
		AlignWithMultiMoveProvider sp = new AlignWithMultiMoveProvider(canvas, collector, interractionLayer, decorator, outerBounds);
		return createMoveAction(sp, sp);
	}

	/**
	 * Creates a align-with resize action.
	 * 
	 * @param collector
	 *            the collector of objects that the alignment is checked
	 *            against.
	 * @param interractionLayer
	 *            the interraction layer where the align-with hint lines are
	 *            placed
	 * @param decorator
	 *            the align-with move decorator
	 * @return the align-with resize action
	 */
	public static WidgetAction createAlignWithResizeAction(AlignWithWidgetCollector collector, LayerWidget interractionLayer, AlignWithMoveDecorator decorator)
	{
		return createAlignWithResizeAction(collector, interractionLayer, decorator, true);
	}

	/**
	 * Creates a align-with resize action.
	 * 
	 * @param collector
	 *            the collector of objects that the alignment is checked
	 *            against.
	 * @param interractionLayer
	 *            the interraction layer where the align-with hint lines are
	 *            placed
	 * @param decorator
	 *            the align-with move decorator
	 * @param outerBounds
	 *            if true, then the align-with is check against whole bounds of
	 *            widgets in collection layer; if false, then the align-with is
	 *            check against client area (widget bounds without border insets
	 * @return the align-with resize action
	 * @since 2.7
	 */
	public static WidgetAction createAlignWithResizeAction(AlignWithWidgetCollector collector, LayerWidget interractionLayer, AlignWithMoveDecorator decorator, boolean outerBounds)
	{
		assert collector != null && interractionLayer != null && decorator != null;
		AlignWithResizeStrategyProvider sp = new AlignWithResizeStrategyProvider(collector, interractionLayer, decorator, outerBounds);
		return createResizeAction(sp, sp);
	}

	/**
	 * Creates a align-with resize action.
	 * 
	 * @param collectionLayer
	 *            the layer with objects that the alignment is checked against.
	 * @param interractionLayer
	 *            the interraction layer where the align-with hint lines are
	 *            placed
	 * @param decorator
	 *            the align-with move decorator
	 * @return the align-with resize action
	 */
	public static WidgetAction createAlignWithResizeAction(LayerWidget collectionLayer, LayerWidget interractionLayer, AlignWithMoveDecorator decorator)
	{
		return createAlignWithResizeAction(collectionLayer, interractionLayer, decorator, true);
	}

	/**
	 * Creates a align-with resize action.
	 * 
	 * @param collectionLayer
	 *            the layer with objects that the alignment is checked against.
	 * @param interractionLayer
	 *            the interraction layer where the align-with hint lines are
	 *            placed
	 * @param decorator
	 *            the align-with move decorator
	 * @param outerBounds
	 *            if true, then the align-with is check against whole bounds of
	 *            widgets in collection layer; if false, then the align-with is
	 *            check against client area (widget bounds without border insets
	 * @return the align-with resize action
	 * @since 2.7
	 */
	public static WidgetAction createAlignWithResizeAction(LayerWidget collectionLayer, LayerWidget interractionLayer, AlignWithMoveDecorator decorator, boolean outerBounds)
	{
		assert collectionLayer != null;
		return createAlignWithResizeAction(new SingleLayerAlignWithWidgetCollector(collectionLayer, outerBounds), interractionLayer, decorator != null ? decorator : ALIGN_WITH_MOVE_DECORATOR_DEFAULT, outerBounds);
	}

	/**
	 * Creates a action that controls a zoom factor of a scene where the action
	 * is assigned. During zooming the view will be centered still.
	 * 
	 * @param zoomMultiplier
	 *            the zoom multiplier
	 * @return the zoom action
	 */
	public static WidgetAction createCenteredZoomAction(double zoomMultiplier)
	{
		return new CenteredZoomAction(zoomMultiplier);
	}

	/**
	 * Creates a connect action with a specific decorator.
	 * 
	 * @param decorator
	 *            the connect decorator; if null, then the default decorator is
	 *            used
	 * @param interractionLayer
	 *            the interraction layer where the temporary connection is
	 *            visualization placed.
	 * @param provider
	 *            the connect logic provider
	 * @return the connect action
	 */
	public static ConnectAction createConnectAction(ConnectDecorator decorator, LayerWidget interractionLayer, ConnectProvider provider)
	{
		assert interractionLayer != null && provider != null;
		return new ConnectAction(decorator != null ? decorator : createDefaultConnectDecorator(), interractionLayer, provider);
	}

	/**
	 * Creates a connect action with a default decorator.
	 * 
	 * @param interractionLayer
	 *            the interraction layer where the temporary connection is
	 *            visualization placed.
	 * @param provider
	 *            the connect logic provider
	 * @return the connect action
	 */
	public static ConnectAction createConnectAction(LayerWidget interractionLayer, ConnectProvider provider)
	{
		return createConnectAction(null, interractionLayer, provider);
	}

	/**
	 * Creates a copy/paste/cut action that acts on the selectes elements on the
	 * scene
	 * 
	 * @param wcpp
	 *            a provider
	 * @return a copy paste action
	 */
	public static WidgetCopyPasteAction createCopyPasteAction(WidgetCopyPasteProvider wcpp)
	{
		COPY_PASTE_ACTION = new WidgetCopyPasteAction(wcpp);
		return COPY_PASTE_ACTION;
	}

	/**
	 * Creates a cycle focus action.
	 * 
	 * @param provider
	 *            the cycle focus provider
	 * @return the cycle focus action
	 */
	public static WidgetAction createCycleFocusAction(CycleFocusProvider provider)
	{
		assert provider != null;
		return new CycleFocusAction(provider);
	}

	/**
	 * 
	 * Creates a cycle focus action which switches focused object on a object
	 * scene.
	 * 
	 * @return the cycle object scene focus action
	 */
	public static WidgetAction createCycleObjectSceneFocusAction()
	{
		return CYCLE_FOCUS_OBJECT_SCENE;
	}

	/**
	 * Creates a default align-with move decorator.
	 * 
	 * @return the move decorator
	 */
	public static AlignWithMoveDecorator createDefaultAlignWithMoveDecorator()
	{
		return ALIGN_WITH_MOVE_DECORATOR_DEFAULT;
	}

	/**
	 * Creates a default connect decorator
	 * 
	 * @return the connect decorator
	 */
	public static ConnectDecorator createDefaultConnectDecorator()
	{
		return CONNECT_DECORATOR_DEFAULT;
	}

	/**
	 * Creates a default move provider where the logic controls the
	 * preferredLocation of a widget where the action is assigned to.
	 * 
	 * @return the move provider
	 */
	public static MoveProvider createDefaultMoveProvider()
	{
		return MOVE_PROVIDER_DEFAULT;
	}

	/**
	 * Creates a default reconnect decorator
	 * 
	 * @return the reconnect decorator
	 */
	public static ReconnectDecorator createDefaultReconnectDecorator()
	{
		return RECONNECT_DECORATOR_DEFAULT;
	}

	/**
	 * Creates a default rectangular select decorator.
	 * 
	 * @param scene
	 *            the scene where an action is used
	 * @return the rectangular select decorator
	 */
	public static RectangularSelectDecorator createDefaultRectangularSelectDecorator(Scene scene)
	{
		assert scene != null;
		return new DefaultRectangularSelectDecorator(scene);
	}

	/**
	 * Creates a default resize control point resolver which is used in resize
	 * action.
	 * 
	 * @return the resize control point resolver
	 */
	public static ResizeControlPointResolver createDefaultResizeControlPointResolver()
	{
		return RESIZE_CONTROL_POINT_RESOLVER_DEFAULT;
	}

	/**
	 * Creates a default resize provider which controls preferredBounds of a
	 * widget where an action is assigned.
	 * 
	 * @return the resize provider
	 */
	public static ResizeProvider createDefaultResizeProvider()
	{
		return RESIZE_PROVIDER_DEFAULT;
	}

	/**
	 * Creates a delete action that delete selected nodes on the scene
	 * 
	 * @param wdp
	 *            a provider to the delete action
	 * @return the delete action
	 */
	public static WidgetDeleteAction createDeleteAction(WidgetDeleteProvider wdp)
	{
		DELETE_ACTION = new WidgetDeleteAction(wdp);
		return DELETE_ACTION;
	}

	/**
	 * Creates an edit action.
	 * 
	 * @param provider
	 *            the edit logic provider.
	 * @return the edit action
	 */
	public static WidgetAction createEditAction(EditProvider provider)
	{
		assert provider != null;
		return new EditAction(provider);
	}

	/**
	 * Creates an extended connect action with a specific decorator. User can
	 * invoke the action only with pressed CTRL key.
	 * 
	 * @param decorator
	 *            the connect decorator; if null, then the default decorator is
	 *            used
	 * @param interractionLayer
	 *            the interraction layer where the temporary connection is
	 *            visualization placed.
	 * @param provider
	 *            the connect logic provider
	 * @return the extended connect action
	 */
	public static WidgetAction createExtendedConnectAction(ConnectDecorator decorator, LayerWidget interractionLayer, ConnectProvider provider)
	{
		assert interractionLayer != null && provider != null;
		return new ExtendedConnectAction(decorator != null ? decorator : createDefaultConnectDecorator(), interractionLayer, provider, InputEvent.CTRL_MASK);
	}

	/**
	 * Creates an extended connect action with a specific decorator which can be
	 * invoked only with specified modifiers (usually it is
	 * <code>MouseEvent.CTRL_MASK</code>).
	 * 
	 * @param decorator
	 *            the connect decorator; if null, then the default decorator is
	 *            used
	 * @param interractionLayer
	 *            the interraction layer where the temporary connection is
	 *            visualization placed.
	 * @param provider
	 *            the connect logic provider
	 * @param modifiers
	 *            the invocation modifiers
	 * @return the extended connect action
	 * @since 2.3
	 */
	public static WidgetAction createExtendedConnectAction(ConnectDecorator decorator, LayerWidget interractionLayer, ConnectProvider provider, int modifiers)
	{
		assert interractionLayer != null && provider != null && modifiers != 0;
		return new ExtendedConnectAction(decorator != null ? decorator : createDefaultConnectDecorator(), interractionLayer, provider, modifiers);
	}

	/**
	 * Creates an extended connect action with a default decorator. User can
	 * invoke the action only with pressed CTRL key.
	 * 
	 * @param interractionLayer
	 *            the interraction layer where the temporary connection is
	 *            visualization placed.
	 * @param provider
	 *            the connect logic provider
	 * @return the extended connect action
	 */
	public static WidgetAction createExtendedConnectAction(LayerWidget interractionLayer, ConnectProvider provider)
	{
		return createExtendedConnectAction(null, interractionLayer, provider);
	}

	/**
	 * This action is used for forwarding key events to another widget. Usually
	 * it could be used to forwarding a key event from a node widget to
	 * node-label widget when a scene is using
	 * process-focused-widget-and-its-parents event processing type.
	 * 
	 * @param forwardToWidget
	 *            the widget to which events are forwarded
	 * @param forwardToTool
	 *            the tool to which events are forwarded; if null, then default
	 *            action chain is used
	 * @return the forward key events action; assign this action to widget from
	 *         which the forwarding should be done
	 */
	public static WidgetAction createForwardKeyEventsAction(Widget forwardToWidget, String forwardToTool)
	{
		assert forwardToWidget != null;
		return new ForwardKeyEventsAction(forwardToWidget, forwardToTool);
	}

	/**
	 * Creates a move control point (of a connection widget) action with no
	 * movement restriction.
	 * 
	 * @return the move control point action
	 */
	public static WidgetAction createFreeMoveControlPointAction()
	{
		return MOVE_CONTROL_POINT_ACTION_FREE;
	}

	/**
	 * Creates a free (without any restriction) move control point (of a
	 * ConnectionWidget) provider.
	 * 
	 * @return the move control point action
	 */
	public static MoveControlPointProvider createFreeMoveControlPointProvider()
	{
		return MOVE_CONTROL_POINT_PROVIDER_FREE;
	}

	/**
	 * Creates a free (without any restriction) move strategy
	 * 
	 * @return the move strategy
	 */
	public static MoveStrategy createFreeMoveStrategy()
	{
		return MOVE_STRATEGY_FREE;
	}

	/**
	 * Creates a free (without any restriction) resize strategy
	 * 
	 * @return the resize strategy
	 */
	public static ResizeStrategy createFreeResizeStategy()
	{
		return RESIZE_STRATEGY_FREE;
	}

	/**
	 * Creates a hover action using a hover provider. Usually the
	 * Scene.createWidgetHoverAction and ObjectScene.createObjectHoverAction
	 * methods are used instead of this method.
	 * 
	 * @param provider
	 *            the hover logic provider
	 * @return the hover action
	 */
	public static WidgetAction createHoverAction(HoverProvider provider)
	{
		assert provider != null;
		return new MouseHoverAction(provider);
	}

	/**
	 * Creates a hover action using a two-stated hover provider. Usually the
	 * Scene.createWidgetHoverAction and ObjectScene.createObjectHoverAction
	 * methods are used instead of this method.
	 * 
	 * @param provider
	 *            the two-stated hover logic provider
	 * @return the hover action.
	 */
	public static WidgetAction createHoverAction(TwoStateHoverProvider provider)
	{
		assert provider != null;
		return new TwoStatedMouseHoverAction(provider);
	}

	/**
	 * Creates an in-place editor action for a specific provider.
	 * 
	 * @param provider
	 *            the in-place editor provider
	 * @return the in-place editor action
	 */
	public static <C extends JComponent> WidgetAction createInplaceEditorAction(InplaceEditorProvider<C> provider)
	{
		return new InplaceEditorAction<C>(provider);
	}

	/**
	 * Creates a text in-place editor action visualized using JTextField.
	 * 
	 * @param editor
	 *            the editor logic
	 * @return the in-place editor action
	 */
	public static WidgetAction createInplaceEditorAction(TextFieldInplaceEditor editor)
	{
		return createInplaceEditorAction(editor, null);
	}

	/**
	 * Creates a text in-place editor action visualized using JTextField.
	 * 
	 * @param editor
	 *            the editor logic
	 * @param expansionDirections
	 *            the expansion directions
	 * @return the in-place editor action
	 */
	public static WidgetAction createInplaceEditorAction(TextFieldInplaceEditor editor, EnumSet<InplaceEditorProvider.ExpansionDirection> expansionDirections)
	{
		return createInplaceEditorAction(new TextFieldInplaceEditorProvider(editor, expansionDirections));
	}

	/**
	 * Creates a action that controls a zoom factor of a scene where the action
	 * is assigned. During zooming the view will be centered to the mouse
	 * cursor.
	 * 
	 * @param zoomMultiplier
	 *            the zoom multiplier
	 * @return the zoom action
	 * @since 2.3
	 */
	public static WidgetAction createMouseCenteredZoomAction(double zoomMultiplier)
	{
		return new MouseCenteredZoomAction(zoomMultiplier);
	}

	/**
	 * Creates a move action with a default (free) strategy. The action provides
	 * movement for a widget where it is assigned.
	 * 
	 * @return the move action
	 */
	public static WidgetAction createMoveAction()
	{
		return MOVE_ACTION;
	}

	/**
	 * Creates a move action with a specified strategy and provider.
	 * 
	 * @param strategy
	 *            the move strategy; if null, then default (free) move strategy
	 *            is used.
	 * @param provider
	 *            the move logic provider; if null, then defaual move logic
	 *            provider is used (provides movement for a widget where it is
	 *            assigned).
	 * @return the move action
	 */
	public static WidgetAction createMoveAction(MoveStrategy strategy, MoveProvider provider)
	{
		return new MoveAction(strategy != null ? strategy : createFreeMoveStrategy(), provider != null ? provider : createDefaultMoveProvider());
	}

	/**
	 * Creates a move control point (of a connection widget) action with a
	 * specified provider.
	 * 
	 * @param provider
	 *            the move control point provider
	 * @return the move control point action
	 */
	public static WidgetAction createMoveControlPointAction(MoveControlPointProvider provider)
	{
		return createMoveControlPointAction(provider, null);
	}

	/**
	 * Creates a move control point (of a connection widget) action with a
	 * specified provider.
	 * 
	 * @param provider
	 *            the move control point provider
	 * @param routingPolicy
	 *            the routing policy that is automatically set to a connection
	 *            widget with control points modified by this action; if null,
	 *            then routing policy is not set
	 * @return the move control point action
	 * @since 2.9
	 */
	public static WidgetAction createMoveControlPointAction(MoveControlPointProvider provider, ConnectionWidget.RoutingPolicy routingPolicy)
	{
		assert provider != null;
		return new MoveControlPointAction(provider, routingPolicy);
	}

	/**
	 * Creates a rectangular select provider which controls a selection of an
	 * object scene.
	 * 
	 * @param scene
	 *            the object scene where an action is used
	 * @return the rectangular select provider
	 */
	public static RectangularSelectProvider createObjectSceneRectangularSelectProvider(ObjectScene scene)
	{
		assert scene != null;
		return new ObjectSceneRectangularSelectProvider(scene);
	}

	/**
	 * Creates a move control point (of a connection widget) action with is used
	 * at ConnectionWidget with OrthogonalSearchRouter.
	 * 
	 * @return the move control point action
	 */
	public static WidgetAction createOrthogonalMoveControlPointAction()
	{
		return MOVE_CONTROL_POINT_ACTION_ORTHOGONAL;
	}

	/**
	 * Creates a orthogonal move control point provider which is usually used
	 * with ConnectionWidget with OrthogonalSearchRouter.
	 * 
	 * @return the move control point provider
	 */
	public static MoveControlPointProvider createOrthogonalMoveControlPointProvider()
	{
		return MOVE_CONTROL_POINT_PROVIDER_ORTHOGONAL;
	}

	/**
	 * Creates a scene view panning action.
	 * 
	 * @return the pan action
	 */
	public static WidgetAction createPanAction()
	{
		return PAN_ACTION;
	}

	/**
	 * Creates a popup menu action with a speicied provider.
	 * 
	 * @param provider
	 *            the popup menu provider
	 * @return the popup menu action
	 */
	public static WidgetAction createPopupMenuAction(final PopupMenuProvider provider)
	{
		assert provider != null;
		return new PopupMenuAction(provider);
	}

	/**
	 * Creates a reconnect action with a specific decorator and logic provider.
	 * 
	 * @param decorator
	 *            the reccont decorator
	 * @param provider
	 *            the reconnect logic provider
	 * @return the reconnect action
	 */
	public static WidgetAction createReconnectAction(ReconnectDecorator decorator, ReconnectProvider provider)
	{
		return new ReconnectAction(decorator != null ? decorator : createDefaultReconnectDecorator(), provider);
	}

	/**
	 * Creates a reconnect action with a default decorator.
	 * 
	 * @param provider
	 *            the reconnect logic provider
	 * @return the reconnect action
	 */
	public static WidgetAction createReconnectAction(ReconnectProvider provider)
	{
		return createReconnectAction(null, provider);
	}

	/**
	 * Creates a rectangular select action for a specified object scene with a
	 * default decorator.
	 * 
	 * @param scene
	 *            the object scene which the selection will be controlled by the
	 *            action
	 * @param interractionLayer
	 *            the interraction layer where the selection rectangle will be
	 *            visualized
	 * @return the rectangular select action
	 */
	public static WidgetAction createRectangularSelectAction(ObjectScene scene, LayerWidget interractionLayer)
	{
		assert scene != null;
		return createRectangularSelectAction(ActionFactory.createDefaultRectangularSelectDecorator(scene), interractionLayer, ActionFactory.createObjectSceneRectangularSelectProvider(scene));
	}

	/**
	 * Creates a rectangular select action with a specified decorator and logic
	 * provider.
	 * 
	 * @param decorator
	 *            the rectangular select decorator
	 * @param interractionLayer
	 *            the interraction layer where the selection rectangle will be
	 *            visualized
	 * @param provider
	 *            the rectangular select logic provider
	 * @return the rectangular select action
	 */
	public static WidgetAction createRectangularSelectAction(RectangularSelectDecorator decorator, LayerWidget interractionLayer, RectangularSelectProvider provider)
	{
		assert decorator != null && interractionLayer != null && provider != null;
		return new RectangularSelectAction(decorator, interractionLayer, provider);
	}

	/**
	 * Creates a resize action with a default (free without any restriction)
	 * strategy and default logic provider (the action affect preferredBounds of
	 * a widget where it is assigned) default resize control point resolver.
	 * 
	 * @return the resize action
	 */
	public static WidgetAction createResizeAction()
	{
		return RESIZE_ACTION;
	}

	/**
	 * Creates a resize action with a specified resize strategy and provider.
	 * 
	 * @param strategy
	 *            the resize strategy; if null, then the default (free without
	 *            any restriction) strategy is used
	 * @param resolver
	 *            the resize control point resolver; if null, then the default
	 *            (points are at corners and center of edges) is used
	 * @param provider
	 *            the resize logic provider; if null, then the default logic
	 *            provider is used (the action affect preferredBounds of a
	 *            widget where it is assigned)
	 * @return the resize action
	 */
	public static WidgetAction createResizeAction(ResizeStrategy strategy, ResizeControlPointResolver resolver, ResizeProvider provider)
	{
		return new ResizeAction(strategy != null ? strategy : createFreeResizeStategy(), resolver != null ? resolver : createDefaultResizeControlPointResolver(), provider != null ? provider : createDefaultResizeProvider());
	}

	/**
	 * Creates a resize action with a specified resize strategy and provider and
	 * default resize control point resolver.
	 * 
	 * @param strategy
	 *            the resize strategy; if null, then the default (free without
	 *            any restriction) strategy is used
	 * @param provider
	 *            the resize logic provider; if null, then the default logic
	 *            provider is used (the action affect preferredBounds of a
	 *            widget where it is assigned)
	 * @return the resize action
	 */
	public static WidgetAction createResizeAction(ResizeStrategy strategy, ResizeProvider provider)
	{
		return createResizeAction(strategy, null, provider);
	}

	/**
	 * Creates a select action. Usually the ObjectScene.createSelectAction
	 * method is used instead of this method.
	 * 
	 * @param provider
	 *            the select logic provider
	 * @return the select action
	 */
	public static WidgetAction createSelectAction(SelectProvider provider)
	{
		assert provider != null;
		return new SelectAction(provider);
	}

	/**
	 * Creates a snap-to-grid move strategy.
	 * 
	 * @param horizontalGridSize
	 *            the horizontal grid size
	 * @param verticalGridSize
	 *            the vertical grid size
	 * @return the move strategy
	 */
	public static MoveStrategy createSnapToGridMoveStrategy(int horizontalGridSize, int verticalGridSize)
	{
		assert horizontalGridSize > 0 && verticalGridSize > 0;
		return new SnapToGridMoveStrategy(horizontalGridSize, verticalGridSize);
	}

	public static MoveStrategy createSnapToLineMoveStrategy(Canvas canvas)
	{
		assert canvas != null;
		return new SnapToLineMoveStrategy(LineProvider.getInstance(canvas));
	}

	/**
	 * Creates a switch card action with controls an active card of a widget
	 * where a card layout is used.
	 * 
	 * @param cardLayoutWidget
	 *            the widget where a card layout is used
	 * @return the switch card action
	 */
	public static WidgetAction createSwitchCardAction(Widget cardLayoutWidget)
	{
		assert cardLayoutWidget != null;
		return new SelectAction(new SwitchCardProvider(cardLayoutWidget));
	}

	/**
	 * Creates a scene view panning action using mouse-wheel.
	 * 
	 * @return the wheel pan action
	 * @since 2.7
	 */
	public static WidgetAction createWheelPanAction()
	{
		return WHEEL_PAN_ACTION;
	}

	/**
	 * Creates a action that controls a zoom factor of a scene where the action
	 * is assigned.
	 * 
	 * @return the zoom action
	 */
	public static WidgetAction createZoomAction()
	{
		return createZoomAction(1.2, true);
	}

	/**
	 * Creates a action that controls a zoom factor of a scene where the action
	 * is assigned.
	 * 
	 * @param zoomMultiplier
	 *            the zoom multiplier of each zoom step
	 * @param animated
	 *            if true, then the zoom factor changed is animated
	 * @return the zoom action
	 */
	public static WidgetAction createZoomAction(double zoomMultiplier, boolean animated)
	{
		return new ZoomAction(zoomMultiplier, animated);
	}

	/**
	 * Returns an editor controller for a specified inplace-editor-action
	 * created by <code>ActionFactory.createInplaceEditorAction</code> method.
	 * 
	 * @param inplaceEditorAction
	 *            the inplace-editor action
	 * @return the editor controller
	 */
	public static InplaceEditorProvider.EditorController getInplaceEditorController(WidgetAction inplaceEditorAction)
	{
		return (InplaceEditorProvider.EditorController) inplaceEditorAction;
	}

}