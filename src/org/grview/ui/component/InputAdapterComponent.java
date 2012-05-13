package org.grview.ui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.grview.ioadapter.InputAdapter;
import org.grview.util.ExtComboBoxUI;
import org.grview.util.Log;


import com.jidesoft.icons.ColorFilter;

public class InputAdapterComponent extends AdapterComponent {

	private JButton formViewButton;
	private JButton codeViewButton;
	private JButton frameViewButton;
	private JPanel window;
	private JPanel toolbar;
	private JComponent contentPane;
	private JLabel listenersLabel;
	private JComboBox listComboBox;
	private JLabel objectsLabel;
	private JComboBox objectsComboBox;
	private JLabel methodsLabel;
	private JComboBox methodsComboBox;
	private JButton addButton;
	private JButton generateButton;

	private InputAdapter ia;

	private boolean built;
	private boolean started;

	private enum VIEW {FORM, CODE, FRAME};
	private VIEW activeView = VIEW.FORM;

	public final static String ICONS_PATH = "/org/grview/images/";
	private String path;

	/** a chain of selected methods and fields **/ 
	private String methodChain1;
	
	/** another chain of selected methods and fields **/
	private String methodChain2;
	
	/**
	 * A map to selected methods
	 */
	private HashMap<String, Method> methodMap;
	
	/**
	 * A map to selected listeners
	 */
	private HashMap<String, Method> listenerMap;
	
	public InputAdapterComponent() {
		ia = new InputAdapter(this);
	}
	
