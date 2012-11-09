package org.grview.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.grview.model.FileNames;
import org.grview.project.ProjectManager;
import org.grview.util.LangHelper;
import org.grview.util.Log;

public class NewFileWizard
{

	private final static String img_path = "/org/grview/images/";

	private final static String newGram = img_path + "new-file-gram.png";
	private final static String newIn = img_path + "new-file-in.png";
	private final static String newLex = img_path + "new-file-lex.png";
	private final static String newOut = img_path + "new-file-out.png";
	private final static String newSem = img_path + "new-file-sem.png";
	private final static String newTxt = img_path + "new-file-txt.png";

	private final ProjectManager projectMediator;

	private FileEntry[] entrys = new FileEntry[]{ new FileEntry(LangHelper.new_gram, newGram, new FileNames(FileNames.GRAM_EXTENSION)), new FileEntry(LangHelper.new_sem, newSem, new FileNames(FileNames.SEM_EXTENSION)), new FileEntry(LangHelper.new_lex, newLex, new FileNames(FileNames.LEX_EXTENSION)), new FileEntry(LangHelper.new_txt, newTxt, new FileNames(FileNames.TXT_EXTENSION)), new FileEntry(LangHelper.new_in, newIn, new FileNames(FileNames.IN_EXTENSION)), new FileEntry(LangHelper.new_out, newOut, new FileNames(FileNames.OUT_EXTENSION)) };

	public HashMap<String, String> descByName = new HashMap<String, String>();

	public NewFileWizard(ProjectManager projectMediator)
	{
		this.projectMediator = projectMediator;
		descByName.put(LangHelper.new_gram, LangHelper.new_gram_desc);
		descByName.put(LangHelper.new_sem, LangHelper.new_sem_desc);
		descByName.put(LangHelper.new_lex, LangHelper.new_lex_desc);
		descByName.put(LangHelper.new_txt, LangHelper.new_txt_desc);
		descByName.put(LangHelper.new_in, LangHelper.new_in_desc);
		descByName.put(LangHelper.new_out, LangHelper.new_out_desc);
		CreateNewFileWizardWindow();
	}

	private void CreateNewFileWizardWindow()
	{
		final NewFileWizardWindow newFileWizardWindow = new NewFileWizardWindow(entrys, descByName);
		newFileWizardWindow.getOkButton().addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				FileEntry entry = (FileEntry) newFileWizardWindow.getJList().getSelectedValue();
				try
				{
					projectMediator.createFile(newFileWizardWindow.getJTextField().getText(), entry.getExtension());
					newFileWizardWindow.setVisible(false);
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(null, "Could not create file!", "Error", JOptionPane.ERROR_MESSAGE);
					Log.log(Log.ERROR, this, "An IOException was thrown while trying to create a new file.", ex);
				}
			}
		});
		newFileWizardWindow.setVisible(true);
	}
}
