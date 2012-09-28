package org.grview.syntax.analyzer.gsll1;

import java.io.File;

import org.grview.lexical.Yylex;
import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.model.ParseStackNode;
import org.grview.syntax.model.TabGraphNode;
import org.grview.syntax.model.TabNode;

public class Analyzer extends Thread
{
	boolean continueSentinel;

	private GrViewStackNode grViewStackNode;

	ParseStackNode auxParseSNode = null;

	private SemanticRoutinesRepo semanticRoutinesRepo;

	private AnalyzerAlternative syntaxAlternative;
	private AnalyzerError syntaxError;
	private AnalyzerToken syntaxToken;
	
	private AnalyzerStacks analyzerStacks;
	private AnalyzerTabs analyzerTabs;
	private AnalyzerPrint analyzerPrint;
	private AnalyzerIndex analyzerIndex;

	public Analyzer(TabGraphNode tabGraphNodes[], TabNode termialTab[], TabNode nTerminalTab[], File fileIn, Yylex yylex)
	{
		analyzerTabs = new AnalyzerTabs(tabGraphNodes, nTerminalTab, termialTab);
		analyzerStacks = new AnalyzerStacks();
		syntaxAlternative = new AnalyzerAlternative(analyzerTabs);
		syntaxToken = new AnalyzerToken(yylex);
		analyzerPrint = new AnalyzerPrint(this);
		analyzerIndex = new AnalyzerIndex();
		syntaxError = new AnalyzerError(fileIn, analyzerTabs, analyzerStacks, analyzerPrint,analyzerIndex, syntaxToken);
	}

	@Override
	public void run()
	{

		syntaxToken.setCurrentSemanticSymbol(null);

		analyzerStacks.init();

		boolean sucess = true;

		semanticRoutinesRepo = new SemanticRoutinesRepo(analyzerStacks.getParseStack(), analyzerTabs.getTermialTab());
		syntaxError.setSemanticRoutinesRepo(semanticRoutinesRepo);

		syntaxToken.getYylex().TabT(analyzerTabs.getTermialTab());

		analyzerTabs.getTabGraphNodes()[0] = new TabGraphNode();
		analyzerTabs.getTabGraphNodes()[0].setIsTerminal(false);
		analyzerTabs.getTabGraphNodes()[0].setNodeReference(1);
		analyzerTabs.getTabGraphNodes()[0].setAlternativeIndex(0);
		analyzerTabs.getTabGraphNodes()[0].setSucessorIndex(0);
		analyzerStacks.getGrViewStack().push(new GrViewStackNode(0, 1));

		syntaxToken.readNext();

		analyzerIndex.setIndexNode(analyzerTabs.getnTerminalTab()[1].getFirstNode());
		analyzerIndex.setTopIndexNode(analyzerIndex.getIndexNode());
		analyzerIndex.setTopParseStackSize(0);
		
		continueSentinel = true;
		while (continueSentinel)
		{
			if (analyzerIndex.getIndexNode() != 0)
			{
				TabGraphNode currentGraphNode = analyzerTabs.getTabGraphNodes()[analyzerIndex.getIndexNode()];
				if (currentGraphNode.IsTerminal())
				{
					TabNode currentTerminal = analyzerTabs.getTermialTab()[currentGraphNode.getNodeReference()];
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
						if ((currentTerminal.getName()).equals(syntaxToken.getCurrentSymbol()))
						{
							analyzerStacks.getParseStack().push(new ParseStackNode(currentTerminal.getFlag(), syntaxToken.getCurrentSymbol(), syntaxToken.getCurrentSemanticSymbol()));
							analyzerPrint.printStack(analyzerStacks.getParseStack());

							semanticRoutinesRepo.setCurrentToken(syntaxToken.getCurrentToken());
							semanticRoutinesRepo.execFunction(currentGraphNode.getSemanticRoutine());
							syntaxToken.readNext();

							analyzerStacks.getnTermStack().clear();

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
								if (analyzerStacks.getnTermStack().empty())
								{
									continueSentinel = syntaxError.dealWithError(analyzerIndex.getTopIndexNode(), analyzerIndex.getTopParseStackSize(), syntaxToken.getCurrentToken().charBegin + 1, syntaxToken.getCurrentToken().line + 1);
									sucess = false;
								}
								else
								{
									int alternative;
									alternative = syntaxAlternative.findAlternative(analyzerIndex.getIndexNode(), analyzerStacks.getnTermStack(), analyzerStacks.getGrViewStack());
									if (alternative != 0)
									{
										analyzerIndex.setIndexNode(alternative);
									}
									else
									{
										continueSentinel = syntaxError.dealWithError(analyzerIndex.getTopIndexNode(), analyzerIndex.getTopParseStackSize(), syntaxToken.getCurrentToken().charBegin + 1, syntaxToken.getCurrentToken().line + 1);
										sucess = false;
									}
								}
							}
						}
					}
				}
				else
				{
					TabNode currentNTerminal = analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[analyzerIndex.getIndexNode()].getNodeReference()];
					analyzerStacks.getGrViewStack().push(new GrViewStackNode(analyzerIndex.getIndexNode(), analyzerStacks.getParseStack().size() + 1));
					analyzerStacks.getnTermStack().push(analyzerIndex.getIndexNode());
					analyzerIndex.setIndexNode(currentNTerminal.getFirstNode());
				}
			}
			else
			{
				if (!analyzerStacks.getGrViewStack().empty())
				{
					grViewStackNode = analyzerStacks.getGrViewStack().pop();
					TabNode currentNTerminal = analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[grViewStackNode.indexNode].getNodeReference()];

					while (analyzerStacks.getParseStack().size() >= grViewStackNode.size)
					{
						auxParseSNode = (ParseStackNode) analyzerStacks.getParseStack().pop();
					}

					analyzerStacks.getParseStack().push(new ParseStackNode(currentNTerminal.getFlag(), currentNTerminal.getName(), auxParseSNode.getSemanticSymbol()));
					analyzerPrint.printStack(analyzerStacks.getParseStack());

					analyzerIndex.setIndexNode(grViewStackNode.indexNode);

					semanticRoutinesRepo.setCurrentToken(syntaxToken.getCurrentToken());
					semanticRoutinesRepo.execFunction(analyzerTabs.getTabGraphNodes()[analyzerIndex.getIndexNode()].getSemanticRoutine());

					analyzerIndex.setIndexNode(analyzerTabs.getTabGraphNodes()[analyzerIndex.getIndexNode()].getSucessorIndex());
					analyzerIndex.setTopIndexNode(analyzerIndex.getIndexNode());
					analyzerIndex.setTopParseStackSize(this.analyzerStacks.getParseStack().size());
				}
				else
				{
					if (!syntaxToken.getCurrentSymbol().equals(new String("$")))
					{
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
