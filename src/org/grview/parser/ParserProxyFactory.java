package org.grview.parser;

import org.grview.ioadapter.IOAdapter;

public abstract class ParserProxyFactory {

	public static ParserProxy create(IOAdapter adapter) {
		return new ParserProxyImpl();
	}
}
