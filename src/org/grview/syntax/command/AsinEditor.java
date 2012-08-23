package org.grview.syntax.command;

import java.util.List;

import org.grview.canvas.Canvas;
import org.grview.canvas.state.CanvasState;
import org.grview.canvas.state.Node;
import org.grview.syntax.grammar.model.AbstractNode;
import org.grview.syntax.grammar.model.Connection;
import org.grview.syntax.grammar.model.NodeLabel;
import org.grview.syntax.grammar.model.SimpleNode;
import org.grview.syntax.grammar.model.SyntaxDefinitions;
import org.grview.syntax.grammar.model.SyntaxElement;
import org.grview.syntax.grammar.model.SyntaxModel;
import org.grview.syntax.grammar.model.SyntaxSubpart;

public class AsinEditor
{

	private SyntaxModel logicDiagram = new SyntaxModel();
	private transient static AsinEditor instance;

	private AsinEditor()
	{
	}

	public static AsinEditor getInstance()
	{
		if (instance == null)
		{
			instance = new AsinEditor();
		}
		return instance;
	}

	private void add(String target, String context)
	{
		if (context.equals(AbstractNode.NTERMINAL) || context.equals(AbstractNode.TERMINAL) || context.equals(AbstractNode.LEFTSIDE) || context.equals(AbstractNode.LAMBDA_ALTERNATIVE) || context.equals(AbstractNode.START))
		{
			SimpleNode node = new SimpleNode(context, target);
			node.setID(target);
			logicDiagram.addChild(node);
		}
	}

	private void addAndRenameNode(CanvasState canvasState, String name, String type)
	{
		Node node = canvasState.findNode(name);
		if (node != null)
		{
			String context = type;
			add(name, context);

			rename(name, name, node.getTitle());

			if (node.getMark() != null && !node.getMark().equals(""))
			{
				addRoutine(name, node.getMark());
			}
		}
	}

	private void addConnection(CanvasState canvasState, String connection, String type)
	{
		org.grview.canvas.state.Connection canvasConnection = canvasState.findConnection((Object) connection);
		if (connection != null)
		{
			connect(canvasConnection.getTarget(), canvasConnection.getSource(), connection, type);
		}
	}

	private void addRoutine(String target, String routineName)
	{

		SimpleNode routineNode = new SimpleNode(AbstractNode.SEMANTIC_ROUTINE, routineName);

		String name = target;
		SyntaxElement se = logicDiagram.findElement(name);
		if (logicDiagram.isNode(se) && se instanceof SyntaxModel)
		{
			((SyntaxModel) se).setSemanticNode(routineNode);
		}
	}

	private void connect(String targe, String source, String connector, String type)
	{
		if (type.equals(SyntaxDefinitions.SucConnection) || type.equals(SyntaxDefinitions.AltConnection))
		{
			SyntaxElement sourceElement = logicDiagram.findElement(source);
			SyntaxElement targetElement = logicDiagram.findElement(targe);
			if (logicDiagram.isNode(sourceElement) && logicDiagram.isNode(targetElement))
			{
				SyntaxSubpart sourceSyntaxSubpart = (SyntaxSubpart) sourceElement;
				SyntaxSubpart targetSyntaxSubpart = (SyntaxSubpart) targetElement;
				Connection connection = new Connection(connector);
				connection.setSource(sourceSyntaxSubpart);
				connection.setTarget(targetSyntaxSubpart);
				logicDiagram.addChild(connection);
				connection.attachTarget(type);
				connection.attachSource();
			}
		}
	}

	private void recreateDiagram(Canvas canvas)
	{

		CanvasState canvasState = canvas.getCanvasState();
		logicDiagram = new SyntaxModel();

		for (String name : canvas.getTerminals())
		{
			addAndRenameNode(canvasState, name, SyntaxDefinitions.Terminal);
		}

		for (String name : canvas.getNterminals())
		{
			addAndRenameNode(canvasState, name, SyntaxDefinitions.NTerminal);
		}

		for (String name : canvas.getLeftSides())
		{
			addAndRenameNode(canvasState, name, SyntaxDefinitions.LeftSide);
		}

		for (String name : canvas.getLambdas())
		{
			Node node = canvasState.findNode(name);
			if (node != null)
			{
				String context = SyntaxDefinitions.LambdaAlternative;
				add(name, context);

				if (node.getMark() != null && !node.getMark().equals(""))
				{
					addRoutine(name, node.getMark());
				}
			}
		}

		for (String name : canvas.getStart())
		{
			addAndRenameNode(canvasState, name, SyntaxDefinitions.Start);
		}

		for (String name : canvas.getSuccessors())
		{
			addConnection(canvasState, name, SyntaxDefinitions.SucConnection);
		}

		for (String name : canvas.getAlternatives())
		{
			addConnection(canvasState, name, SyntaxDefinitions.AltConnection);
		}
	}

	private void rename(String source, String oldName, String newName)
	{
		SyntaxElement syntaxElement = logicDiagram.findElement(source);
		if (logicDiagram.isNode(syntaxElement))
		{
			SyntaxModel syntaxModel = (SyntaxModel) syntaxElement;
			List<NodeLabel> labels = syntaxModel.getChildrenAsLabels();
			for (int i = 0; i < labels.size(); i++)
			{
				if (labels.get(i).getLabelContents().equals(oldName))
				{
					labels.get(i).setLabelContents(newName);
				}
			}
		}
	}

	public SyntaxModel getLogicDiagram(Canvas canvas)
	{
		recreateDiagram(canvas);
		return logicDiagram;
	}
}
