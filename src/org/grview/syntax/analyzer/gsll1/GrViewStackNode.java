package org.grview.syntax.analyzer.gsll1;

public class GrViewStackNode
{
	public int indexNode;
	public int size;

	GrViewStackNode(int indexNode, int size)
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
