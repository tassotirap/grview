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
		/* Percorre os n�s que podem ser inseridos (t1,t2....,tn) */
		while (topIndexNode != 0 && !achou)
		{
			/* o simbolo espereado � terminal? */
			if (analyzerTable.getGraphNode(topIndexNode).IsTerminal())
			{
				// topoaux2 = topoaux1;
				IY = analyzerTable.getGraphNode(topIndexNode).getSucessorIndex();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* � terminal? */
					if (analyzerTable.getGraphNode(IY).IsTerminal())
					{
						/* � lambda-n�? */
						if (analyzerTable.getGraphNode(IY).getNodeReference() == 0)
						{
							IY = analyzerTable.getGraphNode(IY).getSucessorIndex();
						}
						else
						{
							String temp = analyzerTable.getTermial(analyzerTable.getGraphNode(IY).getNodeReference()).getName();
							if (temp.equals(analyzerToken.getCurrentSymbol()))
							{
								/* empilha ti na pilha sint�tica... */
								analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getTermial(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getFlag(), analyzerTable.getTermial(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getName()));
								topps++;
								/* novo n� para asin... */
								analyzerIndex.setIndexNode(IY);
								/* corre��o obteve sucesso... */
								achou = true;
							}
							else
								IY = analyzerAlternative.findAlternative(IY, pilhaNaoTerminalY, analyzerStack.getGrViewStack());
						}
					}
					/* sucessor � um n�o terminal */
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
				 * Nesse ponto a pilhaNaoTerminalY est� vazia, portanto qualquer
				 * n� que tenha sido colocado na analyzerStacks.getGrViewStack()
				 * na busca das alternativas do n� IY, j� foi removido. Logo,
				 * posso desenpilhar um n� da pilha do analisador na busca de
				 * uma alternativa do n� IX sem problemas pois se a
				 * analyzerStacks.getnTermStack() n�o est� vazia, o topo da
				 * pilha do analisador representa um n� n�o terminal que foi
				 * utilizado para chegar at� o n� IX.
				 */
				if (!achou)
					topIndexNode = analyzerAlternative.findAlternative(topIndexNode, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
			}
			else
			{
				/* n�o terminal na sequencia dos esperados */
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
			/* volta um token na cabe�a de leitura do analisador l�xico. */
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
