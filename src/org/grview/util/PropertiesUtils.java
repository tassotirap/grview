package org.grview.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.grview.editor.TextArea;

public class PropertiesUtils
{

	/**
	 * Loads properties from a file
	 * 
	 * @param fileName
	 *            the name of the file containing the properties
	 * @param isXML
	 *            whether is a XML file or not
	 * **/
	public static Properties loadProperties(String fileName, boolean isXML)
	{
		Properties props = new Properties();
		FileInputStream in = null;
		try
		{
			in = new FileInputStream(new File(fileName));
			if (!isXML)
			{
				props.load(in);
			}
			else
			{
				props.loadFromXML(in);

			}
		}
		catch (InvalidPropertiesFormatException e)
		{

		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, TextArea.class, e);
		}
		finally
		{
			IOUtilities.closeQuietly(in);
		}
		return props;
	}

	/**
	 * Store properties in a file
	 * 
	 * @param fileName
	 *            the file that will store the properties
	 * @param properties
	 *            the properties that will be stored
	 * @param comments
	 *            additional and optional comments
	 * @param isXML
	 *            whether is a XML file or not
	 */
	public static void storeProperties(String fileName, Properties properties, String comments, boolean isXML)
	{

		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(new File(fileName));
			if (!isXML)
			{
				properties.store(out, comments);
			}
			else
			{
				properties.storeToXML(out, comments, "UTF-8");
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, TextArea.class, e);
		}
		catch (Exception e)
		{

		}
		finally
		{
			IOUtilities.closeQuietly(out);
		}
	}

}
