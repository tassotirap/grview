package org.grview.syntax.command;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

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

public class AsinEditor implements Serializable
{

	private static final long serialVersionUID = -4587431780615158139L;
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

	/** sets this singleton instance from an existing instance **/
	public static void setInstance(AsinEditor asinEditor)
	{
		instance = asinEditor;
	}

	private void add(Command cmd)
	{
		Object obj = cmd.getContext();
		String sobj = (String) obj;
		if (sobj.equals(AbstractNode.NTERMINAL) || sobj.equals(AbstractNode.TERMINAL) || sobj.equals(AbstractNode.LEFTSIDE) || sobj.equals(AbstractNode.LAMBDA_ALTERNATIVE) || sobj.equals(AbstractNode.START))
		{
			String name = (String) cmd.getTarget();
			SimpleNode node = new SimpleNode(sobj, name);
			node.setID((String) cmd.getTarget());
			logicDiagram.addChild(node);
		}
	}

	private void addAndRenameNode(CanvasState canvasState, String node, String type)
	{
		Node n = canvasState.findNode(node);
		if (n != null)
		{
			String name = node;
			String context = type;
			Command cmd = CommandFactory.createAddCommand();
			cmd.addObject(name, context);
			consumeCommand(cmd);

			RenameCommand rc = CommandFactory.createRenameCommand();
			rc.addObject(n.getTitle(), node, node);
			consumeCommand(rc);

			if (n.getMark() != null && !n.getMark().equals(""))
			{
				AddRoutineCommand command = CommandFactory.createAddRoutineCommand();
				command.addObject(node, n.getMark());
				consumeCommand(command);
			}
		}
	}

	private void addConnection(CanvasState canvasState, String node, String type)
	{
		org.grview.canvas.state.Connection connection = canvasState.findConnection((Object) node);
		if (connection != null)
		{
			String edge = node;
			String context = type;

			Command cmd = CommandFactory.createConnectionCommand();
			cmd.addObject(connection.getTarget(), connection.getSource(), edge, context);
			consumeCommand(cmd);
		}
	}

	private void addRoutine(Command cmd)
	{
		Object obj = cmd.getContext();
		if (obj instanceof String)
		{
			String sobj = (String) obj;
			SimpleNode routineNode = new SimpleNode(AbstractNode.SEMANTIC_ROUTINE, sobj);
			Object obj2 = cmd.getTarget();
			if (obj2 instanceof String)
			{
				String name = (String) obj2;
				SyntaxElement se = logicDiagram.findElement(name);
				if (logicDiagram.isNode(se) && se instanceof SyntaxModel)
				{
					((SyntaxModel) se).setSemanticNode(routineNode);
				}

			}
		}
	}

