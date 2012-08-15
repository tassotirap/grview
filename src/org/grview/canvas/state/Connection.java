package org.grview.canvas.state;

public class Connection extends CanvasSerializableElement implements Comparable<Connection>
{

	private static final long serialVersionUID = -4162245206128920310L;
	private String name;
	private String source;
	private String target;
	private String type;

	@Override
	public int compareTo(Connection c)
	{
		return getName().compareTo(c.getName());
	}

	public String getName()
	{
		return name;
	}

	public String getSource()
	{
		return source;
	}

	public String getTarget()
	{
		return target;
	}

	public String getType()
	{
		return type;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public void setType(String type)
	{
		this.type = type;
	}

}