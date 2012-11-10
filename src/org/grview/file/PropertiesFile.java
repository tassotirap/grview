package org.grview.file;

import java.io.File;
import java.io.IOException;

import org.grview.project.Project;
import org.grview.util.IOUtilities;

public class PropertiesFile extends File
{
	private static final String ORG_GRVIEW_PROJECT_DEFAULT_PROPERTIES_XML = "/org/grview/project/default_properties.xml";
	private static final long serialVersionUID = 1L;

	public PropertiesFile(String pathname)
	{
		super(pathname);
	}

	public void create() throws IOException
	{
		if (!this.exists() && !this.createNewFile())
			throw new IOException("Could not create PropertiesFile");

		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream(ORG_GRVIEW_PROJECT_DEFAULT_PROPERTIES_XML), this);
	}
}
