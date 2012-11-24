package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
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
	private AnalyzerToken analyzerToken;

	public DelimiterSearchStrategy()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
	}

	@Override
	public int tryFix(int UI, int TOP, int column, int line)
	{
		int I = -1;
		GrViewStack pilhaAnalisadorBackup;
		GrViewStack pilhaAuxAnalisador = new GrViewStack();
		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();
		GrViewNode temp;
		pilhaAnalisadorBackup = analyzerStack.getGrViewStack().clone();
		int toppsAux;
		int TOPO_AUX = analyzerStack.getGrViewStack().size();
		int IY;
		int IX = UI;
		/* percorre a pilha do analisador */
		while (TOPO_AUX != 0 && I < 0)
		{
			/*
			 * Ainda existem alternativas a seguir ou é necessario desempilhar
			 * um nó na pilha do analisador
			 */
			toppsAux = analyzerStack.getParseStack().size();
			if (IX != 0)
			{
				/* Procurando por um não terminal */
				while (IX != 0 && analyzerTable.getGraphNode(IX).IsTerminal())
					IX = analyzerTable.getGraphNode(IX).getAlternativeIndex();
			}
			/* Preciso desempilhar um nó da pilha do analisador */
			if (IX == 0)
			{
				/* desempilha e vai para o percurso */
				temp = analyzerStack.getGrViewStack().pop();
				IX = temp.indexNode;
				toppsAux = temp.size;
			}
			/* inicio do percurso */
			IY = analyzerTable.getGraphNode(IX).getSucessorIndex();
			/* inicializando as duas pilhas auxiliares... */
			pilhaNaoTerminalY.clear();
			pilhaAuxAnalisador.clear();
			/* percurso */
			while (IY != 0 && I < 0)
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
							analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getNTerminal(analyzerTable.getGraphNode(IX).getNodeReference()).getFlag(), analyzerTable.getNTerminal(analyzerTable.getGraphNode(IX).getNodeReference()).getName()));
						    I = IY;
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
			if (I < 0)
				IX = analyzerTable.getGraphNode(IX).getAlternativeIndex();
		}
		if (I >= 0)
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
		return I;
	}
	

}
