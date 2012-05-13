package org.grview.util;
import javax.swing.text.html.*;
import javax.swing.text.*;

public class ExtHTMLEditorKit extends HTMLEditorKit {

  @Override
public ViewFactory getViewFactory() {
    return new HTMLFactoryX();
  }
  
  public static class HTMLFactoryX extends HTMLFactory
    implements ViewFactory {
  
	private static String ApplicationImagePath;
	
	public static void setApplicationImagePath(String aip) {
		ApplicationImagePath = aip;
	}
	
    @Override
	public View create(Element elem) {
      Object o = 
        elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (o instanceof HTML.Tag) {
	HTML.Tag kind = (HTML.Tag) o;
        if (kind == HTML.Tag.IMG) 
          return new ExtImageView(elem, ApplicationImagePath);
      }
      return super.create( elem );
    }
  }
}










