package org.grview.canvas.widget;

import java.awt.Color;
import java.awt.Paint;

public interface MarkedWidget
{

	public final static Paint DEFAULT_MARK_BACKGROUND = Color.GRAY;
	public final static Color DEFAULT_MARK_FOREGROUND = Color.WHITE;

	public String getMark();

	public void setMark(String mark);

	public void setMarkBackground(Paint p);

	public void setMarkForeground(Color markForeground);
}
