package org.grview.syntax.analyzer.gsll1.error;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerStackRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerTableRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerToken;
import org.grview.syntax.model.GrViewNode;
import org.grview.syntax.model.NTerminalStack;
import org.grview.syntax.model.ParseNode;
import org.grview.syntax.model.TableGraphNode;
import org.grview.syntax.model.TableNode;

public class InsertStrategy implements IErroStrategy
{
	private AnalyzerTableRepository analyzerTable;
	private AnalyzerStackRepository analyzerStack;
	private AnalyzerAlternative analyzerAlternative;
	private AnalyzerToken analyzerToken;
	private SemanticRoutinesRepo semanticRoutinesRepo;

	public InsertStrategy()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
		this.semanticRoutinesRepo = SemanticRoutinesRepo.getInstance();
	}

	@Override
	public int tryFix(int UI, int TOP, int column, int line)
	{
		int IX = UI;
		int IY;
		int I = -1;
		NTerminalStack pilhaNaoTerminalY = new NTerminalStack();

		while (IX != 0 && I < 0)
		{
			TableGraphNode graphNode = analyzerTable.getGraphNode(IX);
			
			if (graphNode.IsTerminal())
			{				
				TableNode terminalNode = analyzerTable.getTermial(graphNode.getNodeReference());				
				IY = graphNode.getSucessorIndex();
				while (IY != 0 && I < 0)
				{
					if (analyzerTable.getGraphNode(IY).IsTerminal())
					{
						if (analyzerTable.getGraphNode(IY).isLambda())
						{
							IY = analyzerTable.getGraphNode(IY).getSucessorIndex();
						}
						else
						{
							String temp = analyzerTable.getTermial(analyzerTable.getGraphNode(IY).getNodeReference()).getName();
							if (temp.equals(analyzerToken.getCurrentSymbol()))
							{
								AppOutput.displayText("<font color='red'>Symbol \"" + terminalNode.getName() + "\" inserted before column " + column + "\n</font>", TOPIC.Output);
								analyzerStack.getParseStack().push(new ParseNode(terminalNode.getFlag(), terminalNode.getName()));

								semanticRoutinesRepo.setCurrentToken(analyzerToken.getLastToken());
								semanticRoutinesRepo.execFunction(graphNode.getSemanticRoutine());

								analyzerStack.getNTerminalStack().clear();
								
								I = IY;
							}
							else
								IY = analyzerAlternative.findAlternative(IY, pilhaNaoTerminalY, analyzerStack.getGrViewStack());
						}
					}
					else
					{
						analyzerStack.getGrViewStack().push(new GrViewNode(IY, TOP + 2));
						pilhaNaoTerminalY.push(IY);
						IY = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IY).getNodeReference()).getFirstNode();
					}
				}
				if (I < 0)
					IX = analyzerAlternative.findAlternative(IX, analyzerStack.getNTerminalStack(), analyzerStack.getGrViewStack());
			}
			else
			{
				analyzerStack.getGrViewStack().push(new GrViewNode(IX, TOP + 1));
				analyzerStack.getNTerminalStack().push(IX);
				IX = analyzerTable.getNTerminal(analyzerTable.getGraphNode(IX).getNodeReference()).getFirstNode();
			}
		}
		
		if (I < 0)
		{
			while (analyzerStack.getGrViewStack().size() > TOP)
			{
				analyzerStack.getGrViewStack().pop();
			}
		}
		return I;
	}
}
