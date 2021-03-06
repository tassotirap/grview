package org.grview.file;

import java.io.File;
import java.io.IOException;

import org.grview.project.Project;
import org.grview.util.IOUtilities;

public class MetaFile extends File
{

	private static final String ORG_GRVIEW_PROJECT_NEW_METADATA = "/org/grview/project/new_metadata";

	private static final long serialVersionUID = 1L;

	public MetaFile(String pathname)
	{
		super(pathname);
	}

	public void create() throws IOException
	{
		if (!this.exists() && !this.createNewFile())
			throw new IOException("Could not create MetaFile");

		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream(ORG_GRVIEW_PROJECT_NEW_METADATA), this);
	}
}
