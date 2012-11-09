package org.grview.project.interfaces;

import java.util.ArrayList;

import org.grview.ui.DynamicView;

public interface IViewManager
{

	public abstract ArrayList<DynamicView> getUnsavedViews();

	public abstract boolean hasUnsavedView(DynamicView value);

	public abstract boolean hasUnsavedView(String key);

	public abstract void removeUnsavedView(String key);

	public abstract void setUnsavedView(String key, DynamicView value);

}