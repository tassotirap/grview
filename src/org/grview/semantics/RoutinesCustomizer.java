package org.grview.semantics;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovyObject;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scripting.groovy.GroovyObjectCustomizer;

public class RoutinesCustomizer implements GroovyObjectCustomizer, ApplicationContextAware{

	ApplicationContext applicationContext = null;
	
	@Override
	public void customize(GroovyObject goo) {
		goo.setProperty("springAppContext", applicationContext);
		SemanticRoutinesIvoker.getLastInstance().setGoo(goo);
		DelegatingMetaClass metaClass = new DelegatingMetaClass(goo.getMetaClass()) {

			public Object invokeMethod(Object object, String methodName, Object[] arguments) {
				AppOutput.displayText("Invoking '" + methodName + "'.", TOPIC.Output);
				return super.invokeMethod(object, methodName, arguments);
			}
		};
		metaClass.initialize();
		goo.setMetaClass(metaClass);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		
	}
}
