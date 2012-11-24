package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerStackRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerTableRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerToken;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.NTerminalStack;
import org.grview.syntax.model.ParseNode;

public class InsertStrategy implements IErroStrategy
{
	private AnalyzerTableRepository analyzerTable;
	private AnalyzerStackRepository analyzerStack;
	private AnalyzerAlternative analyzerAlternative;
	private AnalyzerToken analyzerToken;

	public InsertStrategy()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
	}

	@Override
	public int tryFix(int UI, int TOP, int column, int line)
	{
		int IX = UI;
		int IY;
		int I = -1;
		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();

		int TOP_AUX1 = analyzerStack.getParseStack().size();
		int TOP_AUX2;
		while (IX != 0 && I < 0)
		{
			if (analyzerTable.getGraphNode(IX).IsTerminal())
			{
				TOP_AUX2 = TOP_AUX1;
				IY = analyzerTable.getGraphNode(IX).getSucessorIndex();
				while (IY != 0 && I < 0)
				{
					if (analyzerTable.getGraphNode(IY).IsTerminal())
					{
						if (analyzerTable.getGraphNode(IY).isLambda())
						{
							IY = analyzerTable.getGraphNode(IY).getSucessorIndex();
						}
						else
						{
							String temp = analyzerTable.getTermial(analyzerTable.getGraphNode(IY).getNodeReference()).getName();
							if (temp.equals(analyzerToken.getCurrentSymbol()))
							{
								analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getTermial(analyzerTable.getGraphNode(IX).getNodeReference()).getFlag(), analyzerTable.getTermial(analyzerTable.getGraphNode(IX).getNodeReference()).getName()));
								TOP = TOP_AUX1 + 1;
								I = IY;
							}
							else
								IY = analyzerAlternative.findAlternative(IY, pilhaNaoTerminalY, analyzerStack.getGrViewStack());
						}
					}
					else
					{
						TOP_AUX2 = TOP_AUX2 + 1;
						analyzerStack.getGrViewStack().push(new GrViewNode(IY, TOP + 2));
						pilhaNaoTerminalY.push(IY);
						IY = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IY).getNodeReference()).getFirstNode();
					}
				}
				if (I < 0)
					IX = analyzerAlternative.findAlternative(IX, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
			}
			else
			{
				TOP_AUX1 = TOP_AUX1 + 1;
				analyzerStack.getGrViewStack().push(new GrViewNode(IX, TOP + 1));
				analyzerStack.getNTerminalStack().push(IX);
				IX = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IX).getNodeReference()).getFirstNode();
			}
		}
		if (I >= 0)
		{
			/* novo topo... */
			// topo = topoaux2;
			// syntaxToken.getCurrentSymbol() = pastSymbol;
			/* volta um token na cabeça de leitura do analisador léxico. */
			// lex.yypushback(lex.yylength());
			AppOutput.displayText("<font color='red'>" + analyzerTable.getTermial(analyzerTable.getGraphNode(UI).getNodeReference()).getName() + " inserted before column " + column + "\n</font>", TOPIC.Output);
		}
		else
		{
			while (analyzerStack.getGrViewStack().size() > TOP)
			{
				analyzerStack.getGrViewStack().pop();
			}
		}
		return I;
	}
}