	private void connect(Command cmd)
	{
		Object obj = cmd.getContext();
		String sobj = (String) obj;
		if (sobj.equals(SyntaxDefinitions.SucConnection) || sobj.equals(SyntaxDefinitions.AltConnection))
		{
			SyntaxElement se = logicDiagram.findElement((String) cmd.getSource());
			SyntaxElement te = logicDiagram.findElement((String) cmd.getTarget());
			if (logicDiagram.isNode(se) && logicDiagram.isNode(te))
			{
				SyntaxSubpart ase = (SyntaxSubpart) se;
				SyntaxSubpart ate = (SyntaxSubpart) te;
				Connection w = new Connection((String) cmd.getConnection());
				w.setSource(ase);
				w.setTarget(ate);
				logicDiagram.addChild(w);
				w.attachTarget(sobj);
				w.attachSource();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void delete(Command cmd)
	{
		Object obj = cmd.getContext();
		if (obj.equals(SyntaxDefinitions.SingleDelete))
		{
			// in this case is expected that target is only one object
			SyntaxElement ae = logicDiagram.findElement((String) cmd.getTarget());
			if (logicDiagram.isNode(ae))
			{
				SyntaxSubpart ase = (SyntaxSubpart) ae;
				String targetType = null;
				if (ase instanceof AbstractNode)
				{
					targetType = ((AbstractNode) ase).getType();
				}
				if (targetType != null)
				{
					((DelCommand) cmd).setTargetType(targetType);
				}
				/*
				 * for (Connection w : ase.getConnections()) { DisconnectCommand
				 * dc = new DisconnectCommand(); SyntaxSubpart as1 =
				 * w.getSource(); SyntaxSubpart as2 = w.getTarget(); String
				 * connType = null; if (as2 == as1.getSucessor()) { connType =
				 * SyntaxDefinitions.SucConnection; } else if (as2 ==
				 * as1.getAlternative()) { connType =
				 * SyntaxDefinitions.AltConnection; }
				 * dc.addObject(w.getSource().getID(), w.getTarget().getID(),
				 * w.getID(),connType); disconnect(dc); cmd.addChild(dc); }
				 */
				logicDiagram.removeChild(ase);
			}
			else if (logicDiagram.isConnection(ae))
			{
				Connection w = (Connection) ae;
				SyntaxSubpart as1 = w.getSource();
				SyntaxSubpart as2 = w.getTarget();
				String connType = null;
				if (as2 == as1.getSucessor())
				{
					connType = SyntaxDefinitions.SucConnection;
				}
				else if (as2 == as1.getAlternative())
				{
					connType = SyntaxDefinitions.AltConnection;
				}
				((DelCommand) cmd).setTargetType(connType);
				DisconnectCommand dc = new DisconnectCommand();
				dc.addObject(w.getSource().getID(), w.getTarget().getID(), w.getID(), connType);
				disconnect(dc);
				cmd.addChild(dc);
			}
		}
		else if (obj.equals(SyntaxDefinitions.MultiDelete))
		{
			// in this case is expected that the target is composed by many
			// objects
			Vector<String> e = (Vector<String>) cmd.getTarget();
			for (String st : e)
			{
				DelCommand dcAux = new DelCommand();
				dcAux.addObject(st, SyntaxDefinitions.SingleDelete);
				if (!consumeCommand(dcAux))
				{
					// ERRO
				}
			}
		}
	}

	private void disconnect(Command cmd)
	{
		Object obj = cmd.getContext();
		if (obj.equals(SyntaxDefinitions.SucConnection) || obj.equals(SyntaxDefinitions.AltConnection))
		{
			SyntaxElement se = logicDiagram.findElement((String) cmd.getSource());
			SyntaxElement te = logicDiagram.findElement((String) cmd.getTarget());
			if (logicDiagram.isNode(se) && logicDiagram.isNode(te))
			{
				SyntaxSubpart ase = (SyntaxSubpart) se;
				SyntaxSubpart ate = (SyntaxSubpart) te;
				Connection[] ws = new Connection[ase.getTargetConnections().size()];
				for (int i = 0; i < ws.length; i++)
				{
					ws[i] = ase.getTargetConnections().elementAt(i);
				}
				for (Connection w : ws)
				{
					if (w.getID().equals(cmd.getConnection()) && w.getSource() == ate)
					{
						w.detachSource();
						w.detachTarget();
						logicDiagram.removeChild(w);
					}
				}
			}
		}
	}

	private void removeRoutine(Command cmd)
	{
		Object obj = cmd.getTarget();
		if (obj instanceof String)
		{
			String name = (String) obj;
			SyntaxElement se = logicDiagram.findElement(name);
			if (logicDiagram.isNode(se) && se instanceof SyntaxModel)
			{
				((SyntaxModel) se).setSemanticNode(null);
			}
		}
	}

	private void rename(Command cmd)
	{
		String source = (String) cmd.getSource();
		String target = (String) cmd.getTarget();
		String context = (String) cmd.getContext();
		SyntaxElement ase = logicDiagram.findElement(source);
		if (logicDiagram.isNode(ase))
		{
			SyntaxModel ad = (SyntaxModel) ase;
			List<NodeLabel> labels = ad.getChildrenAsLabels();
			for (int i = 0; i < labels.size(); i++)
			{
				if (labels.get(i).getLabelContents().equals(context))
				{
					labels.get(i).setLabelContents(target);
				}
			}
		}
	}

	private boolean consumeCommand(Command cmd)
	{
		assert cmd != null;
		// execute first the children
		for (Command child : cmd.getChildren())
		{
			if (!consumeCommand(child))
			{
				return false;
			}
		}
		if (!cmd.isConsumed())
		{
			try
			{
				if (cmd.getID().equals(SyntaxDefinitions.AddCommand))
				{
					add(cmd);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.ConnectionCommand))
				{
					connect(cmd);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.DeleteCommand))
				{
					delete(cmd);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.RenameCommand))
				{
					rename(cmd);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.DisconnectionCommand))
				{
					disconnect(cmd);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.AddRoutineCommand))
				{
					addRoutine(cmd);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.RemoveRoutineCommand))
				{
					removeRoutine(cmd);
				}
				cmd.setConsumed(true);
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public SyntaxModel getLogicDiagram(Canvas canvas)
	{
		recreateDiagram(canvas);
		return logicDiagram;
	}

