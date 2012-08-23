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

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.netbeans.api.visual.action.AlignWithMoveDecorator;
import org.netbeans.api.visual.action.AlignWithWidgetCollector;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.visual.action.AlignWithSupport;

public final class AlignWithMultiMoveProvider extends AlignWithSupport implements MoveStrategy, MoveProvider
{

	private boolean outerBounds;
	private MultiMoveProvider mmp;

	public AlignWithMultiMoveProvider(Canvas canvas, AlignWithWidgetCollector collector, LayerWidget interractionLayer, AlignWithMoveDecorator decorator, boolean outerBounds)
	{
		super(collector, interractionLayer, decorator);
		this.outerBounds = outerBounds;
		mmp = new MultiMoveProvider(CanvasFactory.getCanvas());
	}

	@Override
	public Point getOriginalLocation(Widget widget)
	{
		return mmp.getOriginalLocation(widget);
	}

	@Override
	public Point locationSuggested(Widget widget, Point originalLocation, Point suggestedLocation)
	{
		Point widgetLocation = widget.getLocation();
		Rectangle widgetBounds = outerBounds ? widget.getBounds() : widget.getClientArea();
		Rectangle bounds = widget.convertLocalToScene(widgetBounds);
		bounds.translate(suggestedLocation.x - widgetLocation.x, suggestedLocation.y - widgetLocation.y);
		Insets insets = widget.getBorder().getInsets();
		if (!outerBounds)
		{
			suggestedLocation.x += insets.left;
			suggestedLocation.y += insets.top;
		}
		Point point = super.locationSuggested(widget, bounds, suggestedLocation, true, true, true, true);
		if (!outerBounds)
		{
			point.x -= insets.left;
			point.y -= insets.top;
		}
		return widget.getParentWidget().convertSceneToLocal(point);
	}

	@Override
	public void movementFinished(Widget widget)
	{
		hide();
		mmp.movementFinished(widget);
	}

	@Override
	public void movementStarted(Widget widget)
	{
		show();
		mmp.movementStarted(widget);
	}

	@Override
	public void setNewLocation(Widget widget, Point location)
	{
		mmp.setNewLocation(widget, location);
	}

}