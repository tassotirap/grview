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

	PrintWriter out;
	String path = new String("");
	private Grammar absGrammar;

	private int cont;

	private String htmlOutput; // html formated output

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

	/*
	 * This function extracts the name and the routine semantic number from one
	 * string
	 */
	private String[] nameAndRsExtractor(SyntaxModel syntaxModel)
	{
		String name;
		String semanticRoutine;
		String[] stringValue = new String[2];
		String[] returnValue = new String[3];
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
		returnValue[0] = new String(name);
		returnValue[1] = new String(semanticRoutine);
		returnValue[2] = syntaxModel.getID();
		return returnValue;
	}

	protected boolean calculateEnabled()
	{
		return canPerformAction();
	}

	public String dfs(boolean isFile, SyntaxSubpart o)
	{
		StringBuffer grammar = new StringBuffer();
		String name_smRoutine[];
		name_smRoutine = new String[2];
		name_smRoutine[0] = new String("-1");
		name_smRoutine[1] = new String("-1");
		if (o instanceof SyntaxModel || o instanceof LambdaAlternative)
		{
			/* Marcando o nó, para que ele não seja percorrido mais de uma vez. */
			o.setFlag(true);
			/* O Sucessor do nó atual (o) é preenchido */
			SyntaxSubpart suc = o.getSucessor();
			/* A alternativa do nó atual é preenchida */
			SyntaxSubpart alt = o.getAlternative();
			GrComp thisGuy = new GrComp();

			/*
			 * se o sucessor existir e ainda não foi percorrido, ganha um número
			 * de indentificação
			 */
			if (suc != null && suc.getFlag() == false)
			{
				suc.setNumber(++cont);
			}
			/*
			 * se o nó alternativo existir e ainda não foi percorrido, ganha um
			 * número de indentificação
			 */
			if (alt != null && alt.getFlag() == false)
			{
				alt.setNumber(++cont);
			}
			/*
			 * inicializando a string que será utilizada para guardar os dados
			 * do nó atual
			 */
			String sout = "";
			htmlOutput = htmlOutput + "<tr>";

			/*
			 * Nesse ponto temos que entrar no conteúdo do nó terminal ou não
			 * terminal, para saber qual é a sua entrada. Com esse dado,
			 * preenchere-mos o campo nomer
			 */
			name_smRoutine = nameAndRsExtractor((SyntaxModel) o);

			/* O nó é um não terminal? */
			if (o instanceof AbstractNode && ((AbstractNode) o).getType().equals(AbstractNode.NTERMINAL))
			{
				sout = sout + name_smRoutine[2] + " N";
				htmlOutput = htmlOutput + "<td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_nt2.png\" alt=\"Non-Terminal\"></td>";
				thisGuy = new GrComp(name_smRoutine[0], name_smRoutine[2]);
				thisGuy.setNonterminal(true);
			}
			else if (o instanceof AbstractNode && ((AbstractNode) o).getType().equals(AbstractNode.TERMINAL))
			{
				sout = sout + name_smRoutine[2] + " T";
				htmlOutput = htmlOutput + "<td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_t2.png\" alt=\"Terminal\"></td>";
				thisGuy = new GrComp(name_smRoutine[0], name_smRoutine[2]);
				thisGuy.setTerminal(true);
			}
			else if (o instanceof AbstractNode && ((AbstractNode) o).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE))
			{
				sout = sout + name_smRoutine[2] + " T";
				htmlOutput = htmlOutput + "<td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_l.png\" alt=\"Lambda Alternative\"></td>";
				thisGuy = new GrComp(null, name_smRoutine[2]);
				thisGuy.setLambda(true);
			}

			sout = sout + " " + name_smRoutine[0];
			htmlOutput = htmlOutput + "<td style=\"font-weight: bold;\"><a href=\"" + name_smRoutine[2] + "\">" + name_smRoutine[0] + "</a></td>";

			/*
			 * Agora está na hora de preencher o campo numno, que indica qual o
			 * número desse nó
			 */
			sout = sout + " " + o.getNumber();
			htmlOutput = htmlOutput + "<td align=\"center\">" + o.getNumber() + "</td>";

			/*
			 * Colocando um valor no campo Altr, que indica o nó alternativo ao
			 * nó "o".
			 */
			if (alt == null)
			{
				sout = sout + " 0";
				htmlOutput = htmlOutput + "<td align=\"center\">-</td>";
			}
			else
			{
				sout = sout + " " + alt.getNumber();
				htmlOutput = htmlOutput + "<td align=\"center\">" + alt.getNumber() + "</td>";
			}
			/*
			 * Colocando um valor no campo Sucr, que indica o nó sucessor ao nó
			 * "o"
			 */
			if (suc == null)
			{
				sout = sout + " 0";
				htmlOutput = htmlOutput + "<td align=\"center\">-</td>";
			}
			else
			{
				sout = sout + " " + suc.getNumber();
				htmlOutput = htmlOutput + "<td align=\"center\">" + suc.getNumber() + "</td>";
			}

			/* Este número é referente à rotina semântica. */
			sout = sout + " " + name_smRoutine[1] + "\n";
			htmlOutput = htmlOutput + "<td align=\"center\">" + ((name_smRoutine[1].equals("-1")) ? "-" : "<a href=\"name_smRoutine[1]\">" + name_smRoutine[1] + "</a>") + "</td>";

			/* imprimindo os dados do nó atual (o) no arquivo... */
			if (out != null && isFile)
			{
				out.write(sout);
				out.flush();
			}
			grammar.append(sout);
			/* imprimindo os dados do nó atual (o) na tela... */
			htmlOutput += "</tr>";

			/* chamando a recursão para o nó sucessor (busca em profundidade) */
			absGrammar.setCurrent(thisGuy);
			if (suc != null && suc.getFlag() == false)
			{
				String[] name = nameAndRsExtractor((SyntaxModel) suc);
				GrComp nextGuy = new GrComp(name[0], name[2]);
				if (((AbstractNode) suc).getType().equals(AbstractNode.NTERMINAL))
				{
					nextGuy.setNonterminal(true);
				}
				else if (((AbstractNode) suc).getType().equals(AbstractNode.TERMINAL))
				{
					nextGuy.setTerminal(true);
				}
				else if (((AbstractNode) suc).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE))
				{
					nextGuy.setLambda(true);
				}
				absGrammar.addSuccessor(nextGuy);
				grammar.append(dfs(isFile, suc));
			}
			/* chamando a recursão para o nó alternativo (busca em profundidade) */
			if (alt != null && alt.getFlag() == false)
			{
				String[] name = nameAndRsExtractor((SyntaxModel) alt);
				GrComp nextGuy = new GrComp(name[0], name[2]);
				if (((AbstractNode) alt).getType().equals(AbstractNode.NTERMINAL))
				{
					nextGuy.setNonterminal(true);
				}
				else if (((AbstractNode) alt).getType().equals(AbstractNode.TERMINAL))
				{
					nextGuy.setTerminal(true);
				}
				else if (((AbstractNode) alt).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE))
				{
					nextGuy.setLambda(true);
				}
				absGrammar.addAlternative(nextGuy);
				grammar.append(dfs(isFile, alt));
			}
		}
		return grammar.toString();
	}

	public Grammar getAbsGrammar()
	{
		return absGrammar;
	}

	public String run(boolean isFile) throws Exception
	{

		AppOutput.clearGeneratedGrammar();
		AppOutput.displayHorizontalLine(TOPIC.Output);
		AppOutput.displayText("<a>Run grammar generate...</a><br>", TOPIC.Output);

		StringBuffer grammar = new StringBuffer();

		Canvas canvas = ProjectManager.getMainWindow().getActiveScene();
		List<SyntaxElement> children = AsinEditor.getInstance().getLogicDiagram(canvas).getChildrenNodes();

		/*
		 * This loop will put all LeftSide nodes in startNodes vector, and will
		 * set object flat to false
		 */
		List<AbstractNode> startNodes = clearAndSetStartNodes(children);

		for (int i = 0; i < startNodes.size(); i++)
		{
			cont = 0;
			SyntaxModel headCandidate = ((SyntaxModel) startNodes.get(i));
			if (headCandidate == null)
			{
				throw new Exception("Could not find the grammar start node.");
			}
			SyntaxSubpart successorNode = headCandidate.getSucessor();
			if (successorNode == null)
			{
				throw new Exception("Could not find the successor node of the grammar head.");
			}
			successorNode.setNumber(++cont);

			List listChildren = ((SyntaxModel) startNodes.get(i)).getChildrenAsLabels();
			NodeLabel label = (NodeLabel) listChildren.iterator().next();

			writeBegining(label);

			htmlOutput = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"1px\" width=\"100%\">";
			htmlOutput += "<tr style=\"background-color: #EEEEEE; font-weight: bold;\"><td></td>";
			htmlOutput += "<td>Node</td><td>Number</td><td>Alternative</td><td>Successor</td>";
			htmlOutput += "<td>Semantic Rout.</td></tr>";
			if (((AbstractNode) startNodes.get(i)).getType().equals(AbstractNode.START))
			{
				htmlOutput += "<tr><td style=\"background-color: #EEEEEE;\">";
				htmlOutput += "<img src=\"images/icon_s2.png\" alt=\"Initial Node\"></td>";
				htmlOutput += "<td style=\"font-weight: bold;\" >";
				htmlOutput += "<a href=\"" + ((SyntaxModel) startNodes.get(i)).getID() + "\">" + label.getLabelContents() + "</a></td>";
				htmlOutput += "<td align=\"center\">-1</td><td align=\"center\">-</td><td align=\"center\">" + successorNode.getNumber() + "</td>";
				htmlOutput += "<td align=\"center\">-</td></tr>";
				GrComp grammarHead = new GrComp(label.getLabelContents(), ((SyntaxModel) startNodes.get(i)).getID());
				grammarHead.setHead(true);
				if (i == 0)
					absGrammar = new Grammar(grammarHead);
				absGrammar.setHead(grammarHead);
				absGrammar.setCurrent(grammarHead);
			}
			else
			{
				htmlOutput += "<tr><td style=\"background-color: #EEEEEE;\">";
				htmlOutput += "<img src=\"images/icon_H2.png\" alt=\"Left Side\"></td>";
				htmlOutput += "<td style=\"font-weight: bold;\" >";
				htmlOutput += "<a href=\"" + ((SyntaxModel) startNodes.get(i)).getID() + "\">" + label.getLabelContents() + "</a>";
				htmlOutput += "</td><td align=\"center\">-1</td><td align=\"center\">-</td><td align=\"center\">" + successorNode.getNumber() + "</td>";
				htmlOutput += "<td align=\"center\">-</td></tr>";
				GrComp grammarLeftside = new GrComp(label.getLabelContents(), ((SyntaxModel) startNodes.get(i)).getID());
				grammarLeftside.setLeftHand(true);
				if (i == 0)
					absGrammar = new Grammar(grammarLeftside);
				absGrammar.addLeftHand(grammarLeftside);
				absGrammar.setCurrent(grammarLeftside);
			}
			String sout = ((SyntaxModel) startNodes.get(i)).getID() + " H " + label.getLabelContents() + " -1 -1 -1 -1\n";
			if (isFile && out != null)
			{
				out.write(sout);
				out.flush();
			}
			grammar.append(sout);
			String[] name = nameAndRsExtractor((SyntaxModel) successorNode);
			GrComp firstGuy = new GrComp(name[0], name[2]);
			if (((AbstractNode) successorNode).getType().equals(AbstractNode.NTERMINAL))
			{
				firstGuy.setNonterminal(true);
			}
			else if (((AbstractNode) successorNode).getType().equals(AbstractNode.TERMINAL))
			{
				firstGuy.setTerminal(true);
			}
			else if (((AbstractNode) successorNode).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE))
			{
				firstGuy.setLambda(true);
			}
			absGrammar.addSuccessor(firstGuy);
			grammar.append(dfs(isFile, successorNode));
			htmlOutput += "</table>";
			AppOutput.displayGeneratedGrammar(htmlOutput);
		}
		absGrammar.finalize();
		return grammar.toString();
	}

	private void writeBegining(NodeLabel label)
	{
		AppOutput.displayText("<a>>>Begining a new leftside..." + label.getLabelContents() + "</a><br>", TOPIC.Output);
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

	public void setAbsGrammar(Grammar absGrammar)
	{
		this.absGrammar = absGrammar;
	}

}