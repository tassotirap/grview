package org.grview.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class ClipboardManager
{

	private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	/**
	 * Set the content of the clipboard
	 * 
	 * @param contents
	 * @param owner
	 *            the object that is sending the content
	 */
	public static void setClipboardContents(Transferable contents, ClipboardOwner owner)
	{
		clipboard.setContents(contents, owner);
	}

	/**
	 * Gets whatever is in clipboard since it is a WidgetSelection or
	 * StringSelection
	 * 
	 * @return the contents of the clipboard
	 */
	public static Object getClipboardContents()
	{
		Object result = null;
		Transferable contents = clipboard.getContents(null);
		if (contents != null)
		{
			try
			{
				if (contents.isDataFlavorSupported(DataFlavor.stringFlavor))
				{
					result = contents.getTransferData(DataFlavor.stringFlavor);
				}
				else
				{
					result = contents.getTransferData(null);
				}
			}
			catch (Exception e)
			{
			}
		}
		return result;

	}
}
