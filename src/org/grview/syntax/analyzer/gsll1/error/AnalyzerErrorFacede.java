package org.grview.syntax.analyzer.gsll1.error;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerIndex;
import org.grview.syntax.analyzer.gsll1.AnalyzerPrint;
import org.grview.syntax.analyzer.gsll1.AnalyzerStackRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerTableRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerToken;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.GrViewStack;
import org.grview.syntax.model.NTerminalStack;
import org.grview.syntax.model.ParseNode;
import org.grview.syntax.model.TableGraphNode;

public class AnalyzerErrorFacede
{

	private File fileIn;
	private AnalyzerTableRepository analyzerTable;
	private AnalyzerStackRepository analyzerStack;
	private AnalyzerAlternative syntaxAlternative;

	private AnalyzerToken syntaxToken;
	private SemanticRoutinesRepo semanticRoutinesRepo;
	private AnalyzerPrint analyzerPrint;
	private AnalyzerIndex analyzerIndex;

	public AnalyzerErrorFacede(File fileIn)
	{
		this.fileIn = fileIn;
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.syntaxToken = AnalyzerToken.getInstance();
		this.analyzerPrint = AnalyzerPrint.getInstance();
		this.analyzerIndex = AnalyzerIndex.getInstance();
	}	

	private boolean estrategiaBuscaDelimitador(int IX, int topps, int column, int line)
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
			 * Ainda existem alternativas a seguir ou � necessario desempilhar
			 * um n� na pilha do analisador
			 */
			toppsAux = analyzerStack.getParseStack().size();
			if (IX != 0)
			{
				/* Procurando por um n�o terminal */
				while (IX != 0 && analyzerTable.getGraphNode(IX).IsTerminal())
					IX = analyzerTable.getGraphNode(IX).getAlternativeIndex();
			}
			/* Preciso desempilhar um n� da pilha do analisador */
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
						if (tmp.equals(syntaxToken.getCurrentSymbol()))
						{
							while (analyzerStack.getParseStack().size() >= toppsAux)
							{
								analyzerStack.getParseStack().pop();
							}
							analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getNTerminal(analyzerTable.getGraphNode(IX).getNodeReference()).getFlag(), analyzerTable.getNTerminal(analyzerTable.getGraphNode(IX).getNodeReference()).getName()));
							achou = true;
							analyzerIndex.setIndexNode(IY);
						}
						else
							IY = syntaxAlternative.findAlternative(IY, pilhaNaoTerminalY, pilhaAuxAnalisador);
					}
				}
				else
				{ /* � n�o terminal */
					pilhaAuxAnalisador.push(new GrViewNode(IY, toppsAux));
					pilhaNaoTerminalY.push(IY);
					IY = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IY).getNodeReference()).getFirstNode();

				}
			}
			if (!achou)
				IX = analyzerTable.getGraphNode(IX).getAlternativeIndex();
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

	

	private boolean estrategiaTroca(int IX, int toppsIU, int column, int line)
	{
		int IY;
		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();
		boolean achou = false;
		/*
		 * Esquecer os n�o terminais que aparecem na pilha do analisador ao
		 * tentar as outras corre��os
		 */
		while (analyzerStack.getGrViewStack().size() > toppsIU)
			analyzerStack.getGrViewStack().pop();
		/* le o proximo simbolo */
		syntaxToken.readNext();
		int topps = analyzerStack.getParseStack().size();
		/* Percorre os n�s que podem ser inseridos (t1,t2....,tn) */
		while (IX != 0 && !achou)
		{
			/* o simbolo espereado � terminal? */
			if (analyzerTable.getGraphNode(IX).IsTerminal())
			{
				IY = analyzerTable.getGraphNode(IX).getSucessorIndex();
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
							if (temp.equals(syntaxToken.getCurrentSymbol()))
							{
								/* empilha ti na pilha sint�tica... */
								analyzerStack.getParseStack().push(new ParseNode(analyzerTable.getTermial(analyzerTable.getGraphNode(IX).getNodeReference()).getFlag(), analyzerTable.getTermial(analyzerTable.getGraphNode(IX).getNodeReference()).getName()));
								analyzerPrint.printStack(analyzerStack.getParseStack());
								topps++;
								/* novo n� para asin... */
								analyzerIndex.setIndexNode(IY);
								/* corre��o obteve sucesso... */
								achou = true;
							}
							else
								IY = syntaxAlternative.findAlternative(IY, pilhaNaoTerminalY, analyzerStack.getGrViewStack());
						}
					}
					/* sucessor � um n�o terminal */
					else
					{
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
					IX = syntaxAlternative.findAlternative(IX, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
			}
			else
			{
				/* n�o terminal na sequencia dos esperados */
				analyzerStack.getGrViewStack().push(new GrViewNode(IX, topps + 1));
				analyzerStack.getNTerminalStack().push(IX);
				IX = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IX).getNodeReference()).getFirstNode();
			}
		}
		if (achou)
		{
			/* novo topo... */
			/* volta um token na cabe�a de leitura do analisador l�xico. */
			// lex.yypushback(lex.yylength());
			AppOutput.errorRecoveryStatus("Action: This symbol has been replaced by " + analyzerTable.getTermial(analyzerTable.getGraphNode(IX).getNodeReference()).getName() + "\n");
		}
		else
		{
			syntaxToken.setCurrentSymbol(syntaxToken.getLastSymbol());
			/* volta um token na cabe�a de leitura do analisador l�xico. */
			syntaxToken.getYylex().pushback(syntaxToken.getYylex().yylength());
			/*
			 * Faz com que a pilha do analisador volte a ser o que era antes de
			 * usar essa estrat�gia.
			 */
			while (analyzerStack.getGrViewStack().size() > toppsIU)
			{
				analyzerStack.getGrViewStack().pop();
			}
		}
		return achou;
	}

	public boolean dealWithError(int indexNode, int toppsIU, int column, int line)
	{
		int lastIndexNode = indexNode;

		try
		{
			if (fileIn != null)
			{

				BufferedReader bufferedReader = new BufferedReader(new FileReader(fileIn));
				for (int j = 0; j < line; j++)
				{
					bufferedReader.readLine();
				}
				String wrongLine = bufferedReader.readLine();
				bufferedReader.close();
				AppOutput.displayText("\n" + wrongLine + "\n", TOPIC.Output);
			}

		}
		catch (IOException e)
		{
			if (fileIn != null)
			{
				AppOutput.displayText("File not found...", TOPIC.Output);
			}
		}
		AppOutput.displayText("Error found at the symbol "+ syntaxToken.getCurrentToken().text +" of line: " + line + ", column: " + column + ". ", TOPIC.Output);

		Stack<TableGraphNode> nTerminalStack = new Stack<TableGraphNode>();

		while (indexNode != 0)
		{
			if (analyzerTable.getGraphNode(indexNode).IsTerminal())
			{
				AppOutput.displayText(analyzerTable.getTermial(analyzerTable.getGraphNode(indexNode).getNodeReference()).getName() + " expected.", TOPIC.Output);

				indexNode = analyzerTable.getGraphNode(indexNode).getAlternativeIndex();

				if (indexNode == 0 && nTerminalStack.size() > 0)
				{
					indexNode = nTerminalStack.pop().getAlternativeIndex();
				}

			}
			else
			{
				nTerminalStack.push(analyzerTable.getGraphNode(indexNode));
				indexNode = analyzerTable.getNTerminal(analyzerTable.getGraphNode(indexNode).getNodeReference()).getFirstNode();
			}
		}
		
		IErroStrategy deleteStrategy = new DeleteStrategy();
		IErroStrategy insertStrategy = new InsertStrategy();
		
		
		if(!deleteStrategy.tryFix(lastIndexNode, toppsIU, column, line))
		{
			if(!insertStrategy.tryFix(lastIndexNode, toppsIU, column, line))
			{
				syntaxToken.readNext();
				if (syntaxToken.getCurrentToken().text.equals("$"))
				{
					return false;
				}
				else
				{
					dealWithError(lastIndexNode, toppsIU, syntaxToken.getCurrentToken().charBegin, syntaxToken.getCurrentToken().line);
				}				
			}			
		}
			
			/* Tenta corrigir o erro utilizando a estrat�gia de inser��o */
			//if (!estrategiaInsercao(lastIndexNode, toppsIU, column, line))
			//{
				//
				/* Tenta corrigir o erro utilizando a estrat�gia da troca */
				//if (!estrategiaTroca(lastIndexNode, toppsIU, column, line))
				//{
					//AppOutput.errorRecoveryStatus("Replacing a symbol stategy has not succeeded\n");
					/*
					 * Tenta corrigir o erro utilizando a estrat�gia da busca de
					 * Delimitador
					 */
					//if (!estrategiaBuscaDelimitador(lastIndexNode, toppsIU, column, line))
					//{
						//AppOutput.errorRecoveryStatus("Searching delimiters strategy has not succeeded\n");
						/*
						 * At� esse ponto nenhuma das estrat�gias tiveram
						 * sucesso ao corrigir o erro, ent�o o pr�ximo simbolo
						 * da entrada � lido a rotina de tratamento de erros �
						 * chamada novamente. Dessa vez para esse novo simbolo
						 * lido.
						 */
						//syntaxToken.readNext();
						//if (syntaxToken.getCurrentToken().text.equals("$"))
						//{
						//	return false;
						//}
						//else
						//{
						//	dealWithError(lastIndexNode, toppsIU, syntaxToken.getCurrentToken().charBegin, syntaxToken.getCurrentToken().line);
						//}
					//}
				//}
			//}
		return true;
	}

	public void setSemanticRoutinesRepo(SemanticRoutinesRepo semanticRoutinesRepo)
	{
		this.semanticRoutinesRepo = semanticRoutinesRepo;
	}

}
