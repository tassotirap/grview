package org.grview.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;

import javax.swing.WindowConstants;

import org.grview.canvas.widget.MarkedWidget;
import org.grview.project.ProjectManager;
import org.grview.semantics.SemFileManager;
import org.grview.util.Log;

public class RoutineWizard
{

	private String routineName;
	private final SemFileManager semFileManager;
	/** the widget that will receive a semantic routine **/
	private MarkedWidget widget;
	private String widgetName;

	/**
	 * Constructor
	 * 
	 * @param widget
	 *            , the Widget that will receive the semantic routine
	 */
	public RoutineWizard(String widgetName, MarkedWidget widget, String routineName, PropertyChangeSupport monitor)
	{

		this.widget = widget;
		this.widgetName = widgetName;
		this.routineName = routineName;
		semFileManager = new SemFileManager(ProjectManager.getProject().getSemFile(), monitor);
		if (widgetName != null && widget != null)
		{
			initWindow();
		}
	}

	private void initComponents(final RoutineWizardWindow window)
	{
		window.getInsertButton().addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				String name = window.getNameTextField().getText();
				String code = window.getCodeTextArea().getText();

				if (name.equals("") || code.equals(""))
					return;

				if (((routineName == null && semFileManager.canInsert(routineName)) || (routineName != null && !semFileManager.canInsert(routineName))) && semFileManager.isValid())
				{

					if (routineName != null)
					{
						semFileManager.editRouine(name, code);
						widget.setMark(name);
					}
					else if (semFileManager.InsertRoutine(name, code, widgetName))
					{
						widget.setMark(name);
					}
					else
					{
						Log.log(Log.ERROR, this, "Could not insert routine in semantic file.", new Exception("Could not create semantic routine"));
					}
				}
				else if (!semFileManager.canInsert(routineName))
				{
					Log.log(Log.ERROR, this, "Could not insert this routine.", new Exception("This semantic routine alrealdy existis in the file."));
				}
				else
				{
					Log.log(Log.ERROR, this, "Could not validate the semantic routines file.", new Exception("The semantic routines file is invalid"));
				}
				window.setVisible(false);
			}
		});
		window.getCancelButton().addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				window.setVisible(false);
			}
		});
	}

	private void initWindow()
	{
		RoutineWizardWindow window = new RoutineWizardWindow();
		if (routineName != null)
		{
			window.setTitle("Edit " + routineName);
			window.getInsertButton().setText("Edit");
			window.getNameTextField().setEditable(false);
			if (semFileManager.getCleanCode(routineName, null) != null)
			{
				window.getCodeTextArea().setText(semFileManager.getCleanCode(routineName, null));
			}
			window.getNameTextField().setText(routineName);
		}
		else
		{
			window.setTitle("Create new semantic routine");
		}
		window.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		initComponents(window);
		window.setVisible(true);
	}
}
