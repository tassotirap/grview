package org.grview.syntax.grammar.model;

/** A lamda, or empty alternative **/
public class LambdaAlternative extends EndNode
{
	static final long serialVersionUID = 1;

	@Override
	public String getType()
	{
		return LAMBDA_ALTERNATIVE;
	}

	@Override
	public String toString()
	{
		return LAMBDA_ALTERNATIVE + " #" + getID();
	}

}
