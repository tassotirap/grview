package org.grview.syntax.analyzer.gsll1;

import org.grview.syntax.model.GrViewStack;
import org.grview.syntax.model.NTerminalStack;
import org.grview.syntax.model.ParseStack;

public class AnalyzerStackRepository
{
	private ParseStack parseStack;
	private GrViewStack grViewStack;
	private NTerminalStack nTerminalStack;
	
	private static AnalyzerStackRepository instance;
	
	public static AnalyzerStackRepository getInstance()
	{
		return instance;
	}
	
	public static AnalyzerStackRepository setInstance()
	{
		instance = new AnalyzerStackRepository();
		return instance;
	}
	
	private AnalyzerStackRepository()
	{
		
	}
	
	public GrViewStack getGrViewStack()
	{
		return grViewStack;
	}

	public NTerminalStack getNTerminalStack()
	{
		return nTerminalStack;
	}

	public ParseStack getParseStack()
	{
		return parseStack;
	}

	public void init()
	{
		grViewStack = new GrViewStack();
		nTerminalStack = new NTerminalStack();
		parseStack = new ParseStack();
	}

	public void setGrViewStack(GrViewStack grViewStack)
	{
		this.grViewStack = grViewStack;
	}
}