	private void init() {
		formViewButton = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "view-wizard.png")));
		formViewButton.setOpaque(false);
		formViewButton.setBorder(new EmptyBorder(0,0,0,0));
		formViewButton.setRolloverEnabled(true);
		formViewButton.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon)formViewButton.getIcon()).getImage())));
		formViewButton.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon)formViewButton.getIcon()).getImage())));
		formViewButton.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)formViewButton.getIcon()).getImage())));
		formViewButton.setToolTipText("View Wizard Form");
		formViewButton.setEnabled(true);
		formViewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (activeView != VIEW.FORM) {
					activeView = VIEW.FORM;
					setWindowView(ia.getFormView());
				}
			}
		});
		codeViewButton = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "view-code.png")));
		codeViewButton.setOpaque(false);
		codeViewButton.setBorder(new EmptyBorder(0,0,0,0));
		codeViewButton.setRolloverEnabled(true);
		codeViewButton.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon)codeViewButton.getIcon()).getImage())));
		codeViewButton.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon)codeViewButton.getIcon()).getImage())));
		codeViewButton.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)codeViewButton.getIcon()).getImage())));
		codeViewButton.setToolTipText("View Code");
		codeViewButton.setEnabled(false);
		codeViewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (activeView != VIEW.CODE && built) {
					activeView = VIEW.CODE;
					setWindowView(ia.getCodeView());
				}
			}
		});
		frameViewButton = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "view-frame.png")));
		frameViewButton.setOpaque(false);
		frameViewButton.setBorder(new EmptyBorder(0,0,0,0));
		frameViewButton.setRolloverEnabled(true);
		frameViewButton.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon)frameViewButton.getIcon()).getImage())));
		frameViewButton.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon)frameViewButton.getIcon()).getImage())));
		frameViewButton.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)frameViewButton.getIcon()).getImage())));
		frameViewButton.setToolTipText("View Frame");
		frameViewButton.setEnabled(false);
		frameViewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (activeView != VIEW.FRAME && started) {
					activeView = VIEW.FRAME;
					setWindowView(ia.getFrameView());
				}
			}
		});
		listenersLabel = new JLabel("Listeners");
		listenersLabel.setEnabled(false);
		listComboBox = new JComboBox();
		listComboBox.setEnabled(false);
		//listComboBox.setBackground(Color.WHITE);
		listComboBox.setPreferredSize(new Dimension(150, 25));
		listComboBox.setUI(new ExtComboBoxUI());
		objectsLabel = new JLabel("Objects");
		objectsLabel.setEnabled(false);
		objectsComboBox = new JComboBox();
		objectsComboBox.setEnabled(false);
		//objectsComboBox.setBackground(Color.WHITE);
		objectsComboBox.setPreferredSize(new Dimension(150, 25));
		objectsComboBox.setUI(new ExtComboBoxUI());
		objectsComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateState();
			}
		});
		methodsLabel = new JLabel("Methods");
		methodsLabel.setEnabled(false);
		methodsComboBox = new JComboBox();
		methodsComboBox.setEnabled(false);
		//methodsComboBox.setBackground(Color.WHITE);
		methodsComboBox.setPreferredSize(new Dimension(150, 25));
		methodsComboBox.setUI(new ExtComboBoxUI());
		addButton = new JButton("Add");
		addButton.setEnabled(false);
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listComboBox.getSelectedIndex() != 0) {
					Method m = listenerMap.get(listComboBox.getSelectedItem());
					Class<?>[] parameters = m.getParameterTypes();
					if (parameters.length == 1) {
						methodChain2 = "\t\t" + "getObject()." + ((methodChain1.equals(""))? "" : methodChain1 + ".") +
						m.getName() + "( new " + parameters[0].getName() + "() {\n\n";
						Stack<Class<?>> s = new Stack<Class<?>>();
						s.push(parameters[0]);
						while (!s.isEmpty()) {
							Class<?> _interface = s.pop();
							for (Method im : _interface.getDeclaredMethods()) {
								String modifier = Modifier.toString(im.getModifiers()).replace("abstract", "");
								String returnType = im.getReturnType().getName();
								String name = im.getName();
								String args = "";
								int argCnt = 0;
								boolean hasMore = true;
								for (Class<?> imp : im.getParameterTypes()) {
									if (argCnt + 1 == im.getParameterTypes().length)
										hasMore = false;
									args += imp.getName() + " arg" + argCnt++ + ((hasMore) ? ", " : ""); 
								}	
								methodChain2 += "\t\t\t" + modifier + " " + returnType + " " + name + "(" + args + ") " + "{\n\t\t\t\t//generated method stub\n\t\t\t}\n\n";
							}
							for (Class<?> new_interface : _interface.getInterfaces()) {
								s.push(new_interface);
							}
						}
						methodChain2 += "\t\t});\n\n";
					}
					ia.getCodeTextArea().getBuffer().insert(ia.getStubInitPosition(), methodChain2);
				}
				else if (methodsComboBox.getSelectedIndex() > 0) {
					methodChain1 += ((methodChain1.equals(""))? "" : ".") + methodMap.get(methodsComboBox.getSelectedItem()).getName() + "()\n\n";
					ia.getCodeTextArea().getBuffer().insert(ia.getCodeTextArea().getCaretPosition(), methodChain1);
				}
				methodChain2 = "";
				methodChain1 = "";
				updateState();
			}
		});
		generateButton = new JButton("Generate");
		generateButton.setEnabled(false);
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ia.generate()) {
					frameViewButton.setEnabled(true);
					JOptionPane.showMessageDialog(window, "Successfully generated!");
				}
			}
		});
		toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		toolbar.add(formViewButton);
		toolbar.add(codeViewButton);
		toolbar.add(frameViewButton);
		//toolbar.add(objectsLabel);
		toolbar.add(objectsComboBox);
		//it may be better for layout to comment this lines
		//toolbar.add(listenersLabel);
		toolbar.add(listComboBox);
		//toolbar.add(methodsLabel);
		toolbar.add(methodsComboBox);
		toolbar.add(addButton);
		toolbar.add(generateButton);
		window = new JPanel(new BorderLayout());
		window.add(toolbar, BorderLayout.NORTH);
		jComponent = window;
	}

	/** Updates the active view
	 * 
	 * @param view the JComponent that will be displayed in the main area of the window component
	 */
	protected void setWindowView(JComponent view) {
		window.remove(contentPane);
		window.add(view, BorderLayout.CENTER);
		contentPane = view;
		updateState();
		window.repaint();
	}

	private void updateState() {
		if (activeView == VIEW.CODE) {
			objectsLabel.setEnabled(true);
			objectsComboBox.setEnabled(true);
			Object selectedItem = objectsComboBox.getSelectedItem();
			Class<?> selectedClass = null;
			Object selectedObject = null;
			objectsComboBox.removeAllItems();
			objectsComboBox.addItem("Main Object");
			if (selectedItem == null || selectedItem.equals("Main Object")) {
				objectsComboBox.setSelectedIndex(0);
				methodChain1 = "";
				selectedClass = ia.getAdapterClass();
				selectedObject = ia.getAdapterInstance();
				ia.setLastInstance(null);
			}
			//For now it doesn't allow multiple levels of objects, but that's why it's repopulated all the time
			ArrayList<String> fields = new ArrayList<String>();
			for (Field f : ia.getAdapterClass().getDeclaredFields()) {
				boolean hasGetter = false;
				String beanGetter = "get" + f.getName();
				beanGetter = beanGetter.toLowerCase();
				Method getter = null;
				for (Method m : ia.getAdapterClass().getDeclaredMethods()) {
					if (Modifier.isPublic(m.getModifiers()) &&
							m.getName().toLowerCase().equals(beanGetter)) {
						hasGetter = true;
						getter = m;
						break;
					}
				}
				if (Modifier.isPublic(f.getModifiers()) || hasGetter) {
					if (selectedItem != null && selectedItem.equals(f.getType().getName() + " " + f.getName())) {
						methodChain1 = getter.getName() + "()";
						selectedClass = f.getClass();
						try {
							selectedObject = getter.invoke(ia.getAdapterInstance(), null);
							ia.setLastInstance(selectedObject);
						} catch (Exception e) {
							Log.log(Log.ERROR, this, "An error has occurred when trying an instance of the selected object", e);
							selectedObject = null;
						}
					}
					fields.add(f.getType().getName() + " " + f.getName());
				}
			}
			Collections.sort(fields);
			for (String item : fields) {
				objectsComboBox.addItem(item);
				if (selectedItem != null && item.equals(selectedItem)) {
					objectsComboBox.setSelectedItem(item);
				}
			}
			methodsLabel.setEnabled(true);
			methodsComboBox.setEnabled(true);
			populateMethodsCombo(selectedObject);
			listenersLabel.setEnabled(true);
			listComboBox.setEnabled(true);
			populateListenersCombo();
			addButton.setEnabled(true);
			if (ia.getCodeTextArea().getBufferLength() > 0) {
				generateButton.setEnabled(true);
			}
			else {
				generateButton.setEnabled(false);
			}
		}
		else {
			objectsLabel.setEnabled(false);
			objectsComboBox.setEnabled(false);
			methodsLabel.setEnabled(false);
			methodsComboBox.setEnabled(false);
			listenersLabel.setEnabled(false);
			listComboBox.setEnabled(false);
			addButton.setEnabled(false);
			generateButton.setEnabled(false);
		}
	}

	private void populateMethodsCombo(Object object) {
		Method[] methods = ((object == null) ? ia.getAdapterInstance().getClass().getDeclaredMethods() : object.getClass().getDeclaredMethods());
		ArrayList<String> items = new ArrayList<String>();
		methodMap = new HashMap<String, Method>();
		methodsComboBox.removeAllItems();
		methodsComboBox.addItem("Methods");
		methodsComboBox.setSelectedIndex(0);
		for (Method m : methods) {
			if (Modifier.isPublic(m.getModifiers())) {
				String item = m.getReturnType().getSimpleName() + " " + m.getName() + "(";
				for (Class<?> c : m.getParameterTypes()) {
					item = item + c.getSimpleName() + ", ";
				}
				if (item.endsWith(", ")) {
					item = item.substring(0, item.length() - 1);
				}
				item += ")";
				items.add(item);
				methodMap.put(item, m);
			}
		}
		Collections.sort(items);
		for (String item : items) {
			methodsComboBox.addItem(item);
		}
	}

	private void populateListenersCombo() {
		Method[] methods = ia.getCurrentClass().getMethods();
		ArrayList<String> items = new ArrayList<String>();
		listenerMap = new HashMap<String, Method>();
		listComboBox.removeAllItems();
		listComboBox.addItem("Listeners");
		listComboBox.setSelectedIndex(0);
		for (Method m : methods) {
			String name = m.getName();
			if (name.startsWith("set") || name.startsWith("add") || name.startsWith("register")) {
				if (Modifier.isPublic(m.getModifiers())) {
					for (Class<?> p : m.getParameterTypes()) {
						boolean found = false;
						for (Class<?> i : p.getInterfaces()) {
							if (i == java.util.EventListener.class) {
								String item = m.getName() + "(" + p.getName() + ")";
								items.add(item);
								found = true;
								listenerMap.put(item, m);
								break;
							}
						}
						if (found) { break; }
					}
				}
			}
		}
		Collections.sort(items);
		for (String st : items) {
			listComboBox.addItem(st);
		}
	}

	@Override
	public JComponent create(Object param) throws BadParameterException {
		if (param instanceof String) {
			path = (String) param;
		}
		else {
			throw new BadParameterException("A filename was expected");
		}
		init();
		if (activeView == VIEW.FORM) {
			contentPane = ia.getFormView();
		}
		else if (activeView == VIEW.CODE) {
			contentPane = ia.getCodeView();
		}
		else if (activeView == VIEW.FRAME) {
			contentPane = ia.getFrameView();
		}
		window.add(contentPane, BorderLayout.CENTER);
		window.repaint();
		return window;
	}

	public void setBuilt(boolean built) {
		this.built = built;
	}

	public void setStarted(boolean started) {
		this.started = started;
		codeViewButton.setEnabled(true);
		updateState();
	}

	@Override
	public void fireContentChanged() {
		for (ComponentListener listener : listeners) {
			listener.ContentChanged(this, null, null);
		}
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void saveFile() {
		ia.save();

	}

	@Override
	public void ContentChanged(Component source, Object oldValue,
			Object newValue) {
		fireContentChanged();
		if (ia.getCodeTextArea().getBufferLength() > 0) {
			generateButton.setEnabled(true);
		}
		else {
			generateButton.setEnabled(false);
		}

	}

}
