package org.grview.syntax.analyzer.gsll1.error;

public interface IErroStrategy
{
	int tryFix(int UI, int TOP, int column, int line);
}
