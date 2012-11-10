package org.grview.ui.dynamicview;

import java.util.ArrayList;
import java.util.HashMap;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.ViewMap;

import org.grview.ui.TabItem;
import org.grview.ui.component.AbstractComponent;

public class DynamicViewRepository
{
	private ArrayList<DynamicViewList> defaultLayout;
	private static int DEFAULT_LAYOUT = 6;
	private HashMap<AbstractComponent, DynamicView> dynamicViewsByComponent = new HashMap<AbstractComponent, DynamicView>();
	private HashMap<Integer, DynamicView> dynamicViewsById = new HashMap<Integer, DynamicView>();
	private HashMap<String, DynamicView> dynamicViewsByPath = new HashMap<String, DynamicView>();

	public DynamicViewRepository()
	{
		this.defaultLayout = new ArrayList<DynamicViewList>();
	}

	private void addDynamicView(DynamicView dynamicView)
	{
		dynamicViewsById.put(new Integer(dynamicView.getId()), dynamicView);
		dynamicViewsByComponent.put(dynamicView.getComponentModel(), dynamicView);
		if (dynamicView.getFileName() != null)
		{
			dynamicViewsByPath.put(dynamicView.getFileName(), dynamicView);
		}
	}

	private void removeDynamicView(DynamicView dynamicView)
	{
		dynamicViewsById.remove(new Integer(dynamicView.getId()));
		dynamicViewsByComponent.remove(dynamicView.getComponentModel());
		if (dynamicViewsByPath.containsKey(dynamicView.getFileName()))
		{
			dynamicViewsByPath.remove(dynamicView.getFileName());
		}
	}

	private void updateChildDynamicViews(DockingWindow window, boolean added)
	{
		for (int i = 0; i < window.getChildWindowCount(); i++)
		{
			updateViews(window.getChildWindow(i), added);
		}
	}

	public boolean containsDynamicView(AbstractComponent component)
	{
		return dynamicViewsByComponent.containsKey(component);
	}

	public boolean containsDynamicView(int id)
	{
		return dynamicViewsById.containsKey(id);
	}

	public boolean containsDynamicView(String path)
	{
		return dynamicViewsByPath.containsKey(path);
	}

	public void createDefaultViews(ArrayList<TabItem> tabItems, ViewMap perspectiveMap)
	{
		try
		{
			for (int i = 0; i < DEFAULT_LAYOUT; i++)
				defaultLayout.add(new DynamicViewList());

			for (int i = 0; i < tabItems.size(); i++)
			{
				int nextId = getDynamicViewId();
				DynamicView view = new DynamicView(tabItems.get(i).getTitle(), tabItems.get(i).getViewIcon(), tabItems.get(i).getComponent(), null, null, nextId);
				defaultLayout.get(tabItems.get(i).getLayoutOrder()).add(view);
				perspectiveMap.addView(i, view);
				updateViews(view, true);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public ArrayList<DynamicViewList> getDefaultLayout()
	{
		return defaultLayout;
	}

	public DynamicView getDynamicView(AbstractComponent component)
	{
		return dynamicViewsByComponent.get(component);
	}

	public DynamicView getDynamicView(int id)
	{
		return dynamicViewsById.get(id);
	}

	public DynamicView getDynamicView(String path)
	{
		return dynamicViewsByPath.get(path);
	}

	public int getDynamicViewId()
	{
		int id = 0;

		while (dynamicViewsById.containsKey(new Integer(id)))
			id++;

		return id;
	}

	public void updateViews(DockingWindow window, boolean added)
	{
		if (window instanceof View)
		{
			if (window instanceof DynamicView)
			{
				DynamicView dynamicView = (DynamicView) window;
				if (added)
				{
					addDynamicView(dynamicView);
				}
				else
				{
					removeDynamicView(dynamicView);
				}
			}
		}
		else
		{
			updateChildDynamicViews(window, added);
		}
	}
}
