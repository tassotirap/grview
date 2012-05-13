package org.grview.canvas.state;

import java.awt.Point;
import java.io.Serializable;

public class Node extends CanvasSerializableElement {

	private static final long serialVersionUID = -5146510630189874864L;
	private String name;
	private String title;
	private Point  location;
	private String type;
	private String mark;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getMark() {
		return mark;
	}
	
	
}