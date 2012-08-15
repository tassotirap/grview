package org.grview.ui;

import javax.swing.JOptionPane;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;

import org.grview.project.ProjectManager;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.EmptyComponent;
import org.grview.ui.component.FileComponent;

/** An adapter to control how a window should react when changed **/
public class WindowAdapter extends DockingWindowAdapter
{

	private Window window;

	public WindowAdapter(Window window)
	{
		this.window = window;
	}

	/**
	 * Update view menu items and dynamic view map.
	 * 
	 * @param window
	 *            the window in which to search for views
	 * @param added
	 *            if true the window was added
	 */
	public void updateViews(DockingWindow dWindow, boolean added)
	{
		if (dWindow instanceof View)
		{
			if (dWindow instanceof DynamicView)
			{
				DynamicView dv = (DynamicView) dWindow;
				if (added)
				{
					window.getDynamicViewsById().put(new Integer(dv.getId()), dv);
					window.getDynamicViewByComponent().put(dv.getComponentModel(), dv);
					if (dv.getFileName() != null)
					{
						window.getDynamicViewByPath().put(dv.getFileName(), dv);
					}
				}
				else
				{
					window.getDynamicViewsById().remove(new Integer(dv.getId()));
					window.getDynamicViewByComponent().remove(dv.getComponentModel());
					if (window.getDynamicViewByPath().containsKey(dv.getFileName()))
					{
						window.getDynamicViewByPath().remove(dv.getFileName());
					}
				}
			}
		}
		else
		{
			for (int i = 0; i < dWindow.getChildWindowCount(); i++)
				updateViews(dWindow.getChildWindow(i), added);
		}
	}

	@Override
	public void viewFocusChanged(View ov, View nv)
	{
		super.viewFocusChanged(ov, nv);
		if (nv instanceof DynamicView)
		{
			AbstractComponent comp = ((DynamicView) nv).getComponentModel();
			window.updateFocusedComponent(comp);
		}
	}

	@Override
	public void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow)
	{
		if (addedWindow instanceof DynamicView)
		{
			updateViews(addedWindow, true);
			AbstractComponent comp = ((DynamicView) addedWindow).getComponentModel();
			if (!(comp instanceof EmptyComponent))
			{
				if (window.getTabPage()[Window.CENTER_TABS].getChildWindowIndex(addedWindow) >= 0)
				{
					window.removeDummyView(Window.CENTER_TABS);
				}
			}
		}
	}

	@Override
	public void windowClosed(DockingWindow dWindow)
	{
		if (dWindow instanceof DynamicView)
		{
			DynamicView view = (DynamicView) dWindow;
			if (view.getComponentModel() instanceof FileComponent)
			{
				String path = ((FileComponent) view.getComponentModel()).getPath();
				window.removeFileFromProject(path);
			}
		}
	}

	@Override
	public void windowClosing(DockingWindow dWindow) throws OperationAbortedException
	{
		if (dWindow instanceof DynamicView)
		{
			DynamicView dynamicView = (DynamicView) dWindow;
			if (ProjectManager.hasUnsavedView(dynamicView))
			{
				int option = JOptionPane.showConfirmDialog(window.getFrame(), "Would you like to save '" + dWindow.getTitle().replace(Window.UNSAVED_PREFIX, "") + "' before closing?");
				if (option == JOptionPane.CANCEL_OPTION)
					throw new OperationAbortedException("Window close was aborted!");
				if (option == JOptionPane.YES_OPTION)
				{
					ProjectManager.saveFile(dynamicView.getComponentModel());
				}
			}
		}
		if (window.getTabPage()[Window.CENTER_TABS].getChildWindowIndex(dWindow) >= 0 && window.getTabPage()[Window.CENTER_TABS].getChildWindowCount() == 1)
		{
			window.addDummyView(Window.CENTER_TABS);
		}
	}

	@Override
	public void windowRemoved(DockingWindow removedFromWindow, DockingWindow removedWindow)
	{
		updateViews(removedWindow, false);
	}
}
