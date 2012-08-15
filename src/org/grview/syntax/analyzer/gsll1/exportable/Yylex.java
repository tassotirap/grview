package org.grview.syntax.analyzer.gsll1.exportable;

/* The following code was generated by JFlex 1.4.3 on 02/02/10 08:40 */

/*The line below can not be removed, otherwise the plugin do not works */

/**
 * This class is a scanner generated by <a href="http://www.jflex.de/">JFlex</a>
 * 1.4.3 on 02/02/10 08:40 from the specification file
 * <tt>C:/Users/Gustavo/Documents/Prog/workspace_gr100/Untitled.lex</tt>
 */
class Yylex
{

	/** This character denotes the end of file */
	public static final int YYEOF = -1;

	/** initial size of the lookahead buffer */
	private static final int ZZ_BUFFERSIZE = 16384;

	/** lexical states */
	public static final int YYINITIAL = 0;
	public static final int COMMENT = 2;

	/**
	 * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
	 * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l at the
	 * beginning of a line l is of the form l = 2*k, k a non negative integer
	 */
	private static final int ZZ_LEXSTATE[] = { 0, 0, 1, 1 };

	/**
	 * Translates characters to character classes
	 */
	private static final char[] ZZ_CMAP = { 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 5, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 7, 0, 0, 0, 25, 0, 14, 15, 9, 21, 11, 27, 20, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 12, 13, 23, 22, 24, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 16, 6, 17, 0, 10, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 18, 26, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	/**
	 * Translates DFA states to action switch labels.
	 */
	private static final int[] ZZ_ACTION = zzUnpackAction();

	private static final String ZZ_ACTION_PACKED_0 = "\1\0\1\1\1\2\1\3\1\4\1\1\1\5\1\2" + "\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15" + "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25" + "\1\26\1\27\1\2\3\1\2\2\2\0\1\30\1\31" + "\1\32\1\33\1\34\1\35\3\0\1\1\1\36\1\37" + "\1\1\2\0\1\30";

	/**
	 * Translates a state to a row index in the transition table
	 */
	private static final int[] ZZ_ROWMAP = zzUnpackRowMap();

	private static final String ZZ_ROWMAP_PACKED_0 = "\0\0\0\34\0\70\0\124\0\160\0\214\0\250\0\304" + "\0\340\0\70\0\70\0\374\0\70\0\70\0\70\0\70" + "\0\70\0\70\0\70\0\70\0\70\0\70\0\u0118\0\u0134" + "\0\70\0\70\0\u0150\0\u016c\0\u0188\0\70\0\u01a4\0\u01c0" + "\0\304\0\u01dc\0\70\0\70\0\70\0\70\0\70\0\70" + "\0\u0150\0\u01f8\0\u0214\0\u0230\0\70\0\70\0\u024c\0\u0268" + "\0\u0284\0\304";

	/**
	 * The transition table of the DFA
	 */
	private static final int[] ZZ_TRANS = zzUnpackTrans();

	private static final String ZZ_TRANS_PACKED_0 = "\1\3\1\4\1\5\1\6\1\7\1\6\1\3\1\10" + "\1\11\1\12\1\3\1\13\1\14\1\15\1\16\1\17" + "\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27" + "\1\30\1\31\1\32\1\33\4\34\1\35\1\36\2\34" + "\1\37\1\40\22\34\35\0\2\4\7\0\1\4\23\0" + "\1\5\34\0\1\6\1\7\1\6\31\0\3\7\26\0" + "\4\41\2\0\1\42\1\43\24\41\11\0\1\44\50\0" + "\1\45\33\0\1\46\1\0\1\47\31\0\1\50\7\0" + "\1\5\30\0\1\51\5\34\1\0\2\34\1\52\1\53" + "\27\34\1\36\2\34\1\52\1\53\27\34\1\0\2\34" + "\1\54\1\55\27\34\1\0\2\34\1\56\1\57\22\34" + "\3\41\1\60\2\61\1\42\1\62\24\41\5\34\1\0" + "\2\34\1\54\1\0\27\34\1\0\2\34\1\0\1\57" + "\27\34\1\0\2\34\1\54\1\53\27\34\1\0\2\34" + "\1\52\1\57\22\34\3\41\1\60\2\61\1\42\1\43" + "\24\41\3\0\3\61\1\41\25\0";

	/* error codes */
	private static final int ZZ_UNKNOWN_ERROR = 0;

	private static final int ZZ_NO_MATCH = 1;

	private static final int ZZ_PUSHBACK_2BIG = 2;

	/* error messages for the codes above */
	private static final String ZZ_ERROR_MSG[] = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };

