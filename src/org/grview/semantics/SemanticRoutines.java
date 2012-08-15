package org.grview.semantics;

import java.util.Stack;

import org.grview.lexical.Yytoken;
import org.grview.output.AppOutput;
import org.grview.syntax.model.ParseStackNode;
import org.grview.syntax.model.TabNode;

public class SemanticRoutines implements TokenListener
{

	private Yytoken currentToken;

	/*
	 * Here begin the codes for the semantic routines. If the user wants to add
	 * a semantic routine, he should create a method for it, e.g.: private void
	 * rs5(){....}. The name and return value of this method must be coherent
	 * with the method execFunction above.
	 */
	private void rs1(Stack parseStack, TabNode TabT[])
	{
		System.out.println("ijoi");
		ParseStackNode aux;
		int index = parseStack.size() - 1;// top
		int acumulator;
		aux = (ParseStackNode) parseStack.elementAt(index);
		acumulator = aux.intSem();
		index--;
		while (index > 0)
		{
			aux = (ParseStackNode) parseStack.elementAt(index);
			if (aux.getSyn().equals("+"))
			{
				index--;
				aux = (ParseStackNode) parseStack.elementAt(index);
				acumulator = acumulator + aux.intSem();
				index--;
			}
			else if (aux.getSyn().equals("-"))
			{
				index--;
				aux = (ParseStackNode) parseStack.elementAt(index);
				acumulator = aux.intSem() - acumulator;
				index--;
			}
			else
			{
				break;
			}
		}
		/*
		 * Stores the final result assigned to the non-terminal T into the first
		 * parse and semantic stack cell of T; this way, the semantic stack will
		 * show the partial and final results.
		 */
		((ParseStackNode) parseStack.elementAt(++index)).setSem(acumulator + "");
		if (index <= 0)
		{
			AppOutput.semanticRoutinesOutput("Result: " + acumulator + "\n");
		}
	}

	private void rs2(Stack parseStack, TabNode TabT[])
	{
		System.out.println("ijoi");
		ParseStackNode aux;
		int index = parseStack.size() - 1;// top
		int acumulator;
		aux = (ParseStackNode) parseStack.elementAt(index);
		acumulator = aux.intSem();
		index--;
		while (index >= 0)
		{
			aux = (ParseStackNode) parseStack.elementAt(index);
			if (aux.getSyn().equals("*"))
			{
				index--;
				aux = (ParseStackNode) parseStack.elementAt(index);
				acumulator = acumulator * aux.intSem();
				index--;
			}
			else if (aux.getSyn().equals("/"))
			{
				index--;
				aux = (ParseStackNode) parseStack.elementAt(index);
				acumulator = aux.intSem() / acumulator;
				index--;
			}
			else
			{
				break;
			}
		}
		/*
		 * Stores the final result assigned to the non-terminal T into the first
		 * parse and semantic stack cell of T; this way, the semantic stack will
		 * show the partial and final results.
		 */
		((ParseStackNode) parseStack.elementAt(++index)).setSem(acumulator + "");
	}

	private void rs3(Stack parseStack, TabNode TabT[])
	{
		System.out.println("ijoi");
		Object aux;
		int index = parseStack.size() - 1;// top
		aux = ((ParseStackNode) parseStack.elementAt(index - 1)).getSem();
		((ParseStackNode) parseStack.elementAt(index - 2)).setSem(aux);
	}

	@Override
	public void setCurrentToken(Yytoken currentToken)
	{
		this.currentToken = currentToken;
	}
}
