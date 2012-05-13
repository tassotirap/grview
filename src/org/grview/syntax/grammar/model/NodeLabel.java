package org.grview.syntax.grammar.model;

/** Defines a label for a node **/
public class NodeLabel extends SyntaxSubpart {

	private static final long serialVersionUID = 1L;

	private String text = SyntaxDefinitions.AsinPlugin_Tool_CreationTool_AsinLabel;

	private static int count;

	public NodeLabel(String label) {
		super(getNewID());
		setLabelContents(label);
	}

	public String getLabelContents() {
		return text;
	}

	public static String getNewID() {
		return Integer.toString(count++);
	}

	public void setLabelContents(String s) {
		text = s;
		firePropertyChange("labelContents", "", text);
	}

	@Override
	public String toString() {
		return SyntaxDefinitions.AsinPlugin_Tool_CreationTool_AsinLabel + " #" + getID() + " " 
		+SyntaxDefinitions.PropertyDescriptor_Label_Text + "=" + getLabelContents(); 
	}
	
	@Override
	public String getID() {
		return "";
	}

}