	/**
	 * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
	 */
	private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();

	private static final String ZZ_ATTRIBUTE_PACKED_0 = "\1\0\1\1\1\11\6\1\2\11\1\1\12\11\2\1" + "\2\11\3\1\1\11\2\1\2\0\6\11\3\0\1\1" + "\2\11\1\1\2\0\1\1";

	/** the input device */
	private java.io.Reader zzReader;
	/** the current state of the DFA */
	private int zzState;
	/** the current lexical state */
	private int zzLexicalState = YYINITIAL;

	/**
	 * this buffer contains the current text to be matched and is the source of
	 * the yytext() string
	 */
	private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

	/** the textposition at the last accepting state */
	private int zzMarkedPos;

	/** the current text position in the buffer */
	private int zzCurrentPos;

	/** startRead marks the beginning of the yytext() string in the buffer */
	private int zzStartRead;

	/**
	 * endRead marks the last character in the buffer, that has been read from
	 * input
	 */
	private int zzEndRead;

	/** number of newlines encountered up to the start of the matched text */
	private int yyline;

	/** the number of characters up to the start of the matched text */
	private int yychar;

	/**
	 * the number of characters from the last newline up to the start of the
	 * matched text
	 */
	private int yycolumn;

	/**
	 * zzAtBOL == true <=> the scanner is currently at the beginning of a line
	 */
	private boolean zzAtBOL = true;

	/** zzAtEOF == true <=> the scanner is at the EOF */
	private boolean zzAtEOF;

	/** denotes if the user-EOF-code has already been executed */
	private boolean zzEOFDone;

	/* user code: */
	private int comment_count = 0;

	/* The two lines below can not be removed or edited */
	private Analyzer.TabNode TabT[];

	/**
	 * Creates a new scanner. There is also java.io.Reader version of this
	 * constructor.
	 * 
	 * @param in
	 *            the java.io.Inputstream to read input from.
	 */
	Yylex(java.io.InputStream in)
	{
		this(new java.io.InputStreamReader(in));
	}

	/**
	 * Creates a new scanner There is also a java.io.InputStream version of this
	 * constructor.
	 * 
	 * @param in
	 *            the java.io.Reader to read input from.
	 */
	Yylex(java.io.Reader in)
	{
		this.zzReader = in;
	}

	/* An empty constructor, you'd have to set the reader anyway */
	public Yylex()
	{
	}

