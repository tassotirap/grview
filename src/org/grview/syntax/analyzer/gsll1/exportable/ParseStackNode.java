package org.grview.syntax.analyzer.gsll1.exportable;

/*ParseStackNode objects are pushed on the parseStack*/
public class ParseStackNode
{
	/** The flag, a unique identifier for presentation and debug **/
	private String flag;
	/** The semantic symbol of the token **/
	private Object sem;
	/** The type of the token **/
	private String syn;

	public ParseStackNode(String flag, String str)
	{
		this(flag, str, null);
	}

	public ParseStackNode(String flag, String str, Object obj)
	{
		this.flag = flag;
		syn = str;
		sem = obj;
	}

	public String getFlag()
	{
		return flag;
	}

	public Object getSem()
	{
		return sem;
	}

	public String getSyn()
	{
		return syn;
	}

	public int intSem()
	{
		return Integer.parseInt(stringSem());
	}

	public void setSem(Object obj)
	{
		sem = obj;
	}

	public void setSyn(String str)
	{
		syn = str;
	}

	public String stringSem()
	{
		return sem.toString();
	}

	@Override
	public String toString()
	{
		return "Syn: " + getSyn() + " Sem: " + getSem();
	}

}
