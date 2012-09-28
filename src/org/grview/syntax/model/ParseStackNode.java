package org.grview.syntax.model;

public class ParseStackNode
{
	private String flag;
	
	private Object semanticSymbol;
	
	private String type;

	public ParseStackNode(String flag, String str)
	{
		this(flag, str, null);
	}

	public ParseStackNode(String flag, String type, Object semanticSymbol)
	{
		this.flag = flag;
		this.type = type;
		this.semanticSymbol = semanticSymbol;
	}

	public String getFlag()
	{
		return flag;
	}

	public Object getSemanticSymbol()
	{
		return semanticSymbol;
	}

	public String getType()
	{
		return type;
	}

	public int intSemanticSymbol()
	{
		return Integer.parseInt(stringSemanticSymbol());
	}

	public void setSemanticSymbol(Object semanticSymbol)
	{
		this.semanticSymbol = semanticSymbol;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String stringSemanticSymbol()
	{
		return semanticSymbol.toString();
	}

	@Override
	public String toString()
	{
		return "Syn: " + getType() + " Sem: " + getSemanticSymbol();
	}

}