package org.grview.file;

import java.io.File;
import java.io.IOException;

import org.grview.project.Project;
import org.grview.util.IOUtilities;

public class LexicalFile extends File
{
	private static final long serialVersionUID = 1L;

	public LexicalFile(String pathname)
	{
		super(pathname);
	}

	public void createEmpty() throws IOException
	{
		if(!this.exists() && !this.createNewFile())
			throw new IOException("Could not create LexicalFile");
		
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/empty_lex"), this);		
	}

}
