package org.grview.syntax.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** This class is a container for a grammar **/
public class Grammar
{

	private HashMap<GrammarComponent, List<GrammarComponent>> alternatives;
	private HashMap<GrammarComponent, List<GrammarComponent>> antiAlternatives;
	private HashMap<GrammarComponent, List<GrammarComponent>> antiSuccessors;
	private List<GrammarComponent> components;
	private GrammarComponent current;
	private GrammarComponent head;
	private List<GrammarComponent> heads;
	private List<GrammarComponent> leftHands;
	private HashMap<GrammarComponent, List<GrammarComponent>> successors;

	public Grammar(GrammarComponent current)
	{
		this.alternatives = new HashMap<GrammarComponent, List<GrammarComponent>>();
		this.antiAlternatives = new HashMap<GrammarComponent, List<GrammarComponent>>();
		this.successors = new HashMap<GrammarComponent, List<GrammarComponent>>();
		this.antiSuccessors = new HashMap<GrammarComponent, List<GrammarComponent>>();
		this.leftHands = new ArrayList<GrammarComponent>();
		this.heads = new ArrayList<GrammarComponent>();
		this.components = new ArrayList<GrammarComponent>();
		setCurrent(current);
	}

	private GrammarComponent addComp(GrammarComponent comp)
	{
		// is there a corresponding comp already?
		for (GrammarComponent c : components)
		{
			if (c.getId().equals(comp.getId()))
			{
				return c;
			}
		}
		if (!alternatives.containsKey(comp))
		{
			alternatives.put(comp, new ArrayList<GrammarComponent>());
		}
		if (!successors.containsKey(comp))
		{
			successors.put(comp, new ArrayList<GrammarComponent>());
		}
		if (!antiAlternatives.containsKey(comp))
		{
			antiAlternatives.put(comp, new ArrayList<GrammarComponent>());
		}
		if (!antiSuccessors.containsKey(comp))
		{
			antiSuccessors.put(comp, new ArrayList<GrammarComponent>());
		}
		if (!components.contains(comp) && comp != head)
		{
			components.add(comp);
		}
		return comp;
	}

	public void addAlternative(GrammarComponent alternative)
	{
		alternative = addComp(alternative);
		alternatives.get(current).add(alternative);
		antiAlternatives.get(alternative).add(current);

	}

	public void addLeftHand(GrammarComponent lh)
	{
		addComp(lh);
		leftHands.add(lh);
	}

	public void addSuccessor(GrammarComponent successor)
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

	public List<GrammarComponent> getAlternatives(GrammarComponent source)
	{
		if (alternatives.containsKey(source))
		{
			return alternatives.get(source);
		}
		return null;
	}

	public List<GrammarComponent> getAntiAlternatives(GrammarComponent target)
	{
		if (antiAlternatives.containsKey(target))
		{
			return antiAlternatives.get(target);
		}
		return null;
	}

	public List<GrammarComponent> getAntiSuccessors(GrammarComponent target)
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
	public List<GrammarComponent> getComponents()
	{
		return components;
	}

	public GrammarComponent getCurrent()
	{
		return this.current;
	}

	public GrammarComponent getHead()
	{
		return head;
	}

	public List<GrammarComponent> getHeads()
	{
		return heads;
	}

	public List<GrammarComponent> getLeftHands()
	{
		return leftHands;
	}

	public List<GrammarComponent> getSucessors(GrammarComponent source)
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

	public void setCurrent(GrammarComponent current)
	{
		current = addComp(current);
		this.current = current;
	}

	public void setHead(GrammarComponent head)
	{
		this.head = head;
		this.heads.add(head);
	}
}
