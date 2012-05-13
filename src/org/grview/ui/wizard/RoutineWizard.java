package org.grview.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;

import javax.swing.WindowConstants;

import org.grview.canvas.widget.MarkedWidget;
import org.grview.project.Project;
import org.grview.semantics.SemFileMgr;
import org.grview.util.Log;


public class RoutineWizard {

	/** the widget that will receive a semantic routine **/
	private MarkedWidget widget;
	private String widgetName;
	private String routine;
	private Project project;
	private PropertyChangeSupport monitor;
	private final SemFileMgr sfm;
	/**
	 * Constructor
	 * @param widget, the Widget that will receive the semantic routine
	 */
	public RoutineWizard(String widgetName, MarkedWidget widget, String routine, Project project, PropertyChangeSupport monitor) {
		this.widget = widget;
		this.widgetName = widgetName;
		this.routine = routine;
		this.project = project;
		this.monitor = monitor;
		sfm = new SemFileMgr(project.getSemFile().get(project.getVersion()), monitor);
		if (widgetName != null && widget != null) {
			initWindow();
		}
	}
	
	private void initWindow() {
		RoutineWizardWindow window = new RoutineWizardWindow();
		if (routine != null) {
			window.setTitle("Edit " + routine);
			window.getInsertButton().setText("Edit");
			sfm.updateCodeFromFile(routine);
			if (sfm.getCleanCode(routine, null) != null) {
				window.getCodeTextArea().setText(sfm.getCleanCode(routine, null));
			}
			window.getNameTextField().setText(routine);
		}
		else {
			window.setTitle("Create new semantic routine");
		}
		window.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		initComponents(window);
		window.setVisible(true);
	}
	
	private void initComponents(final RoutineWizardWindow window) {
		window.getInsertButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (((routine == null && sfm.canInsert(routine)) ||
						(routine != null && !sfm.canInsert(routine))) && sfm.isValid()) {
					String name = window.getNameTextField().getText();
					String code = window.getCodeTextArea().getText();
					if (routine != null) {
						sfm.editRouine(routine, name, code);
						widget.setMark(name);
					}
					else if (sfm.InsertRoutine(name, code, widgetName)) {
						widget.setMark(name);
					}
					else {
						Log.log(Log.ERROR,this, "Could not insert routine in semantic file.", new Exception ("Could not create semantic routine"));
					}
				}
				else if (!sfm.canInsert(routine)) {
					Log.log(Log.ERROR,this, "Could not insert this routine.", new Exception ("This semantic routine alrealdy existis in the file."));
				}
				else {
					Log.log(Log.ERROR,this, "Could not validate the semantic routines file.", new Exception ("The semantic routines file is invalid"));
				}
				window.setVisible(false);
			}
		});
		window.getCancelButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				window.setVisible(false);	
			}
		});
	}
}
