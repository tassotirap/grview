package org.grview.ui.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.grview.canvas.Canvas;
import org.grview.output.Output;
import org.grview.output.SyntaxErrorOutput;
import org.grview.output.TokenOutput;


public class OutputComponent extends AbstractComponent {

	public final static String ICONS_PATH = "/org/grview/images/";
	
	@Override
	public JComponent create(Object param) throws BadParameterException {
		if (param instanceof Canvas) {
			final Canvas canvas = (Canvas)param;
			final JScrollPane jsp = new JScrollPane();
			final JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			JButton lastToken = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "token.png")));
			JPanel main = new JPanel(new BorderLayout());
			jsp.setViewportView(Output.getInstance().getView((Canvas)param));
			lastToken.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jsp.setViewportView(TokenOutput.getInstance().getView(canvas));
				}
			});
			lastToken.setBorder(new EmptyBorder(0,0,0,0));
			lastToken.setRolloverEnabled(true);
			lastToken.setBackground(bar.getBackground());
			lastToken.setToolTipText("Tokens");
			JButton errorReovery = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "errorRecoveryStatus.png")));
			errorReovery.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jsp.setViewportView(SyntaxErrorOutput.getInstance().getView(canvas));	
				}
			});
			errorReovery.setBorder(new EmptyBorder(0,0,0,0));
			errorReovery.setRolloverEnabled(true);
			errorReovery.setBackground(bar.getBackground());
			errorReovery.setToolTipText("Error Recovery");
			JButton clear = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "eraser.png")));
			clear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Output.getInstance().clear();
					TokenOutput.getInstance().clear();
					SyntaxErrorOutput.getInstance().clear();
				}
			});
			clear.setBorder(new EmptyBorder(0,0,0,0));
			clear.setRolloverEnabled(true);
			clear.setBackground(bar.getBackground());
			clear.setToolTipText("Clear All");
			JButton output = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "output.png")));
			output.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jsp.setViewportView(Output.getInstance().getView(canvas));
				}
			});
			output.setBorder(new EmptyBorder(0,0,0,0));
			output.setRolloverEnabled(true);
			output.setBackground(bar.getBackground());
			output.setToolTipText("Output");
			JButton saveReport = new JButton(new ImageIcon(getClass().getResource(ICONS_PATH + "html_report.png")));
			saveReport.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Output.getInstance().saveReport(bar);
				}
			});
			saveReport.setBorder(new EmptyBorder(0,0,0,0));
			saveReport.setRolloverEnabled(true);
			saveReport.setBackground(bar.getBackground());
			saveReport.setToolTipText("Save an html report.");
			bar.add(output);
			bar.add(errorReovery);
			bar.add(lastToken);
			bar.add(clear);
			bar.add(saveReport);
			main.add(bar, BorderLayout.NORTH);
			main.add(jsp, BorderLayout.CENTER);
			return main;
		}
		else {
			throw new BadParameterException("Was Expecting a canvas as parameter.");
		}
	}

	@Override
	public void fireContentChanged() {}
	

}
