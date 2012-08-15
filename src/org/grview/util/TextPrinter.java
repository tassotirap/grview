package org.grview.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Utility class to print some lines of text to the default printer. Uses some
 * default font settings, and gets the page size from the PrinterJob object.
 * 
 * Note: this little example class does not handle pagination. All the text must
 * fit on a single page.
 * 
 * This class can also be used as a standalone utility. If the main method is
 * invoked, it reads lines of text from System.in, and prints them to the
 * default printer.
 */

/* FROM http://en.allexperts.com/q/Java-1046/Print-Simple-Text-File.htm */

public class TextPrinter implements Printable
{
	/**
	 * Default font size, 12 point
	 */
	public static final int DEFAULT_FONT_SIZE = 12;

	/**
	 * Default type name, Serif
	 */
	public static final String DEFAULT_FONT_NAME = "Serif";

	private PrinterJob job;
	private String typeName;
	private int typeSize;
	private Font typeFont;
	private Font typeFontBold;
	private String[] header;
	private String[] body;

	/**
	 * Create a TextPrinter object with the default type font and size.
	 */
	public TextPrinter()
	{
		this(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE);
	}

	/**
	 * Create a TextPrinter object ready to print text with a given font and
	 * type size.
	 */
	public TextPrinter(String name, int size)
	{
		if (size < 3 || size > 127)
		{
			throw new IllegalArgumentException("Type size out of range");
		}
		typeName = name;
		typeSize = size;
		typeFont = new Font(typeName, Font.PLAIN, typeSize);
		typeFontBold = new Font(typeName, Font.BOLD, typeSize);
		job = null;
	}

	public static void printText(String text)
	{
		TextPrinter tp;
		tp = new TextPrinter();
		tp.getCharsPerLine();
		String body[] = text.split("\n");
		try
		{
			tp.doPrint(null, body, true);
		}
		catch (PrinterException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the printer job.
	 */
	protected void init()
	{
		job = PrinterJob.getPrinterJob();
	}

	/**
	 * Print some text. Headers are printed first, in bold, followed by the body
	 * text, in plain style. If the boolean argument interactive is set to true,
	 * then the printer dialog gets shown.
	 * 
	 * Either array may be null, in which case they are treated as empty.
	 * 
	 * This method returns true if printing was initiated, or false if the user
	 * cancelled printer. This method may throw PrinterException if printing
	 * could not be started.
	 */
	public boolean doPrint(String[] header, String[] body, boolean interactive) throws PrinterException
	{
		if (job == null)
		{
			init();
		}
		if (interactive)
			try
			{
				if (job.printDialog())
				{
					// we are going to print
				}
				else
				{
					// we are not going to print
					return false;
				}
			}
			catch (Exception pe)
			{
				System.err.println("Could not pop up print dialog");
				// assume user wants to print anyway...
			}

		job.setPrintable(this);
		this.header = header;
		this.body = body;
		job.print();
		job = null; // we are no longer initialized
		return true;
	}

	/**
	 * Initialize the print job, and return the base number of characters per
	 * line with the established font size and font. This is really just a
	 * guess, because we can't get the font metrics yet.
	 */
	public int getCharsPerLine()
	{
		if (job == null)
		{
			init();
		}
		PageFormat pf;
		pf = job.defaultPage();
		double width = pf.getImageableWidth(); // in 72nd of a pt
		double ptsize = typeFont.getSize();
		double ptwid = ptsize * 3 / 4;
		double cnt = (width / ptwid);
		return (int) (Math.round(cnt));
	}

	/**
	 * Perform printing according to the Java printing model. NEVER CALL THIS
	 * DIRECTLY! It will be called by the PrinterJob as necessary. This method
	 * always returns Printable.NO_SUCH_PAGE for any page number greater than 0.
	 */
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
	{
		if (pageIndex != 0)
		{
			return NO_SUCH_PAGE;
		}
		FontMetrics fm;
		graphics.setFont(typeFont);
		graphics.setColor(Color.black);
		fm = graphics.getFontMetrics();

		// fill in geometric and rendering guts here
		int i;
		double x, y;
		x = pageFormat.getImageableX();
		y = pageFormat.getImageableY() + fm.getMaxAscent();

		// do the headings
		if (header != null)
		{
			graphics.setFont(typeFontBold);
			for (i = 0; i < header.length; i++)
			{
				graphics.drawString(header[i], (int) x, (int) y);
				y += fm.getHeight();
			}
		}

		// do the body
		if (body != null)
		{
			graphics.setFont(typeFont);
			for (i = 0; i < body.length; i++)
			{
				graphics.drawString(body[i], (int) x, (int) y);
				y += fm.getHeight();
			}
		}

		return PAGE_EXISTS;
	}
}