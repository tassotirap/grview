package org.grview.ioadapter;

import org.grview.parser.ParserProxy;

public interface InputAdapterImpl {

	public void setObject(Object obj);
	public void setInput(ParserProxy input);
	public void init();
}
