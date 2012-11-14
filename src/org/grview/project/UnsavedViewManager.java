package org.grview.project;

import java.util.ArrayList;
import java.util.HashMap;

import org.grview.project.interfaces.IViewManager;
import org.grview.ui.dynamicview.DynamicView;

public class UnsavedViewManager implements IViewManager
{
	private HashMap<String, DynamicView> unsavedViews;
	
	public UnsavedViewManager()
	{
		unsavedViews = new HashMap<String, DynamicView>();		
	}
	
	@Override
	public ArrayList<DynamicView> getUnsavedViews()
	{
		return new ArrayList<DynamicView>(unsavedViews.values());
	}
	
	@Override
	public boolean hasUnsavedView(DynamicView value)
	{
		return unsavedViews.containsValue(value);
	}
	

	@Override
	public boolean hasUnsavedView(String key)
	{
		return unsavedViews.containsKey(key);
	}
	
	@Override
	public void removeUnsavedView(String key)
	{
		unsavedViews.remove(key);
	}
	
	@Override
	public void setUnsavedView(String key, DynamicView value)
	{
		if (!unsavedViews.containsKey(key))
		{
			unsavedViews.put(key, value);
		}
	}

}
