package org.grview.syntax.analyzer.gsll1;

import org.grview.syntax.model.TabGraphNode;
import org.grview.syntax.model.TabNode;

public class AnalyzerTabs
{
	private TabGraphNode tabGraphNodes[];
	private TabNode nTerminalTab[];
	private TabNode termialTab[];
	
	public AnalyzerTabs(TabGraphNode tabGraphNodes[], TabNode nTerminalTab[], TabNode termialTab[])
	{
		this.tabGraphNodes = tabGraphNodes;
		this.nTerminalTab = nTerminalTab;
		this.termialTab = termialTab;
	}

	public TabGraphNode[] getTabGraphNodes()
	{
		return tabGraphNodes;
	}

	public TabNode[] getnTerminalTab()
	{
		return nTerminalTab;
	}


	public TabNode[] getTermialTab()
	{
		return termialTab;
	}


}
