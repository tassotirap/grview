package org.grview.syntax.grammar;

import java.awt.Cursor;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.DefaultListModel;

import org.grview.output.AppOutput;
import org.grview.parser.ParsingEditor;
import org.grview.project.tree.FileTree;
import org.grview.semantics.SemanticRoutinesIvoker;
import org.grview.syntax.SyntacticLoader;
import org.grview.syntax.TabCreate;
import org.grview.syntax.analyzer.gsll1.Analyzer;
import org.grview.syntax.analyzer.gsll1.exportable.Exporter;
import org.grview.syntax.command.AsinEditor;
import org.grview.syntax.validation.GSLL1Rules;
import org.grview.syntax.validation.GrammarRule;
import org.grview.syntax.validation.InvalidGrammarException;
import org.grview.ui.debug.ErrorDialog;


public class Controller {
	
	public static void generateAndParseCurrentGrammar(boolean export) {
		AppOutput.clearOutputBuffer();
		AppOutput.clearStacks();
		GrammarFactory gf = new GrammarFactory(AsinEditor.getInstance());
		String grammar = null;
		boolean validated = false;
		try {
			grammar = gf.run(false);
			validated = (grammar != null && !grammar.equals(""));
		}
		catch (Exception ex) {
			validated = false;
			errorFound(ex);
		}
		Grammar absGrammar = gf.getAbsGrammar();
		if (absGrammar != null) {
			GrammarRule gr = new GSLL1Rules(absGrammar, false);
			try {
				gr.validate();
			}
			catch (InvalidGrammarException ex) {
				validated = false;
				errorFound(ex);
			}
		}
		if (validated) {
	
			TabCreate tc = new TabCreate(grammar, false);
			SyntacticLoader cs = new SyntacticLoader(tc);
			ParsingEditor pe = ParsingEditor.getInstance().build();
			SemanticRoutinesIvoker.getLastInstance().configureAndLoad();
			if (export) {
				try {
					new Exporter(cs, pe.getRootPath()).export();
					FileTree.reload(pe.getRootPath());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pe.setSyntacticLoader(cs);
		}
	}
	
	private static void errorFound(Exception ex) {
		ErrorDialog ed = new ErrorDialog(null);
		DefaultListModel model = new DefaultListModel();
		model.addElement(ex.getMessage());
		ed.getErrorList().setModel(model);
		ed.getTaErrorDescription().setText("No description available");
		ed.setVisible(true);
		AppOutput.clearGeneratedGrammar();
	}
}
