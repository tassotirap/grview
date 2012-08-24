/*
 * UndoManager.java - Buffer undo manager
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2005 Slava Pestov
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

package org.grview.editor.buffer;

//{{{ Imports
import org.grview.util.Log;

/**
 * A class internal to jEdit's document model. You should not use it directly.
 * To improve performance, none of the methods in this class check for out of
 * bounds access, nor are they thread-safe. The <code>Buffer</code> class,
 * through which these methods must be called through, implements such
 * protection.
 * 
 * @author Slava Pestov
 * @version $Id$
 * @since jEdit 4.0pre1
 */
public class UndoManager
{
	// {{{ CompoundEdit class
	static class CompoundEdit extends Edit
	{
		Edit first, last;

		// {{{ add() method
		public void add(Edit edit)
		{
			if (first == null)
				first = last = edit;
			else
			{
				edit.prev = last;
				last.next = edit;
				last = edit;
			}
		} // }}}

		// {{{ redo() method
		@Override
		public int redo()
		{
			int retVal = -1;
			Edit edit = first;
			while (edit != null)
			{
				retVal = edit.redo();
				edit = edit.next;
			}
			return retVal;
		} // }}}

		// {{{ undo() method
		@Override
		public int undo()
		{
			int retVal = -1;
			Edit edit = last;
			while (edit != null)
			{
				retVal = edit.undo();
				edit = edit.prev;
			}
			return retVal;
		} // }}}
	} // }}}

	// {{{ Edit class
	abstract static class Edit
	{
		Edit prev, next;

		// {{{ redo() method
		abstract int redo();

		// }}}

		// {{{ undo() method
		abstract int undo();
		// }}}
	} // }}}

	// {{{ Insert class
	static class Insert extends Edit
	{
		int length;

		UndoManager mgr;

		int offset;

		String str;

		// {{{ Insert constructor
		Insert(UndoManager mgr, int offset, int length, String str)
		{
			this.mgr = mgr;
			this.offset = offset;
			this.length = length;
			this.str = str;
		} // }}}
			// {{{ redo() method

		@Override
		int redo()
		{
			mgr.buffer.insert(offset, str);
			if (mgr.redoClearDirty == this)
				mgr.buffer.setDirty(false);
			return offset + length;
		} // }}}
			// {{{ undo() method

		@Override
		int undo()
		{
			mgr.buffer.remove(offset, length);
			if (mgr.undoClearDirty == this)
				mgr.buffer.setDirty(false);
			return offset;
		} // }}}
	} // }}}

	// {{{ Remove class
	static class Remove extends Edit
	{
		final RemovedContent content;

		int length;

		UndoManager mgr;

		int offset;

		// {{{ Remove constructor
		Remove(UndoManager mgr, int offset, int length, String str)
		{
			this.mgr = mgr;
			this.offset = offset;
			this.length = length;
			this.content = new RemovedContent(str);
		} // }}}
			// {{{ redo() method

		@Override
		int redo()
		{
			mgr.buffer.remove(offset, length);
			if (mgr.redoClearDirty == this)
				mgr.buffer.setDirty(false);
			return offset;
		} // }}}
			// {{{ undo() method

		@Override
		int undo()
		{
			mgr.buffer.insert(offset, content.str);
			if (mgr.undoClearDirty == this)
				mgr.buffer.setDirty(false);
			return offset + length;
		} // }}}
	} // }}}

	// {{{ RemovedContent clas
	// This class is held in KillRing.
	public static class RemovedContent
	{
		int hashcode;
		boolean inKillRing;
		String str;

		public RemovedContent(String str)
		{
			this.str = str;
			this.hashcode = str.hashCode();
		}

		@Override
		public String toString()
		{
			return str;
		}
	}// }}}

	// {{{ Instance variables
	private JEditBuffer buffer;

	private CompoundEdit compoundEdit;

	private int compoundEditCount;

	private int limit;

	// queue of redos. first is most recent, last is oldest
	private Edit redosFirst;

	// {{{ Private members

	private Edit undoClearDirty, redoClearDirty;

	private int undoCount;
	// queue of undos. last is most recent, first is oldest
	private Edit undosFirst;

	private Edit undosLast;

	// }}}
	// {{{ UndoManager constructor
	public UndoManager(JEditBuffer buffer)
	{
		this.buffer = buffer;
	} // }}}

	// {{{ addEdit() method
	private void addEdit(Edit edit)
	{
		if (undosFirst == null)
			undosFirst = undosLast = edit;
		else
		{
			undosLast.next = edit;
			edit.prev = undosLast;
			undosLast = edit;
		}

		redosFirst = null;

		undoCount++;

		while (undoCount > limit)
		{
			undoCount--;

			if (undosFirst == undosLast)
				undosFirst = undosLast = null;
			else
			{
				undosFirst.next.prev = null;
				undosFirst = undosFirst.next;
			}
		}
	} // }}}
		// {{{ getLastEdit() method

