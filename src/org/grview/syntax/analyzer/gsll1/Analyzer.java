package org.grview.syntax.analyzer.gsll1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;

import org.grview.lexical.Yylex;
import org.grview.lexical.Yytoken;
import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.model.ParseStackNode;
import org.grview.syntax.model.TabGraphNode;
import org.grview.syntax.model.TabNode;
import org.grview.util.Log;

public class Analyzer extends Thread
{
	boolean continueSentinel;
	int indexNode;
	
	
	private String currentSemanticSymbol;
	private String currentSymbol;
	private Yytoken currToken;
	private File fileIn;
	private boolean firstTime = true;
	private GrViewStackNode grViewSNode;
	
	private Yylex yylex;
	
	private Stack<GrViewStackNode> grViewStack;
	private Stack<Integer> nTermStack;
	private Stack<ParseStackNode> parseStack;
	private String lastSymbol;

	private SemanticRoutinesRepo semanticRoutinesRepo;
	private boolean stepping = false;
	
	private TabGraphNode tabGraphNodes[];
	private TabNode nTerminalTab[];
	private TabNode termialTab[];

	

	public Analyzer(TabGraphNode tabGraphNodes[], TabNode termialTab[], TabNode nTerminalTab[], File fileIn, Yylex yylex)
	{
		this.tabGraphNodes = tabGraphNodes;
		this.termialTab = termialTab;
		this.nTerminalTab = nTerminalTab;
		this.fileIn = fileIn;
		this.yylex = yylex;
	}

	private void dealWithError(int IX, int toppsIU, int column, int line)
	{
		int oldI = IX;
		/*
		 * o proximo bloco é responsável por imprimir na tela aonde o erro
		 * ocorreu
		 */
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
		
		while (IX != 0)
		{
			/* is terminal */
			if (tabGraphNodes[IX].IsTerminal())
			{
				AppOutput.displayText(termialTab[tabGraphNodes[IX].getNodeReference()].getName() + " expected.", TOPIC.Output);
				
				IX = tabGraphNodes[IX].getAlternativeIndex();
				
				/* don't have alternative but have non terminal */
				if(IX == 0 && nTerminalStack.size() > 0)
				{
					IX = nTerminalStack.pop().getAlternativeIndex();
				}
				
			}
			else
			{
				/* push non terminal in stack */
				nTerminalStack.push(tabGraphNodes[IX]);
				IX = nTerminalTab[tabGraphNodes[IX].getNodeReference()].getFirstNode();
			}
		}
		/* Tenta corrigir o erro utilizando a estratégia da eliminação */
		if (!deleteStrategy(oldI, toppsIU, column, line))
		{
			AppOutput.errorRecoveryStatus("\nDeleting a symbol strategy has not succeeded\n");
			/* Tenta corrigir o erro utilizando a estratégia de inserção */
			if (!estrategiaInsercao(oldI, toppsIU, column, line))
			{
				AppOutput.errorRecoveryStatus("Inserting a symbol strategy has not succeeded\n");
				/* Tenta corrigir o erro utilizando a estratégia da troca */
				if (!estrategiaTroca(oldI, toppsIU, column, line))
				{
					AppOutput.errorRecoveryStatus("Replacing a symbol stategy has not succeeded\n");
					/*
					 * Tenta corrigir o erro utilizando a estratégia da busca de
					 * Delimitador
					 */
					if (!estrategiaBuscaDelimitador(oldI, toppsIU, column, line))
					{
						AppOutput.errorRecoveryStatus("Searching delimiters strategy has not succeeded\n");
						/*
						 * Até esse ponto nenhuma das estratégias tiveram
						 * sucesso ao corrigir o erro, então o próximo simbolo
						 * da entrada é lido a rotina de tratamento de erros é
						 * chamada novamente. Dessa vez para esse novo simbolo
						 * lido.
						 */
						this.readNext();
						if (currToken.text.equals("$"))
						{
							continueSentinel = false;
						}
						else
						{
							this.dealWithError(oldI, toppsIU, currToken.charBegin, currToken.line);
						}
					}
				}
			}
		}
	}

