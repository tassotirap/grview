package org.grview.lexical;

import java.io.IOException;

import org.grview.syntax.model.TableNode;

public interface Yylex
{

	/*
	 * this method can not be removed, it is used by the error recovery
	 * routines. Basically this method call the private method yypushbacck
	 * generated by the jflex.
	 */
	public abstract void pushback(int number);

	/*
	 * If the simbol in text is in TabT this method returs its index otherwise
	 * returns -1. The parse uses this method to know if the recognized token is
	 * an reserved symbol or not.
	 */
	public abstract int serchTabTSymbol(String text);

	public abstract void setReader(java.io.Reader in);

	public abstract void TabT(TableNode TbT[]);

	/**
	 * Enters a new lexical state
	 * 
	 * @param newState
	 *            the new lexical state
	 */
	public abstract void yybegin(int newState);

	/**
	 * Returns the character at position <tt>pos</tt> from the matched text.
	 * 
	 * It is equivalent to yytext().charAt(pos), but faster
	 * 
	 * @param pos
	 *            the position of the character to fetch. A value from 0 to
	 *            yylength()-1.
	 * 
	 * @return the character at position pos
	 */
	public abstract char yycharat(int pos);

	/**
	 * Closes the input stream.
	 */
	public abstract void yyclose() throws java.io.IOException;

	/**
	 * Returns the length of the matched text region.
	 */
	public abstract int yylength();

	/**
	 * Resumes scanning until the next regular expression is matched, the end of
	 * input is encountered or an I/O-Error occurs.
	 * 
	 * @return the next token
	 * @exception IOException
	 *                if any I/O-Error occurs
	 */
	public abstract Yytoken yylex() throws java.io.IOException;

	/**
	 * Closes the current stream, and resets the scanner to read from a new
	 * input stream.
	 * 
	 * All internal variables are reset, the old input stream <b>cannot</b> be
	 * reused (internal buffer is discarded and lost). Lexical state is set to
	 * <tt>YY_INITIAL</tt>.
	 * 
	 * @param reader
	 *            the new input stream
	 */
	public abstract void yyreset(java.io.Reader reader) throws java.io.IOException;

	/**
	 * Returns the current lexical state.
	 */
	public abstract int yystate();

	/**
	 * Returns the text matched by the current regular expression.
	 */
	public abstract String yytext();

}