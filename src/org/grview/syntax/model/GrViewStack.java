package org.grview.syntax.model;

import java.util.Stack;

public class GrViewStack
{
	Stack<GrViewNode> grViewStack;
	
	public GrViewStack()
	{	
		grViewStack = new Stack<GrViewNode>();
	}
	
	public void push(GrViewNode item)
	{
		grViewStack.push(item);
	}
	
	public GrViewNode pop()
	{
		return grViewStack.pop();
	}
	
	public GrViewNode peak()
	{
		return grViewStack.peek();
	}
	
	@Override
	public GrViewStack clone()
	{
		return (GrViewStack)this.clone();
	}
	
	public boolean empty()
	{
		return grViewStack.empty();
	}
	
	public void clear()
	{
		grViewStack.clear();		
	}
	
	public int size()
	{
		return grViewStack.size();		
	}
}
