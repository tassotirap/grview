package org.grview.syntax.analyzer.gsll1.error;

public interface IErroStrategy
{
	boolean tryFix(int topIndexNode, int topParseStackSize, int column, int line);
}
