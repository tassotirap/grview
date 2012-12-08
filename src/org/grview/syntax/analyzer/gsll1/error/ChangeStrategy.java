package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.syntax.analyzer.gsll1.AnalyzerPrint;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.NTerminalStack;
import org.grview.syntax.model.ParseNode;
import org.grview.syntax.model.TableGraphNode;
import org.grview.syntax.model.TableNode;

public class ChangeStrategy extends IErroStrategy
{

	private AnalyzerPrint analyzerPrint;

	public ChangeStrategy()
	{
		this.analyzerPrint = AnalyzerPrint.getInstance();
	}

	@Override
	public int tryFix(int UI, int column, int line)
	{
		int IY;
		int I = -1;
		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();

		init();

		analyzerToken.readNext();
		while (UI != 0 && I < 0)
		{
			TableGraphNode graphNode = analyzerTable.getGraphNode(UI);
			if (graphNode.IsTerminal())
			{
				TableNode terminalNode = analyzerTable.getTermial(graphNode.getNodeReference());
				IY = graphNode.getSucessorIndex();
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
								analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getTermial(analyzerTable.getGraphNode(UI).getNodeReference()).getFlag(), terminalNode.getName(), terminalNode.getName()));
								analyzerStack.setTop(analyzerStack.getTop() + 1);

								analyzerPrint.printStack(analyzerStack.getParseStack());
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
					UI = analyzerAlternative.findAlternative(UI, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
				}
			}
			else
			{

				analyzerStack.getGrViewStack().push(new GrViewNode(UI, analyzerStack.getTop() + 1));
				analyzerStack.getNTerminalStack().push(UI);
				UI = analyzerTable.getNTerminal(analyzerTable.getGraphNode(UI).getNodeReference()).getFirstNode();
			}
		}
		if (I < 0)
		{
			analyzerToken.setCurrentToken(analyzerToken.getLastToken());

			analyzerToken.getYylex().pushback(analyzerToken.getYylex().yylength());

			restore(true);
		}
		return I;
	}
}
