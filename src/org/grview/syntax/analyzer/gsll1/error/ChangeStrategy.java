package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerIndex;
import org.grview.syntax.analyzer.gsll1.AnalyzerPrint;
import org.grview.syntax.analyzer.gsll1.AnalyzerStackRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerTableRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerToken;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.NTerminalStack;
import org.grview.syntax.model.ParseNode;

public class ChangeStrategy implements IErroStrategy
{

	private AnalyzerTableRepository analyzerTable;
	private AnalyzerStackRepository analyzerStack;
	private AnalyzerAlternative analyzerAlternative;
	private AnalyzerIndex analyzerIndex;
	private AnalyzerToken analyzerToken;
	private AnalyzerPrint analyzerPrint;

	public ChangeStrategy()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerIndex = AnalyzerIndex.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
		this.analyzerPrint = AnalyzerPrint.getInstance();
	}
	
	@Override
	public boolean tryFix(int topIndexNode, int topParseStackSize, int column, int line)
	{
		int IY;
		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();
		boolean achou = false;

		while (analyzerStack.getGrViewStack().size() > topParseStackSize)
			analyzerStack.getGrViewStack().pop();
		analyzerToken.readNext();
		int topps = analyzerStack.getParseStack().size();
		while (topIndexNode != 0 && !achou)
		{
			if (analyzerTable.getGraphNode(topIndexNode).IsTerminal())
			{
				IY = analyzerTable.getGraphNode(topIndexNode).getSucessorIndex();
				pilhaNaoTerminalY.clear();
				while (IY != 0 && !achou)
				{
					if (analyzerTable.getGraphNode(IY).IsTerminal())
					{
						if (analyzerTable.getGraphNode(IY).getNodeReference() == 0)
						{
							IY = analyzerTable.getGraphNode(IY).getSucessorIndex();
						}
						else
						{
							String temp = analyzerTable.getTermial(analyzerTable.getGraphNode(IY).getNodeReference()).getName();
							if (temp.equals(analyzerToken.getCurrentSymbol()))
							{
								analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getTermial(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getFlag(), analyzerTable.getTermial(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getName()));
								analyzerPrint.printStack(analyzerStack.getParseStack());
								topps++;
								analyzerIndex.setIndexNode(IY);
								achou = true;
							}
							else
								IY = analyzerAlternative.findAlternative(IY, pilhaNaoTerminalY, analyzerStack.getGrViewStack());
						}
					}
					else
					{
						analyzerStack.getGrViewStack().push(new GrViewNode(IY, topps + 2));
						pilhaNaoTerminalY.push(IY);
						IY = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IY).getNodeReference()).getFirstNode();
					}
				}
				if (!achou)
					topIndexNode = analyzerAlternative.findAlternative(topIndexNode, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
			}
			else
			{
				analyzerStack.getGrViewStack().push(new GrViewNode(topIndexNode, topps + 1));
				analyzerStack.getNTerminalStack().push(topIndexNode);
				topIndexNode = analyzerTable.getNTerminal(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getFirstNode();
			}
		}
		if (achou)
		{
			AppOutput.errorRecoveryStatus("Action: This symbol has been replaced by " + analyzerTable.getTermial(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getName() + "\n");
		}
		else
		{
			analyzerToken.setCurrentSymbol(analyzerToken.getLastSymbol());
			
			analyzerToken.getYylex().pushback(analyzerToken.getYylex().yylength());

			while (analyzerStack.getGrViewStack().size() > topParseStackSize)
			{
				analyzerStack.getGrViewStack().pop();
			}
		}
		return achou;
	}
}
