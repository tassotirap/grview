package org.grview.ui.dynamicview;

import java.util.ArrayList;

public class DynamicViewList
{
	ArrayList<DynamicView> dynamicViewList;

	public DynamicViewList()
	{
		dynamicViewList = new ArrayList<DynamicView>();
	}

	public boolean add(DynamicView dynamicView)
	{
		return dynamicViewList.add(dynamicView);
	}

	public DynamicView get(int index)
	{
		return dynamicViewList.get(index);
	}

	public int size()
	{
		return dynamicViewList.size();
	}

	public DynamicView[] toArray()
	{
		return dynamicViewList.toArray(new DynamicView[dynamicViewList.size()]);
	}
}
