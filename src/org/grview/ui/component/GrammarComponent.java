package org.grview.ui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.canvas.state.StaticStateManager;
import org.grview.ui.toolbar.ToolBarGrammar;
import org.grview.util.Log;

public class GrammarComponent extends AbstractComponent implements FileComponent, PropertyChangeListener
{

	String path;
	private Canvas canvas;

	@Override
	public JComponent create(Object param) throws BadParameterException
	{
		final Canvas canvas;
		if (param instanceof Canvas || param instanceof String)
		{
			if (param instanceof Canvas)
			{
				canvas = (Canvas) param;
				path = CanvasFactory.getCanvasPath(canvas);
			}
			else
			{
				canvas = CanvasFactory.getCanvasFromFile((String) param);
				path = (String) param;
			}
			this.canvas = canvas;
			JScrollPane jsp = new JScrollPane();
			JComponent view = canvas.createView();
			ToolBarGrammar toolBarGrammar = new ToolBarGrammar(canvas);
			toolBarGrammar.setLayout(new BoxLayout(toolBarGrammar, BoxLayout.PAGE_AXIS));
			JPanel canvasPanel = new JPanel();
			canvasPanel.setLayout(new BorderLayout());
			canvasPanel.add(toolBarGrammar, BorderLayout.WEST);
			jsp.setViewportView(view);
			canvasPanel.add(jsp, BorderLayout.CENTER);
			canvas.setPreferredSize(new Dimension(jsp.getWidth(), jsp.getHeight()));
			CanvasFactory.getVolatileStateManager(canvas.getID()).getMonitor().addPropertyChangeListener("writing", this);
			GrammarRepo.addGramComponent(this);
			return canvasPanel;
		}
		else
		{
			throw new BadParameterException("A reference to a Canvas was expected.");
		}
	}

	@Override
	public void fireContentChanged()
	{
		for (ComponentListener listener : listeners)
		{
			listener.ContentChanged(this, null, null);
		}
	}

	@Override
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		canvas = CanvasFactory.getCanvasFromFile(path);
		this.path = path;
	}

	@Override
	public void saveFile()
	{
		StaticStateManager staticStateManager = canvas.getStaticStateManager();
		try
		{
			staticStateManager.write();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Could not save file!", e);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals("writing"))
		{
			fireContentChanged();
		}
	}
}
