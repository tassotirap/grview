package org.grview.output;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import org.grview.canvas.Canvas;
import org.grview.util.ExampleFileFilter;
import org.grview.util.IOUtilities;
import org.grview.util.Log;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

public class Output extends HtmlViewer {


	private static Output instance;
	private static HashMap<TOPIC, Page> pagesByTopic = new HashMap<TOPIC, Page>();

	protected Output() {
		super();
		if (pagesByTopic == null) {
			pagesByTopic = new HashMap<TOPIC, Page>();
		}
	}

	public static Output getInstance() {
		if (instance == null) {
			instance = new Output();
		}
		return instance;
	}

	public void clearOutputBuffer() {
		for (TOPIC topic : pagesByTopic.keySet()) {
			pagesByTopic.put(topic, new Page());
		}

	}

	public String getReport() {
		Page result = new Page();
		result.write("<h1>Grammar Graph</h1>");
		result.write("<img src=\"images/graph.jpg\" alt=\"Grammar Graph\"><br>");
		for (TOPIC topic : pagesByTopic.keySet()) {
			result.write("<hr width=\"100%\" size=\"1\" color=\"gray\" align=\"center\">");
			result.write("<h1>" + topic.toString() + "</h1>");
			String content = pagesByTopic.get(topic).getContent();
			content = content.replaceAll("<a href=\"[^\"]+\">", "<a href=#>");
			content = content.replaceAll("<a style=\"color: #000000; text-decoration: none; font-weight: bold;\" href=\"[^\"]+\">", "<a href=#>");
			result.write(content);
		}
		return result.getContent();
	}

	public void saveReport(Component parent) {
		JFileChooser c = new JFileChooser();
		c.setFileFilter(new ExampleFileFilter(new String[] {"html", "htm"}, "An html File"));
		int rVal = c.showSaveDialog(parent);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			File file = c.getSelectedFile();
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				File newImagesDir = new File(file.getParentFile(), "images");
				if (!newImagesDir.exists()) {
					newImagesDir.mkdir();
				}
				File oldImagesDir = new File("resources/images/");
				if (oldImagesDir.exists() && oldImagesDir.isDirectory()) {
					for (File f : oldImagesDir.listFiles()) {
						IOUtilities.copyFile(f, new File(newImagesDir, f.getName()));
					}
					int width = (super.getActiveScene().getBounds() == null)?
							super.getActiveScene().getView().getParent().getWidth():
								super.getActiveScene().getBounds().width;
					int height = (super.getActiveScene().getBounds() == null)?
							super.getActiveScene().getView().getParent().getHeight():
								super.getActiveScene().getBounds().height;
					BufferedImage image = new BufferedImage(width,  height,
							BufferedImage.TYPE_INT_RGB);
					Graphics2D g = image.createGraphics();
					super.getActiveScene().paint(g);
					try {         
						ImageIO.write(image, "JPEG", new File(newImagesDir, "graph.jpg"));
					}catch(IOException ioe) {
						System.out.println(ioe.getMessage());
					}
				}
				FileWriter fw = new FileWriter(file);
				fw.write(getReport());
				fw.close();
			} catch (Exception e) {
				Log.log(Log.ERROR, this, "Could not save file: " + file.getName(), e);
			}
		}
	}

	@Override
	public void displayTextExt(String st, TOPIC topic) {
		displayTextExt(st, DEFAULT_FONT, DEFAULT_SIZE, null, topic);
	}

	@Override
	public void displayTextExt(String st, String font, String size, String cssClass, TOPIC topic) {
		EditorKit eKit = getEditorPane().getEditorKit();
		Document doc = getEditorPane().getDocument();
		String html = st;
		String text = "";
		String tag1 = "";
		String tag2 = "";
		if (cssClass == null && font != null && size != null) {
			tag1 = "<font face=\"" + font + "\" size=\""+ size + "\">";
			tag2 = "</font>";
		}
		else if (cssClass != null){
			//TODO should verify whether the class exists or not
			tag1 = "<span class=\"" + cssClass + "\">";
			tag2 = "</span>";
		}
		int offset = 0;
		try {
			Parser p = new Parser();
			p.setInputHTML(new String(st));
			StringFilter sf = new StringFilter();
			NodeList nl = p.parse(sf);
			SimpleNodeIterator ni = nl.elements();
			while (ni.hasMoreNodes()) {
				Node n = ni.nextNode();
				if (n.toHtml().length() > 0) {
					text = tag1 + n.toHtml() + tag2;
					html = html.substring(0, n.getStartPosition() + offset) + text + html.substring(n.getEndPosition() + offset);
					offset += tag1.length() + tag2.length();
				}
			}
		}
		catch (ParserException e) {
			e.printStackTrace();
		}
		StringReader reader = new StringReader(html);
		try { eKit.read(reader, doc, doc.getLength());}
		catch (Exception e) { e.printStackTrace(); }
		if (!pagesByTopic.containsKey(topic)) {
			pagesByTopic.put(topic, new Page());
		}
		pagesByTopic.get(topic).write(html);
	}

	public JComponent getView(Canvas canvas) {
		setActiveScene(canvas);
		return getEditorPane();
	}
}
