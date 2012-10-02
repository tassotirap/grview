package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerIndex;
import org.grview.syntax.analyzer.gsll1.AnalyzerStackRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerTableRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerToken;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.GrViewStack;
import org.grview.syntax.model.NTerminalStack;
import org.grview.syntax.model.ParseNode;

public class DelimiterSearchStrategy implements IErroStrategy
{
	private AnalyzerTableRepository analyzerTable;
	private AnalyzerStackRepository analyzerStack;
	private AnalyzerAlternative analyzerAlternative;
	private AnalyzerIndex analyzerIndex;
	private AnalyzerToken analyzerToken;

	public DelimiterSearchStrategy()
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
		boolean achou = false;
		GrViewStack pilhaAnalisadorBackup;
		GrViewStack pilhaAuxAnalisador = new GrViewStack();
		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();
		GrViewNode temp;
		pilhaAnalisadorBackup = analyzerStack.getGrViewStack().clone();
		int toppsAux;
		int IY;
		/* percorre a pilha do analisador */
		while (!analyzerStack.getGrViewStack().empty() && !achou)
		{
			/*
			 * Ainda existem alternativas a seguir ou é necessario desempilhar
			 * um nó na pilha do analisador
			 */
			toppsAux = analyzerStack.getParseStack().size();
			if (topIndexNode != 0)
			{
				/* Procurando por um não terminal */
				while (topIndexNode != 0 && analyzerTable.getGraphNode(topIndexNode).IsTerminal())
					topIndexNode = analyzerTable.getGraphNode(topIndexNode).getAlternativeIndex();
			}
			/* Preciso desempilhar um nó da pilha do analisador */
			if (topIndexNode == 0)
			{
				/* desempilha e vai para o percurso */
				temp = analyzerStack.getGrViewStack().pop();
				topIndexNode = temp.indexNode;
				toppsAux = temp.size;
			}
			/* inicio do percurso */
			IY = analyzerTable.getGraphNode(topIndexNode).getSucessorIndex();
			/* inicializando as duas pilhas auxiliares... */
			pilhaNaoTerminalY.clear();
			pilhaAuxAnalisador.clear();
			/* percurso */
			while (IY != 0 && !achou)
			{
				if (analyzerTable.getGraphNode(IY).IsTerminal())
				{
					if (analyzerTable.getGraphNode(IY).getNodeReference() == 0)
					{
						IY = analyzerTable.getGraphNode(IY).getSucessorIndex();
					}
					else
					{
						String tmp = analyzerTable.getTermial(analyzerTable.getGraphNode(IY).getNodeReference()).getName();
						if (tmp.equals(analyzerToken.getCurrentSymbol()))
						{
							while (analyzerStack.getParseStack().size() >= toppsAux)
							{
								analyzerStack.getParseStack().pop();
							}
							analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getNTerminal(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getFlag(), analyzerTable.getNTerminal(analyzerTable.getGraphNode(topIndexNode).getNodeReference()).getName()));
							achou = true;
							analyzerIndex.setIndexNode(IY);
						}
						else
							IY = analyzerAlternative.findAlternative(IY, pilhaNaoTerminalY, pilhaAuxAnalisador);
					}
				}
				else
				{ /* é não terminal */
					pilhaAuxAnalisador.push(new GrViewNode(IY, toppsAux));
					pilhaNaoTerminalY.push(IY);
					IY = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IY).getNodeReference()).getFirstNode();

				}
			}
			if (!achou)
				topIndexNode = analyzerTable.getGraphNode(topIndexNode).getAlternativeIndex();
		}
		if (achou)
		{
			while (!pilhaAuxAnalisador.empty())
			/* copia a pilha auxiliar do analisador na pilha do analisador... */
			{
				analyzerStack.getGrViewStack().push(pilhaAuxAnalisador.pop());
			}
			AppOutput.errorRecoveryStatus("Action: the symbol in the column " + column + " has been assumed as delimiter.\n");
		}
		else
		{
			analyzerStack.setGrViewStack(pilhaAnalisadorBackup.clone());
		}
		return achou;
	}
	

}
