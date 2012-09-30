package org.grview.syntax.analyzer.gsll1;

import java.util.Iterator;

import org.grview.output.AppOutput;
import org.grview.syntax.model.ParseNode;
import org.grview.syntax.model.ParseStack;
import org.grview.util.Log;

public class AnalyzerPrint
{
	private boolean firstTime;
	private boolean stepping;
	private Thread thread;
	
	private static AnalyzerPrint instance;
	
	public static AnalyzerPrint getInstance()
	{
		return instance;
	}
	
	public static AnalyzerPrint setInstance(Thread thread)
	{
		instance = new AnalyzerPrint(thread);
		return instance;
	}

	private AnalyzerPrint(Thread thread)
	{
		this.thread = thread;
		this.firstTime = true;
		this.stepping = false;
	}

	private void synchronize()
	{
		if (isStepping() && !firstTime)
		{
			synchronized (thread)
			{
				try
				{
					thread.wait();
				}
				catch (InterruptedException e)
				{
					Log.log(Log.ERROR, this, "An internal error has occurred!", e);
				}
			}
		}
	}

	public boolean isStepping()
	{
		return stepping;
	}

	public void printStack(ParseStack parseStackNode)
	{
		synchronize();

		firstTime = false;
		
		Iterator<ParseNode> iterator = parseStackNode.iterator();
		ParseNode parseStackNodeTemp = null;
		String lineSyntax = "";
		String lineSemantic = "";
		while (iterator.hasNext())
		{
			parseStackNodeTemp = iterator.next();
			lineSyntax += "<a style=\"color: #000000; font-weight: bold;\" href=\"" + parseStackNodeTemp.getFlag() + "\">" + parseStackNodeTemp.getType() + "</a>&nbsp;";
			lineSemantic += parseStackNodeTemp.getSemanticSymbol() + "&nbsp;";
		}
		
		AppOutput.showAndSelectNode((parseStackNode.peek()).getFlag());
		AppOutput.printlnSyntaxStack(lineSyntax, true);
		AppOutput.printlnSemanticStack(lineSemantic, true);
	}

	public void setStepping(boolean stepping)
	{
		this.stepping = stepping;
	}
}
