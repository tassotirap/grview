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
	private AnalyzerIndex analyzerIndex;
	private AnalyzerGlobalVariavel analyzerGlobalVariavel;

	public Analyzer(TableGraphNode tabGraphNodes[], TableNode termialTab[], TableNode nTerminalTab[], File fileIn, Yylex yylex)
	{
		analyzerPrint = AnalyzerPrint.setInstance(this);
		analyzerToken = AnalyzerToken.setInstance(yylex);
		analyzerTabs = AnalyzerTableRepository.setInstance(tabGraphNodes, nTerminalTab, termialTab);

		analyzerStacks = AnalyzerStackRepository.setInstance();

		analyzerGlobalVariavel = AnalyzerGlobalVariavel.setInstance();
		analyzerIndex = AnalyzerIndex.setInstance();

		analyzerAlternative = AnalyzerAlternative.setInstance();
		analyzerError = new AnalyzerErrorFacede(fileIn);
	}

	

	@Override
	public void run()
	{
		analyzerToken.setCurrentSemanticSymbol(null);

		analyzerStacks.init();

		boolean sucess = true;

		semanticRoutinesRepo = SemanticRoutinesRepo.setInstance(analyzerStacks.getParseStack(), analyzerTabs.getTermialTable(), analyzerGlobalVariavel);

		analyzerToken.getYylex().TabT(analyzerTabs.getTermialTable());

		analyzerTabs.setGraphNode(0, new TableGraphNode());
		analyzerTabs.getGraphNode(0).setIsTerminal(false);
		analyzerTabs.getGraphNode(0).setNodeReference(1);
		analyzerTabs.getGraphNode(0).setAlternativeIndex(0);
		analyzerTabs.getGraphNode(0).setSucessorIndex(0);

		analyzerStacks.getGrViewStack().push(new GrViewNode(0, 1));

		analyzerToken.readNext();

		analyzerIndex.setIndexNode(analyzerTabs.getNTerminal(1).getFirstNode());
		analyzerIndex.setTopIndexNode(analyzerIndex.getIndexNode());
		analyzerIndex.setTopParseStackSize(0);

		continueSentinel = true;
		while (continueSentinel)
		{
			if (analyzerIndex.getIndexNode() != 0)
			{
				TableGraphNode currentGraphNode = analyzerTabs.getGraphNode(analyzerIndex.getIndexNode());
				if (currentGraphNode.IsTerminal())
				{
					TableNode currentTerminal = analyzerTabs.getTermial(currentGraphNode.getNodeReference());
					if (currentGraphNode.isLambda())
					{
						semanticRoutinesRepo.setCurrentToken(null);
						semanticRoutinesRepo.execFunction(currentGraphNode.getSemanticRoutine());
						analyzerIndex.setIndexNode(currentGraphNode.getSucessorIndex());
						analyzerIndex.setTopIndexNode(analyzerIndex.getIndexNode());
						analyzerIndex.setTopParseStackSize(analyzerStacks.getParseStack().size());
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

							analyzerStacks.getNTerminalStack().clear();

							analyzerIndex.setIndexNode(currentGraphNode.getSucessorIndex());
							analyzerIndex.setTopIndexNode(analyzerIndex.getIndexNode());
							analyzerIndex.setTopParseStackSize(analyzerStacks.getParseStack().size());
						}
						else
						{
							if (currentGraphNode.getAlternativeIndex() != 0)
							{
								analyzerIndex.setIndexNode(currentGraphNode.getAlternativeIndex());
							}
							else
							{
								if (analyzerStacks.getNTerminalStack().empty())
								{
									continueSentinel = analyzerError.dealWithError(analyzerIndex.getTopIndexNode(), analyzerIndex.getTopParseStackSize(), analyzerToken.getCurrentToken().charBegin + 1, analyzerToken.getCurrentToken().line + 1);
									sucess = false;
								}
								else
								{
									int alternative;
									alternative = analyzerAlternative.findAlternative(analyzerIndex.getIndexNode(), analyzerStacks.getNTerminalStack(), analyzerStacks.getGrViewStack());
									if (alternative != 0)
									{
										analyzerIndex.setIndexNode(alternative);
									}
									else
									{
										continueSentinel = analyzerError.dealWithError(analyzerIndex.getTopIndexNode(), analyzerIndex.getTopParseStackSize(), analyzerToken.getCurrentToken().charBegin + 1, analyzerToken.getCurrentToken().line + 1);
										sucess = false;
									}
								}
							}
						}
					}
				}
				else
				{
					TableNode currentNTerminal = analyzerTabs.getNTerminal(analyzerTabs.getGraphNode(analyzerIndex.getIndexNode()).getNodeReference());
					analyzerStacks.getGrViewStack().push(new GrViewNode(analyzerIndex.getIndexNode(), analyzerStacks.getParseStack().size() + 1));
					analyzerStacks.getNTerminalStack().push(analyzerIndex.getIndexNode());
					analyzerIndex.setIndexNode(currentNTerminal.getFirstNode());
				}
			}
			else
			{
				if (!analyzerStacks.getGrViewStack().empty())
				{
					grViewStackNode = analyzerStacks.getGrViewStack().pop();
					TableNode currentNTerminal = analyzerTabs.getNTerminal(analyzerTabs.getGraphNode(grViewStackNode.indexNode).getNodeReference());

					while (analyzerStacks.getParseStack().size() >= grViewStackNode.size)
					{
						auxParseSNode = analyzerStacks.getParseStack().pop();
					}

					analyzerStacks.getParseStack().push(new ParseNode(currentNTerminal.getFlag(), currentNTerminal.getName(), auxParseSNode.getSemanticSymbol()));
					analyzerPrint.printStack(analyzerStacks.getParseStack());

					analyzerIndex.setIndexNode(grViewStackNode.indexNode);

					semanticRoutinesRepo.setCurrentToken(analyzerToken.getCurrentToken());
					semanticRoutinesRepo.execFunction(analyzerTabs.getGraphNode(analyzerIndex.getIndexNode()).getSemanticRoutine());

					analyzerIndex.setIndexNode(analyzerTabs.getGraphNode(analyzerIndex.getIndexNode()).getSucessorIndex());
					analyzerIndex.setTopIndexNode(analyzerIndex.getIndexNode());
					analyzerIndex.setTopParseStackSize(this.analyzerStacks.getParseStack().size());
				}
				else
				{
					if (!analyzerToken.getCurrentSymbol().equals(new String("$")))
					{
						analyzerError.dealWithError(analyzerIndex.getTopIndexNode(), analyzerIndex.getTopParseStackSize(), analyzerToken.getCurrentToken().charBegin + 1, analyzerToken.getCurrentToken().line + 1);
						AppOutput.displayText("The fist non-teminal of the grammar has been recognized. ", TOPIC.Output);
						AppOutput.displayText("But the end-of-file symbol has not been recognized.", TOPIC.Output);
						sucess = false;
					}
					continueSentinel = false;
				}
			}
		}
		if (sucess)
		{
			AppOutput.displayText("Expression Successfully recognized.", TOPIC.Output);
		}
		else
		{
			AppOutput.displayText("Expression can't be recognized.", TOPIC.Output);
		}
	}

	public void setStepping(boolean stepping)
	{
		analyzerPrint.setStepping(stepping);
	}
}
