package org.grview.ioadapter;

import org.grview.parser.ParserProxy;

public interface InputAdapterImpl
{

	public void init();

	public void setInput(ParserProxy input);

	public void setObject(Object obj);
}
