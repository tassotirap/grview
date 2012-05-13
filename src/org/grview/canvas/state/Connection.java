package org.grview.canvas.state;

import java.io.Serializable;

public class Connection extends CanvasSerializableElement implements Comparable<Connection> {
	
	private static final long serialVersionUID = -4162245206128920310L;
	private String name;
	private String source;
	private String target;
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	
	@Override
	public int compareTo(Connection c) {
		return getName().compareTo(c.getName());
	}
	
}