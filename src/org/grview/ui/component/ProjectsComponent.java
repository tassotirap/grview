package org.grview.ui.component;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.grview.project.Project;
import org.grview.project.tree.FileTree;


public class ProjectsComponent extends Component {

	@Override
	public JComponent create(Object param) throws BadParameterException {
		Project project;
		if (param instanceof Project) {
			project = (Project) param;
			FileTree ft = new FileTree(project);
			JScrollPane jsp = new JScrollPane(ft.getView());
			return jsp;
		}
		else {
			throw new BadParameterException("A Reference to a directoty was expected.");
		}
	}

	@Override
	public void fireContentChanged() {}
	

}
