package org.grview.output;

public class SyntaxErrorOutput extends Output {
	
	private static SyntaxErrorOutput instance;
	
	private SyntaxErrorOutput() {
		super();
	}
	
	public static SyntaxErrorOutput getInstance() {
		if (instance == null) {
			instance = new SyntaxErrorOutput();
		}
		return instance;
	}

}
