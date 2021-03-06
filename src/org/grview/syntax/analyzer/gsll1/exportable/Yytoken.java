package org.grview.syntax.analyzer.gsll1.exportable;

/** The internal representation of a lexical token **/
public class Yytoken
{
	public int m_charBegin;
	public int m_charEnd;
	public int m_line;
	public String m_p1;
	public String m_text;

	/**
	 * constructor
	 * 
	 * @param p1
	 *            the type of this token
	 * @param text
	 *            the text of this token
	 * @param line
	 *            the line where this token was found
	 * @param charBegin
	 *            the position where this token begins
	 * @param charEnd
	 *            the position where this token ends
	 */
	public Yytoken(String p1, String text, int line, int charBegin, int charEnd)
	{
		m_p1 = p1;
		m_text = text;
		m_line = line;
		m_charBegin = charBegin;
		m_charEnd = charEnd;
	}

	/** returns the text of this token **/
	public String token()
	{
		return m_text;
	}

	@Override
	public String toString()
	{
		return "Text   : " + m_text + "   Type : " + m_p1 + "   line  : " + m_line + "   cBeg. : " + m_charBegin + "   cEnd. : " + m_charEnd;
	}
}