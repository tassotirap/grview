package org.grview.output;

public class TokenOutput extends Output
{

	private static TokenOutput instance;

	private TokenOutput()
	{
		super();
	}

	public static TokenOutput getInstance()
	{
		if (instance == null)
		{
			instance = new TokenOutput();
		}
		return instance;
	}
}
