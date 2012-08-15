/*
 * OpenBracketIndentRule.java
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

import java.util.List;

import org.grview.actions.TextUtilities;
import org.grview.editor.buffer.JEditBuffer;

/**
 * @author Slava Pestov
 * @version $Id$
 */
public class OpenBracketIndentRule extends BracketIndentRule
{
	private boolean aligned;

	// {{{ OpenBracketIndentRule constructor
	public OpenBracketIndentRule(char openBracket, boolean aligned)
	{
		super(openBracket, TextUtilities.getComplementaryBracket(openBracket, null));
		this.aligned = aligned;
	} // }}}

	// {{{ handleCollapse() method
	private static void handleCollapse(List<IndentAction> indentActions, boolean delPrevPrevCollapse)
	{
		if (indentActions.contains(IndentAction.PrevCollapse))
		{
			indentActions.clear();
			return;
		}

		if (delPrevPrevCollapse && indentActions.contains(IndentAction.PrevPrevCollapse))
		{
			indentActions.clear();
			return;
		}
	} // }}}

	// {{{ getOpenBracketCount() method
	private int getOpenBracketCount(JEditBuffer buffer, int line)
	{
		if (line == -1)
			return 0;
		else
			return getBrackets(buffer, line).openCount;
	} // }}}

	// {{{ apply() method
	@Override
	public void apply(JEditBuffer buffer, int thisLineIndex, int prevLineIndex, int prevPrevLineIndex, List<IndentAction> indentActions)
	{
		int prevOpenBracketCount = getOpenBracketCount(buffer, prevLineIndex);
		if (prevOpenBracketCount != 0)
		{
			handleCollapse(indentActions, true);
			boolean multiple = buffer.getBooleanProperty("multipleBracketIndent");
			IndentAction increase = new IndentAction.Increase(multiple ? prevOpenBracketCount : 1);
			indentActions.add(increase);
		}
		else if (getOpenBracketCount(buffer, thisLineIndex) != 0)
		{
			handleCollapse(indentActions, false);
		}
	} // }}}
}
