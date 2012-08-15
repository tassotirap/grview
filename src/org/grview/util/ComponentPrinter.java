package org.grview.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.RepaintManager;

import org.netbeans.api.visual.widget.Widget;

/**
 * prints an java.awt.Component, or a org.netbeans.api.visual.widget
 * 
 * @author Gustavo
 * 
 */
public class ComponentPrinter implements Printable
{

	private Component componentToBePrinted;
	private Widget widgetToBePrinted;

	public ComponentPrinter(Component componentToBePrinted)
	{
		this.componentToBePrinted = componentToBePrinted;
	}

	public ComponentPrinter(Widget widgetToBePrinted)
	{
		this.widgetToBePrinted = widgetToBePrinted;
	}

	public static void disableDoubleBuffering(Component c)
	{
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(false);
	}

	public static void enableDoubleBuffering(Component c)
	{
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(true);
	}

	public static void printComponent(Component c)
	{
		new ComponentPrinter(c).print();
	}

	public static void printWidget(Widget w)
	{
		new ComponentPrinter(w).print();
	}

	public void print()
	{
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		if (printJob.printDialog())
			try
			{
				printJob.print();
			}
			catch (PrinterException pe)
			{
				System.out.println("Error printing: " + pe);
			}
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
	{
		if (pageIndex > 0)
		{
			return (NO_SUCH_PAGE);
		}
		else
		{
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			disableDoubleBuffering(componentToBePrinted);
			if (componentToBePrinted != null)
			{
				componentToBePrinted.paint(g2d);
			}
			else if (widgetToBePrinted != null)
			{
				widgetToBePrinted.getScene().paint(g2d);
			}
			enableDoubleBuffering(componentToBePrinted);
			return (PAGE_EXISTS);
		}
	}
}