	private static int[] zzUnpackAction()
	{
		int[] result = new int[50];
		int offset = 0;
		offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackAction(String packed, int offset, int[] result)
	{
		int i = 0; /* index in packed string */
		int j = offset; /* index in unpacked array */
		int l = packed.length();
		while (i < l)
		{
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			do
				result[j++] = value;
			while (--count > 0);
		}
		return j;
	}

	private static int[] zzUnpackAttribute()
	{
		int[] result = new int[50];
		int offset = 0;
		offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackAttribute(String packed, int offset, int[] result)
	{
		int i = 0; /* index in packed string */
		int j = offset; /* index in unpacked array */
		int l = packed.length();
		while (i < l)
		{
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			do
				result[j++] = value;
			while (--count > 0);
		}
		return j;
	}

	private static int[] zzUnpackRowMap()
	{
		int[] result = new int[50];
		int offset = 0;
		offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackRowMap(String packed, int offset, int[] result)
	{
		int i = 0; /* index in packed string */
		int j = offset; /* index in unpacked array */
		int l = packed.length();
		while (i < l)
		{
			int high = packed.charAt(i++) << 16;
			result[j++] = high | packed.charAt(i++);
		}
		return j;
	}

	private static int[] zzUnpackTrans()
	{
		int[] result = new int[672];
		int offset = 0;
		offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackTrans(String packed, int offset, int[] result)
	{
		int i = 0; /* index in packed string */
		int j = offset; /* index in unpacked array */
		int l = packed.length();
		while (i < l)
		{
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			value--;
			do
				result[j++] = value;
			while (--count > 0);
		}
		return j;
	}

	/**
	 * Refills the input buffer.
	 * 
	 * @return <code>false</code>, iff there was new input.
	 * 
	 * @exception java.io.IOException
	 *                if any I/O-Error occurs
	 */
	private boolean zzRefill() throws java.io.IOException
	{

		/* first: make room (if you can) */
		if (zzStartRead > 0)
		{
			System.arraycopy(zzBuffer, zzStartRead, zzBuffer, 0, zzEndRead - zzStartRead);

			/* translate stored positions */
			zzEndRead -= zzStartRead;
			zzCurrentPos -= zzStartRead;
			zzMarkedPos -= zzStartRead;
			zzStartRead = 0;
		}

		/* is the buffer big enough? */
		if (zzCurrentPos >= zzBuffer.length)
		{
			/* if not: blow it up */
			char newBuffer[] = new char[zzCurrentPos * 2];
			System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
			zzBuffer = newBuffer;
		}

		/* finally: fill the buffer with new input */
		int numRead = zzReader.read(zzBuffer, zzEndRead, zzBuffer.length - zzEndRead);

		if (numRead > 0)
		{
			zzEndRead += numRead;
			return false;
		}
		// unlikely but not impossible: read 0 characters, but not at end of
		// stream
		if (numRead == 0)
		{
			int c = zzReader.read();
			if (c == -1)
			{
				return true;
			}
			else
			{
				zzBuffer[zzEndRead++] = (char) c;
				return false;
			}
		}

		// numRead < 0
		return true;
	}

	/**
	 * Reports an error that occured while scanning.
	 * 
	 * In a wellformed scanner (no or only correct usage of yypushback(int) and
	 * a match-all fallback rule) this method will only be called with things
	 * that "Can't Possibly Happen". If this method is called, something is
	 * seriously wrong (e.g. a JFlex bug producing a faulty scanner etc.).
	 * 
	 * Usual syntax/scanner level error handling should be done in error
	 * fallback rules.
	 * 
	 * @param errorCode
	 *            the code of the errormessage to display
	 */
	private void zzScanError(int errorCode)
	{
		String message;
		try
		{
			message = ZZ_ERROR_MSG[errorCode];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
		}

		throw new Error(message);
	}

	/*
	 * this method can not be removed, it is used by the error recovery
	 * routines. Basically this method call the private method yypushbacck
	 * generated by the jflex.
	 */
	public void pushback(int number)
	{
		this.yypushback(number);
	}

	/*
	 * If the simbol in text is in TabT this method returs its index otherwise
	 * returns -1. The parse uses this method to know if the recognized token is
	 * an reserved symbol or not.
	 */
	public int serchTabTSymbol(String text)
	{
		int index = -1;
		int i = 1;
		while (TabT[i] != null)
		{
			if ((TabT[i].getName()).equals(text))
			{
				index = i;
				break;
			}
			i++;
		}
		return index;
	}

	public void setReader(java.io.Reader in)
	{
		zzReader = in;
	}

	public void TabT(Analyzer.TabNode TbT[])
	{
		TabT = TbT;
	}

	/**
	 * Enters a new lexical state
	 * 
	 * @param newState
	 *            the new lexical state
	 */
	public final void yybegin(int newState)
	{
		zzLexicalState = newState;
	}

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
	public final char yycharat(int pos)
	{
		return zzBuffer[zzStartRead + pos];
	}

	/**
	 * Closes the input stream.
	 */
	public final void yyclose() throws java.io.IOException
	{
		zzAtEOF = true; /* indicate end of file */
		zzEndRead = zzStartRead; /* invalidate buffer */

		if (zzReader != null)
			zzReader.close();
	}

	/**
	 * Returns the length of the matched text region.
	 */
	public final int yylength()
	{
		return zzMarkedPos - zzStartRead;
	}

	/**
	 * Resumes scanning until the next regular expression is matched, the end of
	 * input is encountered or an I/O-Error occurs.
	 * 
	 * @return the next token
	 * @exception java.io.IOException
	 *                if any I/O-Error occurs
	 */
	public Yytoken yylex() throws java.io.IOException
	{
		int zzInput;
		int zzAction;

		// cached fields:
		int zzCurrentPosL;
		int zzMarkedPosL;
		int zzEndReadL = zzEndRead;
		char[] zzBufferL = zzBuffer;
		char[] zzCMapL = ZZ_CMAP;

		int[] zzTransL = ZZ_TRANS;
		int[] zzRowMapL = ZZ_ROWMAP;
		int[] zzAttrL = ZZ_ATTRIBUTE;

		while (true)
		{
			zzMarkedPosL = zzMarkedPos;

			yychar += zzMarkedPosL - zzStartRead;

			boolean zzR = false;
			for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL; zzCurrentPosL++)
			{
				switch (zzBufferL[zzCurrentPosL])
				{
					case '\u000B':
					case '\u000C':
					case '\u0085':
					case '\u2028':
					case '\u2029':
						yyline++;
						zzR = false;
						break;
					case '\r':
						yyline++;
						zzR = true;
						break;
					case '\n':
						if (zzR)
							zzR = false;
						else
						{
							yyline++;
						}
						break;
					default:
						zzR = false;
				}
			}

			if (zzR)
			{
				// peek one character ahead if it is \n (if we have counted one
				// line too much)
				boolean zzPeek;
				if (zzMarkedPosL < zzEndReadL)
					zzPeek = zzBufferL[zzMarkedPosL] == '\n';
				else if (zzAtEOF)
					zzPeek = false;
				else
				{
					boolean eof = zzRefill();
					zzEndReadL = zzEndRead;
					zzMarkedPosL = zzMarkedPos;
					zzBufferL = zzBuffer;
					if (eof)
						zzPeek = false;
					else
						zzPeek = zzBufferL[zzMarkedPosL] == '\n';
				}
				if (zzPeek)
					yyline--;
			}
			zzAction = -1;

			zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

			zzState = ZZ_LEXSTATE[zzLexicalState];

			zzForAction:
			{
				while (true)
				{

					if (zzCurrentPosL < zzEndReadL)
						zzInput = zzBufferL[zzCurrentPosL++];
					else if (zzAtEOF)
					{
						zzInput = YYEOF;
						break zzForAction;
					}
					else
					{
						// store back cached positions
						zzCurrentPos = zzCurrentPosL;
						zzMarkedPos = zzMarkedPosL;
						boolean eof = zzRefill();
						// get translated positions and possibly new buffer
						zzCurrentPosL = zzCurrentPos;
						zzMarkedPosL = zzMarkedPos;
						zzBufferL = zzBuffer;
						zzEndReadL = zzEndRead;
						if (eof)
						{
							zzInput = YYEOF;
							break zzForAction;
						}
						else
						{
							zzInput = zzBufferL[zzCurrentPosL++];
						}
					}
					int zzNext = zzTransL[zzRowMapL[zzState] + zzCMapL[zzInput]];
					if (zzNext == -1)
						break zzForAction;
					zzState = zzNext;

					int zzAttributes = zzAttrL[zzState];
					if ((zzAttributes & 1) == 1)
					{
						zzAction = zzState;
						zzMarkedPosL = zzCurrentPosL;
						if ((zzAttributes & 8) == 8)
							break zzForAction;
					}

				}
			}

			// store back cached position
			zzMarkedPos = zzMarkedPosL;

			switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
			{
				case 18:
				{
					return (new Yytoken("+", yytext(), yyline, yychar, yychar + 1));
				}
				case 32:
					break;
				case 28:
				{
					return (new Yytoken("<>", yytext(), yyline, yychar, yychar + 2));
				}
				case 33:
					break;
				case 9:
				{
					return (new Yytoken(":", yytext(), yyline, yychar, yychar + 1));
				}
				case 34:
					break;
				case 5:
				{
					return (new Yytoken("Esp", yytext(), yyline, yychar, yychar + yylength()));
				}
				case 35:
					break;
				case 19:
				{
					return (new Yytoken("=", yytext(), yyline, yychar, yychar + 1));
				}
				case 36:
					break;
				case 2:
				{
					System.out.println("Illegal character: <" + yytext() + ">");
				}
				case 37:
					break;
				case 8:
				{
					return (new Yytoken(",", yytext(), yyline, yychar, yychar + 1));
				}
				case 38:
					break;
				case 4:
				{
					return (new Yytoken("Numb", yytext(), yyline, yychar, yychar + yylength()));
				}
				case 39:
					break;
				case 10:
				{
					return (new Yytoken(";", yytext(), yyline, yychar, yychar + 1));
				}
				case 40:
					break;
				case 13:
				{
					return (new Yytoken("[", yytext(), yyline, yychar, yychar + 1));
				}
				case 41:
					break;
				case 21:
				{
					return (new Yytoken(">", yytext(), yyline, yychar, yychar + 1));
				}
				case 42:
					break;
				case 3:
				{
					int ret = serchTabTSymbol(yytext());
					/* the token is not a reserved symbol, then return IDEN */
					if (ret == -1)
					{
						return (new Yytoken("Iden", yytext(), yyline, yychar, yychar + yylength()));
					}
					/* the token is a reserved symbol */
					else
					{
						return (new Yytoken("Res", yytext(), yyline, yychar, yychar + yylength()));
					}
				}
				case 43:
					break;
				case 15:
				{
					return (new Yytoken("{", yytext(), yyline, yychar, yychar + 1));
				}
				case 44:
					break;
				case 25:
				{
					yybegin(COMMENT);
					comment_count++;
				}
				case 45:
					break;
				case 31:
				{
					if (--comment_count == 0)
						yybegin(YYINITIAL);
				}
				case 46:
					break;
				case 22:
				{
					return (new Yytoken("&", yytext(), yyline, yychar, yychar + 1));
				}
				case 47:
					break;
				case 29:
				{
					return (new Yytoken(">=", yytext(), yyline, yychar, yychar + 2));
				}
				case 48:
					break;
				case 20:
				{
					return (new Yytoken("<", yytext(), yyline, yychar, yychar + 1));
				}
				case 49:
					break;
				case 12:
				{
					return (new Yytoken(")", yytext(), yyline, yychar, yychar + 1));
				}
				case 50:
					break;
				case 23:
				{
					return (new Yytoken("|", yytext(), yyline, yychar, yychar + 1));
				}
				case 51:
					break;
				case 26:
				{
					return (new Yytoken(":=", yytext(), yyline, yychar, yychar + 2));
				}
				case 52:
					break;
				case 17:
				{
					return (new Yytoken(".", yytext(), yyline, yychar, yychar + 1));
				}
				case 53:
					break;
				case 30:
				{
					comment_count++;
				}
				case 54:
					break;
				case 24:
				{
					String str = yytext().substring(1, yylength() - 1);
					return (new Yytoken("String", str, yyline, yychar, yychar + yylength()));
				}
				case 55:
					break;
				case 14:
				{
					return (new Yytoken("]", yytext(), yyline, yychar, yychar + 1));
				}
				case 56:
					break;
				case 7:
				{
					return (new Yytoken("*", yytext(), yyline, yychar, yychar + 1));
				}
				case 57:
					break;
				case 16:
				{
					return (new Yytoken("}", yytext(), yyline, yychar, yychar + 1));
				}
				case 58:
					break;
				case 27:
				{
					return (new Yytoken("<=", yytext(), yyline, yychar, yychar + 2));
				}
				case 59:
					break;
				case 6:
				{
					return (new Yytoken("/", yytext(), yyline, yychar, yychar + 1));
				}
				case 60:
					break;
				case 1:
				{
				}
				case 61:
					break;
				case 11:
				{
					return (new Yytoken("(", yytext(), yyline, yychar, yychar + 1));
				}
				case 62:
					break;
				default:
					if (zzInput == YYEOF && zzStartRead == zzCurrentPos)
					{
						zzAtEOF = true;
						{
							return (new Yytoken("EOF", "$", 1, 1, 1));
						}
					}
					else
					{
						zzScanError(ZZ_NO_MATCH);
					}
			}
		}
	}

	/**
	 * Pushes the specified amount of characters back into the input stream.
	 * 
	 * They will be read again by then next call of the scanning method
	 * 
	 * @param number
	 *            the number of characters to be read again. This number must
	 *            not be greater than yylength()!
	 */
	public void yypushback(int number)
	{
		if (number > yylength())
			zzScanError(ZZ_PUSHBACK_2BIG);

		zzMarkedPos -= number;
	}

	/**
	 * Resets the scanner to read from a new input stream. Does not close the
	 * old reader.
	 * 
	 * All internal variables are reset, the old input stream <b>cannot</b> be
	 * reused (internal buffer is discarded and lost). Lexical state is set to
	 * <tt>ZZ_INITIAL</tt>.
	 * 
	 * @param reader
	 *            the new input stream
	 */
	public final void yyreset(java.io.Reader reader)
	{
		zzReader = reader;
		zzAtBOL = true;
		zzAtEOF = false;
		zzEOFDone = false;
		zzEndRead = zzStartRead = 0;
		zzCurrentPos = zzMarkedPos = 0;
		yyline = yychar = yycolumn = 0;
		zzLexicalState = YYINITIAL;
	}

	/**
	 * Returns the current lexical state.
	 */
	public final int yystate()
	{
		return zzLexicalState;
	}

	/**
	 * Returns the text matched by the current regular expression.
	 */
	public final String yytext()
	{
		return new String(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
	}

}
