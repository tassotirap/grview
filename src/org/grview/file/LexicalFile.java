package org.grview.file;

import java.io.File;
import java.io.IOException;

import org.grview.project.Project;
import org.grview.util.IOUtilities;

public class LexicalFile extends File
{
	private static final String ORG_GRVIEW_PROJECT_EMPTY_LEX = "/org/grview/project/empty_lex";
	private static final long serialVersionUID = 1L;

	public LexicalFile(String pathname)
	{
		super(pathname);
	}

	public void create() throws IOException
	{
		if (!this.exists() && !this.createNewFile())
			throw new IOException("Could not create Lexical File");

		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream(ORG_GRVIEW_PROJECT_EMPTY_LEX), this);
	}
}
