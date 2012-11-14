package org.grview.ui.toolbar;

import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.grview.actions.ActionContextHolder;
import org.grview.canvas.Canvas;
import org.grview.project.GrviewManager;
import org.grview.ui.component.AdapterComponent;

public class ToolBarFactory
{
	private GrviewManager projectManager;
	private HashMap<Object, JComponent> toolBars = new HashMap<Object, JComponent>();
	private JComponent defaultToolBar;

	public ToolBarFactory()
	{
		this.projectManager = GrviewManager.getInstance();
	}

	private ToolBarCanvas createToolBarCanvas(final Canvas canvas)
	{
		ToolBarCanvas toolBarCanvas = new ToolBarCanvas(canvas);
		toolBarCanvas.setLayout(new BoxLayout(toolBarCanvas, BoxLayout.LINE_AXIS));
		return toolBarCanvas;
	}

	@SuppressWarnings("rawtypes")
	private JComponent createToolBarExt(final ActionContextHolder acContextHolder, boolean enableToolBarFile, boolean enableToolBarCanvas)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(createNewFileToolBar());

		if (enableToolBarFile)
		{
			ToolBarDefault toolBarFile = createToolBarFile(acContextHolder);
			panel.add(toolBarFile);
		}
		if (enableToolBarCanvas)
		{
			ToolBarCanvas toolBarCanvas = createToolBarCanvas((Canvas) acContextHolder);
			panel.add(toolBarCanvas);
		}
		return panel;

	}

	private ToolBarDefault<ActionContextHolder> createToolBarFile(final ActionContextHolder ref)
	{
		ToolBarDefault<ActionContextHolder> toolBarFile = new ToolBarDefault<ActionContextHolder>(ref);
		toolBarFile.setLayout(new BoxLayout(toolBarFile, BoxLayout.LINE_AXIS));
		return toolBarFile;
	}

	private BaseToolBar createNewFileToolBar()
	{
		ToolBarFile toolBarNewFile = new ToolBarFile(projectManager);
		toolBarNewFile.setLayout(new BoxLayout(toolBarNewFile, BoxLayout.LINE_AXIS));
		return toolBarNewFile;
	}

	@SuppressWarnings("rawtypes")
	public JComponent createToolBar(final ActionContextHolder reference, boolean enableToolBarFile, boolean enableToolBarCanvas)
	{
		ToolBarFactory toolBarFactory = new ToolBarFactory();
		if (reference == null)
		{
			if (defaultToolBar == null)
			{
				defaultToolBar = toolBarFactory.createToolBarExt(reference, false, false);
				return defaultToolBar;
			}
		}
		if (!toolBars.containsKey(reference) || reference instanceof AdapterComponent)
		{
			toolBars.put(reference, toolBarFactory.createToolBarExt(reference, enableToolBarFile, enableToolBarCanvas));
		}
		return toolBars.get(reference);
	}
}