	private boolean estrategiaBuscaDelimitador(int IX, int topps, int column, int line)
	{
		boolean achou = false;
		Stack pilhaAnalisadorBackup = new Stack();
		Stack<GrViewStackNode> pilhaAuxAnalisador = new Stack<GrViewStackNode>();
		Stack pilhaNaoTerminalY = new Stack();
		GrViewStackNode temp;
		pilhaAnalisadorBackup = (Stack) this.grViewStack.clone();
		int toppsAux;
		int IY;
		/* percorre a pilha do analisador */
		while (!grViewStack.empty() && !achou)
		{
			/*
			 * Ainda existem alternativas a seguir ou é necessario desempilhar
			 * um nó na pilha do analisador
			 */
			toppsAux = this.parseStack.size();
			if (IX != 0)
			{
				/* Procurando por um não terminal */
				while (IX != 0 && this.tabGraphNodes[IX].IsTerminal())
					IX = this.tabGraphNodes[IX].getAlternativeIndex();
			}
			/* Preciso desempilhar um nó da pilha do analisador */
			if (IX == 0)
			{
				/* desempilha e vai para o percurso */
				temp = (GrViewStackNode) grViewStack.pop();
				IX = temp.indexNode;
				toppsAux = temp.size;
			}
			/* inicio do percurso */
			IY = this.tabGraphNodes[IX].getSucessorIndex();
			/* inicializando as duas pilhas auxiliares... */
			pilhaNaoTerminalY.clear();
			pilhaAuxAnalisador.clear();
			/* percurso */
			while (IY != 0 && !achou)
			{
				/* é terminal? */
				if (this.tabGraphNodes[IY].IsTerminal())
				{
					/* é lambda-nó? */
					if (this.tabGraphNodes[IY].getNodeReference() == 0)
					{
						IY = this.tabGraphNodes[IY].getSucessorIndex();
					}
					else
					{
						String tmp = this.termialTab[this.tabGraphNodes[IY].getNodeReference()].getName();
						if (tmp.equals(this.currentSymbol))
						{
							while (this.parseStack.size() >= toppsAux)
							{
								this.parseStack.pop();
							}
							this.parseStack.push(new ParseStackNode(this.nTerminalTab[this.tabGraphNodes[IX].getNodeReference()].getFlag(), this.nTerminalTab[this.tabGraphNodes[IX].getNodeReference()].getName()));
							achou = true;
							indexNode = IY;
						}
						else
							IY = this.findAlternative(IY, pilhaNaoTerminalY, pilhaAuxAnalisador);
					}
				}
				else
				{ /* é não terminal */
					pilhaAuxAnalisador.push(new GrViewStackNode(IY, toppsAux));
					pilhaNaoTerminalY.push(IY);
					IY = this.nTerminalTab[this.tabGraphNodes[IY].getNodeReference()].getFirstNode();

				}
			}
			if (!achou)
				IX = this.tabGraphNodes[IX].getAlternativeIndex();
		}
		if (achou)
		{
			/* copia a pilha auxiliar do analisador na pilha do analisador... */
			for (int i = 0; i < pilhaAuxAnalisador.size(); i++)
			{
				this.grViewStack.push(pilhaAuxAnalisador.elementAt(i));
			}
			// AppOutput.displayText(wrongLine);
			// for (int i = 0; i < column; i++)
			// AppOutput.displayText(">");
			AppOutput.errorRecoveryStatus("Action: the symbol in the column " + column + " has been assumed as delimiter.\n");
		}
		else
		{
			/* Deixa tudo como estava... */
			this.grViewStack = (Stack) pilhaAnalisadorBackup.clone();
		}
		return achou;
	}

