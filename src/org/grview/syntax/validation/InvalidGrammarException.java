package org.grview.syntax.validation;

import java.util.ArrayList;
import java.util.List;

import org.grview.syntax.grammar.GrComp;

public class InvalidGrammarException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2582912391213112321L;

	private GrComp problem;
	private String description;
	
	private List<InvalidGrammarException> nextExceptions = new ArrayList<InvalidGrammarException>();
	private int iteratorIndex;
	
	public InvalidGrammarException(String message, String description, GrComp problem) {
		super(message);
		this.description = description;
		this.problem = problem;
	}
	
	
	public void insertMoreExceptions(InvalidGrammarException ex) {
		nextExceptions.add(ex);
	}
	
	public void resetIterator() {
		iteratorIndex = 0;
	}
	
	public boolean hasNext() {
		return iteratorIndex < nextExceptions.size();
	}
	
	public InvalidGrammarException nextException() {
		if (hasNext()) {
			return nextExceptions.get(iteratorIndex++);
		}
		return null;
	}
	
	public void setGrComp(GrComp problem) {
		this.problem = problem;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String whereLabel() {
		return problem.getContents().toString();
	}
	
	public String whereId() {
		return problem.getId().toString();
	}
	
}