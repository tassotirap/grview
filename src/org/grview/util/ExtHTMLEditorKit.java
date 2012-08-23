package org.grview.util;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class ExtHTMLEditorKit extends HTMLEditorKit
{

	public static class HTMLFactoryX extends HTMLFactory implements ViewFactory
	{

		private static String ApplicationImagePath;

		public static void setApplicationImagePath(String aip)
		{
			ApplicationImagePath = aip;
		}

		@Override
		public View create(Element elem)
		{
			Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
			if (o instanceof HTML.Tag)
			{
				HTML.Tag kind = (HTML.Tag) o;
				if (kind == HTML.Tag.IMG)
					return new ExtImageView(elem, ApplicationImagePath);
			}
			return super.create(elem);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ViewFactory getViewFactory()
	{
		return new HTMLFactoryX();
	}
}
