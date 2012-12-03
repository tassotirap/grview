package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerStackRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerTableRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerToken;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.ParseNode;
import org.grview.syntax.model.TableGraphNode;
import org.grview.syntax.model.TableNode;

public class DeleteStrategy implements IErroStrategy
{
	private AnalyzerTableRepository analyzerTable;
	private AnalyzerStackRepository analyzerStack;
	private AnalyzerAlternative analyzerAlternative;
	private AnalyzerToken analyzerToken;

	private SemanticRoutinesRepo semanticRoutinesRepo;

	public DeleteStrategy()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
		this.semanticRoutinesRepo = SemanticRoutinesRepo.getInstance();
	}

	@Override
	public int tryFix(int UI, int TOP, int column, int line)
	{
		int I = -1;
		int IX = UI;

		analyzerToken.readNext();

		while (IX != 0)
		{
			TableGraphNode graphNode = analyzerTable.getGraphNode(IX);

			if (analyzerTable.getGraphNode(IX).IsTerminal())
			{
				TableNode terminalNode = analyzerTable.getTermial(graphNode.getNodeReference());

				if (terminalNode.getName().equals(analyzerToken.getCurrentSymbol()))
				{
					AppOutput.displayText("<font color='red'>Symbol \"" + analyzerToken.getLastToken().text + "\" was ignored.\n</font>", TOPIC.Output);

					analyzerStack.getParseStack().push(new ParseNode(terminalNode.getFlag(), analyzerToken.getCurrentSymbol(), analyzerToken.getCurrentSemanticSymbol()));

					semanticRoutinesRepo.setCurrentToken(analyzerToken.getCurrentToken());
					semanticRoutinesRepo.execFunction(graphNode.getSemanticRoutine());

					analyzerStack.getNTerminalStack().clear();
					I = IX;

					break;
				}
				else
				{
					int alternative = 0;
					alternative = analyzerAlternative.findAlternative(IX, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
					IX = alternative;
				}
			}
			else
			{
				TableNode nTerminalNode = analyzerTable.getNTerminal(graphNode.getNodeReference());

				analyzerStack.getGrViewStack().push(new GrViewNode(IX, analyzerStack.getParseStack().size() + 1));
				analyzerStack.getNTerminalStack().push(IX);
				IX = nTerminalNode.getFirstNode();
			}
		}

		if (I < 0)
		{
			analyzerToken.setCurrentToken(analyzerToken.getLastToken());
			analyzerToken.getYylex().pushback(analyzerToken.getYylex().yylength());
			while (analyzerStack.getGrViewStack().size() > TOP)
			{
				analyzerStack.getGrViewStack().pop();
			}
		}
		return I;
	}

}
