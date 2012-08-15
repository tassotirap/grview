package org.grview.parser;

public abstract class ParserProxy extends java.util.Observable
{

	public abstract void parse();

	public abstract void print(Object obj);

	public abstract void println(Object obj);

}
