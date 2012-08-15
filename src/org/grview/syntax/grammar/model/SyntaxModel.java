package org.grview.syntax.grammar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a container to all essential elements of a grammar.
 * 
 * @author Gustavo H. Braga
 * 
 */
public class SyntaxModel extends SyntaxSubpart
{

	static final long serialVersionUID = 1;

	private static int count;

	private SimpleNode semanticNode;
	private List<SyntaxElement> children = new ArrayList<SyntaxElement>();
	protected Integer connectionRouter = null;

	public SyntaxModel()
	{
		super(getNewID());
	}

	public SyntaxModel(String id)
	{
		super(id);
	}

	public static String getNewID()
	{
		return SyntaxModel.class.getName() + Integer.toString(count++);
	}

	public void addChild(SyntaxElement child)
	{
		addChild(child, -1);
	}

	public void addChild(SyntaxElement child, int index)
	{
		if (index < 0)
		{
			index = children.size();
		}
		children.add(index, child);
		fireStructureChange(CHILDREN, child);
	}

	public SyntaxElement findElement(String id)
	{
		for (SyntaxElement e : children)
		{
			if (e.getID().equals(id))
			{
				return e;
			}
		}
		return null;
	}

	public List<SyntaxElement> getChildren()
	{
		return children;
	}

	public List<NodeLabel> getChildrenAsLabels()
	{
		ArrayList<NodeLabel> lNodes = new ArrayList<NodeLabel>();
		for (SyntaxElement e : children)
		{
			if (e instanceof NodeLabel)
			{
				lNodes.add((NodeLabel) e);
			}
		}
		return lNodes;
	}

	public List<SyntaxElement> getChildrenConnections()
	{
		ArrayList<SyntaxElement> cNodes = new ArrayList<SyntaxElement>();
		for (SyntaxElement e : children)
		{
			if (e instanceof Connection)
			{
				cNodes.add(e);
			}
		}
		return cNodes;
	}

	public List<SyntaxElement> getChildrenNodes()
	{
		ArrayList<SyntaxElement> cNodes = new ArrayList<SyntaxElement>();
		for (SyntaxElement e : children)
		{
			if (e instanceof SimpleNode)
			{
				cNodes.add(e);
			}
		}
		return cNodes;
	}

	public Object getPropertyValue(Object propName)
	{
		return getProperty((String) propName);
	}

	public SimpleNode getSemanticNode()
	{
		return semanticNode;
	}

	public boolean isConnection(SyntaxElement e)
	{
		return e instanceof Connection;
	}

	public boolean isNode(SyntaxElement e)
	{
		return e instanceof SimpleNode;
	}

	public void removeChild(SyntaxElement child)
	{
		if (child == null)
			return;
		SyntaxElement[] remainingChildren = new SyntaxElement[children.size() - 1];
		int i = 0;
		for (SyntaxElement se : children)
		{
			if (se != child)
			{
				remainingChildren[i++] = se;
			}
		}
		children = new ArrayList<SyntaxElement>();
		for (int j = 0; j < i; j++)
		{
			children.add(remainingChildren[j]);
		}
		fireStructureChange(CHILDREN, child);
	}

	public void setPropertyValue(Object id, Object value)
	{
		super.setProperty((String) id, (String) value);
	}

	public void setSemanticNode(SimpleNode sn)
	{
		this.semanticNode = sn;
	}

	@Override
	public String toString()
	{
		return SyntaxDefinitions.AsinDiagram_LabelText;
	}

}
