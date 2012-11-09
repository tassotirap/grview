package org.grview.syntax.grammar;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.grview.canvas.Canvas;
import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.project.ProjectManager;
import org.grview.syntax.command.AsinEditor;
import org.grview.syntax.grammar.model.AbstractNode;
import org.grview.syntax.grammar.model.LambdaAlternative;
import org.grview.syntax.grammar.model.NodeLabel;
import org.grview.syntax.grammar.model.SimpleNode;
import org.grview.syntax.grammar.model.SyntaxDefinitions;
import org.grview.syntax.grammar.model.SyntaxElement;
import org.grview.syntax.grammar.model.SyntaxModel;
import org.grview.syntax.grammar.model.SyntaxSubpart;

/** Compiles a grammar textual representation from the designed graph **/
public class GrammarFactory
{

	PrintWriter printWriter;
	private Grammar grammar;

	private int cont;

	private String htmlOutput;

	/**
	 * Constructor
	 * 
	 * @param part
	 */
	public GrammarFactory()
	{

	}

	private boolean canPerformAction()
	{
		return true;
	}

	private List<AbstractNode> clearAndSetStartNodes(List<SyntaxElement> children)
	{
		List<AbstractNode> startNodes = new ArrayList<AbstractNode>();

		for (SyntaxElement object : children)
		{
			if (object instanceof SyntaxSubpart)
			{
				((SyntaxSubpart) object).setFlag(false);
			}
			if (object instanceof AbstractNode && ((AbstractNode) object).getType().equals(AbstractNode.LEFTSIDE))
			{
				startNodes.add((AbstractNode) object);
			}
			if (object instanceof AbstractNode && ((AbstractNode) object).getType().equals(AbstractNode.START))
			{
				startNodes.add(0, (AbstractNode) object);
			}
		}
		return startNodes;
	}

