package org.grview.syntax.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** This class is a container for a grammar **/
public class Grammar
{

	private GrComp head;
	private List<GrComp> heads;
	private List<GrComp> leftHands;
	private List<GrComp> components;
	private HashMap<GrComp, List<GrComp>> alternatives;
	private HashMap<GrComp, List<GrComp>> antiAlternatives;
	private HashMap<GrComp, List<GrComp>> successors;
	private HashMap<GrComp, List<GrComp>> antiSuccessors;
	private GrComp current;

	public Grammar(GrComp current)
	{
		this.alternatives = new HashMap<GrComp, List<GrComp>>();
		this.antiAlternatives = new HashMap<GrComp, List<GrComp>>();
		this.successors = new HashMap<GrComp, List<GrComp>>();
		this.antiSuccessors = new HashMap<GrComp, List<GrComp>>();
		this.leftHands = new ArrayList<GrComp>();
		this.heads = new ArrayList<GrComp>();
		this.components = new ArrayList<GrComp>();
		setCurrent(current);
	}

	private GrComp addComp(GrComp comp)
	{
		// is there a corresponding comp already?
		for (GrComp c : components)
		{
			if (c.getId().equals(comp.getId()))
			{
				return c;
			}
		}
		if (!alternatives.containsKey(comp))
		{
			alternatives.put(comp, new ArrayList<GrComp>());
		}
		if (!successors.containsKey(comp))
		{
			successors.put(comp, new ArrayList<GrComp>());
		}
		if (!antiAlternatives.containsKey(comp))
		{
			antiAlternatives.put(comp, new ArrayList<GrComp>());
		}
		if (!antiSuccessors.containsKey(comp))
		{
			antiSuccessors.put(comp, new ArrayList<GrComp>());
		}
		if (!components.contains(comp) && comp != head)
		{
			components.add(comp);
		}
		return comp;
	}

	public void addAlternative(GrComp alternative)
	{
		alternative = addComp(alternative);
		alternatives.get(current).add(alternative);
		antiAlternatives.get(alternative).add(current);

	}

	public void addLeftHand(GrComp lh)
	{
		addComp(lh);
		leftHands.add(lh);
	}

	public void addSuccessor(GrComp successor)
	{
		successor = addComp(successor);
		successors.get(current).add(successor);
		antiSuccessors.get(successor).add(current);
	}

	@Override
	public void finalize()
	{
		this.current = null;
	}

	public List<GrComp> getAlternatives(GrComp source)
	{
		if (alternatives.containsKey(source))
		{
			return alternatives.get(source);
		}
		return null;
	}

	public List<GrComp> getAntiAlternatives(GrComp target)
	{
		if (antiAlternatives.containsKey(target))
		{
			return antiAlternatives.get(target);
		}
		return null;
	}

	public List<GrComp> getAntiSuccessors(GrComp target)
	{
		if (antiSuccessors.containsKey(target))
		{
			return antiSuccessors.get(target);
		}
		return null;
	}

	/**
	 * @return the components
	 */
	public List<GrComp> getComponents()
	{
		return components;
	}

	public GrComp getCurrent()
	{
		return this.current;
	}

	public GrComp getHead()
	{
		return head;
	}

	public List<GrComp> getHeads()
	{
		return heads;
	}

	public List<GrComp> getLeftHands()
	{
		return leftHands;
	}

	public List<GrComp> getSucessors(GrComp source)
	{
		if (successors.containsKey(source))
		{
			return successors.get(source);
		}
		return null;
	}

	public boolean hasCurrent()
	{
		return current != null;
	}

	public void setCurrent(GrComp current)
	{
		current = addComp(current);
		this.current = current;
	}

	public void setHead(GrComp head)
	{
		this.head = head;
		this.heads.add(head);
	}
}
