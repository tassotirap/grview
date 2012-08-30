package org.grview.syntax.validation;

import java.util.ArrayList;
import java.util.List;

import org.grview.syntax.grammar.GrammarComponent;

public class InvalidGrammarException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2582912391213112321L;

	private String description;
	private int iteratorIndex;

	private List<InvalidGrammarException> nextExceptions = new ArrayList<InvalidGrammarException>();
	private GrammarComponent problem;

	public InvalidGrammarException(String message, String description, GrammarComponent problem)
	{
		super(message);
		this.description = description;
		this.problem = problem;
	}

	public String getDescription()
	{
		return this.description;
	}

	public boolean hasNext()
	{
		return iteratorIndex < nextExceptions.size();
	}

	public void insertMoreExceptions(InvalidGrammarException ex)
	{
		nextExceptions.add(ex);
	}

	public InvalidGrammarException nextException()
	{
		if (hasNext())
		{
			return nextExceptions.get(iteratorIndex++);
		}
		return null;
	}

	public void resetIterator()
	{
		iteratorIndex = 0;
	}

	public void setGrComp(GrammarComponent problem)
	{
		this.problem = problem;
	}

	public String whereId()
	{
		return problem.getId().toString();
	}

	public String whereLabel()
	{
		return problem.getContents().toString();
	}

}