	private GrammarData getNameAndSematicRouting(SyntaxModel syntaxModel)
	{
		String name;
		String semanticRoutine;
		String[] stringValue = new String[2];
		GrammarData returnValue = new GrammarData();
		SimpleNode simpleNode;
		List<NodeLabel> listChildren = syntaxModel.getChildrenAsLabels();
		NodeLabel label = listChildren.iterator().next();
		stringValue = label.getLabelContents().split("#");
		if (syntaxModel instanceof AbstractNode && ((AbstractNode) syntaxModel).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE))
		{
			name = SyntaxDefinitions.EmptyNodeLabel;
		}
		else
		{
			name = stringValue[0];
		}
		if (stringValue.length >= 2)
		{
			semanticRoutine = stringValue[1];
		}
		else if ((simpleNode = syntaxModel.getSemanticNode()) != null)
		{
			semanticRoutine = simpleNode.getLabel().getLabelContents();
		}
		else
		{
			semanticRoutine = "-1";
		}
		returnValue.name = new String(name);
		returnValue.semanticRoutine = new String(semanticRoutine);
		returnValue.id = syntaxModel.getID();
		return returnValue;
	}

	private void writeBegining(NodeLabel label)
	{
		AppOutput.displayText("<a>>>Begining a new leftside..." + label.getLabelContents() + "</a><br>", TOPIC.Output);
	}

	protected boolean calculateEnabled()
	{
		return canPerformAction();
	}

	public Grammar getGrammar()
	{
		return grammar;
	}

	public String run(boolean isFile) throws Exception
	{

		AppOutput.clearGeneratedGrammar();
		AppOutput.displayHorizontalLine(TOPIC.Output);
		AppOutput.displayText("<a>Run grammar generate...</a><br>", TOPIC.Output);

		StringBuffer returnString = new StringBuffer();

		Canvas canvas = ProjectManager.getInstance().getMainWindow().getActiveScene();
		
		List<SyntaxElement> children = AsinEditor.getInstance().getLogicDiagram(canvas).getChildrenNodes();
		List<AbstractNode> startNodes = clearAndSetStartNodes(children);

		for (int i = 0; i < startNodes.size(); i++)
		{
			cont = 0;
			
			SyntaxModel startSyntaxModel = ((SyntaxModel) startNodes.get(i));
			if (startSyntaxModel == null)
			{
				throw new Exception("Could not find the grammar start node.");
			}
			
			SyntaxModel successorSyntaxSubpart = (SyntaxModel)startSyntaxModel.getSucessor();
			if (successorSyntaxSubpart == null)
			{
				throw new Exception("Could not find the successor node of the grammar head.");
			}
			successorSyntaxSubpart.setNumber(++cont);
			
			AbstractNode startNode = startNodes.get(i);
			AbstractNode successorNode = (AbstractNode)successorSyntaxSubpart;

			List listChildren = ((SyntaxModel) startNodes.get(i)).getChildrenAsLabels();
			NodeLabel label = (NodeLabel) listChildren.iterator().next();

			writeBegining(label);

			htmlOutput = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"1px\" width=\"100%\">";
			htmlOutput += "<tr style=\"background-color: #EEEEEE; font-weight: bold;\"><td></td>";
			htmlOutput += "<td>Node</td><td>Number</td><td>Alternative</td><td>Successor</td>";
			htmlOutput += "<td>Semantic Rout.</td></tr>";
			if (startNode.getType().equals(AbstractNode.START))
			{
				htmlOutput += "<tr><td style=\"background-color: #EEEEEE;\">";
				htmlOutput += "<img src=\"images/icon_s2.png\" alt=\"Initial Node\"></td>";
				htmlOutput += "<td style=\"font-weight: bold;\" >";
				htmlOutput += "<a href=\"" + ((SyntaxModel) startNodes.get(i)).getID() + "\">" + label.getLabelContents() + "</a></td>";
				htmlOutput += "<td align=\"center\">-1</td><td align=\"center\">-</td><td align=\"center\">" + successorSyntaxSubpart.getNumber() + "</td>";
				htmlOutput += "<td align=\"center\">-</td></tr>";
				GrammarComponent grammarHead = new GrammarComponent(label.getLabelContents(), startSyntaxModel.getID());
				grammarHead.setHead(true);
				if (i == 0)
					grammar = new Grammar(grammarHead);
				grammar.setHead(grammarHead);
				grammar.setCurrent(grammarHead);
			}
			else
			{
				htmlOutput += "<tr><td style=\"background-color: #EEEEEE;\">";
				htmlOutput += "<img src=\"images/icon_H2.png\" alt=\"Left Side\"></td>";
				htmlOutput += "<td style=\"font-weight: bold;\" >";
				htmlOutput += "<a href=\"" + ((SyntaxModel) startNodes.get(i)).getID() + "\">" + label.getLabelContents() + "</a>";
				htmlOutput += "</td><td align=\"center\">-1</td><td align=\"center\">-</td><td align=\"center\">" + successorSyntaxSubpart.getNumber() + "</td>";
				htmlOutput += "<td align=\"center\">-</td></tr>";
				GrammarComponent grammarLeftside = new GrammarComponent(label.getLabelContents(), startSyntaxModel.getID());
				grammarLeftside.setLeftHand(true);
				if (i == 0)
					grammar = new Grammar(grammarLeftside);
				grammar.addLeftHand(grammarLeftside);
				grammar.setCurrent(grammarLeftside);
			}
			String stringOut = startSyntaxModel.getID() + " H " + label.getLabelContents() + " -1 -1 " + successorSyntaxSubpart.getNumber() + " -1\n";
			if (isFile && printWriter != null)
			{
				printWriter.write(stringOut);
				printWriter.flush();
			}
			returnString.append(stringOut);
			GrammarData successorGrammarData = getNameAndSematicRouting(successorSyntaxSubpart);
			GrammarComponent successorGammarComponent = new GrammarComponent(successorGrammarData.name, successorGrammarData.id);
			if (successorNode.getType().equals(AbstractNode.NTERMINAL))
			{
				successorGammarComponent.setNonterminal(true);
			}
			else if (successorNode.getType().equals(AbstractNode.TERMINAL))
			{
				successorGammarComponent.setTerminal(true);
			}
			else if (successorNode.getType().equals(AbstractNode.LAMBDA_ALTERNATIVE))
			{
				successorGammarComponent.setLambda(true);
			}
			grammar.addSuccessor(successorGammarComponent);
			returnString.append(subpartString(isFile, successorSyntaxSubpart));
			htmlOutput += "</table>";
			AppOutput.displayGeneratedGrammar(htmlOutput);
		}
		grammar.finalize();
		return returnString.toString();
	}


	public String subpartString(boolean isFile, SyntaxSubpart syntaxSubpart)
	{
		StringBuffer returnString = new StringBuffer();
		GrammarComponent grammarComponent = null;
		
		AbstractNode subpartNode = (AbstractNode)syntaxSubpart;

		if (syntaxSubpart instanceof SyntaxModel || syntaxSubpart instanceof LambdaAlternative)
		{
			syntaxSubpart.setFlag(true);
			SyntaxModel successor = (SyntaxModel) syntaxSubpart.getSucessor();
			SyntaxModel alternative = (SyntaxModel) syntaxSubpart.getAlternative();
			
			AbstractNode successorNode = (AbstractNode)successor;
			AbstractNode alternativeNode = (AbstractNode)alternative;
			
			if (successor != null && successor.getFlag() == false)
			{
				successor.setNumber(++cont);
			}

			if (alternative != null && alternative.getFlag() == false)
			{
				alternative.setNumber(++cont);
			}

			String stringOut = "";
			htmlOutput = htmlOutput + "<tr>";

			GrammarData grammarData = getNameAndSematicRouting((SyntaxModel) syntaxSubpart);

			if (subpartNode.getType().equals(AbstractNode.NTERMINAL))
			{
				stringOut = grammarData.id + " N";
				htmlOutput = htmlOutput + "<td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_nt2.png\" alt=\"Non-Terminal\"></td>";
				grammarComponent = new GrammarComponent(grammarData.name, grammarData.id);
				grammarComponent.setNonterminal(true);
			}
			else if (subpartNode.getType().equals(AbstractNode.TERMINAL))
			{
				stringOut = grammarData.id + " T";
				htmlOutput = htmlOutput + "<td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_t2.png\" alt=\"Terminal\"></td>";
				grammarComponent = new GrammarComponent(grammarData.name, grammarData.id);
				grammarComponent.setTerminal(true);
			}
			else if (subpartNode.getType().equals(AbstractNode.LAMBDA_ALTERNATIVE))
			{
				stringOut = grammarData.id + " T";
				htmlOutput = htmlOutput + "<td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_l.png\" alt=\"Lambda Alternative\"></td>";
				grammarComponent = new GrammarComponent(null, grammarData.id);
				grammarComponent.setLambda(true);
			}

			stringOut = stringOut + " " + grammarData.name;
			htmlOutput = htmlOutput + "<td style=\"font-weight: bold;\"><a href=\"" + grammarData.id + "\">" + grammarData.name + "</a></td>";

			stringOut = stringOut + " " + syntaxSubpart.getNumber();
			htmlOutput = htmlOutput + "<td align=\"center\">" + syntaxSubpart.getNumber() + "</td>";

			if (alternative == null)
			{
				stringOut = stringOut + " 0";
				htmlOutput = htmlOutput + "<td align=\"center\">-</td>";
			}
			else
			{
				stringOut = stringOut + " " + alternative.getNumber();
				htmlOutput = htmlOutput + "<td align=\"center\">" + alternative.getNumber() + "</td>";
			}

			if (successor == null)
			{
				stringOut = stringOut + " 0";
				htmlOutput = htmlOutput + "<td align=\"center\">-</td>";
			}
			else
			{
				stringOut = stringOut + " " + successor.getNumber();
				htmlOutput = htmlOutput + "<td align=\"center\">" + successor.getNumber() + "</td>";
			}

			stringOut = stringOut + " " + grammarData.semanticRoutine + "\n";
			htmlOutput = htmlOutput + "<td align=\"center\">" + ((grammarData.semanticRoutine.equals("-1")) ? "-" : "<a href=\"name_smRoutine[1]\">" + grammarData.semanticRoutine + "</a>") + "</td>";

			if (printWriter != null && isFile)
			{
				printWriter.write(stringOut);
				printWriter.flush();
			}
			returnString.append(stringOut);

			htmlOutput += "</tr>";

			grammar.setCurrent(grammarComponent);
			if (successor != null && successor.getFlag() == false)
			{
				GrammarData nextGrammarData = getNameAndSematicRouting((SyntaxModel) successor);
				GrammarComponent nextGrammarComponent = new GrammarComponent(nextGrammarData.name, nextGrammarData.id);
				if (successorNode.getType().equals(AbstractNode.NTERMINAL))
				{
					nextGrammarComponent.setNonterminal(true);
				}
				else if (successorNode.getType().equals(AbstractNode.TERMINAL))
				{
					nextGrammarComponent.setTerminal(true);
				}
				else if (successorNode.getType().equals(AbstractNode.LAMBDA_ALTERNATIVE))
				{
					nextGrammarComponent.setLambda(true);
				}
				grammar.addSuccessor(nextGrammarComponent);
				returnString.append(subpartString(isFile, successor));
			}

			if (alternative != null && alternative.getFlag() == false)
			{
				GrammarData nextGrammarData = getNameAndSematicRouting(alternative);
				GrammarComponent nextGuy = new GrammarComponent(nextGrammarData.name, nextGrammarData.id);
				if (alternativeNode.getType().equals(AbstractNode.NTERMINAL))
				{
					nextGuy.setNonterminal(true);
				}
				else if (alternativeNode.getType().equals(AbstractNode.TERMINAL))
				{
					nextGuy.setTerminal(true);
				}
				else if (alternativeNode.getType().equals(AbstractNode.LAMBDA_ALTERNATIVE))
				{
					nextGuy.setLambda(true);
				}
				grammar.addAlternative(nextGuy);
				returnString.append(subpartString(isFile, alternative));
			}
		}
		return returnString.toString();
	}

}