	public void recreateDiagram(Canvas canvas)
	{

		CanvasState canvasState = canvas.getCanvasState();
		logicDiagram = new SyntaxModel();

		for (String node : canvas.getTerminals())
		{
			addAndRenameNode(canvasState, node, SyntaxDefinitions.Terminal);
		}

		for (String node : canvas.getNterminals())
		{
			addAndRenameNode(canvasState, node, SyntaxDefinitions.NTerminal);
		}

		for (String node : canvas.getLeftSides())
		{
			addAndRenameNode(canvasState, node, SyntaxDefinitions.LeftSide);
		}

		for (String node : canvas.getLambdas())
		{
			Node n = canvasState.findNode(node);
			if (n != null)
			{
				String name = node;
				String context = SyntaxDefinitions.LambdaAlternative;
				Command cmd = CommandFactory.createAddCommand();
				cmd.addObject(name, context);
				consumeCommand(cmd);


				if (n.getMark() != null && !n.getMark().equals(""))
				{
					AddRoutineCommand command = CommandFactory.createAddRoutineCommand();
					command.addObject(node, n.getMark());
					consumeCommand(cmd);
				}
			}
		}

		for (String node : canvas.getStart())
		{
			addAndRenameNode(canvasState, node, SyntaxDefinitions.Start);
		}

		for (String node : canvas.getSuccessors())
		{
			addConnection(canvasState, node, SyntaxDefinitions.SucConnection);
		}

		for (String node : canvas.getAlternatives())
		{
			addConnection(canvasState, node, SyntaxDefinitions.AltConnection);
		}
	}


	public boolean undoCommand(Command cmd)
	{
		assert cmd != null;
		if (cmd.isConsumed())
		{
			try
			{
				if (cmd.getID().equals(SyntaxDefinitions.AddCommand))
				{
					DelCommand dc = new DelCommand();
					dc.addObject(cmd.getTarget(), SyntaxDefinitions.SingleDelete);
					delete(dc);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.DisconnectionCommand))
				{
					DisconnectCommand dc = new DisconnectCommand();
					dc.addObject(cmd.getSource(), cmd.getTarget(), cmd.getConnection(), cmd.getContext());
					disconnect(dc);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.ConnectionCommand))
				{
					ConnectCommand cc = new ConnectCommand();
					cc.addObject(cmd.getSource(), cmd.getTarget(), cmd.getConnection(), cmd.getContext());
					connect(cc);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.RenameCommand))
				{
					RenameCommand rc = new RenameCommand();
					rc.addObject(cmd.getTarget(), cmd.getSource(), null);
				}
				else if (cmd.getID().equals(SyntaxDefinitions.SingleDelete))
				{
					DelCommand dc = (DelCommand) cmd;
					if (dc.getTargetType().equals(SyntaxDefinitions.LeftSide) || dc.getTargetType().equals(SyntaxDefinitions.NTerminal) || dc.getTargetType().equals(SyntaxDefinitions.Terminal) || dc.getTargetType().equals(SyntaxDefinitions.LambdaAlternative))
					{
						AddCommand ac = new AddCommand();
						ac.addObject(dc.getTarget(), dc.getTargetType());
					}
				}
				for (int i = cmd.getChildren().size() - 1; i >= 0; i--)
				{
					undoCommand(cmd.getChildren().get(i));
				}
			}
			catch (Exception e)
			{
				// TODO error
			}
			cmd.setConsumed(false);
			return true;
		}
		return false;
	}
}