	private Edit getLastEdit()
	{
		if (undosLast instanceof CompoundEdit)
			return ((CompoundEdit) undosLast).last;
		else
			return undosLast;
	} // }}}
		// {{{ getMergeEdit() method

	private Edit getMergeEdit()
	{
		Edit last = getLastEdit();
		return (compoundEdit != null ? compoundEdit.last : last);
	} // }}}
		// {{{ beginCompoundEdit() method

	public void beginCompoundEdit()
	{
		if (compoundEditCount == 0)
			compoundEdit = new CompoundEdit();

		compoundEditCount++;
	} // }}}
		// {{{ clear() method

	public void clear()
	{
		undosFirst = undosLast = redosFirst = null;
		undoCount = 0;
	} // }}}

	// {{{ contentInserted() method
	public void contentInserted(int offset, int length, String text, boolean clearDirty)
	{
		Edit last = getLastEdit();
		Edit toMerge = getMergeEdit();

		if (!clearDirty && toMerge instanceof Insert && redosFirst == null)
		{
			Insert ins = (Insert) toMerge;
			if (ins.offset == offset)
			{
				ins.str = text.concat(ins.str);
				ins.length += length;
				return;
			}
			else if (ins.offset + ins.length == offset)
			{
				ins.str = ins.str.concat(text);
				ins.length += length;
				return;
			}
		}

		Insert ins = new Insert(this, offset, length, text);

		if (clearDirty)
		{
			redoClearDirty = last;
			undoClearDirty = ins;
		}

		if (compoundEdit != null)
			compoundEdit.add(ins);
		else
			addEdit(ins);
	} // }}}

	// {{{ contentRemoved() method
	public void contentRemoved(int offset, int length, String text, boolean clearDirty)
	{
		Edit last = getLastEdit();
		Edit toMerge = getMergeEdit();

		if (!clearDirty && toMerge instanceof Remove && redosFirst == null)
		{
			Remove rem = (Remove) toMerge;
			if (rem.offset == offset)
			{
				rem.content.str = rem.content.str.concat(text);
				rem.content.hashcode = rem.content.str.hashCode();
				rem.length += length;
				KillRing.getInstance().changed(rem.content);
				return;
			}
			else if (offset + length == rem.offset)
			{
				rem.content.str = text.concat(rem.content.str);
				rem.content.hashcode = rem.content.str.hashCode();
				rem.length += length;
				rem.offset = offset;
				KillRing.getInstance().changed(rem.content);
				return;
			}
		}

		Remove rem = new Remove(this, offset, length, text);
		if (clearDirty)
		{
			redoClearDirty = last;
			undoClearDirty = rem;
		}

		if (compoundEdit != null)
			compoundEdit.add(rem);
		else
			addEdit(rem);

		KillRing.getInstance().add(rem.content);
	} // }}}

	// {{{ endCompoundEdit() method
	public void endCompoundEdit()
	{
		if (compoundEditCount == 0)
		{
			Log.log(Log.WARNING, this, new Exception("Unbalanced begin/endCompoundEdit()"));
			return;
		}
		else if (compoundEditCount == 1)
		{
			if (compoundEdit.first == null)
				/* nothing done between begin/end calls */;
			else if (compoundEdit.first == compoundEdit.last)
				addEdit(compoundEdit.first);
			else
				addEdit(compoundEdit);

			compoundEdit = null;
		}

		compoundEditCount--;
	} // }}}

	// }}}

	// {{{ Inner classes

	// {{{ insideCompoundEdit() method
	public boolean insideCompoundEdit()
	{
		return compoundEditCount != 0;
	} // }}}

	// {{{ redo() method
	public int redo()
	{
		if (insideCompoundEdit())
			throw new InternalError("Unbalanced begin/endCompoundEdit()");

		if (redosFirst == null)
			return -1;
		else
		{
			undoCount++;

			int caret = redosFirst.redo();
			undosLast = redosFirst;
			if (undosFirst == null)
				undosFirst = undosLast;
			redosFirst = redosFirst.next;
			return caret;
		}
	} // }}}

	// {{{ resetClearDirty method
	public void resetClearDirty()
	{
		redoClearDirty = getLastEdit();
		if (redosFirst instanceof CompoundEdit)
			undoClearDirty = ((CompoundEdit) redosFirst).first;
		else
			undoClearDirty = redosFirst;
	} // }}}

	// {{{ setLimit() method
	public void setLimit(int limit)
	{
		this.limit = limit;
	} // }}}

	// {{{ undo() method
	public int undo()
	{
		if (insideCompoundEdit())
			throw new InternalError("Unbalanced begin/endCompoundEdit()");

		if (undosLast == null)
			return -1;
		else
		{
			undoCount--;

			int caret = undosLast.undo();
			redosFirst = undosLast;
			undosLast = undosLast.prev;
			if (undosLast == null)
				undosFirst = null;
			return caret;
		}
	} // }}}

	// }}}
}
