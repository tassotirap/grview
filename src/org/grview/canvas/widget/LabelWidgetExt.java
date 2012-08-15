package org.grview.canvas.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.netbeans.api.visual.widget.Scene;
import org.netbeans.modules.visual.util.GeomUtil;

public class LabelWidgetExt extends org.netbeans.api.visual.widget.LabelWidget implements TypedWidget, MarkedWidget
{

	private String type;
	private String mark;

	private Paint markBackground;
	private Color markForeground;

	public LabelWidgetExt(Scene scene)
	{
		super(scene);
	}

	public LabelWidgetExt(Scene scene, String node)
	{
		super(scene, node);
	}

	@Override
	protected Rectangle calculateClientArea()
	{
		if (getLabel() == null)
			return super.calculateClientArea();
		Graphics2D gr = getGraphics();
		FontMetrics fontMetrics = gr.getFontMetrics(getFont());
		Rectangle2D stringBounds = fontMetrics.getStringBounds(((getMark() == null) ? "" : getMark() + "   ") + getLabel(), gr);
		return GeomUtil.roundRectangle(stringBounds);
	}

	@Override
	protected void paintWidget()
	{
		if (getLabel() == null)
			return;
		Graphics2D gr = getGraphics();
		gr.setFont(getFont());

		FontMetrics fontMetrics = gr.getFontMetrics();
		Rectangle clientArea = getClientArea();

		int x;
		int x1 = clientArea.x + fontMetrics.stringWidth(" ");
		int markInc = ((getMark() == null) ? 0 : fontMetrics.stringWidth(getMark() + "   "));
		switch (getAlignment())
		{
			case BASELINE:
				x = markInc;
				break;
			case LEFT:
				x = clientArea.x + markInc;
				break;
			case CENTER:
				x = clientArea.x + markInc + (clientArea.width - fontMetrics.stringWidth(getLabel())) / 2;
				break;
			case RIGHT:
				x = clientArea.x + clientArea.width - (fontMetrics.stringWidth(getLabel()) + markInc);
				break;
			default:
				return;
		}

		int y;
		int y1;
		switch (getVerticalAlignment())
		{
			case BASELINE:
				y = 0;
				break;
			case TOP:
				y = clientArea.y + fontMetrics.getAscent();
				break;
			case CENTER:
				y = clientArea.y + (clientArea.height + fontMetrics.getAscent() - fontMetrics.getDescent()) / 2;
				break;
			case BOTTOM:
				y = clientArea.y + fontMetrics.getAscent() + clientArea.height - fontMetrics.getDescent();
				break;
			default:
				return;
		}
		y1 = y;

		Paint background = getBackground();
		Paint mBackground = getMarkBackground();
		Paint mForeground = getMarkForeground();
		if (isPaintAsDisabled() && background instanceof Color)
		{
			// TODO paint the mark here too
			Color color = ((Color) background);
			gr.setColor(color.brighter());
			gr.drawString(getLabel(), x + 1, y + 1);
			gr.setColor(color.darker());
			gr.drawString(getLabel(), x, y);
		}
		else
		{
			if (getMark() != null)
			{
				if (mBackground != null)
				{
					gr.setPaint(mBackground);
					gr.fillRoundRect(x1 - fontMetrics.stringWidth(" "), clientArea.y, markInc - fontMetrics.stringWidth(" "), clientArea.height, 10, 10);
				}
				if (mForeground != null)
				{
					gr.setColor(getMarkForeground());
				}
				gr.drawString(getMark(), x1, y1);
				if (background instanceof Color)
				{
					gr.setBackground((Color) background);
				}
				else if (background != null)
				{
					gr.setPaint(background);
				}
			}
			gr.setColor(getForeground());
			gr.drawString(getLabel(), x, y);
		}
	}

	@Override
	public String getMark()
	{
		return this.mark;
	}

	public Paint getMarkBackground()
	{
		return (markBackground == null) ? DEFAULT_MARK_BACKGROUND : markBackground;
	}

	public Color getMarkForeground()
	{
		return (markForeground == null) ? DEFAULT_MARK_FOREGROUND : markForeground;
	}

	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public void setMark(String mark)
	{
		this.mark = mark;
		revalidate();
	}

	@Override
	public void setMarkBackground(Paint p)
	{
		this.markBackground = p;
	}

	@Override
	public void setMarkForeground(Color markForeground)
	{
		this.markForeground = markForeground;
	}

	@Override
	public void setType(String type)
	{
		this.type = type;
	}
}
