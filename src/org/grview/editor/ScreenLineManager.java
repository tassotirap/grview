/*
 * ScreenLineManager.java - Manage screen line counts
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2004 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.grview.editor;

//{{{ Imports
//}}}

import org.grview.actions.Debug;
import org.grview.editor.buffer.JEditBuffer;
import org.grview.util.Log;

/**
 * Performs the Mapping between physical lines and screen lines.
 * 
 * @since jEdit 4.3pre1
 * @author Slava Pestov
 * @version $Id$
 */
class ScreenLineManager
{
	// {{{ Private members
	private static final int SCREEN_LINES_SHIFT = 1;

	private static final int SCREEN_LINES_VALID_MASK = 1;

	private final JEditBuffer buffer;

	/** This array contains the line count for each physical line. */
	private short[] screenLines;

	// }}}

	// {{{ ScreenLineManager constructor
	ScreenLineManager(JEditBuffer buffer)
	{
		this.buffer = buffer;
		if (!buffer.isLoading())
			reset();
	} // }}}

	// {{{ getScreenLineCount() method
	/**
	 * Returns how many screen lines contains the given physical line. It can be
	 * greater than 1 when using soft wrap
	 * 
	 * @param line
	 *            the physical line
	 * @return the screen line count
	 */
	int getScreenLineCount(int line)
	{
		return screenLines[line] >> SCREEN_LINES_SHIFT;
	} // }}}

	// {{{ invalidateScreenLineCounts() method
	void invalidateScreenLineCounts()
	{
		int lineCount = buffer.getLineCount();
		for (int i = 0; i < lineCount; i++)
			screenLines[i] &= ~SCREEN_LINES_VALID_MASK;
	} // }}}

	// {{{ isScreenLineCountValid() method
	boolean isScreenLineCountValid(int line)
	{
		return (screenLines[line] & SCREEN_LINES_VALID_MASK) != 0;
	} // }}}

	// {{{ reset() method
	void reset()
	{
		screenLines = new short[buffer.getLineCount()];
	} // }}}
		// {{{ setScreenLineCount() method

	/**
	 * Sets the number of screen lines that the specified physical line is split
	 * into.
	 * 
	 * @param line
	 *            the line number
	 * @param count
	 *            the line count (1 if no wrap)
	 */
	void setScreenLineCount(int line, int count)
	{
		if (count > Short.MAX_VALUE)
		{
			// limitations...
			count = Short.MAX_VALUE;
		}

		if (Debug.SCREEN_LINES_DEBUG)
			Log.log(Log.DEBUG, this, new Exception("setScreenLineCount(" + line + ',' + count + ')'));
		screenLines[line] = (short) (count << SCREEN_LINES_SHIFT | SCREEN_LINES_VALID_MASK);
	} // }}}

	// {{{ contentInserted() method
	public void contentInserted(int startLine, int numLines)
	{
		int endLine = startLine + numLines;
		screenLines[startLine] &= ~SCREEN_LINES_VALID_MASK;

		int lineCount = buffer.getLineCount();

		if (numLines > 0)
		{
			if (screenLines.length <= lineCount)
			{
				short[] screenLinesN = new short[((lineCount + 1) << 1)];
				System.arraycopy(screenLines, 0, screenLinesN, 0, screenLines.length);
				screenLines = screenLinesN;
			}

			System.arraycopy(screenLines, startLine, screenLines, endLine, lineCount - endLine);

			for (int i = 0; i < numLines; i++)
				screenLines[startLine + i] = 0;
		}
	} // }}}
		// {{{ contentRemoved() method

	public void contentRemoved(int startLine, int numLines)
	{
		int endLine = startLine + numLines;
		screenLines[startLine] &= ~SCREEN_LINES_VALID_MASK;

		if (numLines > 0 && endLine != screenLines.length)
		{
			System.arraycopy(screenLines, endLine + 1, screenLines, startLine + 1, screenLines.length - endLine - 1);
		}
	} // }}}
}
