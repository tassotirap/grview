package org.grview.syntax.analyzer.gsll1.exportable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Scanner;
import java.util.Stack;

public class Analyzer
{

	/* --------------- BEGIN VARIABLES ---------------- */

	static class TabGraphNode implements Serializable
	{

		private static final long serialVersionUID = 687894412957568389L;
		/** When term is true, the node is a terminal **/
		private boolean term;
		/** a reference to this node on the symbols table **/
		private int sim;
		/** the index of this node's alternative **/
		private int alt;
		/** the index of this node's successor **/
		private int suc;
		/** indicates which semantic routine to use **/
		private String sem;

		public TabGraphNode()
		{

		}

		/**
		 * @return the alt
		 */
		public int getAlt()
		{
			return alt;
		}

		/**
		 * @return the sem
		 */
		public String getSem()
		{
			return sem;
		}

		/**
		 * @return the sim
		 */
		public int getSim()
		{
			return sim;
		}

		/**
		 * @return the suc
		 */
		public int getSuc()
		{
			return suc;
		}

		/**
		 * @return the term
		 */
		public boolean isTerm()
		{
			return term;
		}

		/**
		 * @param alt
		 *            the alt to set
		 */
		public void setAlt(int alt)
		{
			this.alt = alt;
		}

		/**
		 * @param sem
		 *            the sem to set
		 */
		public void setSem(String sem)
		{
			this.sem = sem;
		}

		/**
		 * @param sim
		 *            the sim to set
		 */
		public void setSim(int sim)
		{
			this.sim = sim;
		}

		/**
		 * @param suc
		 *            the suc to set
		 */
		public void setSuc(int suc)
		{
			this.suc = suc;
		}

		/**
		 * @param term
		 *            the term to set
		 */
		public void setTerm(boolean term)
		{
			this.term = term;
		}

		@Override
		public String toString()
		{
			return term + " " + sim + " " + alt + " " + suc + " " + sem;
		}

	}

	static class TabNode implements Serializable
	{

		private static final long serialVersionUID = -8851101063205670628L;
		/* an internal flag */
		private String flag;
		/** the name of the node **/
		private String name;
		/** the first node a non-terminal respective production **/
		private int prim;

		/**
		 * Constructor used to build terminal nodes
		 * 
		 * @param flag
		 * @param nodeName
		 *            the name of the node
		 */
		public TabNode(String flag, String nodeName)
		{
			this.flag = flag;
			setName(nodeName);
			setPrim(-1);
		}

		/**
		 * Constructor for building non-terminal nodes
		 * 
		 * @param flag
		 * @param nodeName
		 * @param nodePrim
		 */
		public TabNode(String flag, String nodeName, int nodePrim)
		{
			this.flag = flag;
			setName(nodeName);
			setPrim(nodePrim);
		}

		/**
		 * @return the flag
		 */
		public String getFlag()
		{
			return flag;
		}

		/**
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @return the prim
		 */
		public int getPrim()
		{
			return prim;
		}

		/**
		 * @param flag
		 *            the flag to set
		 */
		public void setFlag(String flag)
		{
			this.flag = flag;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * @param prim
		 *            the prim to set
		 */
		public void setPrim(int prim)
		{
			this.prim = prim;
		}

		/* to string... */
		@Override
		public String toString()
		{
			return this.name;
		}

	}

	/* internal representation of nodes in the stack */
	private class GrViewStackNode
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

	/* Serialized Version of the default grammar */
	public final static byte[] StabGraph = new byte[0];
	public final static byte[] StabT = new byte[0];
	public final static byte[] StabNT = new byte[0];

	/* the actual variables that hold the default grammar */
	/** the graph of nodes **/
	private TabGraphNode tabGraph[];
	/** the symbol table containing the terminal nodes **/
	private TabNode tabT[];
	/** the symbol table containing the non-terminal nodes **/
	private TabNode tabNT[];
	private String currentSymbol;
	private Object currentSemanticSymbol;

