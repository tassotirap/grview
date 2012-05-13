package org.grview.actions;

import org.grview.util.XMLUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

/**
 * this class loads the semantic files into a {@link AsinActionSet}.
 * @author Gustavo H. Braga
 *
 */
public class RoutinesListHandler extends ActionListHandler {

	public RoutinesListHandler(String path, AsinActionSet actionSet) {
		super(path, actionSet);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
	{
		return XMLUtilities.findEntity(systemId, "routines.dtd", getClass());
	}
	
	@Override
	public void startElement(String uri, String localName,
				 String qName, Attributes attrs)
	{
		String tag = pushElement(qName);

		if (tag.equals("ROUTINE"))
		{
			setActionName(attrs.getValue("NAME"));
			setNoRepeat("TRUE".equals(attrs.getValue("NO_REPEAT")));
			setNoRecord("TRUE".equals(attrs.getValue("NO_RECORD")));
			setNoRememberLast("TRUE".equals(attrs.getValue("NO_REMEMBER_LAST")));
			getCode().setLength(0);
			getIsSelected().setLength(0);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
	{
		String tag = peekElement();

		if (qName.equals(tag))
		{
			if (tag.equals("ROUTINE"))
			{
				String selected = (getIsSelected().length() > 0) ?
					getIsSelected().toString() : null;
				AbstractEditAction action = 
					getActionSet().createBeanShellAction(getActionName(),
									getCode().toString(),
									selected,
									isNoRepeat(),
									isNoRecord(),
									isNoRememberLast());
				getActionSet().addAction(action);
				setNoRecord(false);
				setNoRememberLast(false);
				setNoRepeat(false);
				getCode().setLength(0);
				getIsSelected().setLength(0);
			}

			popElement();
		}
		else
		{
			// can't happen
			throw new InternalError();
		}
	}
}
