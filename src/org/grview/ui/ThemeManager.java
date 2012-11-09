package org.grview.ui;

import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.BlueHighlightDockingTheme;
import net.infonode.docking.theme.ClassicDockingTheme;
import net.infonode.docking.theme.DefaultDockingTheme;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.GradientDockingTheme;
import net.infonode.docking.theme.LookAndFeelDockingTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.theme.SlimFlatDockingTheme;
import net.infonode.docking.theme.SoftBlueIceDockingTheme;

import org.grview.project.ProjectManager;

public class ThemeManager
{
	public enum Theme
	{
		BlueHighlightDockingTheme, ClassicDockingTheme, DefaultDockingTheme, GradientDockingTheme, LookAndFeelDockingTheme, ShapedGradientDockingTheme, SlimFlatDockingTheme, SoftBlueIceDockingTheme,
	}

	static DefaultDockingTheme defaultDockingTheme;
	static GradientDockingTheme gradientDockingTheme;
	static LookAndFeelDockingTheme lookAndFeelDockingTheme;
	static ShapedGradientDockingTheme shapedGradientDockingTheme;
	static SlimFlatDockingTheme slimFlatDockingTheme;
	static SoftBlueIceDockingTheme softBlueIceDockingTheme;
	private static BlueHighlightDockingTheme blueHighlightDockingTheme;

	private static ClassicDockingTheme classicDockingTheme;

	private static BlueHighlightDockingTheme getBlueHighlightDockingTheme()
	{
		if (blueHighlightDockingTheme == null)
			blueHighlightDockingTheme = new BlueHighlightDockingTheme();
		return blueHighlightDockingTheme;
	}

	private static ClassicDockingTheme getClassicDockingTheme()
	{
		if (classicDockingTheme == null)
			classicDockingTheme = new ClassicDockingTheme();
		return classicDockingTheme;
	}

	private static DefaultDockingTheme getDefaultDockingTheme()
	{
		if (defaultDockingTheme == null)
			defaultDockingTheme = new DefaultDockingTheme();
		return defaultDockingTheme;
	}

	private static GradientDockingTheme getGradientDockingTheme()
	{
		if (gradientDockingTheme == null)
			gradientDockingTheme = new GradientDockingTheme();
		return gradientDockingTheme;
	}

	private static LookAndFeelDockingTheme getLookAndFeelDockingTheme()
	{
		if (lookAndFeelDockingTheme == null)
			lookAndFeelDockingTheme = new LookAndFeelDockingTheme();
		return lookAndFeelDockingTheme;
	}

	private static ShapedGradientDockingTheme getShapedGradientDockingTheme()
	{
		if (shapedGradientDockingTheme == null)
			shapedGradientDockingTheme = new ShapedGradientDockingTheme();
		return shapedGradientDockingTheme;
	}

	private static SlimFlatDockingTheme getSlimFlatDockingTheme()
	{
		if (slimFlatDockingTheme == null)
			slimFlatDockingTheme = new SlimFlatDockingTheme();
		return slimFlatDockingTheme;
	}

	private static SoftBlueIceDockingTheme getSoftBlueIceDockingTheme()
	{
		if (softBlueIceDockingTheme == null)
			softBlueIceDockingTheme = new SoftBlueIceDockingTheme();
		return softBlueIceDockingTheme;
	}

	public static void changeTheme(RootWindowProperties rootWindowProperties, Theme theme)
	{
		DockingWindowsTheme oldTheme = getCurrentTheme();
		DockingWindowsTheme newTheme = getTheme(theme);
		rootWindowProperties.replaceSuperObject(oldTheme.getRootWindowProperties(), newTheme.getRootWindowProperties());
		ProjectManager.getInstance().getProject().setTheme(theme);
	}

	public static DockingWindowsTheme getCurrentTheme()
	{
		return getTheme(ProjectManager.getInstance().getProject().getTheme());
	}

	public static DockingWindowsTheme getTheme(Theme theme)
	{
		switch (theme)
		{
			case BlueHighlightDockingTheme:
				return getBlueHighlightDockingTheme();
			case SoftBlueIceDockingTheme:
				return getSoftBlueIceDockingTheme();
			case ClassicDockingTheme:
				return getClassicDockingTheme();
			case DefaultDockingTheme:
				return getDefaultDockingTheme();
			case GradientDockingTheme:
				return getGradientDockingTheme();
			case LookAndFeelDockingTheme:
				return getLookAndFeelDockingTheme();
			case ShapedGradientDockingTheme:
				return getShapedGradientDockingTheme();
			case SlimFlatDockingTheme:
				return getSlimFlatDockingTheme();
			default:
				return getShapedGradientDockingTheme();
		}
	}

}
