package org.grview.output;

import org.grview.output.HtmlViewer.TOPIC;
import org.netbeans.api.visual.widget.Widget;

public abstract class AppOutput
{
	// static public Text outview;

	public static void clearGeneratedGrammar()
	{
		GeneratedGrammar.getInstance().clear();
	}

	public static void clearOutputBuffer()
	{
		Output.getInstance().clearOutputBuffer();
	}

	public static void clearStacks()
	{
		SemanticStack.getInstance().clearStack();
		SyntaxStack.getInstance().clearStack();
	}

	static public void displayGeneratedGrammar(String str)
	{
		GeneratedGrammar.getInstance().displayTextExt(str, Output.TOPIC.Grammar);
	}

	static public void displayHorizontalLine(Output.TOPIC topic)
	{
		Output.getInstance().displayHorizontalLineExt(topic);
	}

	static public void displayText(String str, Output.TOPIC topic)
	{
		Output.getInstance().displayTextExt(str, topic);
	}

	static public void errorRecoveryStatus(String str)
	{
		SyntaxErrorOutput.getInstance().displayTextExt(str, TOPIC.Error);
	}

	public static String getReport()
	{
		return Output.getInstance().getReport();
	}

	static public void printlnSemanticStack(String str)
	{
		printSemanticStack(str + "<br>", false);
	}

	static public void printlnSemanticStack(String str, boolean showLine)
	{
		SemanticStack.getInstance().displayTextExt(str + "<br>", showLine);
	}

	static public void printlnSyntaxStack(String str)
	{
		printSyntaxStack(str + "<br>", false);
	}

	static public void printlnSyntaxStack(String str, boolean showLine)
	{
		printSyntaxStack(str + "<br>", showLine);
	}

	static public void printlnToken(String str)
	{
		TokenOutput.getInstance().displayTextExt(str + "<br>", TOPIC.Tokens);
	}

	static public void printSemanticStack(String str)
	{
		printSemanticStack(str, false);
	}

	static public void printSemanticStack(String str, boolean showLine)
	{
		SemanticStack.getInstance().displayTextExt(str, showLine);
	}

	static public void printSyntaxStack(String str)
	{
		printSyntaxStack(str, false);
	}

	static public void printSyntaxStack(String str, boolean showLine)
	{
		SyntaxStack.getInstance().displayTextExt(str, showLine);
	}

	static public void printToken(String str)
	{
		TokenOutput.getInstance().displayTextExt(str, TOPIC.Tokens);
	}

	static public void semanticRoutinesOutput(String str)
	{
		SemanticRoutinesOutput.getInstance().println(str);
	}

	public static void showAndSelectNode(String flag)
	{
		// TODO selecting, but not really showing
		Output.getInstance().getActiveScene().select(flag);
	}

	public abstract void createPartControl(Widget parent);

	public void setFocus()
	{
		// set focus to my widget. For a label, this doesn't
		// make much sense, but for more complex sets of widgets
		// you would decide which one gets the focus.
	}

}
