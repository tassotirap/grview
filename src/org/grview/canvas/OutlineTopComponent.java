package org.grview.canvas;

import java.awt.BorderLayout;
import java.io.Serializable;

import org.netbeans.api.visual.graph.GraphScene;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
public final class OutlineTopComponent extends TopComponent
{

	private static final String PREFERRED_ID = "janelaTopComponent";

	public OutlineTopComponent(GraphScene scene)
	{
		initComponents();
		// setIcon(Utilities.loadImage(ICON_PATH, true));

		add(scene.createSatelliteView(), BorderLayout.CENTER);

		setSize(900, 700);
	}

	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{
		setLayout(new java.awt.BorderLayout());
	}// </editor-fold>//GEN-END:initComponents
		// Variables declaration - do not modify//GEN-BEGIN:variables

	// End of variables declaration//GEN-END:variables

	@Override
	public int getPersistenceType()
	{
		return TopComponent.PERSISTENCE_NEVER;
	}

	@Override
	public void componentOpened()
	{
		// TODO add custom code on component opening
	}

	@Override
	public void componentClosed()
	{
		// TODO add custom code on component closing
	}

	/** replaces this in object stream */
	@Override
	public Object writeReplace()
	{
		return new ResolvableHelper();
	}

	@Override
	protected String preferredID()
	{
		return PREFERRED_ID;
	}

	class ResolvableHelper implements Serializable
	{

		private static final long serialVersionUID = 1L;

		public Object readResolve()
		{
			return this;
		}
	}
}
