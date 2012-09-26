package org.grview.syntax.grammar;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.DefaultListModel;

import org.grview.lexical.YyFactory;
import org.grview.output.AppOutput;
import org.grview.parser.ParsingEditor;
import org.grview.project.ProjectManager;
import org.grview.project.tree.FileTree;
import org.grview.semantics.SemanticRoutinesIvoker;
import org.grview.syntax.SyntacticLoader;
import org.grview.syntax.TableCreate;
import org.grview.syntax.analyzer.gsll1.exportable.Exporter;
import org.grview.syntax.validation.GSLL1Rules;
import org.grview.syntax.validation.GrammarRule;
import org.grview.syntax.validation.InvalidGrammarException;
import org.grview.ui.debug.ErrorDialog;

public class Controller
{

	private static void errorFound(Exception ex)
	{
		ErrorDialog ed = new ErrorDialog(null);
		DefaultListModel model = new DefaultListModel();
		model.addElement(ex.getMessage());
		ed.getErrorList().setModel(model);
		ed.getTaErrorDescription().setText("No description available");
		ed.setVisible(true);
		AppOutput.clearGeneratedGrammar();
	}

	public static void generateAndParseCurrentGrammar(boolean export)
	{
		YyFactory.createYylex(ProjectManager.getProject().getLexFile().getParent(), "generated_code", ProjectManager.getProject().getLexFile().getPath());
		AppOutput.clearOutputBuffer();
		AppOutput.clearStacks();
		GrammarFactory grammarFactory = new GrammarFactory();
		String grammar = null;
		boolean validated = false;
		try
		{
			grammar = grammarFactory.run(false);
			validated = (grammar != null && !grammar.equals(""));
		}
		catch (Exception ex)
		{
			validated = false;
			errorFound(ex);
		}
		Grammar absGrammar = grammarFactory.getGrammar();
		if (absGrammar != null)
		{
			GrammarRule gr = new GSLL1Rules(absGrammar, false);
			try
			{
				gr.validate();
			}
			catch (InvalidGrammarException ex)
			{
				validated = false;
				errorFound(ex);
			}
		}
		if (validated)
		{

			TableCreate tableCreate = new TableCreate(grammar, false);
			SyntacticLoader syntacticLoader = new SyntacticLoader(tableCreate);
			ParsingEditor parsingEditor = ParsingEditor.getInstance().build();
			SemanticRoutinesIvoker.getLastInstance().configureAndLoad();
			if (export)
			{
				try
				{
					new Exporter(syntacticLoader, parsingEditor.getRootPath()).export();
					FileTree.reload(parsingEditor.getRootPath());
				}
				catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			parsingEditor.setSyntacticLoader(syntacticLoader);
		}
	}
}
