/*
 * BracketIndentRule.java
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

import javax.swing.text.Segment;

import org.grview.editor.buffer.JEditBuffer;
import org.grview.editor.syntax.Token;
import org.grview.editor.syntax.TokenHandler;
import org.grview.editor.syntax.TokenMarker;

/**
 * @author Slava Pestov
 * @version $Id$
 */
public abstract class BracketIndentRule implements IndentRule
{
	// {{{ class LineScanner
	private class LineScanner implements TokenHandler
	{
		private final int beginIndex;

		private final int endIndex;
		private int scannedIndex;
		public final Brackets result;

		public LineScanner(int begin, int end)
		{
			this.result = new Brackets();
			this.scannedIndex = 0;
			this.beginIndex = begin;
			this.endIndex = end;
		}

		boolean rejectsToken(byte id)
		{
			// Rejects comments and literals.
			// Accepts all others.
			switch (id)
			{
				case Token.COMMENT1:
				case Token.COMMENT2:
				case Token.COMMENT3:
				case Token.COMMENT4:
				case Token.LITERAL1:
				case Token.LITERAL2:
				case Token.LITERAL3:
				case Token.LITERAL4:
					return true;
				default:
					return false;
			}
		}

		private void scan(Segment seg, int offset, int length)
		{
			int index = scannedIndex;
			if (index >= endIndex)
			{
				return;
			}
			if (index < beginIndex)
			{
				int numToSkip = beginIndex - index;
				if (numToSkip >= length)
				{
					return;
				}
				offset += numToSkip;
				length -= numToSkip;
				index = beginIndex;
			}
			if (index + length > endIndex)
			{
				length = endIndex - index;
			}

			for (int i = 0; i < length; ++i)
			{
				char c = seg.array[seg.offset + offset + i];
				if (c == openBracket)
				{
					result.openCount++;
				}
				else if (c == closeBracket)
				{
					if (result.openCount != 0)
						result.openCount--;
					else
						result.closeCount++;
				}
			}
		}

		@Override
		public void handleToken(Segment seg, byte id, int offset, int length, TokenMarker.LineContext context)
		{
			if (!rejectsToken(id))
			{
				scan(seg, offset, length);
			}
			scannedIndex += length;
		}

		@Override
		public void setLineContext(TokenMarker.LineContext lineContext)
		{
		}
	} // }}}

	// {{{ Brackets class
	public static class Brackets
	{
		int closeCount;
		int openCount;
	} // }}}

	protected char openBracket, closeBracket;

	// {{{ BracketIndentRule constructor
	public BracketIndentRule(char openBracket, char closeBracket)
	{
		this.openBracket = openBracket;
		this.closeBracket = closeBracket;
	} // }}}

	// {{{ getBrackets() method
	public Brackets getBrackets(JEditBuffer buffer, int lineIndex)
	{
		return getBrackets(buffer, lineIndex, 0, buffer.getLineLength(lineIndex));
	} // }}}

	// {{{ getBrackets() method
	public Brackets getBrackets(JEditBuffer buffer, int lineIndex, int begin, int end)
	{
		LineScanner scanner = new LineScanner(begin, end);
		buffer.markTokens(lineIndex, scanner);
		return scanner.result;
	} // }}}

	// {{{ getBrackets() method
	/**
	 * @deprecated Use {@link #getBrackets(JEditBuffer,int,int,int)} instead.
	 *             Brackets in comments or literals should be ignored for
	 *             indent. But it can't be done without syntax parsing of a
	 *             buffer.
	 */
	@Deprecated
	public Brackets getBrackets(String line)
	{
		Brackets brackets = new Brackets();

		for (int i = 0; i < line.length(); i++)
		{
			char ch = line.charAt(i);
			if (ch == openBracket)
			{
				/*
				 * Don't increase indent when we see an explicit fold.
				 */
				if (line.length() - i >= 3)
				{
					if (line.substring(i, i + 3).equals("{{{")) /* }}} */
					{
						i += 2;
						continue;
					}
				}
				brackets.openCount++;
			}
			else if (ch == closeBracket)
			{
				if (brackets.openCount != 0)
					brackets.openCount--;
				else
					brackets.closeCount++;
			}
		}

		return brackets;
	} // }}}

	// {{{ toString() method
	@Override
	public String toString()
	{
		return getClass().getName() + "[" + openBracket + "," + closeBracket + "]";
	} // }}}
}
