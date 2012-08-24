package org.grview.syntax.grammar.model;

/** Refers to any kind of node that a grammar may contain **/
public interface AbstractNode
{
	public final static String LAMBDA_ALTERNATIVE = SyntaxDefinitions.LambdaAlternative;
	public final static String LEFTSIDE = SyntaxDefinitions.LeftSide;
	public final static String NTERMINAL = SyntaxDefinitions.NTerminal;
	public static final String SEMANTIC_ROUTINE = SyntaxDefinitions.SemanticLabel;
	public static final String START = SyntaxDefinitions.Start;
	public final static String TERMINAL = SyntaxDefinitions.Terminal;

	public abstract String getType();
}
