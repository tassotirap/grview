package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class AddRoutineCommand extends Command {

	public final static String id = SyntaxDefinitions.AddRoutineCommand;
	
	@Override
	public String getDescription() {
		return SyntaxDefinitions.AddRoutineCommand_Description + (((String)getContext() == null)?"":" "+(String)getContext());
	}

	@Override
	public String getID() {
		return id;
	}

}
