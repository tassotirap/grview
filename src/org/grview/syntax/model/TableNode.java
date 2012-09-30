package org.grview.syntax.model;

public class TableNode
{
	private String flag;
	
	private String name;

	private int firstNode;

	public TableNode(String flag, String nodeName)
	{
		this.flag = flag;
		getName(nodeName);
		setFirstNode(-1);
	}

	public TableNode(String flag, String nodeName, int firstNode)
	{
		this.flag = flag;
		getName(nodeName);
		setFirstNode(firstNode);
	}

	public String getFlag()
	{
		return flag;
	}

	public String getName()
	{
		return name;
	}

	public void getName(String nodeName)
	{
		name = nodeName;
	}

	public int getFirstNode()
	{
		return firstNode;
	}

	public void setFlag(String flag)
	{
		this.flag = flag;
	}


	public void setFirstNode(int nodePrim)
	{
		firstNode = nodePrim;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
