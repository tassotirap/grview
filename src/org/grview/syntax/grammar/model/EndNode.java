package org.grview.syntax.grammar.model;

public class EndNode extends SimpleOutput implements AbstractNode
{

	static final long serialVersionUID = 1;

	@Override
	public void connectOutput(Connection w)
	{
	}

	@Override
	public void disconnectOutput(Connection w)
	{
	}

	/*---------------Output Connections are not allowed-------------------*/

	@Override
	public boolean getResult()
	{
		return getTargetConnections().size() > 0;
	}

	@Override
	public String getType()
	{
		return SyntaxDefinitions.AndGate_LabelText;
	}

	@Override
	public String toString()
	{
		return SyntaxDefinitions.AndGate_LabelText + " #" + getID();
	}
}
