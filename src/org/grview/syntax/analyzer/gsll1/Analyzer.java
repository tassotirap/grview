package org.grview.syntax.analyzer.gsll1;

import java.io.File;

import org.grview.lexical.Yylex;
import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.analyzer.gsll1.error.AnalyzerErrorFacede;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.ParseNode;
import org.grview.syntax.model.TableGraphNode;
import org.grview.syntax.model.TableNode;

public class Analyzer extends Thread
{
	boolean continueSentinel;

	private GrViewNode grViewStackNode;

	ParseNode auxParseSNode = null;

	private SemanticRoutinesRepo semanticRoutinesRepo;

	private AnalyzerAlternative analyzerAlternative;
	private AnalyzerErrorFacede analyzerError;
	private AnalyzerToken analyzerToken;

	private AnalyzerStackRepository analyzerStacks;
	private AnalyzerTableRepository analyzerTabs;
	private AnalyzerPrint analyzerPrint;
	private AnalyzerSemanticStack analyzerGlobalVariavel;

	public Analyzer(TableGraphNode tabGraphNodes[], TableNode termialTab[], TableNode nTerminalTab[], File fileIn, Yylex yylex)
	{
		analyzerPrint = AnalyzerPrint.setInstance(this);
		analyzerToken = AnalyzerToken.setInstance(yylex);
		analyzerTabs = AnalyzerTableRepository.setInstance(tabGraphNodes, nTerminalTab, termialTab);

		analyzerStacks = AnalyzerStackRepository.setInstance();

		analyzerGlobalVariavel = AnalyzerSemanticStack.setInstance();

		analyzerAlternative = AnalyzerAlternative.setInstance();
		analyzerError = new AnalyzerErrorFacede(fileIn);
	}

	@Override
	public void run()
	{
		int I;
		int UI;

		analyzerToken.setCurrentSemanticSymbol(null);
		analyzerStacks.init();

		boolean sucess = true;
		semanticRoutinesRepo = SemanticRoutinesRepo.setInstance(analyzerGlobalVariavel);

		analyzerToken.getYylex().TabT(analyzerTabs.getTermialTable());

		analyzerTabs.setGraphNode(0, new TableGraphNode());
		analyzerTabs.getGraphNode(0).setIsTerminal(false);
		analyzerTabs.getGraphNode(0).setNodeReference(1);
		analyzerTabs.getGraphNode(0).setAlternativeIndex(0);
		analyzerTabs.getGraphNode(0).setSucessorIndex(0);

		analyzerStacks.getGrViewStack().push(new GrViewNode(0, 1));

		analyzerToken.readNext();

		I = analyzerTabs.getNTerminal(1).getFirstNode();
		UI = I;
		analyzerStacks.setTop(0);

		continueSentinel = true;
		while (continueSentinel)
		{
			if (I != 0)
			{
				TableGraphNode currentGraphNode = analyzerTabs.getGraphNode(I);
				if (currentGraphNode.IsTerminal())
				{
					TableNode currentTerminal = analyzerTabs.getTermial(currentGraphNode.getNodeReference());
					if (currentGraphNode.isLambda())
					{
						semanticRoutinesRepo.setCurrentToken(null);
						semanticRoutinesRepo.execFunction(currentGraphNode.getSemanticRoutine());
						
						I = currentGraphNode.getSucessorIndex();
						UI = I;
					}
					else
					{
						if ((currentTerminal.getName()).equals(analyzerToken.getCurrentSymbol()))
						{
							analyzerStacks.getParseStack().push(new ParseNode(currentTerminal.getFlag(), analyzerToken.getCurrentSymbol(), analyzerToken.getCurrentSemanticSymbol()));
							analyzerPrint.printStack(analyzerStacks.getParseStack());

							semanticRoutinesRepo.setCurrentToken(analyzerToken.getCurrentToken());
							semanticRoutinesRepo.execFunction(currentGraphNode.getSemanticRoutine());

							analyzerToken.readNext();

							I = currentGraphNode.getSucessorIndex();
							UI = I;
							analyzerStacks.setTop(analyzerStacks.getTop() + 1);

							analyzerStacks.getNTerminalStack().clear();
						}
						else
						{
							if (currentGraphNode.getAlternativeIndex() != 0)
							{
								I = currentGraphNode.getAlternativeIndex();
							}
							else
							{
								if (analyzerStacks.getNTerminalStack().empty())
								{
									I = analyzerError.dealWithError(UI, analyzerToken.getCurrentToken().charBegin + 1, analyzerToken.getCurrentToken().line + 1);
									continueSentinel = I >= 0;

									sucess = false;
								}
								else
								{
									int alternative = analyzerAlternative.findAlternative(I, analyzerStacks.getNTerminalStack(), analyzerStacks.getGrViewStack());
									if (alternative != 0)
									{
										I = alternative;
									}
									else
									{
										I = analyzerError.dealWithError(UI, analyzerToken.getCurrentToken().charBegin + 1, analyzerToken.getCurrentToken().line + 1);
										continueSentinel = I >= 0;
										sucess = false;
									}
								}
							}
						}
					}
				}
				else
				{
					TableNode currentNTerminal = analyzerTabs.getNTerminal(analyzerTabs.getGraphNode(I).getNodeReference());
					analyzerStacks.getNTerminalStack().push(I);
					analyzerStacks.getGrViewStack().push(new GrViewNode(I, analyzerStacks.getTop() + 1));
					I = currentNTerminal.getFirstNode();
				}
			}
			else
			{
				if (!analyzerStacks.getGrViewStack().empty())
				{
					grViewStackNode = analyzerStacks.getGrViewStack().pop();
					auxParseSNode = null;

					while (analyzerStacks.getParseStack().size() >= grViewStackNode.size)
					{
						auxParseSNode = analyzerStacks.getParseStack().pop();
					}

					if (auxParseSNode != null)
					{
						TableNode currentNTerminal = analyzerTabs.getNTerminal(analyzerTabs.getGraphNode(grViewStackNode.indexNode).getNodeReference());
						analyzerStacks.getParseStack().push(new ParseNode(currentNTerminal.getFlag(), currentNTerminal.getName(), auxParseSNode.getSemanticSymbol()));
						analyzerPrint.printStack(analyzerStacks.getParseStack());
					}

					I = grViewStackNode.indexNode;

					semanticRoutinesRepo.setCurrentToken(analyzerToken.getCurrentToken());
					semanticRoutinesRepo.execFunction(analyzerTabs.getGraphNode(I).getSemanticRoutine());

					I = analyzerTabs.getGraphNode(I).getSucessorIndex();// I
					UI = I;
				}
				else
				{
					if (!analyzerToken.getCurrentSymbol().equals(new String("$")))
					{
						I = analyzerError.dealWithError(UI, analyzerToken.getCurrentToken().charBegin + 1, analyzerToken.getCurrentToken().line + 1);
						sucess = false;
					}
					continueSentinel = I > 0;
				}
			}
		}
		if (sucess)
		{
			AppOutput.displayText("<font color='green'>Expression Successfully recognized.</font>", TOPIC.Output);
		}
		else
		{
			AppOutput.displayText("<font color='red'>Expression can't be recognized.</font>", TOPIC.Output);
		}
	}

	public void setStepping(boolean stepping)
	{
		analyzerPrint.setStepping(stepping);
	}
}
