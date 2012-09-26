package org.grview.syntax.model;

/*
 * Created on 11/08/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * 
 * @author gohan
 * 
 */
public class TabGraphNode
{

	private int alternativeIndex;
	private String semanticRoutine;
	private int simReference;
	private int sucessorIndex;
	private boolean isTerm;

	public TabGraphNode()
	{

	}

	public int getAlternativeIndex()
	{
		return alternativeIndex;
	}

	public String getSemanticRoutine()
	{
		return semanticRoutine;
	}

	public int getSimReference()
	{
		return simReference;
	}

	public int getSucessorIndex()
	{
		return sucessorIndex;
	}

	public boolean isTerm()
	{
		return isTerm;
	}

	public void setAlternativeIndex(int node)
	{
		alternativeIndex = node;
	}

	public void setSemanticRoutine(String routine)
	{
		semanticRoutine = routine;
	}

	public void setSimReference(int node)
	{
		simReference = node;
	}

	public void setSucessorIndex(int node)
	{
		sucessorIndex = node;
	}

	public void setIsTerm(boolean bool)
	{
		isTerm = bool;
	}

	@Override
	public String toString()
	{
		return isTerm + " " + simReference + " " + alternativeIndex + " " + sucessorIndex + " " + semanticRoutine;
	}

}
