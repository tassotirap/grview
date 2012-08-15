package org.grview.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.grview.actions.Mode;
import org.grview.editor.StandaloneTextArea;
import org.grview.editor.TextArea;
import org.grview.editor.buffer.BufferListener;
import org.grview.editor.buffer.JEditBuffer;
import org.grview.lexical.YyFactory;
import org.grview.lexical.Yylex;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.output.Output;
import org.grview.syntax.SyntacticLoader;
import org.grview.syntax.analyzer.gsll1.Analyzer;
import org.grview.util.Log;

public class ParsingEditor implements BufferListener, CaretListener
{

	private SyntacticLoader cs;
	private StandaloneTextArea sta;
	private StringBuffer newText;
	private int lastLine;
	private String rootPath;
	private Yylex lex;
	private ArrayList<JButton> parsingButtons;
	private Thread parsingThread;
	private StringReader in;

	private static ParsingEditor instance;

	public ParsingEditor(SyntacticLoader cs, Mode mode, String rootPath)
	{
		this.cs = cs;
		this.rootPath = rootPath;
		sta = StandaloneTextArea.createTextArea();
		sta.getBuffer().setMode(mode);
		instance = this;
		newText = new StringBuffer();
		parsingButtons = new ArrayList<JButton>();
		sta.getBuffer().addBufferListener(this);
		sta.addCaretListener(this);
		sta.setCaretBlinkEnabled(true);
		in = new StringReader("");
	}

	public static ParsingEditor getInstance()
	{
		return instance;
	}

	/**
	 * clear the local buffer, inserts a new line, and moves the caret to the
	 * new line
	 * 
	 * @param insertNewLine
	 *            , whether a new line will be inserted or not
	 */
	private void clearBufferAndGoToNextLine(boolean insertNewLine)
	{
		sta.goToBufferEnd(false);
		if (insertNewLine)
			sta.insertEnterAndIndent();
		sta.goToNextLine(false);
		sta.scrollToCaret(true);
		lastLine = sta.getLastPhysicalLine();
		newText = new StringBuffer();
	}

	private void updateParsingButtons()
	{
		if (newText.toString().equals(""))
		{
			for (JButton button : parsingButtons)
			{
				button.setEnabled(false);
			}
		}
		else
		{
			for (JButton button : parsingButtons)
			{
				button.setEnabled(true);
			}
		}
	}

	public void addParsingButtons(JButton... buttons)
	{
		for (JButton button : buttons)
		{
			parsingButtons.add(button);
		}
	}

	// ############### BUFFER LISTENER ###############################
	@Override
	public void bufferLoaded(JEditBuffer buffer)
	{

	}

	public ParsingEditor build()
	{
		lex = YyFactory.getYylex(rootPath + "/generated_code", null, in);
		return instance;
	}

	/* #################### CARET LISTENER ######################### */
	@Override
	public void caretUpdate(CaretEvent e)
	{
		if (sta.getLineOfOffset(e.getDot()) < lastLine)
		{
			if (sta.getLastPhysicalLine() == sta.getLineOfOffset(e.getDot()))
			{
				sta.insertEnterAndIndent();
			}
			else
			{
				sta.getBuffer().setReadOnly(true);
			}
		}
		else
		{
			sta.getBuffer().setReadOnly(false);
		}
	}

	@Override
	public void contentInserted(JEditBuffer buffer, int startLine, int offset, int numLines, int length)
	{
		if (startLine >= lastLine)
		{
			newText.append(buffer.getText(offset, length));
			updateParsingButtons();
		}

	}

	@Override
	public void contentRemoved(JEditBuffer buffer, int startLine, int offset, int numLines, int length)
	{
		if (startLine >= lastLine)
		{
			newText = new StringBuffer();
			newText.append(buffer.getText(buffer.getLineStartOffset(lastLine), buffer.getLength() - buffer.getLineStartOffset(lastLine)));
		}
	}

	public void displayInputTextNewLine(String str)
	{
		sta.goToBufferEnd(false);
		sta.getBuffer().insert(sta.getText().length(), "\n" + str);
	}

	public void displayInputTextNoLine(String str)
	{
		sta.goToBufferEnd(false);
		sta.getBuffer().insert(sta.getText().length(), str);
	}

	public void displayOutputText(String str)
	{
		clearBufferAndGoToNextLine(false);
		sta.getBuffer().insert(sta.getText().length(), str);
		clearBufferAndGoToNextLine(true);
		Output.getInstance().displayTextExt("** Result: " + str, TOPIC.Parser);
	}

