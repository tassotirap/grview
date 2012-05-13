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

	public class VolatileStateManager implements PropertyChangeListener {
		
		private static final long serialVersionUID = 1L;
		private ByteArrayInputStream bais;
		private ByteArrayOutputStream baos;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private HashMap<String, VolatileState> lastStates = new HashMap<String, VolatileState>();
		private HashMap<String, VolatileState> nextStates = new HashMap<String, VolatileState>();
		private final static String head = "%HEAD";
		private Object object;
		private Vector<String> undoables = new Vector<String>();
		private Vector<String> redoables = new Vector<String>();
		
		private long last;
		
		private int capacity;
		
		private PropertyChangeSupport monitor;
		
		public class VolatileState {
			public byte[] serializedObj;
			public Command command;
		}
		
		public VolatileStateManager(Serializable object, int capacity) {
			this.object = object;
			this.capacity = capacity;
			monitor = new PropertyChangeSupport(this);
		}
		
		public void init() {
			try {
				this.write(getUniqueName("base", last), lastStates, null);
				undoables.add(getUniqueName("base", last++));
				}
				catch (Exception e) {
					//TODO error
					e.printStackTrace();
				}
		}
		
		private Object read(String state, HashMap<String, VolatileState> states) throws IOException, ClassNotFoundException {
			for (int i = states.size() - 1; i >= 0; i--) {
				if (states.containsKey(state)) {
					byte[] data = states.get(state).serializedObj;
					bais = new ByteArrayInputStream(data);
					ois = new ObjectInputStream(bais);
					return ois.readObject();
				}
			}
			return null;
		}
		
		private void write(String state, HashMap<String, VolatileState> states, Command cmd) throws IOException{
			monitor.firePropertyChange("writing", null, object);
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			VolatileState vs = new VolatileState();
			vs.serializedObj = baos.toByteArray();
			vs.command = cmd;
			states.put(state, vs);
		}
		
		public boolean hasLastState(String state) {
			for (int i = 0; i < lastStates.size(); i++) {
				if (lastStates.containsKey(getUniqueName(state, i))) {
					return true;
				}
			}
			return false;
		}
		
		public boolean hasNextState(String state) {
			for (int i = 0; i < nextStates.size(); i++) {
				if (nextStates.containsKey(getUniqueName(state, i))) {
					return true;
				}
			}
			return false;
		}
		
		private void clearAll() {
			lastStates.clear();
		}
		
		private String getUniqueName(String state, long position) {
			return head + position + state; 
		}
		
		private String cleanName(String state) {
			return state.replaceFirst(head, "").replaceFirst("[0-9]*", "");
		}
		public boolean hasNextUndo() {
			return undoables.size() > 1;
		}
		
		public boolean hasNextRedo() {
			return redoables.size() > 0;
		}
		
		public String getNextUndoable() {
			if (hasNextUndo()) {
				return cleanName(undoables.get(undoables.size() - 1)); 
			}
			return null;
		}
		
		public String getNextRedoable() {
			if (hasNextRedo()) {
				return cleanName(redoables.get(redoables.size() - 1));
			}
			return null;
		}
		
		public void undo() {
			if (hasNextUndo()) {
				undo(undoables.size() - 1);
			}
		}
		
		public void redo() {
			if (hasNextRedo()) {
				redo(redoables.size() - 1);
			}
		}
		
		public void undo(int index) {
			try {
				Object nobject = read(undoables.get(index - 1), lastStates);
				monitor.firePropertyChange("object_state", object, nobject);
				if (lastStates.get(undoables.get(index - 1)).command != null) {
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
			catch (Exception e) {
				//TODO error
				e.printStackTrace();
			}
		}
		
		public void redo(int index) {
			try {
				Object nobject = read(redoables.get(index), nextStates);
				monitor.firePropertyChange("object_state", object, nobject);
				if (!nextStates.get(redoables.get(index)).command.execute()) {
					//TODO error
				}
				object = nobject;
				undoables.add(redoables.get(index));
				lastStates.put(redoables.get(index), nextStates.get(redoables.get(index)));
				nextStates.remove(redoables.get(index));
				redoables.remove(index);
				limitBuffer(undoables, lastStates);
				limitBuffer(redoables, nextStates);
			}
			catch (Exception e) {
				//TODO error
			}
		}

		public Vector<String> getUndoables() {
			Vector<String> v = new Vector<String>();
			for (String st : undoables) {
				v.insertElementAt(cleanName(st), 0);
			}
			return v;
		}
		
		public Vector<String> getRedoables() {
			Vector<String> v = new Vector<String>();
			for (String st : redoables) {
				v.insertElementAt(cleanName(st), 0);
			}
			return v;
		}
		
		public void limitBuffer(Vector<String> driver, HashMap<String, VolatileState> buffer) {
			int limit = (driver.size() - 1) - capacity;
			for (int i = 0; i <= limit ; i++) {
				buffer.remove(driver.get(i));
				driver.remove(i);
			}
		}
		/**
		 * Receives a new event, typically the execution of a command. Once executed
		 * the command may send a string to identify itself. This string, though, can
		 * not start by a number.
		 */
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals("undoable")) {
				String state = getUniqueName(((Command)event.getNewValue()).getDescription(), last++);
				try {
					write(state, lastStates, (Command)event.getNewValue());
					nextStates.clear();
					redoables.clear();
					undoables.add(state);
					limitBuffer(undoables, lastStates);
					monitor.firePropertyChange("undoable", null,((Command)event.getNewValue()).getDescription());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		public PropertyChangeSupport getMonitor() {
			return monitor;
		}
	}