	private boolean deleteStrategy(int topIndexNodeTemp, int topParseStackSizeTemp, int column, int line)
	{
		boolean success = false;
		
		readNext();

		while (topIndexNodeTemp != 0)
		{

			if (tabGraphNodes[topIndexNodeTemp].IsTerminal())
			{
				if (termialTab[tabGraphNodes[topIndexNodeTemp].getNodeReference()].getName().equals(currentSymbol))
				{
					
					AppOutput.displayText("Action: "+lastSymbol+" symbol will be deleted\n", TOPIC.Output);
					
					parseStack.push(new ParseStackNode(termialTab[tabGraphNodes[topIndexNodeTemp].getNodeReference()].getFlag(), currentSymbol, currentSemanticSymbol));
										
					semanticRoutinesRepo.setCurrentToken(currToken);
					semanticRoutinesRepo.execFunction(tabGraphNodes[topIndexNodeTemp].getSemanticRoutine());
					
					readNext();

					nTermStack.clear();
					
					indexNode = tabGraphNodes[topIndexNodeTemp].getSucessorIndex();
					
					topIndexNodeTemp = indexNode;
					
					topParseStackSizeTemp = this.parseStack.size();
					
					success = true;
					
					break;
				}
				else
				{
					int alternative = 0;
					alternative = this.findAlternative(topIndexNodeTemp, nTermStack, grViewStack);
					topIndexNodeTemp = alternative;
				}
			}
			else
			{
				grViewStack.push(new GrViewStackNode(topIndexNodeTemp, parseStack.size() + 1));
				nTermStack.push(topIndexNodeTemp);
				topIndexNodeTemp = nTerminalTab[tabGraphNodes[topIndexNodeTemp].getNodeReference()].getFirstNode();
			}
		}
		if (!success)
		{
			currentSymbol = lastSymbol;
			yylex.pushback(yylex.yylength());
			while (this.grViewStack.size() > topParseStackSizeTemp)
			{
				this.grViewStack.pop();
			}
		}
		return success;
	}

