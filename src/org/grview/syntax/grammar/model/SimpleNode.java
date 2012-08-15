package org.grview.syntax.grammar.model;

import java.util.Hashtable;

/***
 * A simple node refers to any kind of node that can receive output input
 * connections as usual, and also has a usual node label.
 * 
 * @author Gustavo H. Braga
 * 
 */
public class SimpleNode extends SyntaxModel implements AbstractNode
{

	private static final long serialVersionUID = 1L;

	/**
	 * determines the type of this node (ex: a terminal, a non terminal, etc...)
	 **/
	private String type;

	private NodeLabel label;

	/** gets Track of available IDs by type **/
	private static Hashtable<String, Integer> nextIDByType = new Hashtable<String, Integer>();

	/**
	 * @param type
	 *            the type of this simple node
	 * @param label
	 *            the label of this simple node
	 */
	public SimpleNode(String type, String label)
	{
		super(getNewID(type));
		this.type = type;
		this.label = new NodeLabel(label);
		addChild(this.label);
	}

	/**
	 * Gets the next available ID
	 */
	public static String getNewID(String type)
	{
		if (!nextIDByType.containsKey(type))
		{
			nextIDByType.put(type, 0);
		}
		return String.valueOf(nextIDByType.put(type, nextIDByType.get(type) + 1) + 1);
	}

	public NodeLabel getLabel()
	{
		return label;
	}

	/*----------------------------- getters, setters ---------------------*/

	@Override
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return type + " #" + getID();
	}

}