	@Override
	public void foldHandlerChanged(JEditBuffer buffer)
	{

	}

	@Override
	public void foldLevelChanged(JEditBuffer buffer, int startLine, int endLine)
	{

	}

	public Mode getMode()
	{
		return sta.getBuffer().getMode();
	}

	/**
	 * @return the rootPath
	 */
	public String getRootPath()
	{
		return rootPath;
	}

	public TextArea getTextArea()
	{
		return sta;
	}

	public JComponent getView()
	{
		return sta;
	}

	public void open()
	{
		JFileChooser c = new JFileChooser();
		int rVal = c.showOpenDialog(getView());
		if (rVal == JFileChooser.APPROVE_OPTION)
		{
			File file = c.getSelectedFile();
			try
			{
				FileInputStream fis = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String line = "";
				StringBuffer text = new StringBuffer();
				line = br.readLine();
				while (line != null)
				{
					text.append(line);
					line = br.readLine();
					if (line != null)
					{
						text.append("\n");
					}
				}
				sta.getBuffer().beginCompoundEdit();
				sta.setText(text.toString());
				sta.setCaretPosition(text.length());
				sta.scrollToCaret(true);
				sta.getBuffer().endCompoundEdit();
				br.close();
				fis.close();
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR, this, "Could not open file: " + file.getName(), e);
			}
		}
		if (rVal == JFileChooser.CANCEL_OPTION)
		{
			c.setVisible(false);
		}
	}

	@Override
	public void preContentInserted(JEditBuffer buffer, int startLine, int offset, int numLines, int length)
	{

	}

	@Override
	public void preContentRemoved(JEditBuffer buffer, int startLine, int offset, int numLines, int length)
	{

	}

	public void run(boolean stepping)
	{
		if ((parsingThread != null && !parsingThread.isAlive()) || parsingThread == null)
		{
			if (cs != null)
			{
				if (newText.toString().equals(""))
				{
					JOptionPane.showMessageDialog(null, "There is nothing to parse", "Can not parse", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					Output.getInstance().displayTextExt("<< " + newText.toString(), TOPIC.Parser);
					in = new StringReader(newText.toString());
					try
					{
						lex.yyreset(in);
					}
					catch (IOException e1)
					{
						Log.log(Log.ERROR, this, "An internal error has occurred!", e1);
					}
					Analyzer asin = new Analyzer(cs.tabGraph(), cs.tabT(), cs.tabNt(), null, lex);
					synchronized (asin)
					{
						asin.setStepping(stepping);
					}
					parsingThread = asin;
					parsingThread.start();
					clearBufferAndGoToNextLine(true);
					Thread buttonThread = new Thread(new Runnable()
					{

						@Override
						public void run()
						{
							try
							{
								while (parsingThread.isAlive())
									Thread.sleep(200);
								updateParsingButtons();
							}
							catch (Exception e)
							{
								Log.log(Log.ERROR, this, "An internal error has occurred!", e);
							}
						}
					});
					buttonThread.start();
				}
			}
		}
		else if (parsingThread.isAlive())
		{
			parsingThread.notify();
		}
	}

	public void save()
	{
		JFileChooser c = new JFileChooser();
		int rVal = c.showSaveDialog(getView());
		if (rVal == JFileChooser.APPROVE_OPTION)
		{
			File file = c.getSelectedFile();
			try
			{
				if (!file.exists())
				{
					if (file.createNewFile())
					{
						FileWriter fw = new FileWriter(file);
						fw.write(sta.getText());
						fw.close();
					}
				}
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR, this, "Could not save file: " + file.getName(), e);
			}
		}
		if (rVal == JFileChooser.CANCEL_OPTION)
		{
			c.setVisible(false);
		}
	}

	public void setMode(Mode mode)
	{
		sta.getBuffer().setMode(mode);
		String text = sta.getText();
		int carret = sta.getCaretPosition();
		sta.setText("");
		sta.setText(text);
		sta.setCaretPosition(carret);
		sta.scrollToCaret(true);

	}

	public void setSyntacticLoader(SyntacticLoader cs)
	{
		this.cs = cs;
	}

	public void stepRun()
	{
		if (parsingThread != null && parsingThread.isAlive())
		{
			synchronized (parsingThread)
			{
				parsingThread.notify();
			}
		}
		else
		{
			run(true);
		}
	}

	@Override
	public void transactionComplete(JEditBuffer buffer)
	{

	}

}
