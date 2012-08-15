package org.grview.parser;

import org.grview.util.Log;

public class ParserProxyImpl extends ParserProxy
{

	private ParsingEditor pe;
	private boolean ready;

	public ParsingEditor getPe()
	{
		if (pe == null)
		{
			pe = ParsingEditor.getInstance();
		}
		if (pe == null)
		{
			Log.log(Log.ERROR, this, "The parser is not ready yet!", new Exception("Tried to parse before build"));
			ready = false;
			return null;
		}
		ready = true;
		return pe;
	}

	@Override
	public void parse()
	{
		getPe();
		if (ready)
		{
			getPe().run(false);
		}
	}

	@Override
	public void print(Object obj)
	{
		getPe();
		if (ready)
		{
			getPe().displayInputTextNoLine(obj.toString());
		}
	}

	@Override
	public void println(Object obj)
	{
		getPe();
		if (ready)
		{
			getPe().displayInputTextNewLine(obj.toString());
		}
	}
}
