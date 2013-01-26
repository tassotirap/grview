package org.grview.syntax.analyzer.gsll1;

import java.util.Stack;

import org.grview.lexical.Yytoken;

public class AnalyzerSemanticStack
{
	private static AnalyzerSemanticStack instance;

	public static AnalyzerSemanticStack getInstance()
	{
		return instance;
	}

	public static AnalyzerSemanticStack setInstance()
	{
		instance = new AnalyzerSemanticStack();
		return instance;
	}

	Stack<Object> stack = new Stack<Object>();

	public void push(Object value)
	{
		stack.push(value);
	}
	
	public Object pop()
	{
		return stack.pop();
	}
	
	public Object elementAt(int index)
	{
		return stack.elementAt(index);		
	}
	
	public Object peek()
	{
		return stack.peek();
	}
	
	public Object peek_1()
	{
		if(stack.size() - 2 >= 0)
			return stack.elementAt(stack.size() - 2);
		return "";
	}
	
	public Object peek_2()
	{
		if(stack.size() - 3 >= 0)
			return stack.elementAt(stack.size() - 3);
		return "";
	}
	
	public boolean empty()
	{
		return stack.empty();
	}
	
	public int size()
	{
		return stack.size();
	}
	
	public boolean compareTo(Yytoken firstToken, Yytoken secondToken)
	{
		return firstToken.token().compareTo(secondToken.token()) == 0;
	}
	
	public boolean compareTo(String firstToken, String secondToken)
	{
		return firstToken.compareTo(secondToken) == 0;
	}
	
	public int ToInt(String token)
	{
		return Integer.parseInt(token);
	}
}
