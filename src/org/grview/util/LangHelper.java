package org.grview.util;

public interface LangHelper
{

	String select = "Select";
	String successor = "Successor";
	String alternative = "Alternative";
	String left_hand = "Left Hand";
	String n_terminal = "Non Terminal";
	String terminal = "Terminal";
	String lambda_alternative = "Lambda Alternative";
	String label = "Text";
	String start = "Initial Non Terminal";

	String _open = "Open";
	String open_file = "Open File";
	String open_project = "Open Project";
	String _new = "New";
	String new_file = "New File";
	String new_project = "New Project";
	String new_lex = "Lexical File";
	String new_sem = "Semantic Routines File";
	String new_gram = "Grammar Graph File";
	String new_txt = "Text File";
	String new_in = "Input Adapter";
	String new_out = "Output Adapter";
	String save = "Save";
	String save_all = "Save All";
	String save_as = "Save As";
	String print = "Print";
	String undo = "Undo";
	String redo = "Redo";
	String copy = "Copy";
	String cut = "Cut";
	String paste = "Paste";

	String build = "Build";
	String zoom_plus = "Zoom +";
	String zoom_minus = "Zoom -";

	String last = "Last Node";
	String next_suc = "Next Successor";
	String next_alt = "Next Alternative";
	String follow = "Follow Non Terminal";
	String home = "First Left Hand";

	String split_horizontal = "Split Horizontally";
	String split_vertival = "Split Vertical";
	String unsplit = "Unsplit All";

	String options = "Options";
	String help = "Help";

	String new_gram_desc = "Used to store a graph that represents a grammar.";
	String new_sem_desc = "Contains various semantic routines that may be applied at parsing.";
	String new_lex_desc = "A new lexical rules file, mostly in yylex format.";
	String new_txt_desc = "A plain text file.";
	String new_in_desc = "An adapter that translates calls from an external component to the equivalent parser inputs.";
	String new_out_desc = "An adapter that translates the output of the parser to an external component";

}
