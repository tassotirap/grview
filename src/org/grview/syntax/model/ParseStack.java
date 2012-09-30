package org.grview.syntax.model;

import java.util.Iterator;
import java.util.Stack;

public class ParseStack
{
	Stack<ParseNode> parseNode;
	
	public ParseStack()
	{	
		parseNode = new Stack<ParseNode>();
	}
	
	public void push(ParseNode item)
	{
		parseNode.push(item);
	}
	
	public ParseNode pop()
	{
		return parseNode.pop();
	}
	
	public ParseNode peek()
	{
		return parseNode.peek();
	}
	
	@Override
	public ParseStack clone()
	{
		return (ParseStack)this.clone();
	}
	
	public boolean empty()
	{
		return parseNode.empty();
	}
	
	public void clear()
	{
		parseNode.clear();		
	}
	
	public int size()
	{
		return parseNode.size();		
	}
	
	public Iterator<ParseNode> iterator()
	{
		return parseNode.iterator();
	}
	
}
