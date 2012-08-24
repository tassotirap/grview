package org.grview.syntax.analyzer.gsll1.exportable;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Stack;

public class SemanticRoutines
{

	private Yytoken currentToken;

	private GroovyObject groovyObject;
	private PrintStream out;
	private Stack<ParseStackNode> parseStack;
	private File scriptsFile = new File("srs.groovy");
	private Analyzer.TabNode[] tabT;

	public SemanticRoutines(Stack<ParseStackNode> parseStack, Analyzer.TabNode[] tabT, PrintStream out)
	{
		this(parseStack, tabT, out, null);
	}

	public SemanticRoutines(Stack<ParseStackNode> parseStack, Analyzer.TabNode[] tabT, PrintStream out, File scriptsFile)
	{
		this.tabT = tabT;
		this.parseStack = parseStack;
		this.out = out;
		if (scriptsFile != null)
		{
			this.scriptsFile = scriptsFile;
		}
		initialize();
	}

	private void initialize()
	{
		try
		{
			ClassLoader parent = getClass().getClassLoader();
			GroovyClassLoader groovyClassLoader = new GroovyClassLoader(parent);
			Class groovyClass = groovyClassLoader.parseClass(scriptsFile);
			groovyObject = (GroovyObject) groovyClass.newInstance();
			DelegatingMetaClass metaClass = new DelegatingMetaClass(groovyObject.getMetaClass())
			{

				@Override
				public Object invokeMethod(Object object, String methodName, Object[] arguments)
				{
					return super.invokeMethod(object, methodName, arguments);
				}
			};
			metaClass.initialize();
			groovyObject.setMetaClass(metaClass);
			groovyClassLoader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void execFunction(String sem)
	{
		groovyObject.setProperty("tabT", tabT);
		groovyObject.setProperty("parseStack", parseStack);
		groovyObject.setProperty("currentToken", currentToken);
		groovyObject.setProperty("output", out);
		Object[] args = {};
		for (Method m : groovyObject.getClass().getMethods())
		{
			if (m.getName().equals(sem))
			{
				groovyObject.invokeMethod(sem, args);
			}
		}
	}

	public Yytoken getCurrentToken()
	{
		return currentToken;
	}

	public Stack<ParseStackNode> getParseStack()
	{
		return parseStack;
	}

	public Analyzer.TabNode[] getTabT()
	{
		return tabT;
	}

	public void setCurrentToken(Yytoken currToken)
	{
		this.currentToken = currToken;
	}

	public void setParseStack(Stack<ParseStackNode> parseStack)
	{
		this.parseStack = parseStack;
	}

	public void setTabT(Analyzer.TabNode[] tabT)
	{
		this.tabT = tabT;
	}

}
