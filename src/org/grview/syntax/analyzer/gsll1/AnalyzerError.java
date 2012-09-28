package org.grview.syntax.analyzer.gsll1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.model.ParseStackNode;
import org.grview.syntax.model.TabGraphNode;

public class AnalyzerError
{
	
	private File fileIn;
	private AnalyzerTabs analyzerTabs;
	private AnalyzerStacks analyzerStacks;
	private AnalyzerAlternative syntaxAlternative;
	
	private AnalyzerToken syntaxToken;
	private SemanticRoutinesRepo semanticRoutinesRepo;
	private AnalyzerPrint analyzerPrint;
	private AnalyzerIndex analyzerIndex;
	
	public AnalyzerError(File fileIn, AnalyzerTabs analyzerTabs,AnalyzerStacks analyzerStacks, AnalyzerPrint analyzerPrint, AnalyzerIndex analyzerIndex, AnalyzerToken syntaxToken)
	{
		this.fileIn = fileIn;
		this.analyzerTabs = analyzerTabs;
		this.analyzerStacks = analyzerStacks;
		this.syntaxToken = syntaxToken;
		this.analyzerPrint = analyzerPrint;
		this.analyzerIndex = analyzerIndex;
		syntaxAlternative = new AnalyzerAlternative(analyzerTabs);
		
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
		AppOutput.displayText("Error found at the symbol of line: " + line + ", column: " + column + ". ", TOPIC.Output);

		
		Stack<TabGraphNode> nTerminalStack = new Stack<TabGraphNode>();
		
		while (indexNode != 0)
		{
			/* is terminal */
			if (analyzerTabs.getTabGraphNodes()[indexNode].IsTerminal())
			{
				AppOutput.displayText(analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[indexNode].getNodeReference()].getName() + " expected.", TOPIC.Output);
				
				indexNode = analyzerTabs.getTabGraphNodes()[indexNode].getAlternativeIndex();
				
				/* don't have alternative but have non terminal */
				if(indexNode == 0 && nTerminalStack.size() > 0)
				{
					indexNode = nTerminalStack.pop().getAlternativeIndex();
				}
				
			}
			else
			{
				/* push non terminal in stack */
				nTerminalStack.push(analyzerTabs.getTabGraphNodes()[indexNode]);
				indexNode = analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[indexNode].getNodeReference()].getFirstNode();
			}
		}
		/* Tenta corrigir o erro utilizando a estratégia da eliminação */
		if (!deleteStrategy(lastIndexNode, toppsIU, column, line))
		{
			AppOutput.errorRecoveryStatus("\nDeleting a symbol strategy has not succeeded\n");
			/* Tenta corrigir o erro utilizando a estratégia de inserção */
			if (!estrategiaInsercao(lastIndexNode, toppsIU, column, line))
			{
				AppOutput.errorRecoveryStatus("Inserting a symbol strategy has not succeeded\n");
				/* Tenta corrigir o erro utilizando a estratégia da troca */
				if (!estrategiaTroca(lastIndexNode, toppsIU, column, line))
				{
					AppOutput.errorRecoveryStatus("Replacing a symbol stategy has not succeeded\n");
					/*
					 * Tenta corrigir o erro utilizando a estratégia da busca de
					 * Delimitador
					 */
					if (!estrategiaBuscaDelimitador(lastIndexNode, toppsIU, column, line))
					{
						AppOutput.errorRecoveryStatus("Searching delimiters strategy has not succeeded\n");
						/*
						 * Até esse ponto nenhuma das estratégias tiveram
						 * sucesso ao corrigir o erro, então o próximo simbolo
						 * da entrada é lido a rotina de tratamento de erros é
						 * chamada novamente. Dessa vez para esse novo simbolo
						 * lido.
						 */
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
			}
			
		}
		return true;
	}
	

	private boolean estrategiaBuscaDelimitador(int IX, int topps, int column, int line)
	{
		boolean achou = false;
		Stack pilhaAnalisadorBackup = new Stack();
		Stack<GrViewStackNode> pilhaAuxAnalisador = new Stack<GrViewStackNode>();
		Stack pilhaNaoTerminalY = new Stack();
		GrViewStackNode temp;
		pilhaAnalisadorBackup = (Stack) analyzerStacks.getGrViewStack().clone();
		int toppsAux;
		int IY;
		/* percorre a pilha do analisador */
		while (!analyzerStacks.getGrViewStack().empty() && !achou)
		{
			/*
			 * Ainda existem alternativas a seguir ou é necessario desempilhar
			 * um nó na pilha do analisador
			 */
			toppsAux = analyzerStacks.getParseStack().size();
			if (IX != 0)
			{
				/* Procurando por um não terminal */
				while (IX != 0 && analyzerTabs.getTabGraphNodes()[IX].IsTerminal())
					IX = analyzerTabs.getTabGraphNodes()[IX].getAlternativeIndex();
			}
			/* Preciso desempilhar um nó da pilha do analisador */
			if (IX == 0)
			{
				/* desempilha e vai para o percurso */
				temp = (GrViewStackNode) analyzerStacks.getGrViewStack().pop();
				IX = temp.indexNode;
				toppsAux = temp.size;
			}
			/* inicio do percurso */
			IY = analyzerTabs.getTabGraphNodes()[IX].getSucessorIndex();
			/* inicializando as duas pilhas auxiliares... */
			pilhaNaoTerminalY.clear();
			pilhaAuxAnalisador.clear();
			/* percurso */
			while (IY != 0 && !achou)
			{
				if (analyzerTabs.getTabGraphNodes()[IY].IsTerminal())
				{
					if (analyzerTabs.getTabGraphNodes()[IY].getNodeReference() == 0)
					{
						IY = analyzerTabs.getTabGraphNodes()[IY].getSucessorIndex();
					}
					else
					{
						String tmp = analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[IY].getNodeReference()].getName();
						if (tmp.equals(syntaxToken.getCurrentSymbol()))
						{
							while (analyzerStacks.getParseStack().size() >= toppsAux)
							{
								analyzerStacks.getParseStack().pop();
							}
							analyzerStacks.getParseStack().push(new ParseStackNode(analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getFlag(), analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getName()));
							achou = true;
							analyzerIndex.setIndexNode(IY);
						}
						else
							IY = syntaxAlternative.findAlternative(IY, pilhaNaoTerminalY, pilhaAuxAnalisador);
					}
				}
				else
				{ /* é não terminal */
					pilhaAuxAnalisador.push(new GrViewStackNode(IY, toppsAux));
					pilhaNaoTerminalY.push(IY);
					IY = analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[IY].getNodeReference()].getFirstNode();

				}
			}
			if (!achou)
				IX = analyzerTabs.getTabGraphNodes()[IX].getAlternativeIndex();
		}
		if (achou)
		{
			/* copia a pilha auxiliar do analisador na pilha do analisador... */
			for (int i = 0; i < pilhaAuxAnalisador.size(); i++)
			{
				analyzerStacks.getGrViewStack().push(pilhaAuxAnalisador.elementAt(i));
			}
			// AppOutput.displayText(wrongLine);
			// for (int i = 0; i < column; i++)
			// AppOutput.displayText(">");
			AppOutput.errorRecoveryStatus("Action: the symbol in the column " + column + " has been assumed as delimiter.\n");
		}
		else
		{
			/* Deixa tudo como estava... */
			analyzerStacks.setGrViewStack((Stack) pilhaAnalisadorBackup.clone());
		}
		return achou;
	}

	private boolean deleteStrategy(int topIndexNodeTemp, int topParseStackSizeTemp, int column, int line)
	{
		boolean success = false;
		
		syntaxToken.readNext();

		while (topIndexNodeTemp != 0)
		{

			if (analyzerTabs.getTabGraphNodes()[topIndexNodeTemp].IsTerminal())
			{
				if (analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[topIndexNodeTemp].getNodeReference()].getName().equals(syntaxToken.getCurrentSymbol()))
				{
					
					AppOutput.displayText("Action: "+syntaxToken.getLastSymbol()+" symbol deleted\n", TOPIC.Output);
					
					analyzerStacks.getParseStack().push(new ParseStackNode(analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[topIndexNodeTemp].getNodeReference()].getFlag(), syntaxToken.getCurrentSymbol(), syntaxToken.getCurrentSemanticSymbol()));
										
					semanticRoutinesRepo.setCurrentToken(syntaxToken.getCurrentToken());
					semanticRoutinesRepo.execFunction(analyzerTabs.getTabGraphNodes()[topIndexNodeTemp].getSemanticRoutine());
					
					syntaxToken.readNext();

					analyzerStacks.getnTermStack().clear();
					
					analyzerIndex.setIndexNode(analyzerTabs.getTabGraphNodes()[topIndexNodeTemp].getSucessorIndex());
					
					topIndexNodeTemp = analyzerIndex.getIndexNode();
					
					topParseStackSizeTemp = analyzerStacks.getParseStack().size();
					
					success = true;
					
					break;
				}
				else
				{
					int alternative = 0;
					alternative = syntaxAlternative.findAlternative(topIndexNodeTemp, analyzerStacks.getnTermStack(), analyzerStacks.getGrViewStack());
					topIndexNodeTemp = alternative;
				}
			}
			else
			{
				analyzerStacks.getGrViewStack().push(new GrViewStackNode(topIndexNodeTemp, analyzerStacks.getParseStack().size() + 1));
				analyzerStacks.getnTermStack().push(topIndexNodeTemp);
				topIndexNodeTemp = analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[topIndexNodeTemp].getNodeReference()].getFirstNode();
			}
		}
		if (!success)
		{
			syntaxToken.setCurrentSymbol(syntaxToken.getLastSymbol());
			syntaxToken.getYylex().pushback(syntaxToken.getYylex().yylength());
			while (analyzerStacks.getGrViewStack().size() > topParseStackSizeTemp)
			{
				analyzerStacks.getGrViewStack().pop();
			}
		}
		return success;
	}

	private boolean estrategiaInsercao(int IX, int toppsIU, int column, int line)
	{
		int IY;
		Stack pilhaNaoTerminalY = new Stack();
		boolean achou = false;
		// int topo = asinStack.size()-1;
		int topps = analyzerStacks.getParseStack().size();
		// int topoaux1 = topo;
		// int topoaux2 = 0;
		/* Percorre os nós que podem ser inseridos (t1,t2....,tn) */
		while (IX != 0 && !achou)
		{
			/* o simbolo espereado é terminal? */
			if (analyzerTabs.getTabGraphNodes()[IX].IsTerminal())
			{
				// topoaux2 = topoaux1;
				IY = analyzerTabs.getTabGraphNodes()[IX].getSucessorIndex();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* é terminal? */
					if (analyzerTabs.getTabGraphNodes()[IY].IsTerminal())
					{
						/* é lambda-nó? */
						if (analyzerTabs.getTabGraphNodes()[IY].getNodeReference() == 0)
						{
							IY = analyzerTabs.getTabGraphNodes()[IY].getSucessorIndex();
						}
						else
						{
							String temp = analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[IY].getNodeReference()].getName();
							if (temp.equals(syntaxToken.getCurrentSymbol()))
							{
								/* empilha ti na pilha sintática... */
								analyzerStacks.getParseStack().push(new ParseStackNode(analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getFlag(), analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getName()));
								analyzerPrint.printStack(analyzerStacks.getParseStack());
								topps++;
								/* novo nó para asin... */
								analyzerIndex.setIndexNode(IY);
								/* correção obteve sucesso... */
								achou = true;
							}
							else
								IY = syntaxAlternative.findAlternative(IY, pilhaNaoTerminalY, analyzerStacks.getGrViewStack());
						}
					}
					/* sucessor é um não terminal */
					else
					{
						// topoaux2 = topoaux2 + 1;
						// asinStack.add(topoaux2,new GrViewStackNode(IY,
						// topps + 2));
						analyzerStacks.getGrViewStack().push(new GrViewStackNode(IY, topps + 2));
						pilhaNaoTerminalY.push(IY);
						IY = analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[IY].getNodeReference()].getFirstNode();
					}
				}
				/*
				 * Nesse ponto a pilhaNaoTerminalY está vazia, portanto qualquer
				 * nó que tenha sido colocado na analyzerStacks.getGrViewStack() na busca das
				 * alternativas do nó IY, já foi removido. Logo, posso
				 * desenpilhar um nó da pilha do analisador na busca de uma
				 * alternativa do nó IX sem problemas pois se a analyzerStacks.getnTermStack() não
				 * está vazia, o topo da pilha do analisador representa um nó
				 * não terminal que foi utilizado para chegar até o nó IX.
				 */
				if (!achou)
					IX = syntaxAlternative.findAlternative(IX, analyzerStacks.getnTermStack(), analyzerStacks.getGrViewStack());
			}
			else
			{
				/* não terminal na sequencia dos esperados */
				// topoaux1 = topoaux1 + 1;
				// asinStack.add(topoaux1,new GrViewStackNode(IX, topps +
				// 1));
				analyzerStacks.getGrViewStack().push(new GrViewStackNode(IX, topps + 1));
				analyzerStacks.getnTermStack().push(IX);
				IX = analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getFirstNode();
			}
		}
		if (achou)
		{
			/* novo topo... */
			// topo = topoaux2;
			// syntaxToken.getCurrentSymbol() = pastSymbol;
			/* volta um token na cabeça de leitura do analisador léxico. */
			// lex.yypushback(lex.yylength());
			AppOutput.errorRecoveryStatus("Action: " + analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getName() + " inserted before column " + column + "\n");
		}
		/*
		 * deixa a pilha do Analisador como estava antes de começar essa
		 * estratégia
		 */
		else
		{
			while (analyzerStacks.getGrViewStack().size() > toppsIU)
			{
				analyzerStacks.getGrViewStack().pop();
			}

		}
		return achou;
	}

	private boolean estrategiaTroca(int IX, int toppsIU, int column, int line)
	{
		int IY;
		Stack pilhaNaoTerminalY = new Stack();
		boolean achou = false;
		/*
		 * Esquecer os não terminais que aparecem na pilha do analisador ao
		 * tentar as outras correçãos
		 */
		while (analyzerStacks.getGrViewStack().size() > toppsIU)
			analyzerStacks.getGrViewStack().pop();
		/* le o proximo simbolo */
		syntaxToken.readNext();
		int topps = analyzerStacks.getParseStack().size();
		/* Percorre os nós que podem ser inseridos (t1,t2....,tn) */
		while (IX != 0 && !achou)
		{
			/* o simbolo espereado é terminal? */
			if (analyzerTabs.getTabGraphNodes()[IX].IsTerminal())
			{
				IY = analyzerTabs.getTabGraphNodes()[IX].getSucessorIndex();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* é terminal? */
					if (analyzerTabs.getTabGraphNodes()[IY].IsTerminal())
					{
						/* é lambda-nó? */
						if (analyzerTabs.getTabGraphNodes()[IY].getNodeReference() == 0)
						{
							IY = analyzerTabs.getTabGraphNodes()[IY].getSucessorIndex();
						}
						else
						{
							String temp = analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[IY].getNodeReference()].getName();
							if (temp.equals(syntaxToken.getCurrentSymbol()))
							{
								/* empilha ti na pilha sintática... */
								analyzerStacks.getParseStack().push(new ParseStackNode(analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getFlag(), analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getName()));
								analyzerPrint.printStack(analyzerStacks.getParseStack());
								topps++;
								/* novo nó para asin... */
								analyzerIndex.setIndexNode(IY);
								/* correção obteve sucesso... */
								achou = true;
							}
							else
								IY = syntaxAlternative.findAlternative(IY, pilhaNaoTerminalY, analyzerStacks.getGrViewStack());
						}
					}
					/* sucessor é um não terminal */
					else
					{
						analyzerStacks.getGrViewStack().push(new GrViewStackNode(IY, topps + 2));
						pilhaNaoTerminalY.push(IY);
						IY = analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[IY].getNodeReference()].getFirstNode();
					}
				}
				/*
				 * Nesse ponto a pilhaNaoTerminalY está vazia, portanto qualquer
				 * nó que tenha sido colocado na analyzerStacks.getGrViewStack() na busca das
				 * alternativas do nó IY, já foi removido. Logo, posso
				 * desenpilhar um nó da pilha do analisador na busca de uma
				 * alternativa do nó IX sem problemas pois se a analyzerStacks.getnTermStack() não
				 * está vazia, o topo da pilha do analisador representa um nó
				 * não terminal que foi utilizado para chegar até o nó IX.
				 */
				if (!achou)
					IX = syntaxAlternative.findAlternative(IX, analyzerStacks.getnTermStack(), analyzerStacks.getGrViewStack());
			}
			else
			{
				/* não terminal na sequencia dos esperados */
				analyzerStacks.getGrViewStack().push(new GrViewStackNode(IX, topps + 1));
				analyzerStacks.getnTermStack().push(IX);
				IX = analyzerTabs.getnTerminalTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getFirstNode();
			}
		}
		if (achou)
		{
			/* novo topo... */
			/* volta um token na cabeça de leitura do analisador léxico. */
			// lex.yypushback(lex.yylength());
			AppOutput.errorRecoveryStatus("Action: This symbol has been replaced by " + analyzerTabs.getTermialTab()[analyzerTabs.getTabGraphNodes()[IX].getNodeReference()].getName() + "\n");
		}
		else
		{
			syntaxToken.setCurrentSymbol(syntaxToken.getLastSymbol());
			/* volta um token na cabeça de leitura do analisador léxico. */
			syntaxToken.getYylex().pushback(syntaxToken.getYylex().yylength());
			/*
			 * Faz com que a pilha do analisador volte a ser o que era antes de
			 * usar essa estratégia.
			 */
			while (analyzerStacks.getGrViewStack().size() > toppsIU)
			{
				analyzerStacks.getGrViewStack().pop();
			}
		}
		return achou;
	}

	public void setSemanticRoutinesRepo(SemanticRoutinesRepo semanticRoutinesRepo)
	{
		this.semanticRoutinesRepo = semanticRoutinesRepo;		
	}

}
