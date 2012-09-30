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
public class TableGraphNode
{

	private int alternativeIndex;
	private String semanticRoutine;
	private int nodeReference;
	private int sucessorIndex;
	private boolean isTerminal;

	public TableGraphNode()
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

	public int getNodeReference()
	{
		return nodeReference;
	}

	public int getSucessorIndex()
	{
		return sucessorIndex;
	}

	public boolean IsTerminal()
	{
		return isTerminal;
	}

	public void setAlternativeIndex(int node)
	{
		alternativeIndex = node;
	}

	public void setSemanticRoutine(String routine)
	{
		semanticRoutine = routine;
	}

	public void setNodeReference(int node)
	{
		nodeReference = node;
	}

	public void setSucessorIndex(int node)
	{
		sucessorIndex = node;
	}

	public void setIsTerminal(boolean bool)
	{
		isTerminal = bool;
	}

	public boolean isLambda()
	{
		return nodeReference == 0;
	}

	@Override
	public String toString()
	{
		return isTerminal + " " + nodeReference + " " + alternativeIndex + " " + sucessorIndex + " " + semanticRoutine;
	}

}
