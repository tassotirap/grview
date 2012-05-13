package org.grview.ui;

import java.awt.Component;

import javax.swing.Icon;

import net.infonode.docking.View;

/**
 * A dynamically created view containing an id.
 */
public class DynamicView extends View {
  private int id;
  private org.grview.ui.component.Component componentModel;
  private String fileName;
  /**
   * Constructor.
   *
   * @param title     the view title
   * @param icon      the view icon
   * @param jcomponent the view component
   * @param id        the view id
   */
  public DynamicView(String title, Icon icon, Component jcomponent, org.grview.ui.component.Component componentModel, String fileName, int id) {
    super(title, icon, jcomponent);
    this.id = id;
    this.componentModel = componentModel;
    this.fileName = fileName;
  }

  public org.grview.ui.component.Component getComponentModel() {
	  return componentModel;
  }
  
  public String getFileName() {
	  return fileName;
  }
  /**
   * Returns the view id.
   *
   * @return the view id
   */
  public int getId() {
    return id;
  }
}
