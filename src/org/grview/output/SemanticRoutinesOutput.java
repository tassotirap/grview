package org.grview.output;

import org.grview.parser.ParsingEditor;

public class SemanticRoutinesOutput extends Output {

	private static SemanticRoutinesOutput instance;
	
	private SemanticRoutinesOutput() {
		
	}
	
	public static SemanticRoutinesOutput getInstance() {
		if (instance == null) {
			instance = new SemanticRoutinesOutput();
		}
		return instance;
	}
	
	public void println(String str) {
		ParsingEditor pe = ParsingEditor.getInstance();
		if (pe != null)
			pe.displayOutputText(str);
	}
}
