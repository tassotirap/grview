package org.grview.ui.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JComponent;

import org.grview.actions.Mode;
import org.grview.editor.StandaloneTextArea;
import org.grview.editor.TextArea;
import org.grview.editor.TextAreaBufferListener;
import org.grview.editor.syntax.ModeProvider;
import org.grview.util.Log;


public class AdvancedTextAreaComponent extends Component implements FileComponent {

	protected TextArea ta;
	protected String path;
	protected String rootPath;

	public AdvancedTextAreaComponent(String type) {
		String typeDef = "text";
		if (type != null) {
			typeDef = type;
		}
		ta = StandaloneTextArea.createTextArea();
		Mode mode = new Mode(typeDef);
		mode.setProperty("file","modes/" + typeDef + ".xml");
		ModeProvider.instance.addMode(mode);
		ta.getBuffer().setMode(mode);
		TextAreaRepo.register(this, ta);
	}

	@Override
	public JComponent create(Object param) throws BadParameterException {
		if (param instanceof String) {
			path = (String)param;
			File file = new File(path);
			rootPath = file.getParent();
			TextAreaBufferListener tabf = new TextAreaBufferListener(this);
			try {
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader in = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(in);
				String line;
				while((line = br.readLine()) != null)
					ta.getBuffer().insert(ta.getText().length(), line + "\n");
				ta.getBuffer().addBufferListener(tabf);
				br.close();
				in.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			throw new BadParameterException("A reference to a File was expected.");
		}
		return ta;
	}
	
	@Override
	public void fireContentChanged() {
		for (ComponentListener listener : listeners) {
			listener.ContentChanged(this, null, null);
		}
	}

	@Override
	public String getPath() {
		return path;
	}

	public TextArea getTextArea() {
		return ta;
	}
	
	@Override
	public void saveFile() {
		File file = new File(path);
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(ta.getText());
			fw.close();
		} catch (IOException e) {
			Log.log(Log.ERROR, null, "Could not save file!", e);
		}
	}

}
