package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class DisconnectCommand extends Command {

	@Override
	public String getID() {
		return SyntaxDefinitions.DisconnectionCommand;
	}
	
	@Override
	public String getDescription() {
		return SyntaxDefinitions.DisconnectionCommand_Description + getContext().toString();
	}
	
	@Override
	public boolean addObject(Object target, Object context) {
		return false;
	}
	
	@Override
	public boolean addObject(Object source, Object target, Object context) {
		return false;
	}
	
	@Override
	public boolean addObject(Object source, Object target, Object connector, Object context) {
		try {
			String s = (String)source;
			String t = (String)target;
			String cn = (String)connector;
			String co = (String)context;
			return super.addObject(s,t,cn,co);
		}
		catch (Exception e) {
			//ERRO
			return false;
		}
	}
	

}
