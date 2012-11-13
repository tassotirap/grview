package org.grview.ui;

import javax.swing.JOptionPane;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;

import org.grview.project.ProjectManager;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.BadParameterException;
import org.grview.ui.component.EmptyComponent;
import org.grview.ui.component.FileComponent;
import org.grview.ui.dynamicview.DynamicView;

/** An adapter to control how a window should react when changed **/
public class WindowAdapter extends DockingWindowAdapter
{

	private MainWindow window;
	private ProjectManager projectManager;

	public WindowAdapter(MainWindow window, ProjectManager projectMediator)
	{
		this.window = window;
		this.projectManager = projectMediator;
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
			window.updateWindow(addedWindow, true);
			AbstractComponent comp = ((DynamicView) addedWindow).getComponentModel();
			if (!(comp instanceof EmptyComponent))
			{
				if (window.getTabs().getCenterTab().getChildWindowIndex(addedWindow) >= 0)
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
				projectManager.closeFile(path);
			}
		}
	}

	@Override
	public void windowClosing(DockingWindow dWindow) throws OperationAbortedException
	{
		if (dWindow instanceof DynamicView)
		{
			DynamicView dynamicView = (DynamicView) dWindow;
			if (projectManager.hasUnsavedView(dynamicView))
			{
				int option = JOptionPane.showConfirmDialog(window.getFrame(), "Would you like to save '" + dWindow.getTitle().replace(MainWindow.UNSAVED_PREFIX, "") + "' before closing?");
				if (option == JOptionPane.CANCEL_OPTION)
					throw new OperationAbortedException("Window close was aborted!");
				if (option == JOptionPane.YES_OPTION)
				{
					projectManager.saveFile(dynamicView.getComponentModel());
				}
			}
		}
		if (window.getTabs().getCenterTab().getChildWindowIndex(dWindow) >= 0 && window.getTabs().getCenterTab().getChildWindowCount() == 1)
		{
			try
			{
				window.addEmptyDynamicView();
			}
			catch (BadParameterException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void windowRemoved(DockingWindow removedFromWindow, DockingWindow removedWindow)
	{
		window.updateWindow(removedWindow, false);
	}
}
