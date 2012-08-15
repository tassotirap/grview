package org.grview.util;

/**
 * This exception should refer to an unexpected format on a given file.
 * 
 * @author Gustavo Braga
 * 
 */
public class UnexpectedFormatException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnexpectedFormatException(String message)
	{
		super(message);
	}
}
