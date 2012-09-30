package org.grview.syntax.analyzer.gsll1;

import org.grview.syntax.model.TableGraphNode;
import org.grview.syntax.model.TableNode;

public class AnalyzerTableRepository
{
	private TableGraphNode graphNodesTable[];
	private TableNode nTerminalTable[];
	private TableNode termialTable[];
	
	private static AnalyzerTableRepository instance;
	
	public static AnalyzerTableRepository getInstance()
	{
		return instance;
	}
	
	public static AnalyzerTableRepository setInstance(TableGraphNode tabGraphNodes[], TableNode nTerminalTab[], TableNode termialTab[])
	{
		instance = new AnalyzerTableRepository(tabGraphNodes, nTerminalTab, termialTab);
		return instance;
	}
	
	private AnalyzerTableRepository(TableGraphNode tabGraphNodes[], TableNode nTerminalTab[], TableNode termialTab[])
	{
		this.graphNodesTable = tabGraphNodes;
		this.nTerminalTable = nTerminalTab;
		this.termialTable = termialTab;
	}
	
	public TableNode getNTerminal(int index)
	{
		if(index < nTerminalTable.length)
		{
			return nTerminalTable[index];			
		}
		return null;
	}
	
	public void setNTermial(int index, TableNode value)
	{
		if(index < nTerminalTable.length)
		{
			nTerminalTable[index] = value;			
		}
	}			
	
	public TableGraphNode getGraphNode(int index)
	{
		if(index < graphNodesTable.length)
		{
			return graphNodesTable[index];			
		}
		return null;	
	}
	
	public void setGraphNode(int index, TableGraphNode value)
	{
		if(index < graphNodesTable.length)
		{
			graphNodesTable[index] = value;			
		}
	}

	public TableNode[] getTermialTable()
	{
		return termialTable;
	}
	
	public TableNode getTermial(int index)
	{
		if(index < termialTable.length)
		{
			return termialTable[index];			
		}
		return null;
	}
	
	public void setTermial(int index, TableNode value)
	{
		if(index < termialTable.length)
		{
			termialTable[index] = value;			
		}
	}
}
