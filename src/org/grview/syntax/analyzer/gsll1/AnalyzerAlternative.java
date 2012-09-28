package org.grview.syntax.analyzer.gsll1;

import java.util.Stack;

public class AnalyzerAlternative
{
	AnalyzerTabs analyzerTabs;
	
	public AnalyzerAlternative(AnalyzerTabs analyzerTabs)
	{
		this.analyzerTabs = analyzerTabs;
	}
	
	public int findAlternative(int indexNode, Stack<Integer> nTermStack, Stack<GrViewStackNode> grViewStack)
	{
		int alternative = 0;
		alternative = analyzerTabs.getTabGraphNodes()[indexNode].getAlternativeIndex();
		while (alternative == 0 && !nTermStack.empty())
		{
			grViewStack.pop();
			alternative = analyzerTabs.getTabGraphNodes()[(nTermStack.pop()).intValue()].getAlternativeIndex();
		}
		return alternative;
	}
}
