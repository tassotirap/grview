package org.grview.ui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.grview.actions.AbstractEditAction;
import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.canvas.state.StaticStateManager;
import org.grview.ui.ToolBar;
import org.grview.util.Log;
import org.netbeans.api.visual.model.ObjectSceneEvent;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.model.ObjectState;


public class GramComponent extends Component implements FileComponent, PropertyChangeListener {
	
	String path;
	private Canvas canvas;
	
	@Override
	public JComponent create(Object param) throws BadParameterException {
		final Canvas canvas;
		if (param instanceof Canvas || param instanceof String) {
			if (param instanceof Canvas) {
				canvas = (Canvas)param;
				path = CanvasFactory.getCanvasPath(canvas);
			}
			else {
				canvas = CanvasFactory.getCanvasFromFile((String)param);
				path = (String) param;
			}
			this.canvas = canvas;
			JScrollPane jsp = new JScrollPane();
			JComponent view = canvas.createView();
			ToolBar tb = ToolBar.getInstance();
			ToolBar.T6<Canvas> t6 = tb.new T6<Canvas>(canvas) {
				private static final long serialVersionUID = 1L;

				@Override
				public AbstractEditAction<Canvas> getAction(String action) {
					return canvas.getActionContext().getAction(action);
				}

			};
			t6.initLayout();
			t6.initActions();
			t6.setLayout(new BoxLayout(t6, BoxLayout.PAGE_AXIS));
			JPanel canvasPanel = new JPanel();
			canvasPanel.setLayout(new BorderLayout());
			canvasPanel.add(t6,BorderLayout.WEST);
			jsp.setViewportView(view);
			canvasPanel.add(jsp, BorderLayout.CENTER);
			canvas.setPreferredSize(new Dimension(jsp.getWidth(), jsp.getHeight()));
			CanvasFactory.getVolatileStateManager(canvas.getID()).getMonitor().addPropertyChangeListener("writing", this);
			GrammarRepo.addGramComponent(canvas, this);
			return  canvasPanel;
		}
		else {
			throw new BadParameterException("A reference to a Canvas was expected.");
		}
	}

	@Override
	public void fireContentChanged() {
		for (ComponentListener listener : listeners) {
			listener.ContentChanged(this, null, null);
		}
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void saveFile() {
		StaticStateManager ssm = canvas.getStaticStateManager();
		try {
			ssm.write();
		} catch (IOException e) {
			Log.log(Log.ERROR, this, "Could not save file!", e);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("writing")) {
			fireContentChanged();
		}
	}
}
