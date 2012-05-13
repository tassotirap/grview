package org.grview.output;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.StyleSheet;

import org.grview.canvas.Canvas;
import org.grview.util.ExtHTMLEditorKit;


public abstract class HtmlViewer implements HyperlinkListener{

	private Canvas activeScene;

	public static enum TOPIC {Grammar, SyntaxStack, SemanticStack, Output, Tokens, Error, Parser}; 
	
	private JEditorPane editorPane = new JEditorPane();
	private ExtHTMLEditorKit kit = new ExtHTMLEditorKit();
	private StyleSheet cssSheet = new StyleSheet();
	
	public static final String ApplicationImagePath = 
		new File("resources").getAbsolutePath();
	public static final String SystemImagePathKey = 
		"system.image.path.key";
	public static final String SystemImagePath = 
		new File("resources/images").getAbsolutePath();

	public final static String HORIZONTAL_LINE =
		"<hr width=\"100%\" size=\"1\" color=\"gray\" align=\"center\">";
	public final static String DEFAULT_FONT = "Arial";
	public final static String DEFAULT_SIZE = "3";
	
	public final static boolean USE_CSSFILE = false;
	
	public HtmlViewer() {
		try {cssSheet.loadRules(new InputStreamReader(HtmlViewer.class.getResourceAsStream("/org/grview/output/output.css")), null); }
		catch (IOException e) { e.printStackTrace(); };
		if (USE_CSSFILE) {
			kit.setStyleSheet(cssSheet);
		}
		kit.setLinkCursor(new Cursor(Cursor.HAND_CURSOR));
		setSystemImagePath(SystemImagePath);
		ExtHTMLEditorKit.HTMLFactoryX.setApplicationImagePath(ApplicationImagePath);
		editorPane.setEditorKit(kit);
		Document doc = editorPane.getDocument();
		StringReader reader = new StringReader(new Page().getContent());
		try { kit.read(reader, doc, 0); }
		catch (Exception e) { e.printStackTrace(); }
		editorPane.setDoubleBuffered(true);
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		editorPane.addHyperlinkListener(this);
	}
	
	public void clear() {
		editorPane.setText("");
	}
	
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			activeScene.select(e.getDescription());
		}
	}
	
	public void displayTextExt(String st, TOPIC topic) {}
	
	void displayTextExt(String st, String font, String size, String cssClass, TOPIC topic) {}
	
	public void displayHorizontalLineExt(TOPIC topic) {
		displayTextExt(HORIZONTAL_LINE, topic);
	}
	
	public void setSystemImagePath(String path) {
		System.setProperty(SystemImagePathKey, path);
	}
	
	public void setActiveScene(Canvas canvas) {
		activeScene = canvas;
	}
	
	public Canvas getActiveScene() {
		return activeScene;
	}
	
	public JEditorPane getEditorPane() {
		return editorPane;
	}
	
	public ExtHTMLEditorKit getKit() {
		return kit;
	}
	
	public StyleSheet getCssSheet() {
		return cssSheet;
	}
	
	protected class Page {
		final static String HEAD = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + 
									"<html>" + 
									"<head>" +
									"<meta http-equiv=\"Content-Type\" content=\"text/html\">" +
									"<title>Gr View gerenerated page</title>" +
									"</head>";
		final static String TAIL = "</body></html>";
		StringBuffer content = new StringBuffer(HEAD + TAIL);

		void write(String text) {
			String newText = text;
			content.insert(content.length() - TAIL.length(), newText);
		}
		
		String getContent() {
			return content.toString();
		}
	}
}
