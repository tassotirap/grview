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
import org.grview.ui.dynamicview.DynamicView;

/** An adapter to control how a window should react when changed **/
public class WindowAdapter extends DockingWindowAdapter
{

	private Window window;
	private ProjectManager projectMediator;

	public WindowAdapter(Window window, ProjectManager projectMediator)
	{
		this.window = window;
		this.projectMediator = projectMediator;
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
			window.update(addedWindow, true);
			AbstractComponent comp = ((DynamicView) addedWindow).getComponentModel();
			if (!(comp instanceof EmptyComponent))
			{
				if (window.getTabPage().getCenterTab().getChildWindowIndex(addedWindow) >= 0)
				{
					window.removeEmptyDynamicView();
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
			if (projectMediator.hasUnsavedView(dynamicView))
			{
				int option = JOptionPane.showConfirmDialog(window.getFrame(), "Would you like to save '" + dWindow.getTitle().replace(Window.UNSAVED_PREFIX, "") + "' before closing?");
				if (option == JOptionPane.CANCEL_OPTION)
					throw new OperationAbortedException("Window close was aborted!");
				if (option == JOptionPane.YES_OPTION)
				{
					projectMediator.saveFile(dynamicView.getComponentModel());
				}
			}
		}
		if (window.getTabPage().getCenterTab().getChildWindowIndex(dWindow) >= 0 && window.getTabPage().getCenterTab().getChildWindowCount() == 1)
		{
			window.addEmptyDynamicView();
		}
	}

	@Override
	public void windowRemoved(DockingWindow removedFromWindow, DockingWindow removedWindow)
	{
		window.update(removedWindow, false);
	}
}
