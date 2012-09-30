package org.grview.syntax.model;

import java.util.Stack;

public class NTerminalStack
{
	Stack<Integer> nTerminalStack;
	
	public NTerminalStack()
	{	
		nTerminalStack = new Stack<Integer>();
	}
	
	public void push(Integer item)
	{
		nTerminalStack.push(item);
	}
	
	public Integer pop()
	{
		return nTerminalStack.pop();
	}
	
	public Integer peak()
	{
		return nTerminalStack.peek();
	}
	
	@Override
	public NTerminalStack clone()
	{
		return (NTerminalStack)this.clone();
	}
	
	public boolean empty()
	{
		return nTerminalStack.empty();
	}
	
	public void clear()
	{
		nTerminalStack.clear();		
	}
	
	public int size()
	{
		return nTerminalStack.size();		
	}

}
