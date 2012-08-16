package org.grview.semantics;

import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.grview.syntax.command.AddRoutineCommand;
import org.grview.syntax.command.CommandFactory;
import org.grview.util.Log;

public class SemFileManager
{

	private File file;
	private PropertyChangeSupport monitor;

	public SemFileManager(File file, PropertyChangeSupport monitor)
	{
		this.file = file;
		this.monitor = monitor;
	}

	private String getFormatedCode(String name, String code)
	{
		if (!code.trim().startsWith("void " + "name"))
		{
			String[] codeLines = code.split("\n");
			code = "void " + name + "() {\n";
			for (String line : codeLines)
			{
				code += "\t" + line + "\n";
			}
			code += "}";
		}
		return code;
	}

	private boolean addToFile(String name, String code)
	{
		try
		{
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
			String line = bufferedReader.readLine();
			String currentFile = "";
			while (line != null)
			{
				currentFile += line + "\n";
				if (line.equals(SemanticRoutinesRepo.BEGIN_SEMANTIC_ROUTINES))
				{
					currentFile += SemanticRoutinesRepo.BEGIN_ROUTINE + name + " */ \n";
					currentFile += code + "\n";
					currentFile += SemanticRoutinesRepo.END_ROUTINE + name + " */ \n";
				}
				line = bufferedReader.readLine();
			}
			bufferedReader.close();

			FileWriter fileWriter = new FileWriter(file);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(currentFile);
			printWriter.close();

		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, "Could not create new routine", e);
			return false;
		}
		return true;
	}

	public boolean canInsert(String routine)
	{
		return SemanticRoutinesRepo.getCode(routine) == null;
	}

	public void editRouine(String name, String code)
	{
		try
		{
			code = getFormatedCode(name, code);
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
			String line = bufferedReader.readLine();
			String currentFile = "";
			
			//HEAD
			while (line != null)
			{
				currentFile += line + "\n";
				if (line.equals(SemanticRoutinesRepo.BEGIN_SEMANTIC_ROUTINES))
				{
					line = bufferedReader.readLine();
					break;
				}
				line = bufferedReader.readLine();
			}
			
			//
			while (line != null)
			{
				currentFile += line + "\n";
				if (line.contains(SemanticRoutinesRepo.BEGIN_ROUTINE + name))
				{
					currentFile += code + "\n";
					line = bufferedReader.readLine();
					while (line != null)
					{
						if (line.contains(SemanticRoutinesRepo.END_ROUTINE + name))
						{
							currentFile += line + "\n";
							break;
						}
						line = bufferedReader.readLine();	
					}					
				}
				
				
				line = bufferedReader.readLine();
							
			}
			bufferedReader.close();

			FileWriter fileWriter = new FileWriter(file);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(currentFile);
			printWriter.close();

		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, "Could not create new routine", e);
		}
	}

	public String getCleanCode(String routine, String code)
	{
		if (code == null)
		{
			code = SemanticRoutinesRepo.getCode(routine);
			if (code == null)
			{
				return null;
			}
		}
		code = code.replaceFirst("(\\s*\\t*)*void(\\s*\\t*)*" + routine + "(\\s*\\t*)*\\(\\)(\\s*\\t*)*\\{", "");
		code = code.replaceFirst("(\\s*\\t*)*\\n", "");
		if (code.lastIndexOf("}") >= 0)
			code = code.substring(0, code.lastIndexOf("}"));
		if (code.lastIndexOf("\n") >= 0)
			code = code.substring(0, code.lastIndexOf("\n"));
		String[] lines = code.split("\n");
		code = "";
		for (String line : lines)
		{
			if (line.startsWith("\t"))
			{
				code += line.replaceFirst("\t", "") + "\n";
			}
			else
			{
				code += line + "\n";
			}
		}
		if (code.lastIndexOf("\n") >= 0)
			code = code.substring(0, code.lastIndexOf("\n"));
		return code;
	}

	public boolean InsertRoutine(String name, String code, String widgetName)
	{
		AddRoutineCommand command = CommandFactory.createAddRoutineCommand();
		monitor.firePropertyChange("undoable", null, command);
		code = getFormatedCode(name, code);
		if (command.addObject(widgetName, name) && command.execute())
		{
			addToFile(name, code);
			return true;
		}
		return false;
	}

	/**
	 * Validates the content of the NewRoutineWizard form.
	 * 
	 * @return true if the content is in a valid format, false otherwise.
	 */
	public boolean isValid()
	{
		return true;
	}
}
