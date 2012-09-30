package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerIndex;
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
	private AnalyzerIndex analyzerIndex;
	private AnalyzerToken analyzerToken;

	private SemanticRoutinesRepo semanticRoutinesRepo;

	public DeleteStrategy()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerIndex = AnalyzerIndex.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
		this.semanticRoutinesRepo = SemanticRoutinesRepo.getInstance();
	}

	@Override
	public boolean tryFix(int topIndexNode, int topParseStackSize, int column, int line)
	{
		boolean success = false;

		analyzerToken.readNext();

		while (topIndexNode != 0)
		{
			TableGraphNode graphNode = analyzerTable.getGraphNode(topIndexNode);

			if (analyzerTable.getGraphNode(topIndexNode).IsTerminal())
			{
				TableNode terminalNode = analyzerTable.getTermial(graphNode.getNodeReference());

				if (terminalNode.getName().equals(analyzerToken.getCurrentSymbol()))
				{
					

					analyzerStack.getParseStack().push(new ParseNode(terminalNode.getFlag(), analyzerToken.getCurrentSymbol(), analyzerToken.getCurrentSemanticSymbol()));

					semanticRoutinesRepo.setCurrentToken(analyzerToken.getCurrentToken());
					semanticRoutinesRepo.execFunction(graphNode.getSemanticRoutine());

					analyzerToken.readNext();

					analyzerStack.getNTerminalStack().clear();
					analyzerIndex.setIndexNode(graphNode.getSucessorIndex());

					success = true;

					break;
				}
				else
				{
					int alternative = 0;
					alternative = analyzerAlternative.findAlternative(topIndexNode, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
					topIndexNode = alternative;
				}
			}
			else
			{
				TableNode nTerminalNode = analyzerTable.getNTerminal(graphNode.getNodeReference());

				analyzerStack.getGrViewStack().push(new GrViewNode(topIndexNode, analyzerStack.getParseStack().size() + 1));
				analyzerStack.getNTerminalStack().push(topIndexNode);
				topIndexNode = nTerminalNode.getFirstNode();
			}
		}
		
		if(success)
		{
			AppOutput.displayText("Action: " + analyzerToken.getLastSymbol() + " symbol deleted\n", TOPIC.Output);
		}
		else
		{
			analyzerToken.setCurrentSymbol(analyzerToken.getLastSymbol());
			analyzerToken.getYylex().pushback(analyzerToken.getYylex().yylength());
			while (analyzerStack.getGrViewStack().size() > topParseStackSize)
			{
				analyzerStack.getGrViewStack().pop();
			}

			AppOutput.errorRecoveryStatus("\nDeleting a symbol strategy has not succeeded\n");
		}
		return success;
	}

}