	private boolean estrategiaInsercao(int IX, int toppsIU, int column, int line)
	{
		int IY;
		Stack pilhaNaoTerminalY = new Stack();
		boolean achou = false;
		// int topo = this.asinStack.size()-1;
		int topps = this.parseStack.size();
		// int topoaux1 = topo;
		// int topoaux2 = 0;
		/* Percorre os nós que podem ser inseridos (t1,t2....,tn) */
		while (IX != 0 && !achou)
		{
			/* o simbolo espereado é terminal? */
			if (this.tabGraphNodes[IX].IsTerminal())
			{
				// topoaux2 = topoaux1;
				IY = this.tabGraphNodes[IX].getSucessorIndex();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* é terminal? */
					if (this.tabGraphNodes[IY].IsTerminal())
					{
						/* é lambda-nó? */
						if (this.tabGraphNodes[IY].getNodeReference() == 0)
						{
							IY = this.tabGraphNodes[IY].getSucessorIndex();
						}
						else
						{
							String temp = this.termialTab[this.tabGraphNodes[IY].getNodeReference()].getName();
							if (temp.equals(this.currentSymbol))
							{
								/* empilha ti na pilha sintática... */
								this.parseStack.push(new ParseStackNode(this.termialTab[this.tabGraphNodes[IX].getNodeReference()].getFlag(), this.termialTab[this.tabGraphNodes[IX].getNodeReference()].getName()));
								this.printStack(parseStack);
								topps++;
								/* novo nó para asin... */
								indexNode = IY;
								/* correção obteve sucesso... */
								achou = true;
							}
							else
								IY = this.findAlternative(IY, pilhaNaoTerminalY, grViewStack);
						}
					}
					/* sucessor é um não terminal */
					else
					{
						// topoaux2 = topoaux2 + 1;
						// this.asinStack.add(topoaux2,new GrViewStackNode(IY,
						// topps + 2));
						this.grViewStack.push(new GrViewStackNode(IY, topps + 2));
						pilhaNaoTerminalY.push(IY);
						IY = this.nTerminalTab[this.tabGraphNodes[IY].getNodeReference()].getFirstNode();
					}
				}
				/*
				 * Nesse ponto a pilhaNaoTerminalY está vazia, portanto qualquer
				 * nó que tenha sido colocado na grViewStack na busca das
				 * alternativas do nó IY, já foi removido. Logo, posso
				 * desenpilhar um nó da pilha do analisador na busca de uma
				 * alternativa do nó IX sem problemas pois se a nTermStack não
				 * está vazia, o topo da pilha do analisador representa um nó
				 * não terminal que foi utilizado para chegar até o nó IX.
				 */
				if (!achou)
					IX = this.findAlternative(IX, nTermStack, grViewStack);
			}
			else
			{
				/* não terminal na sequencia dos esperados */
				// topoaux1 = topoaux1 + 1;
				// this.asinStack.add(topoaux1,new GrViewStackNode(IX, topps +
				// 1));
				this.grViewStack.push(new GrViewStackNode(IX, topps + 1));
				this.nTermStack.push(IX);
				IX = this.nTerminalTab[this.tabGraphNodes[IX].getNodeReference()].getFirstNode();
			}
		}
		if (achou)
		{
			/* novo topo... */
			// topo = topoaux2;
			// currentSymbol = pastSymbol;
			/* volta um token na cabeça de leitura do analisador léxico. */
			// lex.yypushback(lex.yylength());
			AppOutput.errorRecoveryStatus("Action: " + this.termialTab[this.tabGraphNodes[IX].getNodeReference()].getName() + " inserted before column " + column + "\n");
		}
		/*
		 * deixa a pilha do Analisador como estava antes de começar essa
		 * estratégia
		 */
		else
		{
			while (this.grViewStack.size() > toppsIU)
			{
				this.grViewStack.pop();
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
		while (grViewStack.size() > toppsIU)
			grViewStack.pop();
		/* le o proximo simbolo */
		this.readNext();
		int topps = this.parseStack.size();
		/* Percorre os nós que podem ser inseridos (t1,t2....,tn) */
		while (IX != 0 && !achou)
		{
			/* o simbolo espereado é terminal? */
			if (this.tabGraphNodes[IX].IsTerminal())
			{
				IY = this.tabGraphNodes[IX].getSucessorIndex();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* é terminal? */
					if (this.tabGraphNodes[IY].IsTerminal())
					{
						/* é lambda-nó? */
						if (this.tabGraphNodes[IY].getNodeReference() == 0)
						{
							IY = this.tabGraphNodes[IY].getSucessorIndex();
						}
						else
						{
							String temp = this.termialTab[this.tabGraphNodes[IY].getNodeReference()].getName();
							if (temp.equals(this.currentSymbol))
							{
								/* empilha ti na pilha sintática... */
								this.parseStack.push(new ParseStackNode(this.termialTab[this.tabGraphNodes[IX].getNodeReference()].getFlag(), this.termialTab[this.tabGraphNodes[IX].getNodeReference()].getName()));
								this.printStack(parseStack);
								topps++;
								/* novo nó para asin... */
								indexNode = IY;
								/* correção obteve sucesso... */
								achou = true;
							}
							else
								IY = this.findAlternative(IY, pilhaNaoTerminalY, grViewStack);
						}
					}
					/* sucessor é um não terminal */
					else
					{
						this.grViewStack.push(new GrViewStackNode(IY, topps + 2));
						pilhaNaoTerminalY.push(IY);
						IY = this.nTerminalTab[this.tabGraphNodes[IY].getNodeReference()].getFirstNode();
					}
				}
				/*
				 * Nesse ponto a pilhaNaoTerminalY está vazia, portanto qualquer
				 * nó que tenha sido colocado na grViewStack na busca das
				 * alternativas do nó IY, já foi removido. Logo, posso
				 * desenpilhar um nó da pilha do analisador na busca de uma
				 * alternativa do nó IX sem problemas pois se a nTermStack não
				 * está vazia, o topo da pilha do analisador representa um nó
				 * não terminal que foi utilizado para chegar até o nó IX.
				 */
				if (!achou)
					IX = this.findAlternative(IX, nTermStack, grViewStack);
			}
			else
			{
				/* não terminal na sequencia dos esperados */
				this.grViewStack.push(new GrViewStackNode(IX, topps + 1));
				this.nTermStack.push(IX);
				IX = this.nTerminalTab[this.tabGraphNodes[IX].getNodeReference()].getFirstNode();
			}
		}
		if (achou)
		{
			/* novo topo... */
			/* volta um token na cabeça de leitura do analisador léxico. */
			// lex.yypushback(lex.yylength());
			AppOutput.errorRecoveryStatus("Action: This symbol has been replaced by " + this.termialTab[this.tabGraphNodes[IX].getNodeReference()].getName() + "\n");
		}
		else
		{
			currentSymbol = lastSymbol;
			/* volta um token na cabeça de leitura do analisador léxico. */
			yylex.pushback(yylex.yylength());
			/*
			 * Faz com que a pilha do analisador volte a ser o que era antes de
			 * usar essa estratégia.
			 */
			while (this.grViewStack.size() > toppsIU)
			{
				this.grViewStack.pop();
			}
		}
		return achou;
	}

