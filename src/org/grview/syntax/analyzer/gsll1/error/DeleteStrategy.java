package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.TableGraphNode;
import org.grview.syntax.model.TableNode;

public class DeleteStrategy extends IErroStrategy
{
	@Override
	public int tryFix(int UI, int column, int line)
	{
		int I = -1;
		int IX = UI;

		init();

		analyzerToken.readNext();

		while (IX != 0)
		{
			TableGraphNode graphNode = analyzerTable.getGraphNode(IX);

			if (analyzerTable.getGraphNode(IX).IsTerminal())
			{
				TableNode terminalNode = analyzerTable.getTermial(graphNode.getNodeReference());

				if (terminalNode.getName().equals(analyzerToken.getCurrentSymbol()))
				{
					AppOutput.displayText("<font color='red'>Symbol \"" + analyzerToken.getLastToken().text + "\" was ignored.\n</font>", TOPIC.Output);
					I = IX;
					break;
				}
				else
				{
					int alternative = 0;
					alternative = analyzerAlternative.findAlternative(IX, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
					IX = alternative;
				}
			}
			else
			{

				TableNode nTerminalNode = analyzerTable.getNTerminal(graphNode.getNodeReference());
				analyzerStack.setTop(analyzerStack.getTop() + 1);
				analyzerStack.getGrViewStack().push(new GrViewNode(IX, analyzerStack.getTop()));
				analyzerStack.getNTerminalStack().push(IX);
				IX = nTerminalNode.getFirstNode();
			}
		}

		if (I < 0)
		{
			analyzerToken.setCurrentToken(analyzerToken.getLastToken());
			analyzerToken.getYylex().pushback(analyzerToken.getYylex().yylength());

			restore(true);
		}
		return I;
	}

}
