package org.grview.file;

import java.io.File;
import java.io.IOException;

import org.grview.project.Project;
import org.grview.util.IOUtilities;

public class SemanticFile extends File
{
	private static final long serialVersionUID = 1L;

	public SemanticFile(String pathname)
	{
		super(pathname);
	}

	public void createEmpty() throws IOException
	{
		if(!this.exists() && !this.createNewFile())
			throw new IOException("Could not create SemanticFile");
		
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/empty_semmantic"), this);		
	}

}
