package org.grview.syntax.grammar.model;

import java.util.Vector;

/**
 * This class refers to a element or set of elements that have input and output
 * connections
 **/
abstract public class SyntaxSubpart extends SyntaxElement
{

	/**
	 * the number used to indicate this subpart, an alternative representation
	 * necessary when compiling this grammar
	 **/
	protected int number;
	/** used during search for mark this subpart for depth first search **/
	protected boolean flag;
	/**
	 * The next subpart, observe that for now there's only support for one
	 * successor only
	 **/
	protected SyntaxSubpart Sucessor;
	/**
	 * The next alternative subpart, observe that for now there's only support
	 * for one alternative only
	 **/
	protected SyntaxSubpart Alternative;
	private String id;
	protected Vector<Connection> inputs = new Vector<Connection>(4, 4);
	protected Vector<Connection> outputs = new Vector<Connection>(4, 4);
	static final long serialVersionUID = 1;

	public SyntaxSubpart(String id)
	{
		this.id = id;
	}

	/*------------------CONNECTION DISCONNECTION METHODS ------------------------*/

	public void connectInput(Connection w)
	{
		inputs.addElement(w);
		update();
		fireStructureChange(INPUTS, w);
	}

	public void connectOutput(Connection w)
	{
		outputs.addElement(w);
		update();
		fireStructureChange(OUTPUTS, w);
	}

	public void disconnectInput(Connection w)
	{
		inputs.remove(w);
		update();
		fireStructureChange(INPUTS, w);
	}

	public void disconnectOutput(Connection w)
	{
		outputs.removeElement(w);
		update();
		fireStructureChange(OUTPUTS, w);
	}

	/*------------------- GETTERS AND SETTES FROM HERE ON --------------------------*/

	public SyntaxSubpart getAlternative()
	{
		return Alternative;
	}

	public Vector<Connection> getConnections()
	{
		Vector<Connection> v = new Vector<Connection>();
		v.addAll(outputs);
		v.addAll(inputs);
		return v;
	}

	public boolean getFlag()
	{
		return flag;
	}

	@Override
	public String getID()
	{
		return id;
	}

	public int getNumber()
	{
		return number;
	}

	public Vector<Connection> getSourceConnections()
	{
		return outputs;
	}

	public SyntaxSubpart getSucessor()
	{
		return Sucessor;
	}

	public Vector<Connection> getTargetConnections()
	{
		return inputs;
	}

	public void setAlternative(SyntaxSubpart e)
	{
		Alternative = e;
	}

	public void setFlag(boolean b)
	{
		flag = b;
	}

	@Override
	public void setID(String s)
	{
		id = s;
	}

	public void setNumber(int n)
	{
		number = n;
	}

	public void setSucessor(SyntaxSubpart e)
	{
		Sucessor = e;
	}
}
