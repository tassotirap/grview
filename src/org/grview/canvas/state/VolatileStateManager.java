package org.grview.canvas.state;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import org.grview.syntax.command.Command;

public class VolatileStateManager implements PropertyChangeListener
{

	public class VolatileState
	{
		public byte[] serializedObject;
		public Command command;
	}

	private ByteArrayInputStream bais;
	private ObjectInputStream ois;
	private HashMap<String, VolatileState> lastStates = new HashMap<String, VolatileState>();
	private HashMap<String, VolatileState> nextStates = new HashMap<String, VolatileState>();
	private final static String head = "%HEAD";
	private Object object;
	private Vector<String> undoables = new Vector<String>();

	private Vector<String> redoables = new Vector<String>();

	private long last;

	private int capacity;

	private PropertyChangeSupport monitor;

	public VolatileStateManager(Serializable object, int capacity)
	{
		this.object = object;
		this.capacity = capacity;
		monitor = new PropertyChangeSupport(this);
	}

	private String cleanName(String state)
	{
		return state.replaceFirst(head, "").replaceFirst("[0-9]*", "");
	}

	private String getUniqueName(String state, long position)
	{
		return head + position + state;
	}

	private Object read(String state, HashMap<String, VolatileState> states) throws IOException, ClassNotFoundException
	{
		for (int i = states.size() - 1; i >= 0; i--)
		{
			if (states.containsKey(state))
			{
				byte[] data = states.get(state).serializedObject;
				bais = new ByteArrayInputStream(data);
				ois = new ObjectInputStream(bais);
				return ois.readObject();
			}
		}
		return null;
	}

	private void writeCanvas(String state, HashMap<String, VolatileState> states, Command command) throws IOException
	{
		monitor.firePropertyChange("writing", null, object);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);
		VolatileState volatileState = new VolatileState();
		volatileState.serializedObject = byteArrayOutputStream.toByteArray();
		volatileState.command = command;
		states.put(state, volatileState);
	}

	public PropertyChangeSupport getMonitor()
	{
		return monitor;
	}

	public String getNextRedoable()
	{
		if (hasNextRedo())
		{
			return cleanName(redoables.get(redoables.size() - 1));
		}
		return null;
	}

	public String getNextUndoable()
	{
		if (hasNextUndo())
		{
			return cleanName(undoables.get(undoables.size() - 1));
		}
		return null;
	}

	public Vector<String> getRedoables()
	{
		Vector<String> v = new Vector<String>();
		for (String st : redoables)
		{
			v.insertElementAt(cleanName(st), 0);
		}
		return v;
	}

	public Vector<String> getUndoables()
	{
		Vector<String> v = new Vector<String>();
		for (String st : undoables)
		{
			v.insertElementAt(cleanName(st), 0);
		}
		return v;
	}

	public boolean hasLastState(String state)
	{
		for (int i = 0; i < lastStates.size(); i++)
		{
			if (lastStates.containsKey(getUniqueName(state, i)))
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasNextRedo()
	{
		return redoables.size() > 0;
	}

	public boolean hasNextState(String state)
	{
		for (int i = 0; i < nextStates.size(); i++)
		{
			if (nextStates.containsKey(getUniqueName(state, i)))
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasNextUndo()
	{
		return undoables.size() > 1;
	}

	public void init()
	{
		try
		{
			this.writeCanvas(getUniqueName("base", last), lastStates, null);
			undoables.add(getUniqueName("base", last++));
		}
		catch (Exception e)
		{
			// TODO error
			e.printStackTrace();
		}
	}

	public void limitBuffer(Vector<String> driver, HashMap<String, VolatileState> buffer)
	{
		int limit = (driver.size() - 1) - capacity;
		for (int i = 0; i <= limit; i++)
		{
			buffer.remove(driver.get(i));
			driver.remove(i);
		}
	}

	/**
	 * Receives a new event, typically the execution of a command. Once executed
	 * the command may send a string to identify itself. This string, though,
	 * can not start by a number.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getPropertyName().equals("undoable"))
		{
			String state = getUniqueName(((Command) event.getNewValue()).getDescription(), last++);
			try
			{
				writeCanvas(state, lastStates, (Command) event.getNewValue());
				nextStates.clear();
				redoables.clear();
				undoables.add(state);
				limitBuffer(undoables, lastStates);
				monitor.firePropertyChange("undoable", null, ((Command) event.getNewValue()).getDescription());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	public void redo()
	{
		if (hasNextRedo())
		{
			redo(redoables.size() - 1);
		}
	}

	public void redo(int index)
	{
		try
		{
			Object nobject = read(redoables.get(index), nextStates);
			monitor.firePropertyChange("object_state", object, nobject);
			object = nobject;
			undoables.add(redoables.get(index));
			lastStates.put(redoables.get(index), nextStates.get(redoables.get(index)));
			nextStates.remove(redoables.get(index));
			redoables.remove(index);
			limitBuffer(undoables, lastStates);
			limitBuffer(redoables, nextStates);
		}
		catch (Exception e)
		{
			// TODO error
		}
	}

	public void undo()
	{
		if (hasNextUndo())
		{
			undo(undoables.size() - 1);
		}
	}

	public void undo(int index)
	{
		try
		{
			Object nobject = read(undoables.get(index - 1), lastStates);
			monitor.firePropertyChange("object_state", object, nobject);
			if (lastStates.get(undoables.get(index - 1)).command != null)
			{
				lastStates.get(undoables.get(index - 1)).command.undo();
			}
			object = nobject;
			redoables.add(undoables.get(index));
			nextStates.put(undoables.get(index), lastStates.get(undoables.get(index)));
			lastStates.remove(undoables.get(index));
			undoables.remove(index);
			limitBuffer(undoables, lastStates);
			limitBuffer(redoables, nextStates);
		}
		catch (Exception e)
		{
			// TODO error
			e.printStackTrace();
		}
	}
}