package org.grview.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.grview.model.FileExtension;
import org.grview.project.ProjectManager;
import org.grview.util.LangHelper;
import org.grview.util.Log;


public class NewFileWizard {

	private final static String img_path = "/org/grview/images/";
	
	private final static String newLex = img_path + "new-file-lex.png";
	private final static String newSem = img_path + "new-file-sem.png";
	private final static String newGram = img_path + "new-file-gram.png";
	private final static String newTxt = img_path + "new-file-txt.png";
	private final static String newIn = img_path + "new-file-in.png";
	private final static String newOut = img_path + "new-file-out.png";
	
	private FileEntry[] entrys = new FileEntry[] {
			new FileEntry(LangHelper.new_gram, newGram, new FileExtension(FileExtension.GRAM_FILE)),
			new FileEntry(LangHelper.new_sem, newSem, new FileExtension(FileExtension.SEM_FILE)),
			new FileEntry(LangHelper.new_lex, newLex, new FileExtension(FileExtension.LEX_FILE)),
			new FileEntry(LangHelper.new_txt, newTxt, new FileExtension(FileExtension.TXT_FILE)),
			new FileEntry(LangHelper.new_in, newIn, new FileExtension(FileExtension.IN_FILE)),
			new FileEntry(LangHelper.new_out, newOut, new FileExtension(FileExtension.OUT_FILE))
	};
	
	public HashMap<String, String> descByName = new HashMap<String, String>();
	
	public NewFileWizard(ProjectManager pManager) {
		final ProjectManager projectManager = pManager;
		descByName.put(LangHelper.new_gram, LangHelper.new_gram_desc);
		descByName.put(LangHelper.new_sem, LangHelper.new_sem_desc);
		descByName.put(LangHelper.new_lex, LangHelper.new_lex_desc);
		descByName.put(LangHelper.new_txt, LangHelper.new_txt_desc);
		descByName.put(LangHelper.new_in, LangHelper.new_in_desc);
		descByName.put(LangHelper.new_out, LangHelper.new_out_desc);
		final NewFileWizardWindow nfww = new NewFileWizardWindow(entrys, descByName);
		nfww.getOkButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FileEntry entry = (FileEntry) nfww.getJList().getSelectedValue();
				try {
					projectManager.createFile(nfww.getJTextField().getText(), entry.getExtension());
					nfww.setVisible(false);
				}
				catch (IOException ex) {
					JOptionPane.showMessageDialog(null, "Could not create file!", "Error", JOptionPane.ERROR_MESSAGE);
					Log.log(Log.ERROR, this, "An IOException was thrown while trying to create a new file.", ex);
				}
			}
		});
		nfww.setVisible(true);
	}
	
	public class FileEntry {
		  private final String title;
		  private final String imagePath;
		  private final FileExtension extension;
		  private ImageIcon image;

		  public FileEntry(String title, String imagePath, FileExtension extension) {
		    this.title = title;
		    this.imagePath = imagePath;
		    this.extension = extension;
		  }

		  public String getTitle() {
		    return title;
		  }

		  public ImageIcon getImage() {
		    if (image == null) {
		      image = new ImageIcon(getClass().getResource(imagePath));
		    }
		    return image;
		  }
		  
		  public FileExtension getExtension() {
			  return extension;
		  }
	}
	
}