	/*
	 * Esta função é chamada quando o nó atual não possui como alternativa um
	 * terminal mas sim um não terminal. Então percorremos os não terminais
	 * guardados na pilha nãoTerminal até achar um simbolo terminal. Como tal
	 * simbolo é alternativa do nó sendo visitado, retornamos ele. Se não existe
	 * tal simbolo, retornamos "0" para indicar que o nó não possui
	 * alternativas.
	 */
	private int findAlternative(int IZ, Stack pilhaNaoTerm, Stack pilhaAna)
	{
		int alternative = 0;
		alternative = this.tabGraphNodes[IZ].getAlternativeIndex();
		while (alternative == 0 && !pilhaNaoTerm.empty())
		{
			pilhaAna.pop();
			alternative = tabGraphNodes[((Integer) pilhaNaoTerm.pop()).intValue()].getAlternativeIndex();
		}
		return alternative;
	}

	public void printStack(Stack s)
	{
		if (stepping && !firstTime)
		{
			synchronized (this)
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
					Log.log(Log.ERROR, this, "An internal error has occurred!", e);
				}
			}
		}
		firstTime = false;
		Iterator i = s.iterator();
		ParseStackNode tmp;
		String lineSyn = "";
		String lineSem = "";
		while (i.hasNext())
		{
			tmp = (ParseStackNode) i.next();
			lineSyn += "<a style=\"color: #000000; text-decoration: none; font-weight: bold;\" href=\"" + tmp.getFlag() + "\">" + tmp.getSyn() + "</a>&nbsp;";
			lineSem += tmp.getSem() + "&nbsp;";
		}
		AppOutput.showAndSelectNode(((ParseStackNode) s.peek()).getFlag());
		AppOutput.printlnSyntaxStack(lineSyn, true);
		AppOutput.printlnSemanticStack(lineSem, true);
	}


	/**
	 * Read next char
	 */
	public void readNext()
	{
		try
		{
			currToken = yylex.yylex();
			lastSymbol = currentSymbol;
			if (currToken.type.equals("Res") || currToken.type.equals("Esp") || currToken.type.equals("EOF"))
			{
				currentSymbol = currToken.text;
			}
			else
			{
				currentSymbol = currToken.type;
			}
			currentSemanticSymbol = currToken.text;
		}
		catch (IOException e)
		{
			AppOutput.printlnToken("Token read error\n");
		}
		AppOutput.printToken("Current Token: " + currToken);
	}

	@Override
	public void run()
	{
		int topParseStackSize;
		int topIndexNode;
		ParseStackNode auxParseSNode = null;
		
		currentSemanticSymbol = null;	
		
		grViewStack = new Stack<GrViewStackNode>();
		parseStack = new Stack<ParseStackNode>();
		nTermStack = new Stack<Integer>();		
		
		boolean sucess = true;
		
		semanticRoutinesRepo = new SemanticRoutinesRepo(parseStack, termialTab);

		yylex.TabT(termialTab);

		tabGraphNodes[0] = new TabGraphNode();
		tabGraphNodes[0].setIsTerminal(false);
		tabGraphNodes[0].setNodeReference(1);
		tabGraphNodes[0].setAlternativeIndex(0);
		tabGraphNodes[0].setSucessorIndex(0);
		grViewStack.push(new GrViewStackNode(0, 1));

		readNext();
		
		topIndexNode = indexNode = nTerminalTab[1].getFirstNode();
		topParseStackSize = 0;
		continueSentinel = true;
		while (continueSentinel)
		{
			if (indexNode != 0)
			{
				if (tabGraphNodes[indexNode].IsTerminal())
					if (tabGraphNodes[indexNode].getNodeReference() == 0)
					{
						semanticRoutinesRepo.setCurrentToken(null);
						semanticRoutinesRepo.execFunction(tabGraphNodes[indexNode].getSemanticRoutine());
						indexNode = tabGraphNodes[indexNode].getSucessorIndex();
						topIndexNode = indexNode;
						topParseStackSize = this.parseStack.size();
					}
					else
					{
						if ((termialTab[tabGraphNodes[indexNode].getNodeReference()].getName()).equals(currentSymbol))
						{
							parseStack.push(new ParseStackNode(termialTab[tabGraphNodes[indexNode].getNodeReference()].getFlag(), currentSymbol, currentSemanticSymbol));
							printStack(parseStack);
							
							semanticRoutinesRepo.setCurrentToken(currToken);
							semanticRoutinesRepo.execFunction(tabGraphNodes[indexNode].getSemanticRoutine());
							readNext();
							
							nTermStack.clear();
							
							indexNode = tabGraphNodes[indexNode].getSucessorIndex();
							topIndexNode = indexNode;
							topParseStackSize = this.parseStack.size();
						}
						else
						{
							if (tabGraphNodes[indexNode].getAlternativeIndex() != 0)
							{
								indexNode = tabGraphNodes[indexNode].getAlternativeIndex();
							}
							else
							{
								if (nTermStack.empty())
								{
									dealWithError(topIndexNode, topParseStackSize, currToken.charBegin + 1, currToken.line + 1);
									sucess = false;
								}
								else
								{
									int alternative;
									alternative = this.findAlternative(indexNode, nTermStack, grViewStack);
									if (alternative != 0)
									{
										indexNode = alternative;
									}
									else
									{
										dealWithError(topIndexNode, topParseStackSize, currToken.charBegin + 1, currToken.line + 1);
										sucess = false;
									}
								}
							}
						}
					}
				else
				{
					grViewStack.push(new GrViewStackNode(indexNode, parseStack.size() + 1));
					nTermStack.push(indexNode);
					indexNode = nTerminalTab[tabGraphNodes[indexNode].getNodeReference()].getFirstNode();
				}
			}
			else
			{
				if (!grViewStack.empty())
				{

					grViewSNode = (GrViewStackNode) grViewStack.pop();

					while (parseStack.size() >= grViewSNode.size)
					{
						auxParseSNode = (ParseStackNode) parseStack.pop();
					}
					
					parseStack.push(new ParseStackNode(nTerminalTab[tabGraphNodes[grViewSNode.indexNode].getNodeReference()].getFlag(), nTerminalTab[tabGraphNodes[grViewSNode.indexNode].getNodeReference()].getName(), auxParseSNode.getSem()));
					printStack(parseStack);
					
					indexNode = grViewSNode.indexNode;
					
					semanticRoutinesRepo.setCurrentToken(currToken);
					semanticRoutinesRepo.execFunction(tabGraphNodes[indexNode].getSemanticRoutine());
					
					indexNode = tabGraphNodes[indexNode].getSucessorIndex();
					topIndexNode = indexNode;
					topParseStackSize = this.parseStack.size();
				}
				else
				{
					if (!currentSymbol.equals(new String("$")))
					{
						sucess = false;
						// AppOutput.displayText("End of File");
						//sucess = true;
					}
//					else
//					{
//						AppOutput.displayText("The fist non-teminal of the grammar has been recognized. ", TOPIC.Output);
//						AppOutput.displayText("But the end-of-file symbol has not been recognized.", TOPIC.Output);
//						
//					}
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
		this.stepping = stepping;
	}
}
