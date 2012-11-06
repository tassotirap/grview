package org.grview.file;

import java.io.File;
import java.io.IOException;

import org.grview.canvas.Canvas;
import org.grview.project.Project;
import org.grview.project.ProjectMediator;
import org.grview.ui.component.GrammarComponent;
import org.grview.ui.component.GrammarFactory;
import org.grview.util.IOUtilities;

public class GrammarFile extends File
{
	private static final long serialVersionUID = 1L;

	public GrammarFile(String pathname)
	{
		super(pathname);
	}
	
	public void createEmpty() throws IOException
	{
		if(!this.exists() && !this.createNewFile())
			throw new IOException("Could not create GrammarFile");
		
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/empty_grammar"), this);
	}
	
	public String saveGrammarFile(Canvas canvas)
	{
		GrammarComponent gramComponent = GrammarFactory.getCompByCanvas();
		if (ProjectMediator.hasUnsavedView(gramComponent.getPath()))
		{
			gramComponent.saveFile();

			return gramComponent.getPath();
		}
		return null;
	}

}
