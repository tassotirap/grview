package org.grview.syntax.analyzer.gsll1;

public class AnalyzerIndex
{
	private int topParseStackSize;
	private int topIndexNode;
	private int indexNode;
	
	private static AnalyzerIndex instance;
	
	public static AnalyzerIndex getInstance()
	{
		return instance;		
	}
	
	public static AnalyzerIndex setInstance()
	{
		instance = new AnalyzerIndex();
		return instance;		
	}
	
	private AnalyzerIndex()
	{
		
	}
	
	public int getIndexNode()
	{
		return indexNode;
	}
	
	public int getTopIndexNode()
	{
		return topIndexNode;
	}
	
	public int getTopParseStackSize()
	{
		return topParseStackSize;
	}
	
	public void setIndexNode(int indexNode)
	{
		this.indexNode = indexNode;
	}
	
	public void setTopIndexNode(int topIndexNode)
	{
		this.topIndexNode = topIndexNode;
	}
	
	public void setTopParseStackSize(int topParseStackSize)
	{
		this.topParseStackSize = topParseStackSize;
	}	
}
