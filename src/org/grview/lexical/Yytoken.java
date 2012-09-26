package org.grview.lexical;

public class Yytoken
{
	public int charBegin;
	public int charEnd;
	public int line;
	public String type;
	public String text;

	public Yytoken(String p1, String text, int line, int charBegin, int charEnd)
	{
		this.type = p1;
		this.text = text;
		this.line = line;
		this.charBegin = charBegin;
		this.charEnd = charEnd;
	}

	public String token()
	{
		return text;
	}

	@Override
	public String toString()
	{
		return "Text   : " + text + "   Type : " + type + "   line  : " + line + "   cBeg. : " + charBegin + "   cEnd. : " + charEnd;
	}
}
