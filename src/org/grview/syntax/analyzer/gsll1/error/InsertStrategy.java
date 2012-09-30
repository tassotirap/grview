package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerIndex;
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
	private AnalyzerIndex analyzerIndex;
	private AnalyzerToken analyzerToken;

	public InsertStrategy()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerIndex = AnalyzerIndex.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
	}
	
	@Override
	public boolean tryFix(int topIndexNode, int topParseStackSize, int column, int line)
	{
		int IY;
		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();
		boolean achou = false;
		// int topo = asinStack.size()-1;
		int topps = analyzerStack.getParseStack().size();
		// int topoaux1 = topo;
		// int topoaux2 = 0;
		/* Percorre os nós que podem ser inseridos (t1,t2....,tn) */
		while (topIndexNode != 0 && !achou)
		{
			/* o simbolo espereado é terminal? */
			if (analyzerTable.getGraphNode(topIndexNode).IsTerminal())
			{
				// topoaux2 = topoaux1;
				IY = analyzerTable.getGraphNode(topIndexNode).getSucessorIndex();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* é terminal? */
					if (analyzerTable.getGraphNode(IY).IsTerminal())
					{
						/* é lambda-nó? */
						if (analyzerTable.getGraphNode(IY).getNodeReference() == 0)
						{
							IY = analyzerTable.getGraphNode(IY).getSucessorIndex();
						}
						else
						{
							String temp = analyzerTable.getTermial(analyzerTable.getGraphNode(IY).getNodeReference()).getName();
							if (temp.equals(analyzerToken.getCurrentSymbol()))
							{
								/* empilha ti na pilha sintática... */
								analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getTermial(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getFlag(), analyzerTable.getTermial(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getName()));
								topps++;
								/* novo nó para asin... */
								analyzerIndex.setIndexNode(IY);
								/* correção obteve sucesso... */
								achou = true;
							}
							else
								IY = analyzerAlternative.findAlternative(IY, pilhaNaoTerminalY, analyzerStack.getGrViewStack());
						}
					}
					/* sucessor é um não terminal */
					else
					{
						// topoaux2 = topoaux2 + 1;
						// asinStack.add(topoaux2,new GrViewStackNode(IY,
						// topps + 2));
						analyzerStack.getGrViewStack().push(new GrViewNode(IY, topps + 2));
						pilhaNaoTerminalY.push(IY);
						IY = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IY).getNodeReference()).getFirstNode();
					}
				}
				/*
				 * Nesse ponto a pilhaNaoTerminalY está vazia, portanto qualquer
				 * nó que tenha sido colocado na analyzerStacks.getGrViewStack()
				 * na busca das alternativas do nó IY, já foi removido. Logo,
				 * posso desenpilhar um nó da pilha do analisador na busca de
				 * uma alternativa do nó IX sem problemas pois se a
				 * analyzerStacks.getnTermStack() não está vazia, o topo da
				 * pilha do analisador representa um nó não terminal que foi
				 * utilizado para chegar até o nó IX.
				 */
				if (!achou)
					topIndexNode = analyzerAlternative.findAlternative(topIndexNode, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
			}
			else
			{
				/* não terminal na sequencia dos esperados */
				// topoaux1 = topoaux1 + 1;
				// asinStack.add(topoaux1,new GrViewStackNode(IX, topps +
				// 1));
				analyzerStack.getGrViewStack().push(new GrViewNode(topIndexNode, topps + 1));
				analyzerStack.getNTerminalStack().push(topIndexNode);
				topIndexNode = analyzerTable.getNTerminal(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getFirstNode();
			}
		}
		if (achou)
		{
			/* novo topo... */
			// topo = topoaux2;
			// syntaxToken.getCurrentSymbol() = pastSymbol;
			/* volta um token na cabeça de leitura do analisador léxico. */
			// lex.yypushback(lex.yylength());
			AppOutput.displayText("Action: " + analyzerTable.getTermial(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getName() + " inserted before column " + column + "\n", TOPIC.Output);
		}
		else
		{
			while (analyzerStack.getGrViewStack().size() > topParseStackSize)
			{
				analyzerStack.getGrViewStack().pop();
			}		
			AppOutput.errorRecoveryStatus("Inserting a symbol strategy has not succeeded\n");

		}
		return achou;
	}

}
