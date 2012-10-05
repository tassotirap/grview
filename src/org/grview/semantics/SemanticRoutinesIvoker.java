package org.grview.semantics;

import groovy.lang.GroovyObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.grview.lexical.Yytoken;
import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.output.SemanticRoutinesOutput;
import org.grview.project.Project;
import org.grview.syntax.model.ParseStack;
import org.grview.syntax.model.TableNode;
import org.grview.util.Log;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * 
 * Load Semantic Routines file [FileName].sem
 */
public class SemanticRoutinesIvoker implements Cloneable, TokenListener
{

	private static SemanticRoutinesIvoker lastInstance;
	public static final String DEFAULT_SR_CLASS = "org.grview.semantics.SemanticRoutines";
	public static final String SPRING_XML_MODEL = "beaninjection.xml";

	private FileSystemXmlApplicationContext ctx;

	private Yytoken currentToken;

	private GroovyObject goo;
	private boolean loaded = false;
	private ParseStack parseStack;

	private SemanticRoutinesRepo repository;
	private Object scriptlet;

	private TableNode[] tabT;
	private File semFile;

	public SemanticRoutinesIvoker(File semFile) throws MalformedURLException
	{
		this.semFile = semFile;
		lastInstance = this;
	}

	public static SemanticRoutinesIvoker getLastInstance()
	{
		return lastInstance;
	}

	public static SemanticRoutinesIvoker getLastInstance(ParseStack parseStack, TableNode[] tabT, SemanticRoutinesRepo repository)
	{

		SemanticRoutinesIvoker instance = lastInstance;
		instance.parseStack = parseStack;
		instance.tabT = tabT;
		instance.repository = repository;
		if (!instance.loaded)
			instance.configureAndLoad();
		return instance;
	}

	public void configureAndLoad()
	{
		File modBeanInjection = new File(System.getProperty("java.io.tmpdir"), "beaninjection.xml");
		File beanInjection = new File(SPRING_XML_MODEL);
		try
		{
			if (!modBeanInjection.exists())
			{
				modBeanInjection.createNewFile();
			}
			FileInputStream fileInputStream = new FileInputStream(beanInjection);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
			String line = "";
			String outputString = "";
			while ((line = bufferedReader.readLine()) != null)
			{
				if (line.contains("$FILENAME"))
				{
					line = line.replace("$FILENAME", semFile.getAbsolutePath());
				}
				outputString += line + "\n";
			}
			FileOutputStream fileOutputStream = new FileOutputStream(modBeanInjection);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			outputStreamWriter.write(outputString);
			outputStreamWriter.close();
			fileOutputStream.close();
			bufferedReader.close();
			fileInputStream.close();
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, "Could not semantic routines file.", e);
		}
		ctx = new FileSystemXmlApplicationContext(modBeanInjection.getAbsolutePath());
		scriptlet = ctx.getBean("routines");
		loaded = true;
	}

	public Yytoken getCurrentToken()
	{
		return currentToken;
	}

	public String getExtenalSemanticRoutinesClass()
	{
		return DEFAULT_SR_CLASS;
	}

	public GroovyObject getGoo()
	{
		return goo;
	}

	public ParseStack getParseStack()
	{
		return parseStack;
	}

	public SemanticRoutinesRepo getRepository()
	{
		return repository;
	}

	public TableNode[] getTabT()
	{
		return tabT;
	}

	public void ivokeMethodFromClass(String function)
	{
		try
		{
			String _class = getExtenalSemanticRoutinesClass();
			if (_class == null)
			{
				_class = DEFAULT_SR_CLASS;
			}
			Class<?> c = Class.forName(_class);
			Object t = c.newInstance();

			if (t instanceof TokenListener)
			{
				((TokenListener) t).setCurrentToken(currentToken);
				repository.getListeners().add((TokenListener) t);
			}
			Method[] allMethods = c.getDeclaredMethods();
			for (Method m : allMethods)
			{
				String mname = m.getName();
				if (!mname.equals(function))
				{
					continue;
				}
				// you could verify this method's parameters here.
				// See:
				// http://java.sun.com/docs/books/tutorial/reflect/member/methodInvocation.html
				AppOutput.displayText(String.format("invoking %s()%n", mname), TOPIC.Output);
				try
				{
					m.setAccessible(true);
					Object o = m.invoke(t, parseStack, tabT);
					if (m.getGenericReturnType() == boolean.class)
					{
						AppOutput.displayText(String.format("%s() returned %b%n", mname, o), TOPIC.Output);
					}
					else if (m.getGenericReturnType() == int.class)
					{
						AppOutput.displayText(String.format("%s() returned %d%n", mname, o), TOPIC.Output);
					}
					else if (m.getGenericReturnType() == String.class)
					{
						AppOutput.displayText(String.format("%s() returned %s%n", mname, o), TOPIC.Output);
					}

					// Handle any exceptions thrown by method to be invoked.
				}
				catch (InvocationTargetException x)
				{
					Throwable cause = x.getCause();
					AppOutput.semanticRoutinesOutput(String.format("invocation of %s failed: %s%n", mname, cause.getMessage()));
				}
			}

			// production code should handle these exceptions more gracefully
		}
		catch (ClassNotFoundException x)
		{
			Log.log(Log.ERROR, this, String.format("Could not execute semantic routine: %s", function), x);
		}
		catch (InstantiationException x)
		{
			Log.log(Log.ERROR, this, String.format("Could not execute semantic routine: %s", function), x);
		}
		catch (IllegalAccessException x)
		{
			Log.log(Log.ERROR, this, String.format("Could not execute semantic routine: %s", function), x);
		}
	}

	public void ivokeMethodFromFile(String function)
	{
		goo.setProperty("tabT", tabT);
		goo.setProperty("parseStack", parseStack);
		goo.setProperty("currentToken", currentToken);
		goo.setProperty("output", SemanticRoutinesOutput.getInstance());

		try
		{
			Method method = scriptlet.getClass().getMethod(function);
			method.invoke(scriptlet, new java.lang.Object[]{});
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, "A parsing error has ocurred while trying to access " + function, e);
		}
	}

	@Override
	public void setCurrentToken(Yytoken currentToken)
	{
		this.currentToken = currentToken;
	}

	public void setGoo(GroovyObject goo)
	{
		this.goo = goo;
	}

	public void setParseStack(ParseStack parseStack)
	{
		this.parseStack = parseStack;
	}

	public void setRepository(SemanticRoutinesRepo repository)
	{
		this.repository = repository;
	}

	public void setTabT(TableNode[] tabT)
	{
		this.tabT = tabT;
	}

}
