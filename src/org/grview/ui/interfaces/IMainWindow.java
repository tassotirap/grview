package org.grview.ui.interfaces;

import javax.swing.Icon;
import javax.swing.JFrame;

import net.infonode.docking.DockingWindow;

import org.grview.model.ui.IconView;
import org.grview.ui.TabWindowList;
import org.grview.ui.ThemeManager.Theme;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.BadParameterException;

public interface IMainWindow
{
	public final static String DEFAULT_NAME = "GrView Window";
	public final static String DEFAULT_TITLE = "GrView";
	public final static String UNSAVED_PREFIX = "* ";
	public final static Icon VIEW_ICON = new IconView();

	public abstract void addEmptyDynamicView() throws BadParameterException;

	public abstract void changeTheme(Theme theme);

	public abstract JFrame getFrame();

	public abstract TabWindowList getTabs();

	public abstract TabWindowList getTabWindowList();

	public abstract void removeEmptyDynamicView();

	public abstract void setSaved(String path);

	public abstract void updateFocusedComponent(AbstractComponent component);

	public abstract void updateWindow(DockingWindow window, boolean added);

}