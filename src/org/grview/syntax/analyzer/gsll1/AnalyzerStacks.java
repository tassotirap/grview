package org.grview.syntax.analyzer.gsll1;

import java.util.Stack;

import org.grview.syntax.model.ParseStackNode;

public class AnalyzerStacks
{
	private Stack<GrViewStackNode> grViewStack;
	private Stack<Integer> nTermStack;
	private Stack<ParseStackNode> parseStack;

	public AnalyzerStacks()
	{

	}

	public void init()
	{
		grViewStack = new Stack<GrViewStackNode>();
		nTermStack = new Stack<Integer>();
		parseStack = new Stack<ParseStackNode>();
	}

	public Stack<GrViewStackNode> getGrViewStack()
	{
		return grViewStack;
	}

	public void setGrViewStack(Stack<GrViewStackNode> grViewStack)
	{
		this.grViewStack = grViewStack;
	}

	public Stack<Integer> getnTermStack()
	{
		return nTermStack;
	}

	public Stack<ParseStackNode> getParseStack()
	{
		return parseStack;
	}
}
