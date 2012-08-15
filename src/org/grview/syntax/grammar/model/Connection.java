package org.grview.syntax.grammar.model;

/** A connection between nodes **/
public class Connection extends SyntaxElement
{

	static final long serialVersionUID = 1;
	protected boolean value;
	protected SyntaxSubpart source;
	protected SyntaxSubpart target;

	private String id;

	public Connection()
	{
		this.id = this.toString();
	}

	public Connection(String id)
	{
		this.id = id;
	}

	/** Attach connection to the source node **/
	public void attachSource()
	{
		if (getSource() == null || getSource().getSourceConnections().contains(this))
		{
			return;
		}
		getSource().connectOutput(this);
	}

	/**
	 * Attach connection to the target node.
	 * 
	 * @param context
	 *            could be a successor or an alternative connection
	 */
	public void attachTarget(Object context)
	{
		if (target == null || source == null)
		{
			return;
		}
		// putting target as successor of the source
		Object e = this.getTarget();
		if (e instanceof SimpleNode)
		{
			if (getSource() instanceof SyntaxModel)
			{
				SyntaxModel l = (SyntaxModel) getSource();
				if (context.equals(SyntaxDefinitions.SucConnection))
				{
					l.setSucessor((SyntaxSubpart) e);
				}
				else if (context.equals(SyntaxDefinitions.AltConnection))
				{
					l.setAlternative((SyntaxSubpart) e);
				}
			}
		}
		getTarget().connectInput(this);
	}

	/** detach connection from source **/
	public void detachSource()
	{
		if (getSource() == null)
			return;
		getSource().disconnectOutput(this);
	}

	/** detach connection on target **/
	public void detachTarget()
	{
		if (getTarget() == null)
			return;
		// Removing target as source's successor
		SyntaxSubpart e = this.getTarget();
		if (e instanceof SimpleNode)
		{
			if (getSource() instanceof SyntaxModel)
			{
				SyntaxModel l = (SyntaxModel) getSource();
				if (l.getSucessor() == e)
				{
					l.setSucessor(null);
				}
				else if (l.getAlternative() == e)
				{
					l.setAlternative(null);
				}
			}
		}
		getTarget().disconnectInput(this);
	}

	@Override
	public String getID()
	{
		return this.id;
	}

	/*------------------------------- GETTERS AND SETTERS ----------------------*/

	public SyntaxSubpart getSource()
	{
		return source;
	}

	public String getSourceTerminal()
	{
		return source.getID();
	}

	public SyntaxSubpart getTarget()
	{
		return target;
	}

	public String getTargetTerminal()
	{
		return target.getID();
	}

	public boolean getValue()
	{
		return value;
	}

	@Override
	public void setID(String id)
	{
		this.id = id;
	}

	public void setSource(SyntaxSubpart e)
	{
		Object old = source;
		source = e;
		firePropertyChange("source", old, source);//$NON-NLS-1$
	}

	public void setTarget(SyntaxSubpart e)
	{
		Object old = target;
		target = e;
		firePropertyChange("target", old, target);
	}

	public void setValue(boolean value)
	{
		if (value == this.value)
			return;
		this.value = value;
		if (target != null)
			target.update();
		firePropertyChange("value", null, null);//$NON-NLS-1$
	}

	@Override
	public String toString()
	{
		return "Wire(" + getSource() + "," + getSourceTerminal() + "->" + getTarget() + "," + getTargetTerminal() + ")";//$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}
}
