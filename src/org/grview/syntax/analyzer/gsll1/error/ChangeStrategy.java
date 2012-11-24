package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
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
	private AnalyzerToken analyzerToken;
	private AnalyzerPrint analyzerPrint;

	public ChangeStrategy()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
		this.analyzerPrint = AnalyzerPrint.getInstance();
	}
	
	@Override
	public int tryFix(int UI, int TOP, int column, int line)
	{
		int IY;
		int I = -1;
		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();
		

		while (analyzerStack.getGrViewStack().size() > TOP)
			analyzerStack.getGrViewStack().pop();
		analyzerToken.readNext();
		int topps = analyzerStack.getParseStack().size();
		while (UI != 0 && I < 0)
		{
			if (analyzerTable.getGraphNode(UI).IsTerminal())
			{
				IY = analyzerTable.getGraphNode(UI).getSucessorIndex();
				pilhaNaoTerminalY.clear();
				while (IY != 0 && I < 0)
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
								analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getTermial(analyzerTable.getGraphNode(UI).getNodeReference()).getFlag(), analyzerTable.getTermial(analyzerTable.getGraphNode(UI).getNodeReference()).getName()));
								analyzerPrint.printStack(analyzerStack.getParseStack());
								topps++;
								I = IY;
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
				if (I < 0)
					UI = analyzerAlternative.findAlternative(UI, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
			}
			else
			{
				analyzerStack.getGrViewStack().push(new GrViewNode(UI, topps + 1));
				analyzerStack.getNTerminalStack().push(UI);
				UI = analyzerTable.getNTerminal(analyzerTable.getGraphNode(UI).getNodeReference()).getFirstNode();
			}
		}
		if (I >= 0)
		{
			AppOutput.errorRecoveryStatus("Action: This symbol has been replaced by " + analyzerTable.getTermial(analyzerTable.getGraphNode(UI).getNodeReference()).getName() + "\n");
		}
		else
		{
			analyzerToken.setCurrentSymbol(analyzerToken.getLastSymbol());
			
			analyzerToken.getYylex().pushback(analyzerToken.getYylex().yylength());

			while (analyzerStack.getGrViewStack().size() > TOP)
			{
				analyzerStack.getGrViewStack().pop();
			}
		}
		return I;
	}
}