	private String pastSymbol;
	private String wrongLine;
	private Yytoken currToken;
	private Stack<GrViewStackNode> grViewStack;
	private Stack<ParseStackNode> parseStack;

	private Stack<Integer> nTermStack;

	private GrViewStackNode grViewSNode;
	private ParseStackNode auxParseSNode;
	SemanticRoutines sr;
	private BufferedReader in;
	private File sourceFile = null;
	private PrintStream out;
	private PrintStream err;
	private int I;
	private int IU;

	/* --------------- END VARIABLES ---------------- */

	private int toppsIU;

	private Yylex lex;

	private boolean continueSentinel;

	/**
	 * A constructor
	 * 
	 * @param out
	 *            , the output stream of the analyser
	 * @param err
	 *            , the error output stream of the analyser
	 * @param sourceFile
	 *            , a file containing the source to be parsed
	 */
	public Analyzer(File sourceFile, PrintStream out, PrintStream err, Yylex lex) throws IOException, ClassNotFoundException
	{
		this.sourceFile = sourceFile;
		FileReader fileReader = new FileReader(sourceFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while ((bufferedReader.readLine()) != null)
		{
		}
		bufferedReader.close();

		in = new BufferedReader(fileReader);
		this.lex = lex;
		initialize(out, err);
	}

	/**
	 * A constructor
	 * 
	 * @param source
	 *            , the source text to be parsed
	 * @param out
	 *            , the output stream of the analyser
	 * @param err
	 *            , the error output stream of the analyser
	 */
	public Analyzer(String source, PrintStream out, PrintStream err, Yylex lex) throws IOException, ClassNotFoundException
	{
		this.lex = lex;
		in = new BufferedReader(new StringReader(source));
		initialize(out, err);
	}

	public static void main(String[] args)
	{
		boolean interactive = false;
		boolean isFile = false;
		String source = null;

		Yylex lex = new Yylex();
		if (args.length == 0)
		{
			interactive = true;
		}
		else if (args.length <= 3)
		{
			for (String st : args)
			{
				if (st.equals("-i"))
				{
					interactive = true;
				}
				if (st.equals("-f"))
				{
					isFile = true;
				}
				else
				{
					source = st;
				}
			}
		}
		if (isFile)
		{
			try
			{
				Analyzer analyzer = new Analyzer(new File(source), System.out, System.err, lex);
				analyzer.run();
			}
			catch (Exception e)
			{
				System.err.println("Could not create and run the analyzer");
				e.printStackTrace();
			}
		}
		else if (interactive)
		{
			while (true)
			{
				System.out.print("> ");
				Scanner scanner = new Scanner(System.in);
				source = scanner.next();
				try
				{
					new Analyzer(source, System.out, System.err, lex).run();
				}
				catch (Exception e)
				{
					System.err.println("Could not create and run the analyzer");
					e.printStackTrace();
				}
				scanner.close();
			}
		}

		if (args.length > 3 || (interactive == false && source == null))
		{
			System.err.println("Error -> Format: javac <jvm options> Analyzer <-i|-f> <source file or text>");
		}

	}

	/**
	 * The method is called when the symbol read was not expected, in this case
	 * a number of error recovery techniques can be tried.
	 * 
	 * @param IX
	 * @param toppsIU
	 * @param column
	 * @param line
	 */
	private void dealWithError(int IX, int toppsIU, int column, int line)
	{
		int oldI = IX;
		try
		{
			if (sourceFile != null)
			{
				BufferedReader bufferedReader = new BufferedReader(new FileReader(sourceFile));
				for (int j = 0; j < line; j++)
				{
					bufferedReader.readLine();
				}
				wrongLine = bufferedReader.readLine();
				bufferedReader.close();
			}
			displayError("\n" + wrongLine + "\n");
		}
		catch (IOException e)
		{
			if (sourceFile != null)
			{
				displayError("File not found...");
			}
		}
		displayError("Error found at the symbol of line: " + line + ", column: " + column + ". ");
		while (IX != 0)
		{
			if (tabGraph[IX].isTerm())
			{
				displayError(tabT[tabGraph[IX].getSim()].getName() + "  ");
				IX = tabGraph[IX].getAlt();
			}
			else
			{
				IX = tabNT[tabGraph[IX].getSim()].getPrim();
			}
		}
		displayError(" expected. ");
		if (!deletingStrategy(oldI, toppsIU, column, line))
		{
			displayText("\nDeleting a symbol strategy has not succeeded\n");
			if (!insertionStrategy(oldI, toppsIU, column, line))
			{
				displayText("Inserting a symbol strategy has not succeeded\n");
				if (!replaceStrategy(oldI, toppsIU, column, line))
				{
					displayText("Replacing a symbol stategy has not succeeded\n");
					if (!searchSymbolStrategy(oldI, toppsIU, column, line))
					{
						displayText("Searching delimiters strategy has not succeeded\n");
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

	/**
	 * This method implements an error recovery technique in which a symbol is
	 * deleted
	 * 
	 * @param IX
	 * @param toppsIU
	 * @param column
	 * @param line
	 * @return
	 */
	private boolean deletingStrategy(int IX, int toppsIU, int column, int line)
	{
		boolean success = false;
		readNext();
		while (IX != 0)
		{
			if (tabGraph[IX].isTerm())
			{
				if (tabT[tabGraph[IX].getSim()].getName().equals(currentSymbol))
				{
					parseStack.push(new ParseStackNode(tabT[tabGraph[IX].getSim()].getFlag(), currentSymbol, currentSemanticSymbol));
					readNext();
					nTermStack.clear();
					I = tabGraph[IX].getSuc();
					IU = I;
					toppsIU = this.parseStack.size();
					displayText("Action: This symbol will be ignorated\n");
					success = true;
					break;
				}
				else
				{
					int alternative = 0;
					alternative = this.findAlternative(IX, nTermStack, grViewStack);
					IX = alternative;
				}
			}
			else
			{
				grViewStack.push(new GrViewStackNode(IX, parseStack.size() + 1));
				nTermStack.push(new Integer(IX));
				IX = tabNT[tabGraph[IX].getSim()].getPrim();
			}
		}
		if (!success)
		{
			currentSymbol = pastSymbol;
			lex.pushback(lex.yylength());
			while (this.grViewStack.size() > toppsIU)
			{
				this.grViewStack.pop();
			}
		}
		return success;
	}

	private void displayError(String string)
	{
		err.println(string);
	}

	private void displayText(String string)
	{
		out.println(string);
	}

	/**
	 * This method is called when the current node does not have a terminal, but
	 * a non-terminal as alternative. Then the non-terminals stacked are
	 * verified until a terminal symbol is recognized.
	 * 
	 * @param IZ
	 * @param nonTermStack
	 * @param anaStack
	 * @return the alternative node number, or 0 if the node does not have an
	 *         alternative.
	 */
	private int findAlternative(int IZ, Stack<Integer> nonTermStack, Stack<GrViewStackNode> anaStack)
	{
		int alternative = 0;
		alternative = this.tabGraph[IZ].getAlt();
		while (alternative == 0 && !nonTermStack.empty())
		{
			anaStack.pop();
			alternative = tabGraph[nonTermStack.pop().intValue()].getAlt();
		}
		return alternative;
	}

	private void initialize(PrintStream out, PrintStream err) throws IOException, ClassNotFoundException
	{
		tabGraph = (TabGraphNode[]) read(StabGraph);
		tabT = (TabNode[]) read(StabT);
		tabNT = (TabNode[]) read(StabNT);
		this.out = out;
		this.err = err;
	}

	/**
	 * This method implements an error recovery strategy that consists of
	 * inserting a missing symbol
	 */
	private boolean insertionStrategy(int IX, int toppsIU, int column, int line)
	{
		int IY;
		Stack<Integer> yNonTerminalStack = new Stack<Integer>();
		boolean found = false;
		int topps = this.parseStack.size();
		while (IX != 0 && !found)
		{
			if (this.tabGraph[IX].isTerm())
			{
				IY = this.tabGraph[IX].getSuc();
				yNonTerminalStack.clear();
				while (IY != 0 && !found)
				{
					if (this.tabGraph[IY].isTerm())
					{
						if (this.tabGraph[IY].getSim() == 0)
						{
							IY = this.tabGraph[IY].getSuc();
						}
						else
						{
							String temp = this.tabT[this.tabGraph[IY].getSim()].getName();
							if (temp.equals(this.currentSymbol))
							{
								this.parseStack.push(new ParseStackNode(this.tabT[this.tabGraph[IX].getSim()].getFlag(), this.tabT[this.tabGraph[IX].getSim()].getName()));
								topps++;
								I = IY;
								found = true;
							}
							else
								IY = this.findAlternative(IY, yNonTerminalStack, grViewStack);
						}
					}
					else
					{
						this.grViewStack.push(new GrViewStackNode(IY, topps + 2));
						yNonTerminalStack.push(new Integer(IY));
						IY = this.tabNT[this.tabGraph[IY].getSim()].getPrim();
					}
				}
				if (!found)
					IX = this.findAlternative(IX, nTermStack, grViewStack);
			}
			else
			{
				this.grViewStack.push(new GrViewStackNode(IX, topps + 1));
				this.nTermStack.push(new Integer(IX));
				IX = this.tabNT[this.tabGraph[IX].getSim()].getPrim();
			}
		}
		if (found)
		{
			displayText("Action: " + this.tabT[this.tabGraph[IX].getSim()].getName() + " inserted before column " + column + "\n");
		}
		else
		{
			while (this.grViewStack.size() > toppsIU)
			{
				this.grViewStack.pop();
			}

		}
		return found;
	}

	/**
	 * This method implements an error recovery strategy that consists of
	 * replacing a symbol by another one.
	 */
	private boolean replaceStrategy(int IX, int toppsIU, int column, int line)
	{
		int IY;
		Stack<Integer> yNonTerminalStack = new Stack<Integer>();
		boolean found = false;
		while (grViewStack.size() > toppsIU)
			grViewStack.pop();
		this.readNext();
		int topps = this.parseStack.size();
		while (IX != 0 && !found)
		{
			if (this.tabGraph[IX].isTerm())
			{
				IY = this.tabGraph[IX].getSuc();
				yNonTerminalStack.clear();
				while (IY != 0 && !found)
				{
					if (this.tabGraph[IY].isTerm())
					{
						if (this.tabGraph[IY].getSim() == 0)
						{
							IY = this.tabGraph[IY].getSuc();
						}
						else
						{
							String temp = this.tabT[this.tabGraph[IY].getSim()].getName();
							if (temp.equals(this.currentSymbol))
							{
								this.parseStack.push(new ParseStackNode(this.tabT[this.tabGraph[IX].getSim()].getFlag(), this.tabT[this.tabGraph[IX].getSim()].getName()));
								topps++;
								I = IY;
								found = true;
							}
							else
								IY = this.findAlternative(IY, yNonTerminalStack, grViewStack);
						}
					}
					else
					{
						this.grViewStack.push(new GrViewStackNode(IY, topps + 2));
						yNonTerminalStack.push(new Integer(IY));
						IY = this.tabNT[this.tabGraph[IY].getSim()].getPrim();
					}
				}
				if (!found)
					IX = this.findAlternative(IX, nTermStack, grViewStack);
			}
			else
			{
				this.grViewStack.push(new GrViewStackNode(IX, topps + 1));
				this.nTermStack.push(new Integer(IX));
				IX = this.tabNT[this.tabGraph[IX].getSim()].getPrim();
			}
		}
		if (found)
		{
			displayText("Action: This symbol has been replaced by " + this.tabT[this.tabGraph[IX].getSim()].getName() + "\n");
		}
		else
		{
			currentSymbol = pastSymbol;
			lex.pushback(lex.yylength());
			while (this.grViewStack.size() > toppsIU)
			{
				this.grViewStack.pop();
			}
		}
		return found;
	}

	/**
	 * This method implements an error discovery strategy in which a symbol
	 * capable of replacing the non-terminal missing is searched.
	 * **/
	@SuppressWarnings("unchecked")
	private boolean searchSymbolStrategy(int IX, int topps, int column, int line)
	{
		boolean found = false;
		Stack<GrViewStackNode> backupAnalyzerStack = new Stack<GrViewStackNode>();
		Stack<GrViewStackNode> auxAnalyzerStack = new Stack<GrViewStackNode>();
		Stack<Integer> yNonTerminalStack = new Stack<Integer>();
		GrViewStackNode temp;
		backupAnalyzerStack = (Stack<GrViewStackNode>) this.grViewStack.clone();
		int toppsAux;
		int IY;
		while (!grViewStack.empty() && !found)
		{
			toppsAux = this.parseStack.size();
			if (IX != 0)
			{
				while (IX != 0 && this.tabGraph[IX].isTerm())
					IX = this.tabGraph[IX].getAlt();
			}
			if (IX == 0)
			{
				temp = grViewStack.pop();
				IX = temp.no;
				toppsAux = temp.r;
			}
			IY = this.tabGraph[IX].getSuc();
			yNonTerminalStack.clear();
			auxAnalyzerStack.clear();
			while (IY != 0 && !found)
			{
				if (this.tabGraph[IY].isTerm())
				{
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
							found = true;
							I = IY;
						}
						else
							IY = this.findAlternative(IY, yNonTerminalStack, auxAnalyzerStack);
					}
				}
				else
				{
					auxAnalyzerStack.push(new GrViewStackNode(IY, toppsAux));
					yNonTerminalStack.push(new Integer(IY));
					IY = this.tabNT[this.tabGraph[IY].getSim()].getPrim();

				}
			}
			if (!found)
				IX = this.tabGraph[IX].getAlt();
		}
		if (found)
		{
			for (int i = 0; i < auxAnalyzerStack.size(); i++)
			{
				this.grViewStack.push(auxAnalyzerStack.elementAt(i));
			}
			displayText("Action: the symbol in the column " + column + " has been assumed as delimiter.\n");
		}
		else
		{
			this.grViewStack = (Stack<GrViewStackNode>) backupAnalyzerStack.clone();
		}
		return found;
	}

	/*-----------------AUXILIAR CLASSES------------------*/

	public Object read(byte[] byteArray) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		Object object;
		if (byteArray != null)
		{
			ObjectInputStream ois = new ObjectInputStream(bais);
			object = ois.readObject();
			return object;
		}
		return null;
	}

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
			displayError("Token read error\n");
			displayError("Current Token: " + currToken + "\n");
		}
	}

	public void run()
	{
		currentSemanticSymbol = null;
		grViewStack = new Stack<GrViewStackNode>();
		parseStack = new Stack<ParseStackNode>();
		nTermStack = new Stack<Integer>();
		boolean sucess = false;
		/* Construct semantic routines */
		sr = new SemanticRoutines(parseStack, tabT, out);

		/* Initialize and Declare the scanner Yylex */
		lex.yyreset(in);
		/* Passing the Tab of terminals to scanner */
		lex.TabT(tabT);

		int objective = 1;
		/* To let the things work, tabGraph[0]=(false,objetivo,0,0) */
		tabGraph[0] = new TabGraphNode();
		tabGraph[0].setTerm(false);
		tabGraph[0].setSim(objective);
		tabGraph[0].setAlt(0);
		tabGraph[0].setSuc(0);
		/* Read the first entry's simbol and put in currentSymbol */
		readNext();
		/* Initialization of analyzer stack */
		grViewStack.push(new GrViewStackNode(0, 1));
		/*
		 * Starts the walk in the graph by the first node indicated by the
		 * initial non-terminal symbol
		 */
		I = tabNT[objective].getPrim();
		IU = I;
		toppsIU = this.parseStack.size();
		/* Initialize the sentinel */
		continueSentinel = true;
		/* go through the parser graph */
		while (continueSentinel)
		{
			/* couldn't get to the end of a production right side */
			if (I != 0)
			{
				/* Is current node a terminal? */
				if (tabGraph[I].isTerm())
					/* Is terminal a empty chain? */
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
						/* It isn't the lambda-node */
						if ((tabT[tabGraph[I].getSim()].getName()).equals(currentSymbol))
						{
							/*
							 * If the node referenced by I is equal to the last
							 * read value, then put the last value read in the
							 * parseStack
							 */
							parseStack.push(new ParseStackNode(tabT[tabGraph[I].getSim()].getFlag(), currentSymbol, currentSemanticSymbol));
							/*
							 * call the semantic routine indicated by the
							 * terminal node that was found
							 */
							sr.setCurrentToken(currToken);
							sr.execFunction(tabGraph[I].getSem());
							readNext();
							/*
							 * after recognizing a symbol, the non-terminals
							 * stack can be cleared
							 */
							nTermStack.clear();
							/* I is made successor of the actual I */
							I = tabGraph[I].getSuc();
							IU = I;
							toppsIU = this.parseStack.size();
						}
						else
						{
							/*
							 * If the node represented by I is not the last
							 * value read
							 */
							if (tabGraph[I].getAlt() != 0)
								/*
								 * If I has an alternative, I now represents
								 * this alternative
								 */
								I = tabGraph[I].getAlt();
							else
							{
								/* There is not an alternative for I */
								if (nTermStack.empty())
								{
									/*
									 * The non-terminals stack is empty, so
									 * there must be an error
									 */
									dealWithError(IU, toppsIU, currToken.m_charBegin, currToken.m_line);
								}
								else
								{
									/*
									 * If the non-terminals stack is not empty,
									 * I is the alternative to the non-terminal
									 * in the top of the stack.
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
					/* If I is a terminal node */
					/* Push the node referenced by I in the analyzer stack */
					grViewStack.push(new GrViewStackNode(I, parseStack.size() + 1));
					/* Push the node referenced by I on the non-terminals stack */
					nTermStack.push(new Integer(I));
					/*
					 * I now represents the first node produced by this
					 * non-terminal
					 */
					I = tabNT[tabGraph[I].getSim()].getPrim();
				}
			}
			else
			{
				/* The right side of the production is finished? */
				if (!grViewStack.empty())
				{
					grViewSNode = grViewStack.pop();
					while (parseStack.size() >= grViewSNode.r)
					{
						auxParseSNode = parseStack.pop();
					}
					parseStack.push(new ParseStackNode(tabNT[tabGraph[grViewSNode.no].getSim()].getFlag(), tabNT[tabGraph[grViewSNode.no].getSim()].getName(), auxParseSNode.getSem()));
					I = grViewSNode.no;
					sr.setCurrentToken(currToken);
					sr.execFunction(tabGraph[I].getSem());
					I = tabGraph[I].getSuc();
					IU = I;
					toppsIU = this.parseStack.size();
				}
				else
				{
					if (currentSymbol.equals(new String("$")))
					{
						sucess = true;
					}
					else
					{
						displayText("The fist non-teminal of the grammar has been recognized. ");
						displayText("But the end-of-file symbol has not been recognized.");
						sucess = false;
					}
					continueSentinel = false;
				}
			}
		}
		if (sucess)
		{
			displayText("Expression Successfully recognized.");
		}
	}

}
