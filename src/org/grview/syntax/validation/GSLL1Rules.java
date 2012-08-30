package org.grview.syntax.validation;

import java.util.ArrayList;
import java.util.List;

import org.grview.syntax.grammar.GrammarComponent;
import org.grview.syntax.grammar.Grammar;

public class GSLL1Rules extends GrammarRule
{

	private Grammar grammar;
	private boolean onTheFly;

	public GSLL1Rules(Grammar grammar, boolean onTheFly)
	{
		super(grammar, onTheFly);
		this.onTheFly = onTheFly;
		this.grammar = grammar;
	}

	/** Is there a valid header **/
	public InvalidGrammarException r0()
	{
		if (!onTheFly && grammar.getHeads().size() == 0)
		{
			return new InvalidGrammarException("There must be an initial non-terminal.", "Any grammar must have exactly one start point: an initial non-terminal.", null);
		}
		if (grammar.getHeads().size() > 1)
		{
			return new InvalidGrammarException("There must only one initial non-terminal.", "Only one initial point by grammar is allowed. Only one grammar is allowed within each file.", grammar.getHeads().get(grammar.getHeads().size() - 1));
		}
		return null;
	}

	/**
	 * The header or a left side is someone's alternative or successor, or does
	 * not have a successor
	 **/
	public InvalidGrammarException r1()
	{
		GrammarComponent head = grammar.getHead();
		int index = -1;
		if (grammar.getLeftHands() != null && grammar.getLeftHands().size() > 0)
		{
			index = grammar.getLeftHands().size();
		}
		while (head != null)
		{
			if (grammar.getAlternatives(head).size() > 0)
			{
				return new InvalidGrammarException("A left non-terminal can only have an successor.", "A left non-terminal can not be immediately followed by an alternative node", head);
			}
			if (!onTheFly && head != grammar.getHead() && grammar.getSucessors(head).size() != 1)
			{
				return new InvalidGrammarException("A left non-terminal must have exactly one successor.", null, head);
			}
			if ((grammar.getAntiAlternatives(head) != null && grammar.getAntiAlternatives(head).size() > 0) || (grammar.getAntiSuccessors(head) != null && grammar.getAntiSuccessors(head).size() > 0))
			{
				return new InvalidGrammarException("A left non-terminal can not be alternative or successor of any other node", null, head);
			}
			if (index >= 1)
			{
				head = grammar.getLeftHands().get(--index);
			}
			else
			{
				head = null;
			}
		}
		return null;
	}

	/** avoid repeated left hands **/
	public InvalidGrammarException r2()
	{
		for (int i = 0; i < grammar.getLeftHands().size(); i++)
		{
			for (int j = 0; j < grammar.getLeftHands().size(); j++)
			{
				if (i != j)
				{
					if (grammar.getLeftHands().get(i).getContents().equals(grammar.getLeftHands().get(j).getContents()))
					{
						new InvalidGrammarException("Only one left hand by label is allowed", "You must join the two productions in one, through the use of alternative connections. (Ex. M -> a and M -> b is the same as M -> a | b", grammar.getLeftHands().get(j));
					}
				}
			}
		}
		return null;
	}

	/** only one successor and one alternative by node **/
	public InvalidGrammarException r3()
	{
		for (GrammarComponent comp : grammar.getComponents())
		{
			if (grammar.getAntiAlternatives(comp).size() > 1 || grammar.getAntiSuccessors(comp).size() > 1 || grammar.getSucessors(comp).size() > 1 || grammar.getAlternatives(comp).size() > 1)
			{
				return new InvalidGrammarException("It is not allowed to have a node being simultaneously alternative or successor of two distinct nodes. ", null, comp);
			}
		}
		return null;
	}

	/** all nodes must be included in the graph **/
	public InvalidGrammarException r4()
	{
		if (!onTheFly)
		{
			for (GrammarComponent comp : grammar.getComponents())
			{
				if (comp != grammar.getHead() && grammar.getAlternatives(comp).size() == 0 && grammar.getSucessors(comp).size() == 0 && grammar.getAntiAlternatives(comp).size() == 0 && grammar.getAntiSuccessors(comp).size() == 0)
				{
					return new InvalidGrammarException("There is node not connected to the graph.", "All terminal and non-terminal nodes present in the drawing area must be connected to the graph.", comp);
				}
			}
		}
		return null;
	}

	/**
	 * two different terminal nodes in sequence of alternatives can not have the
	 * same label. And if a non-terminal is in a sequence of alternatives, then
	 * the first terminal produced by this non-terminal can not be included in
	 * the sequence of alternatives
	 **/
	public InvalidGrammarException r5()
	{
		for (GrammarComponent comp : grammar.getComponents())
		{
			if (comp.isTerminal() && grammar.getAlternatives(comp).size() > 0)
			{
				GrammarComponent alt = grammar.getAlternatives(comp).get(0);
				while (alt != null)
				{
					if ((alt.isTerminal() && alt.getContents().equals(comp.getContents())) || alt == comp)
					{
						return new InvalidGrammarException("Two terminal nodes in a sequence of alternatives can not be same, or have the same label", "This cases leads to a non-determinism.", alt);
					}
					if (alt.isNonterminal())
					{
						for (GrammarComponent lh : grammar.getLeftHands())
						{
							if (lh.getContents().equals(alt.getContents()))
							{
								if (grammar.getSucessors(lh).size() > 0)
								{
									if (grammar.getSucessors(lh).get(0).getContents().equals(comp.getContents()))
									{
										return new InvalidGrammarException("The first symbol produced by non-terminal can not be in its sequence of alternatives.", "This cases leads to a non-determinism.", alt);
									}
								}
							}
						}
					}
					if (grammar.getAlternatives(alt).size() > 0)
					{
						alt = grammar.getAlternatives(alt).get(0);
					}
					else
					{
						alt = null;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void validate() throws InvalidGrammarException
	{
		List<InvalidGrammarException> exs = new ArrayList<InvalidGrammarException>();
		InvalidGrammarException ex;
		ex = r0();
		if (ex != null)
			exs.add(ex);
		ex = r1();
		if (ex != null)
			exs.add(ex);
		ex = r2();
		if (ex != null)
			exs.add(ex);
		/*
		 * ex = r3(); if (ex != null) exs.add(ex);
		 */
		ex = r4();
		if (ex != null)
			exs.add(ex);
		ex = r5();
		if (ex != null)
			exs.add(ex);
		if (exs.size() > 0)
		{
			int index = 1;
			InvalidGrammarException result = exs.get(0);
			while (exs.size() > index)
			{
				result.insertMoreExceptions(exs.get(index++));
			}
			throw result;
		}

	}
}
