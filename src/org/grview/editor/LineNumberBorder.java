package org.grview.editor;

import java.awt.*;
import javax.swing.*;

/**
 * Inspired by
 * http://forum.java.sun.com/thread.jspa?threadID=260022&messageID=976235
 */
public class LineNumberBorder extends JComponent {
    private final static Color DEFAULT_BACKGROUND = Color.lightGray;
    private final static Color DEFAULT_FOREGROUND = Color.black;
    private final static Color DEFAULT_HIGHLIGHT_COLOR = Color.RED;
    private final static Font DEFAULT_FONT = new Font("monospaced", Font.PLAIN, 12);

    // LineNumber height (abends when I use MAX_VALUE)
    private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

    // Set right/left margin
    private final static int MARGIN = 5;

    // Variables for this LineNumber component
    private FontMetrics fontMetrics;
    private int lineHeight;
    private int currentRowWidth;

    // Metrics of the component used in the constructor
    private JComponent component;
    private int componentFontHeight;
    private int componentFontAscent;

    private int highlightLinenumber = -1;

    /**
     * Convenience constructor for Text Components
     */
    public LineNumberBorder(JComponent component) {
        if (component == null) {
            setBackground(DEFAULT_BACKGROUND);
            setForeground(DEFAULT_FOREGROUND);
            setFont(DEFAULT_FONT);
            this.component = this;
        } else {
            setBackground(DEFAULT_BACKGROUND);
            setForeground(component.getForeground());
            setFont(component.getFont());
            this.component = component;
        }

        componentFontHeight = component.getFontMetrics(component.getFont()).getHeight();
        componentFontAscent = component.getFontMetrics(component.getFont()).getAscent();
        setPreferredWidth(9999);
    }

    public int getHighlightLinenumber() {
        return highlightLinenumber;
    }

    public void setHighlightLinenumber(int highlightLinenumber) {
        if (this.highlightLinenumber!=highlightLinenumber){
            this.highlightLinenumber = highlightLinenumber;
            repaint();
        }
    }

    /**
     * Using FontMetrics, calculate the width of the given integer and then
     * set the preferred size of the component.
     */
    public void setPreferredWidth(int row) {
        int width = fontMetrics.stringWidth(String.valueOf(row));

        if (currentRowWidth < width) {
            currentRowWidth = width;
            setPreferredSize(new Dimension(2 * MARGIN + width, HEIGHT));
        }
    }

    /**
     * Reset variables that are dependent on the font.
     */
    @Override
	public void setFont(Font font) {
        super.setFont(font);
        fontMetrics = getFontMetrics(getFont());
    }

    /**
     * The line height defaults to the line height of the font for this
     * component.
     */
    public int getLineHeight() {
        if (lineHeight == 0)
            return componentFontHeight;
        else
            return lineHeight;
    }

    /**
     * Override the default line height with a positive value.
     * For example, when you want line numbers for a JTable you could
     * use the JTable row height.
     */
    public void setLineHeight(int lineHeight) {
        if (lineHeight > 0)
            this.lineHeight = lineHeight;
    }

    public int getStartOffset() {
        return component.getInsets().top + componentFontAscent;
    }

    @Override
	public void paintComponent(Graphics g) {
        int lineHeight = getLineHeight();
        int startOffset = getStartOffset();
        Rectangle drawHere = g.getClipBounds();
// System.out.println( drawHere );

// Paint the background

        g.setColor(getBackground());
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

// Determine the number of lines to draw in the foreground.


        int startLineNumber = (drawHere.y / lineHeight) + 1;
        int endLineNumber = startLineNumber + (drawHere.height / lineHeight);

        int start = (drawHere.y / lineHeight) * lineHeight + startOffset;

// System.out.println( startLineNumber + " : " + endLineNumber + " : " + start );

        for (int i = startLineNumber; i <= endLineNumber; i++) {
            String lineNumber = String.valueOf(i);
            int width = fontMetrics.stringWidth(lineNumber);
            g.setColor(i==highlightLinenumber?DEFAULT_HIGHLIGHT_COLOR:getForeground());

            g.drawString(lineNumber, MARGIN + currentRowWidth - width, start);
            start += lineHeight;
        }

        setPreferredWidth(endLineNumber);
    }
}