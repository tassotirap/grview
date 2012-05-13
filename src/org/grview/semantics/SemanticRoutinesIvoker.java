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
import java.util.Stack;

import org.grview.lexical.Yytoken;
import org.grview.output.AppOutput;
import org.grview.output.Output;
import org.grview.output.SemanticRoutinesOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.parser.ParsingEditor;
import org.grview.project.Project;
import org.grview.syntax.model.TabNode;
import org.grview.util.Log;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class SemanticRoutinesIvoker implements Cloneable, TokenListener {

	private Stack parseStack;
	private TabNode[] tabT;
	private Yytoken currentToken;

	private SemanticRoutinesRepo repository;

	private Project project;

	private static SemanticRoutinesIvoker lastInstance;
	public static final String DEFAULT_SR_CLASS = "org.grview.semantics.SemanticRoutines";
	public static final String SPRING_XML_MODEL = "beaninjection.xml";

	private FileSystemXmlApplicationContext ctx;
	private Object scriptlet;
	private GroovyObject goo;
	
	private boolean loaded = false;

	public SemanticRoutinesIvoker(Project project) throws MalformedURLException {
		this.project = project;
		lastInstance = this;
	}

	public void configureAndLoad() {
		File semFile = project.getSemFile().get(project.getVersion());
		File modBeanInjection = new File(System.getProperty("java.io.tmpdir"),"beaninjection.xml");
		File beanInjection = new File(SPRING_XML_MODEL);
		try {
			if (!modBeanInjection.exists()) {
				modBeanInjection.createNewFile();
			}
			FileInputStream fis = new FileInputStream(beanInjection);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = "";
			String output = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("$FILENAME")) {
					line = line.replace("$FILENAME", semFile.getAbsolutePath());
				}
				output += line + "\n";
			}
			FileOutputStream fos = new FileOutputStream(modBeanInjection);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(output);
			osw.close();
			fos.close();
			br.close();
			fis.close();
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Could not semantic routines file.", e);
		}
		ctx = new FileSystemXmlApplicationContext(modBeanInjection.getAbsolutePath());
		scriptlet = (Object)ctx.getBean("routines");
		loaded = true;
	}
	public static SemanticRoutinesIvoker getLastInstance() {
		return lastInstance;
	}

	public static SemanticRoutinesIvoker getLastInstance(Stack parseStack, TabNode[] tabT, SemanticRoutinesRepo repository) {

		SemanticRoutinesIvoker instance = lastInstance;
		instance.parseStack = parseStack;
		instance.tabT = tabT;
		instance.repository = repository;
		if (!instance.loaded)
			instance.configureAndLoad();
		return instance;
	}

	public SemanticRoutinesRepo getRepository() {
		return repository;
	}

	public void setRepository(SemanticRoutinesRepo repository) {
		this.repository = repository;
	}

	@Override
	public void setCurrentToken(Yytoken currentToken) {
		this.currentToken = currentToken;
	}

	public Stack getParseStack() {
		return parseStack;
	}

	public void setParseStack(Stack parseStack) {
		this.parseStack = parseStack;
	}

	public TabNode[] getTabT() {
		return tabT;
	}

	public void setTabT(TabNode[] tabT) {
		this.tabT = tabT;
	}

	public Yytoken getCurrentToken() {
		return currentToken;
	}

	public String getExtenalSemanticRoutinesClass() {
		return project.getProperty("semanticRoutineClass");
	}

	public void ivokeFromFile(String function) {
		goo.setProperty("tabT", tabT);
		goo.setProperty("parseStack", parseStack);
		goo.setProperty("currentToken", currentToken);
		goo.setProperty("output", SemanticRoutinesOutput.getInstance());
		
		try {
			Method method = scriptlet.getClass().getMethod(function);
			method.invoke(scriptlet, new java.lang.Object[] {});
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "A parsing error has ocurred while trying to access " + function, e);
		}
	}

	public void ivokeFromClass(String function) {
		try {
			String _class;
			_class = getExtenalSemanticRoutinesClass();
			if (_class == null) {
				_class = DEFAULT_SR_CLASS;
			}
			Class<?> c = Class.forName(_class);
			Object t = c.newInstance();

			if (t instanceof TokenListener) {
				((TokenListener) t).setCurrentToken(currentToken);
				repository.getListeners().add((TokenListener) t);
			}
			Method[] allMethods = c.getDeclaredMethods();
			for (Method m : allMethods) {
				String mname = m.getName();
				if (!mname.equals(function)) {
					continue;
				}
				// you could verify this method's parameters here.
				//See: http://java.sun.com/docs/books/tutorial/reflect/member/methodInvocation.html
				AppOutput.displayText(String.format("invoking %s()%n", mname), TOPIC.Output);
				try {
					m.setAccessible(true);
					Object o = m.invoke(t, parseStack, tabT);
					if (m.getGenericReturnType() == boolean.class) {
						AppOutput.displayText(String.format("%s() returned %b%n", mname, o), TOPIC.Output);
					}
					else if (m.getGenericReturnType() == int.class) {
						AppOutput.displayText(String.format("%s() returned %d%n", mname, o), TOPIC.Output);
					}
					else if (m.getGenericReturnType() == String.class) {
						AppOutput.displayText(String.format("%s() returned %s%n", mname, o), TOPIC.Output);
					}

					// Handle any exceptions thrown by method to be invoked.
				} catch (InvocationTargetException x) {
					Throwable cause = x.getCause();
					AppOutput.semanticRoutinesOutput(String.format("invocation of %s failed: %s%n",
							mname, cause.getMessage()));
				}
			}

			// production code should handle these exceptions more gracefully
		} catch (ClassNotFoundException x) {
			Log.log(Log.ERROR, this, String.format("Could not execute semantic routine: %s", function), x);
		} catch (InstantiationException x) {
			Log.log(Log.ERROR, this, String.format("Could not execute semantic routine: %s", function), x);
		} catch (IllegalAccessException x) {
			Log.log(Log.ERROR, this, String.format("Could not execute semantic routine: %s", function), x);
		}
	}

	public void setGoo(GroovyObject goo) {
		this.goo = goo;
	}

	public GroovyObject getGoo() {
		return goo;
	}

}
