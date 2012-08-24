/*
 * TextAreaTransferHandler.java - Drag and drop support
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
import java.awt.Point;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import javax.swing.SwingUtilities;

import org.grview.editor.buffer.JEditBuffer;
import org.grview.util.Log;

//}}}

/**
 * @author Slava Pestov
 * @version $Id$
 */
class TextAreaDropHandler extends DropTargetAdapter
{
	private JEditBuffer savedBuffer;
	private int savedCaret;
	private final TextArea textArea;

	// {{{ TextAreaDropHandler constructor
	TextAreaDropHandler(TextArea textArea)
	{
		this.textArea = textArea;
	} // }}}

	// {{{ dragEnter() method
	@SuppressWarnings("deprecation")
	@Override
	public void dragEnter(DropTargetDragEvent dtde)
	{
		Log.log(Log.DEBUG, this, "Drag enter");
		savedBuffer = textArea.getBuffer();
		textArea.setDragInProgress(true);
		// textArea.getBuffer().beginCompoundEdit();
		savedCaret = textArea.getCaretPosition();
	} // }}}

	// {{{ dragExit() method
	@SuppressWarnings("deprecation")
	@Override
	public void dragExit(DropTargetEvent dtde)
	{
		Log.log(Log.DEBUG, this, "Drag exit");
		textArea.setDragInProgress(false);
		// textArea.getBuffer().endCompoundEdit();
		if (textArea.getBuffer() == savedBuffer)
		{
			textArea.moveCaretPosition(savedCaret, TextArea.ELECTRIC_SCROLL);
		}
		savedBuffer = null;
	} // }}}

	// {{{ dragOver() method
	@Override
	public void dragOver(DropTargetDragEvent dtde)
	{
		Point p = dtde.getLocation();
		p = SwingUtilities.convertPoint(textArea, p, textArea.getPainter());
		int pos = textArea.xyToOffset(p.x, p.y, !(textArea.getPainter().isBlockCaretEnabled() || textArea.isOverwriteEnabled()));
		if (pos != -1)
		{
			textArea.moveCaretPosition(pos, TextArea.ELECTRIC_SCROLL);
		}
	} // }}}

	// {{{ drop() method
	@SuppressWarnings("deprecation")
	@Override
	public void drop(DropTargetDropEvent dtde)
	{
		Log.log(Log.DEBUG, this, "Drop");
		textArea.setDragInProgress(false);
		// textArea.getBuffer().endCompoundEdit();
		savedBuffer = null;
	} // }}}
}
