package org.grview.syntax.model;

public class GrViewNode
{
	public int indexNode;
	public int size;

	public GrViewNode(int indexNode, int size)
	{
		this.indexNode = indexNode;
		this.size = size;
	}

	@Override
	public String toString()
	{
		return indexNode + "," + size;
	}
}
