package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerStackRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerTableRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerToken;
import org.grview.syntax.model.GrViewStack;
import org.grview.syntax.model.NTerminalStack;
import org.grview.syntax.model.TableNode;

public class DelimiterSearchStrategy extends IErroStrategy
{
	private AnalyzerTableRepository analyzerTable;
	private AnalyzerStackRepository analyzerStack;
	private AnalyzerToken analyzerToken;

	public DelimiterSearchStrategy()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
	}

	@Override
	public int tryFix(int UI, int column, int line)
	{
		int IX;

		IX = UI;

		int I = -1;

		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();

		init();

		while (IX != 0 && I < 0)
		{
			if (analyzerTable.getGraphNode(IX).IsTerminal())
			{

				TableNode terminalNode = analyzerTable.getTermial(analyzerTable.getGraphNode(IX).getNodeReference());

				String temp = analyzerTable.getTermial(analyzerTable.getGraphNode(IX).getNodeReference()).getName();
				if (temp.equals(analyzerToken.getCurrentSymbol()))
				{
					AppOutput.displayText("<font color='red'>Symbol \"" + terminalNode.getName() + "\" at before column " + column + " assumed as delimiter.\n</font>", TOPIC.Output);
					I = IX;
				}
				else
				{

					if (analyzerTable.getGraphNode(IX).getSucessorIndex() != 0)
					{
						IX = analyzerTable.getGraphNode(IX).getSucessorIndex();

					}
					else if (analyzerTable.getGraphNode(IX).isLambda())
					{
						IX = analyzerTable.getGraphNode(IX).getSucessorIndex();
					}
					else
					{
						IX = analyzerTable.getGraphNode(IX).getAlternativeIndex();
					}
				}
			}
			else
			{
				pilhaNaoTerminalY.push(IX);
				if(analyzerTable.getGraphNode(IX).getSucessorIndex() != 0)
				{
					IX = analyzerTable.getGraphNode(IX).getSucessorIndex();
				}
				
				if(IX != 0)
				{
					analyzerTable.getGraphNode(pilhaNaoTerminalY.pop()).getAlternativeIndex();
				}
			}
		}

		if (I < 0)
		{
			restore(false);
		}

		return I;
	}

}
