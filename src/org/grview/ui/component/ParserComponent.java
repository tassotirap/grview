package org.grview.ui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.grview.actions.Mode;
import org.grview.editor.syntax.ModeProvider;
import org.grview.parser.ParsingEditor;

import com.jidesoft.icons.ColorFilter;

public class ParserComponent extends Component {

	  private JButton open;
	  private JButton save;
	  private JButton parse;
	  private JButton parseNextStep;
	  private JPanel btBar;
	  private JPanel btBarLeft;
	  private JPanel btBarRight;
	  private JPanel main;
	  private JLabel modesLabel;
	  private JComboBox modes;
	  private String rootPath;
	  
	  public final static String ICONS_PATH = "/org/grview/images/";
	  
	@Override
	public JComponent create(Object param) throws BadParameterException {
		final ParsingEditor parser;
		if (param instanceof String) {
			rootPath = (String) param;
		}
		else {
			throw new BadParameterException("A string refering to the root path was expected");
		}
		modesLabel = new JLabel("Modes: ");
		modes = new JComboBox(getModes());
		modes.setSelectedItem("java");
		modes.setEditable(false);
		modes.setPreferredSize(new Dimension(120, 16));
		modes.setFont(new Font("Arial", Font.PLAIN, 12));
		modes.setBackground(Color.WHITE);
		open = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "parser-open.png")));
		open.setOpaque(false);
		open.setBorder(new EmptyBorder(0,0,0,0));
		open.setRolloverEnabled(true);
		open.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon)open.getIcon()).getImage())));
		open.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon)open.getIcon()).getImage())));
		open.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)open.getIcon()).getImage())));
		open.setToolTipText("Open File With Expression");
		save = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "parser-save.png")));
		save.setOpaque(false);
		save.setBorder(new EmptyBorder(0,0,0,0));
		save.setRolloverEnabled(true);
		save.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon)save.getIcon()).getImage())));
		save.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon)save.getIcon()).getImage())));
		save.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)save.getIcon()).getImage())));
		save.setToolTipText("Save Parser Content on File");
		parse = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "parser-parse.png")));
		parse.setOpaque(false);
		parse.setBorder(new EmptyBorder(0,0,0,0));
		parse.setRolloverEnabled(true);
		parse.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon)parse.getIcon()).getImage())));
		parse.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon)parse.getIcon()).getImage())));
		parse.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)parse.getIcon()).getImage())));
		parse.setToolTipText("Parse Expression");
		parse.setEnabled(false);
		parseNextStep = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "parser-step.png")));
		parseNextStep.setOpaque(false);
		parseNextStep.setBorder(new EmptyBorder(0,0,0,0));
		parseNextStep.setRolloverEnabled(true);
		parseNextStep.setSelectedIcon(new ImageIcon(ColorFilter.createDarkerImage(((ImageIcon)parseNextStep.getIcon()).getImage())));
		parseNextStep.setRolloverIcon(new ImageIcon(ColorFilter.createBrighterImage(((ImageIcon)parseNextStep.getIcon()).getImage())));
		parseNextStep.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)parseNextStep.getIcon()).getImage())));
		parseNextStep.setToolTipText("Parse Next Step");
		parseNextStep.setEnabled(false);
		String modeDef = (String) modes.getSelectedItem();
		Mode mode = new Mode(modeDef);
		mode.setProperty("file","modes/" + modeDef + ".xml");
		ModeProvider.instance.addMode(mode);
		if (ParsingEditor.getInstance() == null) {
			parser = new ParsingEditor(null, mode, rootPath);
		}
		else {
			parser = ParsingEditor.getInstance();
		}
		parser.addParsingButtons(parse, parseNextStep);
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				parser.open();	
			}
		});
		modes.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String modeDef = (String) modes.getSelectedItem();
				Mode mode = new Mode(modeDef);
				mode.setProperty("file","/modes/" + modeDef + ".xml");
				ModeProvider.instance.addMode(mode);
				parser.setMode(mode);
			}
		});
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				parser.save();	
			}
		});
		parse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				parser.run(false);	
			}
		});
		parseNextStep.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				parser.stepRun();	
			}
		});
		btBarLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btBarLeft.add(modesLabel);
		btBarLeft.add(modes);
		btBarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btBarRight.add(open);
		btBarRight.add(save);
		btBarRight.add(parseNextStep);
		btBarRight.add(parse);
		btBar = new JPanel(new BorderLayout());
		btBar.add(btBarLeft, BorderLayout.WEST);
		btBar.add(btBarRight, BorderLayout.EAST);
		main = new JPanel(new BorderLayout());
		main.add(btBar, BorderLayout.NORTH);
		main.add(parser.getView(), BorderLayout.CENTER);
		return main;
	}

	private Object[] getModes() {
		ArrayList<String> modes = new ArrayList<String>();
		File file;
		file = new File("modes");
		if (file.isDirectory()) {
			for (String name : file.list()) {
				if (name.endsWith(".xml")) {
					modes.add(name.replace(".xml", ""));
				}
			}
		}
		Collections.sort(modes);
		return modes.toArray();
	}
	@Override
	public void fireContentChanged() {}

}
