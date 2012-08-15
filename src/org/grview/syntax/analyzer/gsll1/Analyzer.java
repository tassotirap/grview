/*
 * Created on 21/08/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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

/**
 * @author gohan
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class Analyzer extends Thread
{
	class GrViewStackNode
	{
		public int no;
		public int r;

		GrViewStackNode(int no, int r)
		{
			this.no = no;
			this.r = r;
		}

		@Override
		public String toString()
		{
			return no + "," + r;

		}
	}

	private SemanticRoutinesRepo sr;
	private String currentSymbol;
	private Object currentSemanticSymbol;
	private String pastSymbol;
	private String wrongLine;
	private Yytoken currToken;
	private Stack grViewStack;
	private Stack parseStack;
	private Stack nTermStack;
	private TabGraphNode tabGraph[];
	private TabNode tabT[];
	private TabNode tabNT[];
	private File fileIn;
	private GrViewStackNode grViewSNode;

	private ParseStackNode auxParseSNode;
	private boolean stepping = false;

	private boolean firstTime = true;
	int I;
	int IU;
	int toppsIU;
	private Yylex lex;

	boolean continueSentinel;

	public Analyzer(TabGraphNode tbG[], TabNode tbT[], TabNode tbNt[], File fileIn, Yylex lex)
	{
		this.tabGraph = tbG;
		this.tabT = tbT;
		this.tabNT = tbNt;
		this.fileIn = fileIn;
		this.lex = lex;
	}

	/*
	 * A rotina de tratamento de erro é chamada quando o sibolo lido não
	 * coincide com os simbolos esperados, nesse caso, tentamos quatro técnicas
	 * de correção de erros são elas: a)Eliminação de um símbolo: Nesta
	 * estratégia supomos que o usuário cometeu o engano de escrever a cadeia de
	 * entrada com um simbolo a mais. Tal simbolo (o simbolo que acabou de ser
	 * lido) deve então ser eliminado. b)Inserção de um símbolo: Nessa
	 * estratégia supomos que o usuário cometeu o engano de omitir um símbolo
	 * que deve ser inserido. c)Um símbolo trocado: Nesta estratégia supomos que
	 * o usuário escreveu um símbolo erradamente. d)Busca de Delimitador: Nesta
	 * estratégia, procuramos um simbolo que delimite o não terminal sendo
	 * procurado.
	 */
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
				/* Imprime a linha que occorreu o erro */
				wrongLine = bufferedReader.readLine();
				bufferedReader.close();
			}
			AppOutput.displayText("\n" + wrongLine + "\n", TOPIC.Output);
		}
		catch (IOException e)
		{
			if (fileIn != null)
			{
				AppOutput.displayText("File not found...", TOPIC.Output);
			}
		}
		// AppOutput.displayText("\nErro:");
		AppOutput.displayText("Error found at the symbol of line: " + line + ", column: " + column + ". ", TOPIC.Output);
		// for (int i = 0; i < column; i++)
		// txt = txt+"_";
		// AppOutput.displayText(txt+"^: ");
		/* falta fazer back-tracking */
		while (IX != 0)
		{
			/* eh terminal? */
			if (tabGraph[IX].isTerm())
			{
				/* emite terminal esperado */
				AppOutput.displayText(tabT[tabGraph[IX].getSim()].getName() + "  ", TOPIC.Output);
				/* vai para a proxima alternativa */
				IX = tabGraph[IX].getAlt();
			}
			else
			{
				/* no nao terminal */
				IX = tabNT[tabGraph[IX].getSim()].getPrim();
			}
		}
		AppOutput.displayText(" expected. ", TOPIC.Output);
		/* Tenta corrigir o erro utilizando a estratégia da eliminação */
		if (!estrategiaEliminacao(oldI, toppsIU, column, line))
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
						if (currToken.m_text.equals("$"))
							continueSentinel = false;
						else
							this.dealWithError(oldI, toppsIU, currToken.m_charBegin, currToken.m_line);
					}
				}
			}
		}
	}

	private boolean estrategiaBuscaDelimitador(int IX, int topps, int column, int line)
	{
		boolean achou = false;
		Stack pilhaAnalisadorBackup = new Stack();
		Stack pilhaAuxAnalisador = new Stack();
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
				while (IX != 0 && this.tabGraph[IX].isTerm())
					IX = this.tabGraph[IX].getAlt();
			}
			/* Preciso desempilhar um nó da pilha do analisador */
			if (IX == 0)
			{
				/* desempilha e vai para o percurso */
				temp = (GrViewStackNode) grViewStack.pop();
				IX = temp.no;
				toppsAux = temp.r;
			}
			/* inicio do percurso */
			IY = this.tabGraph[IX].getSuc();
			/* inicializando as duas pilhas auxiliares... */
			pilhaNaoTerminalY.clear();
			pilhaAuxAnalisador.clear();
			/* percurso */
			while (IY != 0 && !achou)
			{
				/* é terminal? */
				if (this.tabGraph[IY].isTerm())
				{
					/* é lambda-nó? */
					if (this.tabGraph[IY].getSim() == 0)
					{
						IY = this.tabGraph[IY].getSuc();
					}
					else
					{
						String tmp = this.tabT[this.tabGraph[IY].getSim()].getName();
						if (tmp.equals(this.currentSymbol))
						{
							while (this.parseStack.size() >= toppsAux)
							{
								this.parseStack.pop();
							}
							this.parseStack.push(new ParseStackNode(this.tabNT[this.tabGraph[IX].getSim()].getFlag(), this.tabNT[this.tabGraph[IX].getSim()].getName()));
							achou = true;
							I = IY;
						}
						else
							IY = this.findAlternative(IY, pilhaNaoTerminalY, pilhaAuxAnalisador);
					}
				}
				else
				{ /* é não terminal */
					pilhaAuxAnalisador.push(new GrViewStackNode(IY, toppsAux));
					pilhaNaoTerminalY.push(new Integer(IY));
					IY = this.tabNT[this.tabGraph[IY].getSim()].getPrim();

				}
			}
			if (!achou)
				IX = this.tabGraph[IX].getAlt();
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

	private boolean estrategiaEliminacao(int IX, int toppsIU, int column, int line)
	{
		boolean sucesso = false;
		/* ler o proximo simbolo */
		readNext();
		/*
		 * Tenta achar um terminal que seja igual ao simbolo lido, percorrendo
		 * as alternativas do nó atual
		 */
		while (IX != 0)
		{
			/* eh terminal? */
			if (tabGraph[IX].isTerm())
			{
				/* o simbolo deste terminal é igual ao simbolo atual? */
				if (tabT[tabGraph[IX].getSim()].getName().equals(currentSymbol))
				{
					/*
					 * 2 Se o nó representado por I é igual ao ultimo valor
					 * lido.
					 */
					/* 2 Então coloque o ultimo valor lido na parseStack */
					parseStack.push(new ParseStackNode(tabT[tabGraph[IX].getSim()].getFlag(), currentSymbol, currentSemanticSymbol));
					// imprimePilha(parseStack);
					readNext();
					/*
					 * 2 Acabei de reconhecer um simbolo, posso esvaziar a
					 * pilhaNãoTerminal
					 */
					nTermStack.clear();
					/* 2 Faça I <- sucessor do IX atual */
					I = tabGraph[IX].getSuc();
					IU = I;
					toppsIU = this.parseStack.size();
					/*
					 * imprimir mensagem que o penultimo simbolo lido foi
					 * ignorado
					 */
					AppOutput.errorRecoveryStatus("Action: This symbol will be ignorated\n");
					/* consegui corrigir */
					sucesso = true;
					/* sair do laço */
					break;
				}
				else
				{
					/* não é igual, vai para a proxima alternativa */
					int alternative = 0;
					alternative = this.findAlternative(IX, nTermStack, grViewStack);
					IX = alternative;
				}
			}
			else
			{
				/* 2 Se IX não representa nó terminal */
				/* 2 Empilha o nó representado por IX na pilha do analisador */
				grViewStack.push(new GrViewStackNode(IX, parseStack.size() + 1));
				/* 2 Empilha o nó representado por IX na pilhaNãoTerminal */
				nTermStack.push(new Integer(IX));
				/* 2 Faço IX representar o primeiro nó desse nao terminal */
				IX = tabNT[tabGraph[IX].getSim()].getPrim();
			}
		}
		if (!sucesso)
		{
			currentSymbol = pastSymbol;
			/* volta um token na cabeça de leitura do analisador léxico. */
			lex.pushback(lex.yylength());
			while (this.grViewStack.size() > toppsIU)
			{
				this.grViewStack.pop();
			}
		}
		return sucesso;
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
			if (this.tabGraph[IX].isTerm())
			{
				// topoaux2 = topoaux1;
				IY = this.tabGraph[IX].getSuc();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* é terminal? */
					if (this.tabGraph[IY].isTerm())
					{
						/* é lambda-nó? */
						if (this.tabGraph[IY].getSim() == 0)
						{
							IY = this.tabGraph[IY].getSuc();
						}
						else
						{
							String temp = this.tabT[this.tabGraph[IY].getSim()].getName();
							if (temp.equals(this.currentSymbol))
							{
								/* empilha ti na pilha sintática... */
								this.parseStack.push(new ParseStackNode(this.tabT[this.tabGraph[IX].getSim()].getFlag(), this.tabT[this.tabGraph[IX].getSim()].getName()));
								this.printStack(parseStack);
								topps++;
								/* novo nó para asin... */
								I = IY;
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
						pilhaNaoTerminalY.push(new Integer(IY));
						IY = this.tabNT[this.tabGraph[IY].getSim()].getPrim();
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
				this.nTermStack.push(new Integer(IX));
				IX = this.tabNT[this.tabGraph[IX].getSim()].getPrim();
			}
		}
		if (achou)
		{
			/* novo topo... */
			// topo = topoaux2;
			// currentSymbol = pastSymbol;
			/* volta um token na cabeça de leitura do analisador léxico. */
			// lex.yypushback(lex.yylength());
			AppOutput.errorRecoveryStatus("Action: " + this.tabT[this.tabGraph[IX].getSim()].getName() + " inserted before column " + column + "\n");
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
			if (this.tabGraph[IX].isTerm())
			{
				IY = this.tabGraph[IX].getSuc();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* é terminal? */
					if (this.tabGraph[IY].isTerm())
					{
						/* é lambda-nó? */
						if (this.tabGraph[IY].getSim() == 0)
						{
							IY = this.tabGraph[IY].getSuc();
						}
						else
						{
							String temp = this.tabT[this.tabGraph[IY].getSim()].getName();
							if (temp.equals(this.currentSymbol))
							{
								/* empilha ti na pilha sintática... */
								this.parseStack.push(new ParseStackNode(this.tabT[this.tabGraph[IX].getSim()].getFlag(), this.tabT[this.tabGraph[IX].getSim()].getName()));
								this.printStack(parseStack);
								topps++;
								/* novo nó para asin... */
								I = IY;
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
						pilhaNaoTerminalY.push(new Integer(IY));
						IY = this.tabNT[this.tabGraph[IY].getSim()].getPrim();
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
				this.nTermStack.push(new Integer(IX));
				IX = this.tabNT[this.tabGraph[IX].getSim()].getPrim();
			}
		}
		if (achou)
		{
			/* novo topo... */
			/* volta um token na cabeça de leitura do analisador léxico. */
			// lex.yypushback(lex.yylength());
			AppOutput.errorRecoveryStatus("Action: This symbol has been replaced by " + this.tabT[this.tabGraph[IX].getSim()].getName() + "\n");
		}
		else
		{
			currentSymbol = pastSymbol;
			/* volta um token na cabeça de leitura do analisador léxico. */
			lex.pushback(lex.yylength());
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
		alternative = this.tabGraph[IZ].getAlt();
		while (alternative == 0 && !pilhaNaoTerm.empty())
		{
			pilhaAna.pop();
			alternative = tabGraph[((Integer) pilhaNaoTerm.pop()).intValue()].getAlt();
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

	/*
	 * Esta função lê o proximo token de entrada, guardando o token em currToken
	 * e o simbolo correspondente em simbolo Atual
	 */
	public void readNext()
	{
		try
		{
			currToken = lex.yylex();
			// System.err.println(lex.yylength());
			pastSymbol = currentSymbol;
			String tkAp1 = currToken.m_p1;
			if (tkAp1.equals("Res") || tkAp1.equals("Esp") || tkAp1.equals("EOF"))
			{
				currentSymbol = currToken.m_text;
			}
			else
			{
				currentSymbol = currToken.m_p1;
			}
			currentSemanticSymbol = currToken.m_text;
		}
		catch (IOException e)
		{
			AppOutput.printlnToken("Token read error\n");
		}
		AppOutput.printlnToken("Current Token: " + currToken + "\n");
	}

	@Override
	public void run()
	{
		currentSemanticSymbol = null;
		/* 2 Initialize and Delare grViewStack */
		grViewStack = new Stack();
		/* 2 Initialize and Declare parseStack */
		parseStack = new Stack();
		/* 2 Initialize and Declare nTermStack */
		nTermStack = new Stack();
		/* 2 Declaration of boolean variable sucesso */
		boolean sucess = false;
		/* Construct semantic routines */
		sr = new SemanticRoutinesRepo(parseStack, tabT);

		/* Passing the Tab of terminals to scanner */
		lex.TabT(tabT);

		int objective = 1;
		/* 2 To let the things work, tabGraph[0]=(false,objetivo,0,0) */
		tabGraph[0] = new TabGraphNode();
		tabGraph[0].setTerm(false);
		tabGraph[0].setSim(objective);
		tabGraph[0].setAlt(0);
		tabGraph[0].setSuc(0);
		/* 2 Read the first entry's simbol and put in currentSymbol */
		readNext();
		/* 2 Initialization of analyzer stack */
		grViewStack.push(new GrViewStackNode(0, 1));
		/*
		 * 2 Inicia o percurso do grafo sintatico pelo primeiro nó apontado pelo
		 * simbolo não terminal inicial
		 */
		I = tabNT[objective].getPrim();
		IU = I;
		toppsIU = this.parseStack.size();
		/* 2 Initialize the sentinel */
		continueSentinel = true;
		/* 2 go through the parser graph */
		while (continueSentinel)
		{
			/* 2 Não chegou ao fim do lado direito de uma produção: */
			if (I != 0)
			{
				/* 2 Is current node a terminal? */
				if (tabGraph[I].isTerm())
					/* 2 Is terminal a empty chain? */
					if (tabGraph[I].getSim() == 0)
					{
						/* calling the semantic routine */
						sr.setCurrentToken(null);
						sr.execFunction(tabGraph[I].getSem());
						I = tabGraph[I].getSuc();
						IU = I;
						toppsIU = this.parseStack.size();
					}
					else
					{
						/* 2 I isn't the lambda-node */
						if ((tabT[tabGraph[I].getSim()].getName()).equals(currentSymbol))
						{
							/*
							 * 2 Se o nó representado por I é igual ao ultimo
							 * valor lido.
							 */
							/* 2 Então coloque o ultimo valor lido na parseStack */
							parseStack.push(new ParseStackNode(tabT[tabGraph[I].getSim()].getFlag(), currentSymbol, currentSemanticSymbol));
							printStack(parseStack);
							/*
							 * 2 chamada da RS referenciada pelo nó terminal
							 * reconhecido
							 */
							sr.setCurrentToken(currToken);
							sr.execFunction(tabGraph[I].getSem());
							readNext();
							/*
							 * 2 Acabei de reconhecer um simbolo, posso esvaziar
							 * a pilhaNãoTerminal
							 */
							nTermStack.clear();
							/* 2 Faça I <- sucessor do I atual */
							I = tabGraph[I].getSuc();
							IU = I;
							toppsIU = this.parseStack.size();
						}
						else
						{
							/*
							 * 2 Se o nó representado por I não é igual ao
							 * ultimo valor lido
							 */
							if (tabGraph[I].getAlt() != 0)
								/*
								 * 2 Se o nó representado por I possui
								 * alternativa.
								 */
								/* 2 Faça o I representar essa alternativa */
								I = tabGraph[I].getAlt();
							else
							{
								/*
								 * 2 Se o nó representado por I não possui
								 * alternativa
								 */
								if (nTermStack.empty())
								{
									/*
									 * 2 Se a pilhaNãoTerminal está vazia, tenho
									 * que tratar o erro
									 */
									dealWithError(IU, toppsIU, currToken.m_charBegin, currToken.m_line);
								}
								else
								{
									/* 2 Se a pilhaNãoTerminal não está vazia. */
									/*
									 * 2 Desempilho então um nó da pilha, que no
									 * caso corresponde a uma não terminal,
									 */
									/*
									 * 2 e atribuo à I o nó alternativo desse
									 * não terminal
									 */
									int alternative;
									alternative = this.findAlternative(I, nTermStack, grViewStack);
									if (alternative != 0)
									{
										I = alternative;
									}
									else
									{
										dealWithError(IU, toppsIU, currToken.m_charBegin, currToken.m_line);
									}
								}
							}
						}
					}
				else
				{
					/* 2 Se I não representa nó terminal */
					/* 2 Empilha o nó representado por I na pilha do analisador */
					grViewStack.push(new GrViewStackNode(I, parseStack.size() + 1));
					/* 2 Empilha o nó representado por I na pilhaNãoTerminal */
					nTermStack.push(new Integer(I));
					/* 2 Faço I representar o primeiro nó desse nao terminal */
					I = tabNT[tabGraph[I].getSim()].getPrim();
				}
			}
			else
			{
				/* 2 Terminou o lado direito de uma produção? */
				if (!grViewStack.empty())
				{
					/* 2 A pilha do analisador não está vazia? */
					/*
					 * 2 Desempilha o nó da pilha do analisador e coloco-o em
					 * grViewSNode
					 */
					grViewSNode = (GrViewStackNode) grViewStack.pop();
					/*
					 * 2 Desempilho todos os nós da parseStack que representam o
					 * não terminal reconhecido
					 */
					while (parseStack.size() >= grViewSNode.r)
					{
						auxParseSNode = (ParseStackNode) parseStack.pop();
					}
					/* 2 Empilha na parseStack o nó do não terminal reconhecido */
					parseStack.push(new ParseStackNode(tabNT[tabGraph[grViewSNode.no].getSim()].getFlag(), tabNT[tabGraph[grViewSNode.no].getSim()].getName(), auxParseSNode.getSem()));
					printStack(parseStack);
					/* 2 Faço I representar o nó correspondente à grViewSNode */
					I = grViewSNode.no;
					/*
					 * 2 Chamada a rotina semantica referenciada pelo nó
					 * não-terminal reconhecido
					 */
					sr.setCurrentToken(currToken);
					sr.execFunction(tabGraph[I].getSem());
					/* 2 Faço I representar o nó sucessor ao I atual */
					I = tabGraph[I].getSuc();
					IU = I;
					toppsIU = this.parseStack.size();
				}
				else
				{
					/* 2 Não atigiu o lado direito de uma produçao */
					/* 2 Se a pilha do analisador está vazia */
					if (currentSymbol.equals(new String("$")))
					{
						/*
						 * 2 Se a ultima palavra lida for igual ao final de
						 * arquivo
						 */
						/* 2 Então reconheci a linguagem... */
						// AppOutput.displayText("End of File");
						sucess = true;
					}
					else
					{
						/*
						 * 2 Se a ultima palavra lida não for igual ao final de
						 * arquivo
						 */
						/* 2 Então não reconheci a linguagem... */
						AppOutput.displayText("The fist non-teminal of the grammar has been recognized. ", TOPIC.Output);
						AppOutput.displayText("But the end-of-file symbol has not been recognized.", TOPIC.Output);
						sucess = false;
					}
					/* 2 Indica que deve encerrar a analise */
					continueSentinel = false;
				}
			}
		}
		if (sucess)
		{
			AppOutput.displayText("Expression Successfully recognized.", TOPIC.Output);
		}
	}

	public void setStepping(boolean stepping)
	{
		this.stepping = stepping;
	}
}
