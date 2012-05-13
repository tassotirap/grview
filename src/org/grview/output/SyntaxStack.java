package org.grview.output;

public class SyntaxStack extends Output {

	private static SyntaxStack instance;
	private int lastLine;
	
	private SyntaxStack() {
		super();
	}
	
	public static SyntaxStack getInstance() {
		if (instance == null) {
			instance = new SyntaxStack();
		}
		return instance;
	}

	public void displayTextExt(String str, boolean showLine) {
		if (showLine) {
			displayTextExt(String.format("<b>%d.&nbsp;&nbsp;</b>%s", ++lastLine, str), TOPIC.SyntaxStack);
		}
		else {
			displayTextExt(str, TOPIC.SyntaxStack);
		}
	}
	
	public void clearStack() {
		lastLine = 0;
		clear();
	}
}
