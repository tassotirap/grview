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
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String line;
				while ((line = br.readLine()) != null)
				{
					sb.append(line + "\n");
				}
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
