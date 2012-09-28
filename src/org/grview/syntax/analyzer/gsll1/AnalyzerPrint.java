package org.grview.syntax.analyzer.gsll1;

import java.util.Iterator;
import java.util.Stack;

import org.grview.output.AppOutput;
import org.grview.syntax.model.ParseStackNode;
import org.grview.util.Log;

public class AnalyzerPrint
{
	private boolean firstTime = true;
	private boolean stepping = false;
	
	Analyzer analyzer;
	
	public AnalyzerPrint(Analyzer analyzer)
	{
		this.analyzer = analyzer;
	}
	
	public void printStack(Stack s)
	{
		if (isStepping() && !firstTime)
		{
			synchronized (analyzer)
			{
				try
				{
					analyzer.wait();
				}
				catch (InterruptedException e)
				{
					Log.log(Log.ERROR, this, "An internal error has occurred!", e);
				}
			}
		}
		firstTime = false;
		Iterator i = s.iterator();
		ParseStackNode tmp;
		String lineSyn = "";
		String lineSem = "";
		while (i.hasNext())
		{
			tmp = (ParseStackNode) i.next();
			lineSyn += "<a style=\"color: #000000; text-decoration: none; font-weight: bold;\" href=\"" + tmp.getFlag() + "\">" + tmp.getType() + "</a>&nbsp;";
			lineSem += tmp.getSemanticSymbol() + "&nbsp;";
		}
		AppOutput.showAndSelectNode(((ParseStackNode) s.peek()).getFlag());
		AppOutput.printlnSyntaxStack(lineSyn, true);
		AppOutput.printlnSemanticStack(lineSem, true);
	}

	public boolean isStepping()
	{
		return stepping;
	}

	public void setStepping(boolean stepping)
	{
		this.stepping = stepping;
	}
}
