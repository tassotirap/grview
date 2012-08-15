/*
 * IndentAction.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 Slava Pestov
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

package org.grview.editor.indent;

import org.grview.editor.buffer.JEditBuffer;
import org.grview.util.StandardUtilities;

/**
 * @author Slava Pestov
 * @version $Id$
 */
public interface IndentAction
{
	/**
	 * @author Matthieu Casanova
	 */
	class AlignOffset implements IndentAction
	{
		private int offset;

		public AlignOffset(int offset)
		{
			this.offset = offset;
		}

		@Override
		public int calculateIndent(JEditBuffer buffer, int line, int oldIndent, int newIndent)
		{
			return offset;
		}

		@Override
		public boolean keepChecking()
		{
			return false;
		}
	}

	/**
	 * Indent action used for deep indent.
	 * 
	 * @author Matthieu Casanova
	 */
	class AlignParameter implements IndentAction
	{
		private int openParensColumn;

		public AlignParameter(int openParensColumn)
		{
			this.openParensColumn = openParensColumn;
		}

		@Override
		public int calculateIndent(JEditBuffer buffer, int line, int oldIndent, int newIndent)
		{
			return openParensColumn + 1;
		}

		@Override
		public boolean keepChecking()
		{
			return false;
		}
	}

	/** See comments for each instance of this class below. */
	class Collapse implements IndentAction
	{
		private Collapse()
		{
		}

		/**
		 * This does nothing; it is merely a sentinel for the
		 * <code>OpenBracketIndentRule</code>.
		 */
		@Override
		public int calculateIndent(JEditBuffer buffer, int line, int oldIndent, int newIndent)
		{
			return newIndent;
		}

		@Override
		public boolean keepChecking()
		{
			return true;
		}
	}

	class Decrease implements IndentAction
	{
		@Override
		public int calculateIndent(JEditBuffer buffer, int line, int oldIndent, int newIndent)
		{
			return newIndent - buffer.getIndentSize();
		}

		@Override
		public boolean keepChecking()
		{
			return true;
		}
	}

	class Increase implements IndentAction
	{
		private int amount;

		public Increase()
		{
			amount = 1;
		}

		public Increase(int amount)
		{
			this.amount = amount;
		}

		@Override
		public int calculateIndent(JEditBuffer buffer, int line, int oldIndent, int newIndent)
		{
			return newIndent + buffer.getIndentSize() * amount;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof Increase)
				return ((Increase) o).amount == amount;
			else
				return false;
		}

		@Override
		public boolean keepChecking()
		{
			return true;
		}
	}

	/**
	 * Used to cancel increases in indentation.
	 * 
	 * @author Marcelo Vanzin
	 */
	class NoIncrease implements IndentAction
	{
		@Override
		public int calculateIndent(JEditBuffer buffer, int line, int oldIndent, int newIndent)
		{
			int current = StandardUtilities.getLeadingWhiteSpaceWidth(buffer.getLineSegment(line), buffer.getTabSize());
			return (current < newIndent) ? current : newIndent;
		}

		@Override
		public boolean keepChecking()
		{
			return true;
		}
	}

	class Reset implements IndentAction
	{
		@Override
		public int calculateIndent(JEditBuffer buffer, int line, int oldIndent, int newIndent)
		{
			return oldIndent;
		}

		@Override
		public boolean keepChecking()
		{
			return true;
		}
	}

	/**
	 * This handles the following Java code: if(something) { // no indentation
	 * on this line, even though previous matches a rule
	 */
	Collapse PrevCollapse = new Collapse();

	/**
	 * This handles cases like: if (foo) bar; for (something; condition; action)
	 * { } Without this the "for" line would be incorrectly indented.
	 */
	Collapse PrevPrevCollapse = new Collapse();

	/**
	 * @param buffer
	 *            The buffer
	 * @param line
	 *            The line number that matched the rule; not necessarily the
	 *            line being indented.
	 * @param oldIndent
	 *            Original indent.
	 * @param newIndent
	 *            The new indent -- ie, indent returned by previous indent
	 *            action.
	 */
	int calculateIndent(JEditBuffer buffer, int line, int oldIndent, int newIndent);

	/**
	 * @return true if the indent engine should keep processing after this rule.
	 */
	boolean keepChecking();
}
