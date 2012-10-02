package org.grview.syntax.analyzer.gsll1;

import java.util.Dictionary;
import java.util.Hashtable;

public class AnalyzerGlobalVariavel
{
	private static AnalyzerGlobalVariavel instance;
	
	private int varNumber = 0;
	private String varName = "var";

	public static AnalyzerGlobalVariavel getInstance()
	{
		return instance;
	}

	public static AnalyzerGlobalVariavel setInstance()
	{
		instance = new AnalyzerGlobalVariavel();
		return instance;
	}

	Dictionary<String, Object> globalList = new Hashtable<String, Object>();

	public void setVariable(String name, Object value)
	{
		globalList.put(name, value);
	}
	
	public void newVariable(Object value)
	{
		varNumber++;
		globalList.put(varName+varNumber, value);
	}
	
	public Object getLastVariable()
	{
		return globalList.get(varName+varNumber);
	}

	public Object getObjectVariable(String name)
	{
		return globalList.get(name);
	}
	
	public int getIntVariable(String name)
	{
		return (int)globalList.get(name);
	}
	
	public String getStringVariable(String name)
	{
		return (String)globalList.get(name);
	}
}
