package org.grview.syntax.grammar.model;

/** Abstract class for all nodes that can't have successors or alternatives **/
public abstract class SimpleOutput extends SyntaxSubpart
{

	static final long serialVersionUID = 1;

	private static int count;

	public SimpleOutput()
	{
		super(getNewID());
	}

	public static String getNewID()
	{
		return Integer.toString(count++);
	}

	abstract public boolean getResult();

	public void removeOutput(Connection w)
	{
		outputs.remove(w);
	}

}
