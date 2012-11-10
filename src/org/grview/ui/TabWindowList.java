package org.grview.ui;

import java.util.ArrayList;

import net.infonode.docking.TabWindow;

public class TabWindowList
{
	
	public enum TabPlace
	{
		LEFT_TOP_TABS,
		RIGHT_BOTTOM_TABS,
		RIGHT_TOP_TABS,
		BOTTOM_LEFT_TABS,
		CENTER_TABS,
		BOTTOM_RIGHT_TABS		
	}
	
	private ArrayList<TabWindow> tabWindowList;
	
	public static final int TAB_SIZE = 6;
	
	public TabWindowList()
	{
		tabWindowList = new ArrayList<>(TAB_SIZE);
	}
	
	public TabWindow getLeftTopTab()
	{
		
		return tabWindowList.get(TabPlace.LEFT_TOP_TABS.ordinal());
	}
	
	public TabWindow getRightBottonTab()
	{
		return tabWindowList.get(TabPlace.RIGHT_BOTTOM_TABS.ordinal());
	}
	
	public TabWindow getRightTopTab()
	{
		return tabWindowList.get(TabPlace.RIGHT_TOP_TABS.ordinal());
	}
	
	public TabWindow getBottonLeftTab()
	{
		return tabWindowList.get(TabPlace.BOTTOM_LEFT_TABS.ordinal());
	}
	
	public TabWindow getCenterTab()
	{
		return tabWindowList.get(TabPlace.CENTER_TABS.ordinal());
	}
	
	public TabWindow getBottonRightTab()
	{
		return tabWindowList.get(TabPlace.BOTTOM_RIGHT_TABS.ordinal());
	}
	
	public boolean add(TabWindow tabWindow)
	{
		return tabWindowList.add(tabWindow);
	}

	public TabWindow getTabWindow(TabPlace place)
	{
		return tabWindowList.get(place.ordinal());
	}
	
	public TabWindow getTabWindow(int place)
	{
		return tabWindowList.get(place);
	}
}
