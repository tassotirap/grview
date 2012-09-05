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

	boolean continueSentinel;
	int I;
	int IU;
	int toppsIU;
	private ParseStackNode auxParseSNode;
	private Object currentSemanticSymbol;
	private String currentSymbol;
	private Yytoken currToken;
	private File fileIn;
	private boolean firstTime = true;
	private GrViewStackNode grViewSNode;
	private Stack grViewStack;
	private Yylex lex;
	private Stack nTermStack;

	private Stack parseStack;
	private String pastSymbol;

	private SemanticRoutinesRepo sr;
	private boolean stepping = false;
	private TabGraphNode tabGraph[];
	private TabNode tabNT[];
	private TabNode tabT[];

	

	public Analyzer(TabGraphNode tbG[], TabNode tbT[], TabNode tbNt[], File fileIn, Yylex lex)
	{
		this.tabGraph = tbG;
		this.tabT = tbT;
		this.tabNT = tbNt;
		this.fileIn = fileIn;
		this.lex = lex;
	}

	/*
	 * A rotina de tratamento de erro � chamada quando o sibolo lido n�o
	 * coincide com os simbolos esperados, nesse caso, tentamos quatro t�cnicas
	 * de corre��o de erros s�o elas: a)Elimina��o de um s�mbolo: Nesta
	 * estrat�gia supomos que o usu�rio cometeu o engano de escrever a cadeia de
	 * entrada com um simbolo a mais. Tal simbolo (o simbolo que acabou de ser
	 * lido) deve ent�o ser eliminado. b)Inser��o de um s�mbolo: Nessa
	 * estrat�gia supomos que o usu�rio cometeu o engano de omitir um s�mbolo
	 * que deve ser inserido. c)Um s�mbolo trocado: Nesta estrat�gia supomos que
	 * o usu�rio escreveu um s�mbolo erradamente. d)Busca de Delimitador: Nesta
	 * estrat�gia, procuramos um simbolo que delimite o n�o terminal sendo
	 * procurado.
	 */
	private void dealWithError(int IX, int toppsIU, int column, int line)
	{
		int oldI = IX;
		/*
		 * o proximo bloco � respons�vel por imprimir na tela aonde o erro
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
			if (tabGraph[IX].isTerm())
			{
				AppOutput.displayText(tabT[tabGraph[IX].getSim()].getName() + " expected.", TOPIC.Output);
				
				IX = tabGraph[IX].getAlt();
				
				/* don't have alternative but have non terminal */
				if(IX == 0 && nTerminalStack.size() > 0)
				{
					IX = nTerminalStack.pop().getAlt();
				}
				
			}
			else
			{
				/* push non terminal in stack */
				nTerminalStack.push(tabGraph[IX]);
				IX = tabNT[tabGraph[IX].getSim()].getPrim();
			}
		}
		/* Tenta corrigir o erro utilizando a estrat�gia da elimina��o */
		if (!estrategiaEliminacao(oldI, toppsIU, column, line))
		{
			AppOutput.errorRecoveryStatus("\nDeleting a symbol strategy has not succeeded\n");
			/* Tenta corrigir o erro utilizando a estrat�gia de inser��o */
			if (!estrategiaInsercao(oldI, toppsIU, column, line))
			{
				AppOutput.errorRecoveryStatus("Inserting a symbol strategy has not succeeded\n");
				/* Tenta corrigir o erro utilizando a estrat�gia da troca */
				if (!estrategiaTroca(oldI, toppsIU, column, line))
				{
					AppOutput.errorRecoveryStatus("Replacing a symbol stategy has not succeeded\n");
					/*
					 * Tenta corrigir o erro utilizando a estrat�gia da busca de
					 * Delimitador
					 */
					if (!estrategiaBuscaDelimitador(oldI, toppsIU, column, line))
					{
						AppOutput.errorRecoveryStatus("Searching delimiters strategy has not succeeded\n");
						/*
						 * At� esse ponto nenhuma das estrat�gias tiveram
						 * sucesso ao corrigir o erro, ent�o o pr�ximo simbolo
						 * da entrada � lido a rotina de tratamento de erros �
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
			 * Ainda existem alternativas a seguir ou � necessario desempilhar
			 * um n� na pilha do analisador
			 */
			toppsAux = this.parseStack.size();
			if (IX != 0)
			{
				/* Procurando por um n�o terminal */
				while (IX != 0 && this.tabGraph[IX].isTerm())
					IX = this.tabGraph[IX].getAlt();
			}
			/* Preciso desempilhar um n� da pilha do analisador */
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
				/* � terminal? */
				if (this.tabGraph[IY].isTerm())
				{
					/* � lambda-n�? */
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
				{ /* � n�o terminal */
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
		 * as alternativas do n� atual
		 */
		while (IX != 0)
		{
			/* eh terminal? */
			if (tabGraph[IX].isTerm())
			{
				/* o simbolo deste terminal � igual ao simbolo atual? */
				if (tabT[tabGraph[IX].getSim()].getName().equals(currentSymbol))
				{
					/*
					 * 2 Se o n� representado por I � igual ao ultimo valor
					 * lido.
					 */
					/* 2 Ent�o coloque o ultimo valor lido na parseStack */
					parseStack.push(new ParseStackNode(tabT[tabGraph[IX].getSim()].getFlag(), currentSymbol, currentSemanticSymbol));
					// imprimePilha(parseStack);
					readNext();
					/*
					 * 2 Acabei de reconhecer um simbolo, posso esvaziar a
					 * pilhaN�oTerminal
					 */
					nTermStack.clear();
					/* 2 Fa�a I <- sucessor do IX atual */
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
					/* sair do la�o */
					break;
				}
				else
				{
					/* n�o � igual, vai para a proxima alternativa */
					int alternative = 0;
					alternative = this.findAlternative(IX, nTermStack, grViewStack);
					IX = alternative;
				}
			}
			else
			{
				/* 2 Se IX n�o representa n� terminal */
				/* 2 Empilha o n� representado por IX na pilha do analisador */
				grViewStack.push(new GrViewStackNode(IX, parseStack.size() + 1));
				/* 2 Empilha o n� representado por IX na pilhaN�oTerminal */
				nTermStack.push(new Integer(IX));
				/* 2 Fa�o IX representar o primeiro n� desse nao terminal */
				IX = tabNT[tabGraph[IX].getSim()].getPrim();
			}
		}
		if (!sucesso)
		{
			currentSymbol = pastSymbol;
			/* volta um token na cabe�a de leitura do analisador l�xico. */
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
		/* Percorre os n�s que podem ser inseridos (t1,t2....,tn) */
		while (IX != 0 && !achou)
		{
			/* o simbolo espereado � terminal? */
			if (this.tabGraph[IX].isTerm())
			{
				// topoaux2 = topoaux1;
				IY = this.tabGraph[IX].getSuc();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* � terminal? */
					if (this.tabGraph[IY].isTerm())
					{
						/* � lambda-n�? */
						if (this.tabGraph[IY].getSim() == 0)
						{
							IY = this.tabGraph[IY].getSuc();
						}
						else
						{
							String temp = this.tabT[this.tabGraph[IY].getSim()].getName();
							if (temp.equals(this.currentSymbol))
							{
								/* empilha ti na pilha sint�tica... */
								this.parseStack.push(new ParseStackNode(this.tabT[this.tabGraph[IX].getSim()].getFlag(), this.tabT[this.tabGraph[IX].getSim()].getName()));
								this.printStack(parseStack);
								topps++;
								/* novo n� para asin... */
								I = IY;
								/* corre��o obteve sucesso... */
								achou = true;
							}
							else
								IY = this.findAlternative(IY, pilhaNaoTerminalY, grViewStack);
						}
					}
					/* sucessor � um n�o terminal */
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
				 * Nesse ponto a pilhaNaoTerminalY est� vazia, portanto qualquer
				 * n� que tenha sido colocado na grViewStack na busca das
				 * alternativas do n� IY, j� foi removido. Logo, posso
				 * desenpilhar um n� da pilha do analisador na busca de uma
				 * alternativa do n� IX sem problemas pois se a nTermStack n�o
				 * est� vazia, o topo da pilha do analisador representa um n�
				 * n�o terminal que foi utilizado para chegar at� o n� IX.
				 */
				if (!achou)
					IX = this.findAlternative(IX, nTermStack, grViewStack);
			}
			else
			{
				/* n�o terminal na sequencia dos esperados */
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
			/* volta um token na cabe�a de leitura do analisador l�xico. */
			// lex.yypushback(lex.yylength());
			AppOutput.errorRecoveryStatus("Action: " + this.tabT[this.tabGraph[IX].getSim()].getName() + " inserted before column " + column + "\n");
		}
		/*
		 * deixa a pilha do Analisador como estava antes de come�ar essa
		 * estrat�gia
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
		 * Esquecer os n�o terminais que aparecem na pilha do analisador ao
		 * tentar as outras corre��os
		 */
		while (grViewStack.size() > toppsIU)
			grViewStack.pop();
		/* le o proximo simbolo */
		this.readNext();
		int topps = this.parseStack.size();
		/* Percorre os n�s que podem ser inseridos (t1,t2....,tn) */
		while (IX != 0 && !achou)
		{
			/* o simbolo espereado � terminal? */
			if (this.tabGraph[IX].isTerm())
			{
				IY = this.tabGraph[IX].getSuc();
				pilhaNaoTerminalY.clear();
				/* percorre s1,s2,...,sn */
				while (IY != 0 && !achou)
				{
					/* � terminal? */
					if (this.tabGraph[IY].isTerm())
					{
						/* � lambda-n�? */
						if (this.tabGraph[IY].getSim() == 0)
						{
							IY = this.tabGraph[IY].getSuc();
						}
						else
						{
							String temp = this.tabT[this.tabGraph[IY].getSim()].getName();
							if (temp.equals(this.currentSymbol))
							{
								/* empilha ti na pilha sint�tica... */
								this.parseStack.push(new ParseStackNode(this.tabT[this.tabGraph[IX].getSim()].getFlag(), this.tabT[this.tabGraph[IX].getSim()].getName()));
								this.printStack(parseStack);
								topps++;
								/* novo n� para asin... */
								I = IY;
								/* corre��o obteve sucesso... */
								achou = true;
							}
							else
								IY = this.findAlternative(IY, pilhaNaoTerminalY, grViewStack);
						}
					}
					/* sucessor � um n�o terminal */
					else
					{
						this.grViewStack.push(new GrViewStackNode(IY, topps + 2));
						pilhaNaoTerminalY.push(new Integer(IY));
						IY = this.tabNT[this.tabGraph[IY].getSim()].getPrim();
					}
				}
				/*
				 * Nesse ponto a pilhaNaoTerminalY est� vazia, portanto qualquer
				 * n� que tenha sido colocado na grViewStack na busca das
				 * alternativas do n� IY, j� foi removido. Logo, posso
				 * desenpilhar um n� da pilha do analisador na busca de uma
				 * alternativa do n� IX sem problemas pois se a nTermStack n�o
				 * est� vazia, o topo da pilha do analisador representa um n�
				 * n�o terminal que foi utilizado para chegar at� o n� IX.
				 */
				if (!achou)
					IX = this.findAlternative(IX, nTermStack, grViewStack);
			}
			else
			{
				/* n�o terminal na sequencia dos esperados */
				this.grViewStack.push(new GrViewStackNode(IX, topps + 1));
				this.nTermStack.push(new Integer(IX));
				IX = this.tabNT[this.tabGraph[IX].getSim()].getPrim();
			}
		}
		if (achou)
		{
			/* novo topo... */
			/* volta um token na cabe�a de leitura do analisador l�xico. */
			// lex.yypushback(lex.yylength());
			AppOutput.errorRecoveryStatus("Action: This symbol has been replaced by " + this.tabT[this.tabGraph[IX].getSim()].getName() + "\n");
		}
		else
		{
			currentSymbol = pastSymbol;
			/* volta um token na cabe�a de leitura do analisador l�xico. */
			lex.pushback(lex.yylength());
			/*
			 * Faz com que a pilha do analisador volte a ser o que era antes de
			 * usar essa estrat�gia.
			 */
			while (this.grViewStack.size() > toppsIU)
			{
				this.grViewStack.pop();
			}
		}
		return achou;
	}

	/*
	 * Esta fun��o � chamada quando o n� atual n�o possui como alternativa um
	 * terminal mas sim um n�o terminal. Ent�o percorremos os n�o terminais
	 * guardados na pilha n�oTerminal at� achar um simbolo terminal. Como tal
	 * simbolo � alternativa do n� sendo visitado, retornamos ele. Se n�o existe
	 * tal simbolo, retornamos "0" para indicar que o n� n�o possui
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
	 * Esta fun��o l� o proximo token de entrada, guardando o token em currToken
	 * e o simbolo correspondente em simbolo Atual
	 */
	public void readNext()
	{
		try
		{
			currToken = lex.yylex();
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
		AppOutput.printToken("Current Token: " + currToken);
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
		 * 2 Inicia o percurso do grafo sintatico pelo primeiro n� apontado pelo
		 * simbolo n�o terminal inicial
		 */
		I = tabNT[objective].getPrim();
		IU = I;
		toppsIU = this.parseStack.size();
		/* 2 Initialize the sentinel */
		continueSentinel = true;
		/* 2 go through the parser graph */
		while (continueSentinel)
		{
			/* 2 N�o chegou ao fim do lado direito de uma produ��o: */
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
							 * 2 Se o n� representado por I � igual ao ultimo
							 * valor lido.
							 */
							/* 2 Ent�o coloque o ultimo valor lido na parseStack */
							parseStack.push(new ParseStackNode(tabT[tabGraph[I].getSim()].getFlag(), currentSymbol, currentSemanticSymbol));
							printStack(parseStack);
							/*
							 * 2 chamada da RS referenciada pelo n� terminal
							 * reconhecido
							 */
							sr.setCurrentToken(currToken);
							sr.execFunction(tabGraph[I].getSem());
							readNext();
							/*
							 * 2 Acabei de reconhecer um simbolo, posso esvaziar
							 * a pilhaN�oTerminal
							 */
							nTermStack.clear();
							/* 2 Fa�a I <- sucessor do I atual */
							I = tabGraph[I].getSuc();
							IU = I;
							toppsIU = this.parseStack.size();
						}
						else
						{
							/*
							 * 2 Se o n� representado por I n�o � igual ao
							 * ultimo valor lido
							 */
							if (tabGraph[I].getAlt() != 0)
								/*
								 * 2 Se o n� representado por I possui
								 * alternativa.
								 */
								/* 2 Fa�a o I representar essa alternativa */
								I = tabGraph[I].getAlt();
							else
							{
								/*
								 * 2 Se o n� representado por I n�o possui
								 * alternativa
								 */
								if (nTermStack.empty())
								{
									/*
									 * 2 Se a pilhaN�oTerminal est� vazia, tenho
									 * que tratar o erro
									 */
									dealWithError(IU, toppsIU, currToken.m_charBegin + 1, currToken.m_line + 1);
								}
								else
								{
									/* 2 Se a pilhaN�oTerminal n�o est� vazia. */
									/*
									 * 2 Desempilho ent�o um n� da pilha, que no
									 * caso corresponde a uma n�o terminal,
									 */
									/*
									 * 2 e atribuo � I o n� alternativo desse
									 * n�o terminal
									 */
									int alternative;
									alternative = this.findAlternative(I, nTermStack, grViewStack);
									if (alternative != 0)
									{
										I = alternative;
									}
									else
									{
										dealWithError(IU, toppsIU, currToken.m_charBegin + 1, currToken.m_line + 1);
									}
								}
							}
						}
					}
				else
				{
					/* 2 Se I n�o representa n� terminal */
					/* 2 Empilha o n� representado por I na pilha do analisador */
					grViewStack.push(new GrViewStackNode(I, parseStack.size() + 1));
					/* 2 Empilha o n� representado por I na pilhaN�oTerminal */
					nTermStack.push(new Integer(I));
					/* 2 Fa�o I representar o primeiro n� desse nao terminal */
					I = tabNT[tabGraph[I].getSim()].getPrim();
				}
			}
			else
			{
				/* 2 Terminou o lado direito de uma produ��o? */
				if (!grViewStack.empty())
				{
					/* 2 A pilha do analisador n�o est� vazia? */
					/*
					 * 2 Desempilha o n� da pilha do analisador e coloco-o em
					 * grViewSNode
					 */
					grViewSNode = (GrViewStackNode) grViewStack.pop();
					/*
					 * 2 Desempilho todos os n�s da parseStack que representam o
					 * n�o terminal reconhecido
					 */
					while (parseStack.size() >= grViewSNode.r)
					{
						auxParseSNode = (ParseStackNode) parseStack.pop();
					}
					/* 2 Empilha na parseStack o n� do n�o terminal reconhecido */
					parseStack.push(new ParseStackNode(tabNT[tabGraph[grViewSNode.no].getSim()].getFlag(), tabNT[tabGraph[grViewSNode.no].getSim()].getName(), auxParseSNode.getSem()));
					printStack(parseStack);
					/* 2 Fa�o I representar o n� correspondente � grViewSNode */
					I = grViewSNode.no;
					/*
					 * 2 Chamada a rotina semantica referenciada pelo n�
					 * n�o-terminal reconhecido
					 */
					sr.setCurrentToken(currToken);
					sr.execFunction(tabGraph[I].getSem());
					/* 2 Fa�o I representar o n� sucessor ao I atual */
					I = tabGraph[I].getSuc();
					IU = I;
					toppsIU = this.parseStack.size();
				}
				else
				{
					/* 2 N�o atigiu o lado direito de uma produ�ao */
					/* 2 Se a pilha do analisador est� vazia */
					if (currentSymbol.equals(new String("$")))
					{
						/*
						 * 2 Se a ultima palavra lida for igual ao final de
						 * arquivo
						 */
						/* 2 Ent�o reconheci a linguagem... */
						// AppOutput.displayText("End of File");
						sucess = true;
					}
					else
					{
						/*
						 * 2 Se a ultima palavra lida n�o for igual ao final de
						 * arquivo
						 */
						/* 2 Ent�o n�o reconheci a linguagem... */
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
