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

	class ResolvableHelper implements Serializable
	{

		private static final long serialVersionUID = 1L;

		public Object readResolve()
		{
			return this;
		}
	}

	private static final String PREFERRED_ID = "janelaTopComponent";

	public OutlineTopComponent(GraphScene scene)
	{
		initComponents();

		add(scene.createSatelliteView(), BorderLayout.CENTER);

		setSize(900, 700);
	}

	private void initComponents()
	{
		setLayout(new java.awt.BorderLayout());
	}

	@Override
	protected String preferredID()
	{
		return PREFERRED_ID;
	}

	@Override
	public void componentClosed()
	{
	}

	@Override
	public void componentOpened()
	{
	}

	@Override
	public int getPersistenceType()
	{
		return TopComponent.PERSISTENCE_NEVER;
	}

	@Override
	public Object writeReplace()
	{
		return new ResolvableHelper();
	}
}
