package org.grview.semantics;

import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.grview.syntax.command.AddRoutineCommand;
import org.grview.syntax.command.CommandFactory;
import org.grview.util.Log;

public class SemFileMgr
{

	private File file;
	private PropertyChangeSupport monitor;

	public SemFileMgr(File file, PropertyChangeSupport monitor)
	{
		this.file = file;
		this.monitor = monitor;
	}

	private int countOpenBrackets(String string)
	{
		char[] chars = string.toCharArray();
		int openBrackets = 0;
		for (char c : chars)
		{
			if (c == '{')
			{
				openBrackets++;
			}
			else if (c == '}')
			{
				openBrackets--;
			}
		}
		return openBrackets;
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

	private boolean rewriteFile(String name)
	{
		// TODO this should be done more elegantly
		String header = "";
		String tail = "";
		String body = "";
		try
		{
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			StringBuffer read = new StringBuffer();
			boolean headerRead = true;
			boolean bodyRead = false;
			boolean tailRead = false;
			int cnt = 0;
			while ((line = br.readLine()) != null)
			{
				if (headerRead)
				{
					header += line + "\n";
					if (cnt == 1)
					{
						cnt = 0;
						headerRead = false;
						bodyRead = true;
					}
					if (line.contains("SEMANTIC ROUTINES ESPECIFICATION"))
					{
						cnt = 1;
					}
				}
				else if (bodyRead)
				{
					if (line.contains("SELF GENERATED METHODS"))
					{
						bodyRead = false;
						tailRead = true;
						tail += line + "\n";
					}
					else
					{
						body += line + "\n";
					}
				}
				else if (tailRead)
				{
					tail += line + "\n";
				}
			}
			br.close();
			fis.close();
			StringBuffer content = new StringBuffer();
			if (name != null)
			{
				String code = SemanticRoutinesRepo.getCode(name);
				if (code != null)
				{
					String[] codeLines = code.split("\n");
					code = "";
					for (String cl : codeLines)
					{
						code += "\t" + cl + "\n";
					}
					content.append(code);
				}
			}
			content.append(body);
			String text = header + content.toString() + tail;
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(text);
			osw.close();
			fos.close();
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

	public void editRouine(String oldName, String name, String code)
	{
		try
		{
			int openBrackets = 0;
			String remainingCode = "";
			code = getFormatedCode(name, getCleanCode(oldName, code));
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			boolean found = false;
			while ((line = br.readLine()) != null)
			{
				if (openBrackets > 0)
				{
					openBrackets += countOpenBrackets(line);
				}
				else if (line.matches("(\\s*\\t*)*void(\\s*\\t*)*" + oldName + "(\\s*\\t*)*\\(\\)(\\s*\\t*)*\\{.*"))
				{
					openBrackets = countOpenBrackets(line);
					found = true;
				}
				if (openBrackets == 0 && found)
				{
					remainingCode += code;
					found = false;
				}
				else if (openBrackets == 0)
				{
					remainingCode += line + "\n";
				}
			}
			SemanticRoutinesRepo.setCode(name, code);
			br.close();
			fis.close();
			FileWriter fw = new FileWriter(file);
			fw.write(remainingCode);
			fw.close();
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, "Could not insert routine in semantic file.", e);
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
		for (String l : lines)
		{
			if (l.startsWith("\t"))
			{
				code += l.replaceFirst("\t\t", "") + "\n";
			}
			else
			{
				code += l + "\n";
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
		// TODO more sophisticated methods (such as methods that can make
		// references to each other
		// format the code as a fully qualified beanshell code
		code = getFormatedCode(name, code);
		if (command.addObject(widgetName, name) && command.execute())
		{
			SemanticRoutinesRepo.registerRoutine(name);
			SemanticRoutinesRepo.setCode(name, code);
			rewriteFile(name);
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

	public void updateCodeFromFile(String name)
	{
		try
		{
			int openBrackets = 0;
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			String code = "";
			boolean found = false;
			while ((line = br.readLine()) != null)
			{
				if (openBrackets > 0)
				{
					openBrackets += countOpenBrackets(line);
					code += line + "\n";
				}
				else if (line.matches("(\\s*\\t*)*void(\\s*\\t*)*" + name + "(\\s*\\t*)*\\(\\)(\\s*\\t*)*\\{.*"))
				{
					openBrackets = countOpenBrackets(line);
					code += line + "\n";
					found = true;
				}
				else if (openBrackets == 0 && found)
				{
					break;
				}
			}
			SemanticRoutinesRepo.setCode(name, code);
			br.close();
			fis.close();
		}
		catch (Exception e)
		{
			// do nothing
		}
	}
}
