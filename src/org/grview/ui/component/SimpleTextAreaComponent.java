package org.grview.ui.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SimpleTextAreaComponent extends AbstractComponent
{

	/**
	 * Creates a view component containing a simple text area.
	 * 
	 * @param param
	 *            if it is a string instance, then the text is inserted in the
	 *            text area
	 * @return the view component
	 */
	@Override
	public JComponent create(Object param)
	{
		StringBuffer sb = new StringBuffer();
		if (param instanceof String)
		{
			File f = new File((String) param);
			try
			{
				FileReader fileReader = new FileReader(f);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line;
				while ((line = bufferedReader.readLine()) != null)
				{
					sb.append(line + "\n");
				}
				bufferedReader.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return new JScrollPane(new JTextArea(sb.toString()));
	}

	@Override
	public void fireContentChanged()
	{
	}
}
