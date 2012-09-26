package org.grview.syntax.analyzer.gsll1;

public class GrViewStackNode
{
	public int no;
	public int r;

	GrViewStackNode(int no, int r)
	{
		this.no = no;
		this.r = r;
	}

	@Override
	public String toString()
	{
		return no + "," + r;

	}
}
