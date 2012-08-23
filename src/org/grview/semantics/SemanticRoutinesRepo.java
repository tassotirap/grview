/*
 * Created on 12/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.grview.semantics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import org.grview.lexical.Yytoken;
import org.grview.project.ProjectManager;
import org.grview.syntax.model.TabNode;

public class SemanticRoutinesRepo
{
	public static final String END_ROUTINE = "/* END ROUTINE: ";

	public static final String END_SEMANTIC_ROUTINES = "/* END SEMANTIC ROUTINES */";

	public static final String BEGIN_SEMANTIC_ROUTINES = "/* BEGIN SEMANTIC ROUTINES */";

	public static final String BEGIN_ROUTINE = "/* BEGIN ROUTINE: ";
	
	private Yytoken currentToken;

	private ArrayList<TokenListener> listeners = new ArrayList<TokenListener>();

	private SemanticRoutinesIvoker srIvoker;

	/* The constructor of this class */
	public SemanticRoutinesRepo(Stack parseStack, TabNode tabNodes[])
	{
		srIvoker = SemanticRoutinesIvoker.getLastInstance(parseStack, tabNodes, this);
		srIvoker.setCurrentToken(currentToken);
		listeners.add(srIvoker);
	}

	private static String getRoutineName(String line)
	{
		return line.substring(line.indexOf(BEGIN_ROUTINE) + BEGIN_ROUTINE.length()).replace("*/","").trim();
	}

	public static String getCode(String routineName)
	{
		if (routineName != null)
		{ 
			HashMap<String, String> routineCode = getRoutineCode();
			if (routineCode.containsKey(routineName))
			{
				return routineCode.get(routineName);
			}
		}
		return null;
	}
	
	public static Set<String> getRegRoutines()
	{
		return getRoutineCode().keySet();
	}

	public static HashMap<String, String> getRoutineCode()
	{
		HashMap<String, String> routineCode = new HashMap<String, String>();
		try
		{
			FileInputStream fileInputStream = new FileInputStream(ProjectManager.getProject().getSemFile());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
			String line = bufferedReader.readLine();
			while (line != null)
			{
				if (line.equals(BEGIN_SEMANTIC_ROUTINES))
				{
					line = bufferedReader.readLine();
					while (line != null)
					{
						if (line.contains(BEGIN_ROUTINE))
						{
							String name = getRoutineName(line);
							String code = "";
							line = bufferedReader.readLine();
							while (line != null)
							{
								if (line.contains((END_ROUTINE + name)))
								{
									break;
								}
								else
								{
									code += line;
									line = bufferedReader.readLine();
								}
							}
							routineCode.put(name, code);
						}
						if (line.equals(END_SEMANTIC_ROUTINES))
						{
							break;
						}
						line = bufferedReader.readLine();
					}
				}
				
				line = bufferedReader.readLine();
			}
			bufferedReader.close();

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return routineCode;
	}

	/**
	 * The method below switches to the semantic routine to executed. In this
	 * default file, there are only 3 semantic routines; if the user wants to
	 * add more routines, he should add them here, e.g.: case 5: this.rs5();
	 * break; ...
	 **/
	public void execFunction(String function)
	{
		if (function == null || function.equals("-1"))
		{
			return;
		}
		if (function.matches("[0-9]+"))
		{
			function = "rs" + function;
		}
		if (srIvoker != null && getRoutineCode().containsKey(function))
		{
			srIvoker.ivokeMethodFromFile(function);
		}
		else if (srIvoker != null)
		{
			srIvoker.ivokeMethodFromClass(function);
		}
	}

	/**
	 * @return the listeners
	 */
	public ArrayList<TokenListener> getListeners()
	{
		return listeners;
	}

	/*
	 * The parse calls this method when it recognizes a token. Thus the variable
	 * currentToken contains the last recognized token
	 */
	public void setCurrentToken(Yytoken cToken)
	{
		currentToken = cToken;
		for (TokenListener listener : listeners)
		{
			listener.setCurrentToken(cToken);
		}
	}
}
