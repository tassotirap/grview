package org.grview.ui.component;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.grview.project.Project;
import org.grview.project.tree.FileTree;

public class ProjectsComponent extends AbstractComponent
{

	@Override
	public JComponent create(Object param) throws BadParameterException
	{
		Project project;
		if (param instanceof Project)
		{
			project = (Project) param;
			FileTree fileTree = new FileTree(project);
			JScrollPane jScrollPane = new JScrollPane(fileTree.getView());
			return jScrollPane;
		}
		else
		{
			throw new BadParameterException("A Reference to a directoty was expected.");
		}
	}

	@Override
	public void fireContentChanged()
	{
	}

}
