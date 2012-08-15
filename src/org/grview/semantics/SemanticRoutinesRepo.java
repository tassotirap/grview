/*
 * Created on 12/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.grview.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import org.grview.lexical.Yytoken;
import org.grview.syntax.model.TabNode;

public class SemanticRoutinesRepo
{
	/* The user may add variables of any type. */
	private Stack parseStack; // This variable is the parse Stack. **Should not
								// be removed.**
	private TabNode tabNodes[];// This variable is the table of reserved
								// symbols. **Should not be removed.**
	private Yytoken currentToken;// This variable contains the last recognized
									// token **Should not be removed.**

	private ArrayList<TokenListener> listeners = new ArrayList<TokenListener>();

	private SemanticRoutinesIvoker srIvoker;

	/* The routines that can be registered here at runtime */
	private static HashMap<String, String> routineCode;

	/* The constructor of this class */
	public SemanticRoutinesRepo(Stack parseStack, TabNode tabNodes[])
	{
		this.parseStack = parseStack;
		this.tabNodes = tabNodes;
		srIvoker = SemanticRoutinesIvoker.getLastInstance(parseStack, tabNodes, this);
		srIvoker.setCurrentToken(currentToken);
		listeners.add(srIvoker);
	}

	public static String getCode(String routineName)
	{
		if (routineName != null)
		{
			if (routineCode.containsKey(routineName))
			{
				return routineCode.get(routineName);
			}
		}
		return null;
	}

	public static Set<String> getRegRoutines()
	{
		return routineCode.keySet();
	}

	/**
	 * @return the routineCode
	 */
	public static HashMap<String, String> getRoutineCode()
	{
		return routineCode;
	}

	/* register a routine */
	public static void registerRoutine(String routineName)
	{
		routineCode.put(routineName, "");
	}

	/* removes a routine if it is registered */
	public static void removeRoutine(String routineName)
	{
		if (routineCode.containsKey(routineName))
		{
			routineCode.remove(routineName);
		}
	}

	public static void setCode(String routineName, String code)
	{
		if (routineName != null && code != null)
		{
			routineCode.put(routineName, code);
		}
	}

	/**
	 * @param routineCode
	 *            the routineCode to set
	 */
	public static void setRoutineCode(HashMap<String, String> routineCode)
	{
		SemanticRoutinesRepo.routineCode = routineCode;
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
		if (srIvoker != null && routineCode.containsKey(function))
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
