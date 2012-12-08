package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.syntax.analyzer.gsll1.AnalyzerPrint;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.NTerminalStack;
import org.grview.syntax.model.ParseNode;
import org.grview.syntax.model.TableNode;

public class ChangeStrategy extends IErroStrategy
{

	private AnalyzerPrint analyzerPrint;

	public ChangeStrategy()
	{
		this.analyzerPrint = AnalyzerPrint.getInstance();
	}

	public int tryFix(int UI, int column, int line)
	{
		int IX, IY;
		int I = -1;
		
		IX = UI;

		init();

		analyzerToken.readNext();
		while (IX != 0 && I < 0)
		{
			if (analyzerTable.getGraphNode(IX).IsTerminal())
			{
				NTerminalStack pilhaNaoTerminalY = new NTerminalStack();
				
				TableNode terminalNode = analyzerTable.getTermial(analyzerTable.getGraphNode(IX).getNodeReference());
				IY = analyzerTable.getGraphNode(IX).getSucessorIndex();
				
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
								AppOutput.displayText("<font color='red'>Symbol \"" + analyzerToken.getLastToken().text + "\" has been replaced by \"" + terminalNode.getName() + "\"\n", TOPIC.Output);
								analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getTermial(analyzerTable.getGraphNode(IX).getNodeReference()).getFlag(), terminalNode.getName(), terminalNode.getName()));
								analyzerStack.setTop(analyzerStack.getTop() + 1);
								
								semanticRoutinesRepo.setCurrentToken(analyzerToken.getLastToken());
								semanticRoutinesRepo.execFunction(analyzerTable.getGraphNode(IX).getSemanticRoutine());

								analyzerPrint.printStack(analyzerStack.getParseStack());
								analyzerStack.getNTerminalStack().clear();
								I = IY;
							}
							else
								IY = analyzerAlternative.findAlternative(IY, pilhaNaoTerminalY, analyzerStack.getGrViewStack());
						}
					}
					else
					{
						analyzerStack.getGrViewStack().push(new GrViewNode(IY, analyzerStack.getTop() + 2));
						pilhaNaoTerminalY.push(IY);
						IY = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IY).getNodeReference()).getFirstNode();
					}
				}
				if (I < 0)
				{
					IX = analyzerAlternative.findAlternative(IX, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
				}
			}
			else
			{
				analyzerStack.getGrViewStack().push(new GrViewNode(IX, analyzerStack.getTop() + 1));
				analyzerStack.getNTerminalStack().push(IX);
				IX = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IX).getNodeReference()).getFirstNode();
			}
		}
		
		if (I < 0)
		{
			restore(true);
		}
		return I;
	}
}
