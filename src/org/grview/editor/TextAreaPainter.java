/*
 * TextAreaPainter.java - Paints the text area
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2005 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.grview.editor;

//{{{ Imports
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.text.TabExpander;

import org.grview.actions.Debug;
import org.grview.editor.buffer.IndentFoldHandler;
import org.grview.editor.buffer.JEditBuffer;
import org.grview.editor.syntax.Chunk;
import org.grview.editor.syntax.SyntaxStyle;
import org.grview.editor.syntax.Token;
import org.grview.util.Log;

//}}}

/**
 * The text area painter is the component responsible for displaying the text of
 * the current buffer. The only methods in this class that should be called by
 * plugins are those for adding and removing text area extensions.
 * 
 * @see #addExtension(TextAreaExtension)
 * @see #addExtension(int,TextAreaExtension)
 * @see #removeExtension(TextAreaExtension)
 * @see TextAreaExtension
 * @see TextArea
 * 
 * @author Slava Pestov
 * @version $Id$
 */
public class TextAreaPainter extends JComponent implements TabExpander
{
	// {{{ PaintCaret class
	class PaintCaret extends TextAreaExtension
	{
		@Override
		public void paintValidLine(Graphics2D gfx, int screenLine, int physicalLine, int start, int end, int y)
		{
			if (!textArea.isCaretVisible())
				return;

			int caret = textArea.getCaretPosition();
			if (caret < start || caret >= end)
				return;

			int offset = caret - textArea.getLineStartOffset(physicalLine);
			textArea.offsetToXY(physicalLine, offset, textArea.offsetXY);
			int caretX = textArea.offsetXY.x;
			int lineHeight = fm.getHeight();

			gfx.setColor(caretColor);

			if (textArea.isOverwriteEnabled())
			{
				gfx.drawLine(caretX, y + lineHeight - 1, caretX + textArea.charWidth, y + lineHeight - 1);
			}
			else if (blockCaret)
				gfx.drawRect(caretX, y, textArea.charWidth - 1, lineHeight - 1);
			else
			{
				if (thickCaret)
					gfx.drawRect(caretX, y, 1, lineHeight - 1);
				else
					gfx.drawLine(caretX, y, caretX, y + lineHeight - 1);
			}
		}
	} // }}}

	// {{{ PaintLineBackground class
	class PaintLineBackground extends TextAreaExtension
	{
		// {{{ shouldPaintLineHighlight() method
		private boolean shouldPaintLineHighlight(int caret, int start, int end)
		{
			if (!isLineHighlightEnabled() || caret < start || caret >= end)
			{
				return false;
			}

			int count = textArea.getSelectionCount();
			if (count == 1)
			{
				Selection s = textArea.getSelection(0);
				return s.getStartLine() == s.getEndLine();
			}
			else
				return (count == 0);
		} // }}}

		// {{{ paintValidLine() method
		@Override
		public void paintValidLine(Graphics2D gfx, int screenLine, int physicalLine, int start, int end, int y)
		{
			// minimise access$ methods
			TextArea textArea = TextAreaPainter.this.textArea;
			JEditBuffer buffer = textArea.getBuffer();

			// {{{ Paint line highlight and collapsed fold highlight
			boolean collapsedFold = (physicalLine < buffer.getLineCount() - 1 && buffer.isFoldStart(physicalLine) && !textArea.displayManager.isLineVisible(physicalLine + 1));

			SyntaxStyle foldLineStyle = null;
			if (collapsedFold)
			{
				int level = buffer.getFoldLevel(physicalLine + 1);
				if (buffer.getFoldHandler() instanceof IndentFoldHandler)
					level = Math.max(1, level / buffer.getIndentSize());
				if (level > 3)
					level = 0;
				foldLineStyle = TextAreaPainter.this.foldLineStyle[level];
			}

			int caret = textArea.getCaretPosition();
			boolean paintLineHighlight = shouldPaintLineHighlight(caret, start, end);

			Color bgColor;
			if (paintLineHighlight)
				bgColor = lineHighlightColor;
			else if (collapsedFold)
			{
				bgColor = foldLineStyle.getBackgroundColor();
				if (bgColor == null)
					bgColor = getBackground();
			}
			else
				bgColor = getBackground();

			if (paintLineHighlight || collapsedFold)
			{
				gfx.setColor(bgColor);
				gfx.fillRect(0, y, getWidth(), fm.getHeight());
			} // }}}

			// {{{ Paint token backgrounds
			ChunkCache.LineInfo lineInfo = textArea.chunkCache.getLineInfo(screenLine);

			if (lineInfo.chunks != null)
			{
				float baseLine = y + fm.getHeight() - fm.getLeading() - fm.getDescent();
				Chunk.paintChunkBackgrounds(lineInfo.chunks, gfx, textArea.getHorizontalOffset(), baseLine);
			} // }}}
		} // }}}
	} // }}}

	// {{{ PaintSelection class
	class PaintSelection extends TextAreaExtension
	{
		// {{{ paintSelection() method
		private void paintSelection(Graphics2D gfx, int screenLine, int physicalLine, int y, Selection s)
		{
			int[] selectionStartAndEnd = textArea.selectionManager.getSelectionStartAndEnd(screenLine, physicalLine, s);
			if (selectionStartAndEnd == null)
				return;

			int x1 = selectionStartAndEnd[0];
			int x2 = selectionStartAndEnd[1];

			gfx.fillRect(x1, y, x2 - x1, fm.getHeight());
		} // }}}

		// {{{ paintValidLine() method
		@Override
		public void paintValidLine(Graphics2D gfx, int screenLine, int physicalLine, int start, int end, int y)
		{
			if (textArea.getSelectionCount() == 0)
				return;

			gfx.setColor(textArea.isMultipleSelectionEnabled() ? getMultipleSelectionColor() : getSelectionColor());

			Iterator<Selection> iter = textArea.getSelectionIterator();
			while (iter.hasNext())
			{
				Selection s = iter.next();
				paintSelection(gfx, screenLine, physicalLine, y, s);
			}
		} // }}}
	} // }}}

	// {{{ PaintText class
	class PaintText extends TextAreaExtension
	{
		@Override
		public void paintValidLine(Graphics2D gfx, int screenLine, int physicalLine, int start, int end, int y)
		{
			ChunkCache.LineInfo lineInfo = textArea.chunkCache.getLineInfo(screenLine);

			Font defaultFont = getFont();
			Color defaultColor = getForeground();

			gfx.setFont(defaultFont);
			gfx.setColor(defaultColor);

			int x = textArea.getHorizontalOffset();
			int originalX = x;

			float baseLine = y + fm.getHeight() - fm.getLeading() - fm.getDescent();

			if (lineInfo.chunks != null)
			{
				x += Chunk.paintChunkList(lineInfo.chunks, gfx, textArea.getHorizontalOffset(), baseLine, !Debug.DISABLE_GLYPH_VECTOR);
			}

			JEditBuffer buffer = textArea.getBuffer();

			if (!lineInfo.lastSubregion)
			{
				gfx.setFont(defaultFont);
				gfx.setColor(eolMarkerColor);
				gfx.drawString(":", Math.max(x, textArea.getHorizontalOffset() + textArea.wrapMargin + textArea.charWidth), baseLine);
				x += textArea.charWidth;
			}
			else if (physicalLine < buffer.getLineCount() - 1 && buffer.isFoldStart(physicalLine) && !textArea.displayManager.isLineVisible(physicalLine + 1))
			{
				int level = buffer.getFoldLevel(physicalLine + 1);
				if (buffer.getFoldHandler() instanceof IndentFoldHandler)
					level = Math.max(1, level / buffer.getIndentSize());
				if (level > 3)
					level = 0;
				SyntaxStyle foldLineStyle = TextAreaPainter.this.foldLineStyle[level];

				Font font = foldLineStyle.getFont();
				gfx.setFont(font);
				gfx.setColor(foldLineStyle.getForegroundColor());

				int nextLine;
				int nextScreenLine = screenLine + 1;
				if (nextScreenLine < textArea.getVisibleLines())
				{
					nextLine = textArea.chunkCache.getLineInfo(nextScreenLine).physicalLine;
				}
				else
				{
					nextLine = textArea.displayManager.getNextVisibleLine(physicalLine);
				}

				if (nextLine == -1)
					nextLine = textArea.getLineCount();

				int count = nextLine - physicalLine - 1;
				String str = " [" + count + " lines]";

				float width = getStringWidth(str);

				gfx.drawString(str, x, baseLine);
				x += width;
			}
			else if (eolMarkers)
			{
				gfx.setFont(defaultFont);
				gfx.setColor(eolMarkerColor);
				gfx.drawString(".", x, baseLine);
				x += textArea.charWidth;
			}

			lineInfo.width = (x - originalX);
		}
	} // }}}

	// {{{ PaintWrapGuide class
	class PaintWrapGuide extends TextAreaExtension
	{
		@Override
		public String getToolTipText(int x, int y)
		{
			if (textArea.wrapMargin != 0 && !textArea.wrapToWidth && isWrapGuidePainted())
			{
				int wrapGuidePos = textArea.wrapMargin + textArea.getHorizontalOffset();
				if (Math.abs(x - wrapGuidePos) < 5)
				{
					return String.valueOf(textArea.getBuffer().getProperty("maxLineLen"));
				}
			}

			return null;
		}

		@Override
		public void paintScreenLineRange(Graphics2D gfx, int firstLine, int lastLine, int[] physicalLines, int[] start, int[] end, int y, int lineHeight)
		{
			if (textArea.wrapMargin != 0 && !textArea.wrapToWidth && isWrapGuidePainted())
			{
				gfx.setColor(getWrapGuideColor());
				int x = textArea.getHorizontalOffset() + textArea.wrapMargin;
				gfx.drawLine(x, y, x, y + (lastLine - firstLine + 1) * lineHeight);
			}
		}
	} // }}}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Constructor sm_frcConstructor = null;

	private static Object sm_hrgbRender = null;

	/**
	 * Below selection layer. The JDiff plugin will use this.
	 * 
	 * @see #addExtension(int,TextAreaExtension)
	 * @since jEdit 4.0pre4
	 */
	public static final int BACKGROUND_LAYER = -60;

	/**
	 * Below most extensions layer.
	 * 
	 * @see #addExtension(int,TextAreaExtension)
	 * @since jEdit 4.0pre4
	 */
	public static final int BELOW_MOST_EXTENSIONS_LAYER = -10;

	/**
	 * Below selection layer.
	 * 
	 * @see #addExtension(int,TextAreaExtension)
	 * @since jEdit 4.0pre4
	 */
	public static final int BELOW_SELECTION_LAYER = -40;

	/**
	 * Block caret layer. Most extensions will be below this layer.
	 * 
	 * @since jEdit 4.2pre1
	 */
	public static final int BLOCK_CARET_LAYER = 50;

	/**
	 * Bracket highlight layer. Most extensions will be below this layer.
	 * 
	 * @since jEdit 4.0pre4
	 */
	public static final int BRACKET_HIGHLIGHT_LAYER = 100;

	/**
	 * Caret layer. Most extensions will be below this layer.
	 * 
	 * @since jEdit 4.2pre1
	 */
	public static final int CARET_LAYER = 300;

	/**
	 * Default extension layer. This is above the wrap guide but below the
	 * structure highlight.
	 * 
	 * @since jEdit 4.0pre4
	 */
	public static final int DEFAULT_LAYER = 0;

	/**
	 * Highest possible layer.
	 * 
	 * @since jEdit 4.0pre4
	 */
	public static final int HIGHEST_LAYER = Integer.MAX_VALUE;
	// }}}

	// {{{ Getters and setters

	/**
	 * The line highlight and collapsed fold highlight layer.
	 * 
	 * @see #addExtension(int,TextAreaExtension)
	 * @since jEdit 4.0pre7
	 */
	public static final int LINE_BACKGROUND_LAYER = -50;

	// {{{ Layers
	/**
	 * The lowest possible layer.
	 * 
	 * @see #addExtension(int,TextAreaExtension)
	 * @since jEdit 4.0pre4
	 */
	public static final int LOWEST_LAYER = Integer.MIN_VALUE;

	/**
	 * Selection layer. Most extensions will be above this layer, but some (eg,
	 * JDiff) will want to be below the selection.
	 * 
	 * @see #addExtension(int,TextAreaExtension)
	 * @since jEdit 4.0pre4
	 */
	public static final int SELECTION_LAYER = -30;

	/**
	 * Text layer. Most extensions will be below this layer.
	 * 
	 * @since jEdit 4.2pre1
	 */
	public static final int TEXT_LAYER = 200;

	/**
	 * Wrap guide layer. Most extensions will be above this layer.
	 * 
	 * @since jEdit 4.0pre4
	 */
	public static final int WRAP_GUIDE_LAYER = -20;

	AntiAlias antiAlias;

	boolean blockCaret;

	Color caretColor;

	Color eolMarkerColor;

	boolean eolMarkers;

	// should try to use this as little as possible.
	FontMetrics fm;
	// }}}

	SyntaxStyle[] foldLineStyle;

	boolean fracFontMetrics;

	boolean lineHighlight;

	Color lineHighlightColor;

	Color multipleSelectionColor;

	Color selectionColor;

	boolean structureHighlight;

	Color structureHighlightColor;

	SyntaxStyle[] styles;

	// {{{ Instance variables
	/*
	 * package-private since they are accessed by inner classes and we want this
	 * to be fast
	 */
	TextArea textArea;

	boolean thickCaret;

	boolean wrapGuide;

	Color wrapGuideColor;

	private final PaintCaret caretExtension;

	// {{{ Instance variables
	private final ExtensionManager extensionMgr;

	private FontRenderContext fontRenderContext;

	private final Map fonts;
	// }}}

	private RenderingHints renderingHints;

	// {{{ TextAreaPainter constructor
	/**
	 * Creates a new painter. Do not create instances of this class directly.
	 */
	TextAreaPainter(TextArea textArea)
	{
		enableEvents(AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);

		this.textArea = textArea;
		antiAlias = new AntiAlias(0);
		fonts = new HashMap();
		extensionMgr = new ExtensionManager();

		setAutoscrolls(true);
		setOpaque(true);
		setRequestFocusEnabled(false);
		setDoubleBuffered(false);

		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

		fontRenderContext = new FontRenderContext(null, false, false);

		addExtension(LINE_BACKGROUND_LAYER, new PaintLineBackground());
		addExtension(SELECTION_LAYER, new PaintSelection());
		addExtension(WRAP_GUIDE_LAYER, new PaintWrapGuide());
		addExtension(BRACKET_HIGHLIGHT_LAYER, new StructureMatcher.Highlight(textArea));
		addExtension(TEXT_LAYER, new PaintText());
		caretExtension = new PaintCaret();
	} // }}}

	static
	{
		try
		{
			Field f = RenderingHints.class.getField("VALUE_TEXT_ANTIALIAS_LCD_HRGB");
			sm_hrgbRender = f.get(null);
			Class[] fracFontMetricsTypeList = new Class[]{ AffineTransform.class, Object.class, Object.class };
			sm_frcConstructor = FontRenderContext.class.getConstructor(fracFontMetricsTypeList);
		}
		catch (NullPointerException npe)
		{
		}
		catch (SecurityException se)
		{
		}
		catch (NoSuchFieldException nsfe)
		{
		}
		catch (IllegalArgumentException iae)
		{
		}
		catch (IllegalAccessException iae)
		{
		}
		catch (NoSuchMethodException nsme)
		{
		}
	}

	// {{{ updateRenderingHints() method
	private void updateRenderingHints()
	{
		Map<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>();

		hints.put(RenderingHints.KEY_FRACTIONALMETRICS, fracFontMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

		if (antiAlias.val() == 0)
		{
			hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		/**
		 * LCD HRGB mode - works with JRE 1.6 only, which is why we use
		 * reflection
		 */
		else if (antiAlias.val() == 2 && sm_hrgbRender != null)
		{
			hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, sm_hrgbRender);
			Object fontRenderHint = fracFontMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
			Object[] paramList = new Object[]{ null, sm_hrgbRender, fontRenderHint };
			try
			{
				fontRenderContext = (FontRenderContext) sm_frcConstructor.newInstance(paramList);
			}
			catch (Exception e)
			{
				fontRenderContext = new FontRenderContext(null, antiAlias.val() > 0, fracFontMetrics);
			}
		}
		else
		/** Standard Antialias Version */
		{
			hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			fontRenderContext = new FontRenderContext(null, antiAlias.val() > 0, fracFontMetrics);
		}

		renderingHints = new RenderingHints(hints);

	} // }}}

	// {{{ addExtension() method
	/**
	 * Adds a text area extension, which can perform custom painting and tool
	 * tip handling.
	 * 
	 * @param layer
	 *            The layer to add the extension to. Note that more than
	 *            extension can share the same layer.
	 * @param extension
	 *            The extension
	 * @since jEdit 4.0pre4
	 */
	public void addExtension(int layer, TextAreaExtension extension)
	{
		extensionMgr.addExtension(layer, extension);
		repaint();
	} // }}}

	// {{{ addExtension() method
	/**
	 * Adds a text area extension, which can perform custom painting and tool
	 * tip handling.
	 * 
	 * @param extension
	 *            The extension
	 * @since jEdit 4.0pre4
	 */
	public void addExtension(TextAreaExtension extension)
	{
		extensionMgr.addExtension(DEFAULT_LAYER, extension);
		repaint();
	} // }}}

	/**
	 * @return the AntiAlias value that is currently used for TextAreas.
	 * @since jedit 4.3pre4
	 */
	public AntiAlias getAntiAlias()
	{
		return antiAlias;
	}

	// {{{ getCaretColor() method
	/**
	 * Returns the caret color.
	 */
	public final Color getCaretColor()
	{
		return caretColor;
	} // }}}

	// {{{ getEOLMarkerColor() method
	/**
	 * Returns the EOL marker color.
	 */
	public final Color getEOLMarkerColor()
	{
		return eolMarkerColor;
	} // }}}

	// }}}

	// {{{ getEOLMarkersPainted() method
	/**
	 * Returns true if EOL markers are drawn, false otherwise.
	 */
	public final boolean getEOLMarkersPainted()
	{
		return eolMarkers;
	} // }}}

	// {{{ getExtensions() method
	/**
	 * Returns an array of registered text area extensions. Useful for debugging
	 * purposes.
	 * 
	 * @since jEdit 4.1pre5
	 */
	public TextAreaExtension[] getExtensions()
	{
		return extensionMgr.getExtensions();
	} // }}}

	// {{{ getFocusTraversalKeysEnabled() method
	/**
	 * Makes the tab key work in Java 1.4.
	 * 
	 * @since jEdit 3.2pre4
	 */
	@Override
	public boolean getFocusTraversalKeysEnabled()
	{
		return false;
	} // }}}

	// {{{ getFoldLineStyle() method
	/**
	 * Returns the fold line style. The first element is the style for lines
	 * with a fold level greater than 3. The remaining elements are for fold
	 * levels 1 to 3.
	 */
	public final SyntaxStyle[] getFoldLineStyle()
	{
		return foldLineStyle;
	} // }}}

	// {{{ getFontMetrics() method
	/**
	 * Returns the font metrics used by this component.
	 */
	public FontMetrics getFontMetrics()
	{
		return fm;
	} // }}}

	// {{{ getFontRenderContext() method
	/**
	 * Returns the font render context.
	 * 
	 * @since jEdit 4.0pre4
	 */
	public FontRenderContext getFontRenderContext()
	{
		return fontRenderContext;
	} // }}}

	// {{{ getLineHighlightColor() method
	/**
	 * Returns the line highlight color.
	 */
	public final Color getLineHighlightColor()
	{
		return lineHighlightColor;
	} // }}}

	// {{{ getMinimumSize() method
	/**
	 * Returns the painter's minimum size.
	 */
	@Override
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	} // }}}

	// {{{ getMultipleSelectionColor() method
	/**
	 * Returns the multiple selection color.
	 * 
	 * @since jEdit 4.2pre1
	 */
	public final Color getMultipleSelectionColor()
	{
		return multipleSelectionColor;
	} // }}}

	// {{{ getPreferredSize() method
	/**
	 * Returns the painter's preferred size.
	 */
	@Override
	public Dimension getPreferredSize()
	{
		Dimension dim = new Dimension();

		char[] foo = new char[80];
		for (int i = 0; i < foo.length; i++)
			foo[i] = ' ';
		dim.width = (int) getStringWidth(new String(foo));
		dim.height = fm.getHeight() * 25;
		return dim;
	} // }}}

	// {{{ getSelectionColor() method
	/**
	 * Returns the selection color.
	 */
	public final Color getSelectionColor()
	{
		return selectionColor;
	} // }}}

	// {{{ getStringWidth() method
	/**
	 * Returns the width of the given string, in pixels, using the text area's
	 * current font.
	 * 
	 * @since jEdit 4.2final
	 */
	public float getStringWidth(String str)
	{
		if (textArea.charWidth != 0)
			return textArea.charWidth * str.length();
		else
		{
			return (float) getFont().getStringBounds(str, getFontRenderContext()).getWidth();
		}
	} // }}}

	// {{{ getStructureHighlightColor() method
	/**
	 * Returns the structure highlight color.
	 * 
	 * @since jEdit 4.2pre3
	 */
	public final Color getStructureHighlightColor()
	{
		return structureHighlightColor;
	} // }}}

	// {{{ Package-private members

	// {{{ getStyles() method
	/**
	 * Returns the syntax styles used to paint colorized text. Entry <i>n</i>
	 * will be used to paint tokens with id = <i>n</i>.
	 * 
	 * @return an array of SyntaxStyles
	 * @see bsh.syntax.Token
	 */
	public final SyntaxStyle[] getStyles()
	{
		return styles;
	} // }}}

	// {{{ getToolTipText() method
	/**
	 * Returns the tool tip to display at the specified location.
	 * 
	 * @param evt
	 *            The mouse event
	 */
	@Override
	public String getToolTipText(MouseEvent evt)
	{
		if (textArea.getBuffer().isLoading())
			return null;

		return extensionMgr.getToolTipText(evt.getX(), evt.getY());
	} // }}}
		// {{{ getWrapGuideColor() method

	/**
	 * Returns the wrap guide color.
	 */
	public final Color getWrapGuideColor()
	{
		return wrapGuideColor;
	} // }}}
		// {{{ isAntiAliasEnabled() method

	/**
	 * Returns if anti-aliasing is enabled.
	 * 
	 * @since jEdit 3.2pre6
	 * @deprecated - use @ref getAntiAlias()
	 */
	@Deprecated
	public boolean isAntiAliasEnabled()
	{
		return antiAlias.val() > 0;
	} // }}}
		// {{{ isBlockCaretEnabled() method

	/**
	 * Returns true if the caret should be drawn as a block, false otherwise.
	 */
	public final boolean isBlockCaretEnabled()
	{
		return blockCaret;
	} // }}}
		// {{{ isFractionalFontMetricsEnabled() method

	/**
	 * Returns if fractional font metrics are enabled.
	 * 
	 * @since jEdit 3.2pre6
	 */
	public boolean isFractionalFontMetricsEnabled()
	{
		return fracFontMetrics;
	} // }}}
		// {{{ isLineHighlightEnabled() method

	/**
	 * Returns true if line highlight is enabled, false otherwise.
	 */
	public final boolean isLineHighlightEnabled()
	{
		return lineHighlight;
	} // }}}
		// {{{ isStructureHighlightEnabled() method

	/**
	 * Returns true if structure highlighting is enabled, false otherwise.
	 * 
	 * @since jEdit 4.2pre3
	 */
	public final boolean isStructureHighlightEnabled()
	{
		return structureHighlight;
	} // }}}
		// {{{ isThickCaretEnabled() method

	/**
	 * Returns true if the caret should be drawn with a thick line, false
	 * otherwise.
	 * 
	 * @since jEdit 4.3pre15
	 */
	public final boolean isThickCaretEnabled()
	{
		return thickCaret;
	} // }}}

	// {{{ isWrapGuidePainted() method
	/**
	 * Returns true if the wrap guide is drawn, false otherwise.
	 * 
	 * @since jEdit 4.0pre4
	 */
	public final boolean isWrapGuidePainted()
	{
		return wrapGuide;
	} // }}}

	// {{{ nextTabStop() method
	/**
	 * Implementation of TabExpander interface. Returns next tab stop after a
	 * specified point.
	 * 
	 * @param x
	 *            The x co-ordinate
	 * @param tabOffset
	 *            Ignored
	 * @return The next tab stop after <i>x</i>
	 */
	@Override
	public float nextTabStop(float x, int tabOffset)
	{
		int ntabs = (int) (x / textArea.tabSize);
		return (ntabs + 1) * textArea.tabSize;
	} // }}}
		// {{{ paint() method

	/**
	 * Repaints the text.
	 * 
	 * @param _gfx
	 *            The graphics context
	 */
	@Override
	public void paint(Graphics _gfx)
	{
		assert (_gfx instanceof Graphics2D);
		Graphics2D gfx = (Graphics2D) _gfx;
		gfx.setRenderingHints(renderingHints);
		fontRenderContext = gfx.getFontRenderContext();

		Rectangle clipRect = gfx.getClipBounds();
		int lineHeight = fm.getHeight();
		if (lineHeight == 0 || textArea.getBuffer().isLoading())
		{
			gfx.setColor(getBackground());
			gfx.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
		}
		else
		{
			long prepareTime = System.nanoTime();
			// Because the clipRect's height is usually an even multiple
			// of the font height, we subtract 1 from it, otherwise one
			// too many lines will always be painted.
			int firstLine = clipRect.y / lineHeight;
			int lastLine = (clipRect.y + clipRect.height - 1) / lineHeight;
			gfx.setColor(getBackground());
			gfx.setFont(getFont());
			prepareTime = (System.nanoTime() - prepareTime);

			long linesTime = System.nanoTime();
			int numLines = (lastLine - firstLine + 1);
			int y = firstLine * lineHeight;
			gfx.fillRect(0, y, getWidth(), numLines * lineHeight);
			extensionMgr.paintScreenLineRange(textArea, gfx, firstLine, lastLine, y, lineHeight);
			linesTime = (System.nanoTime() - linesTime);

			if (Debug.PAINT_TIMER && numLines >= 1)
				Log.log(Log.DEBUG, this, "repainting " + numLines + " lines took " + prepareTime + "/" + linesTime + " ns");
		}

		textArea.updateMaxHorizontalScrollWidth();
	} // }}}
		// {{{ removeExtension() method

	/**
	 * Removes a text area extension. It will no longer be asked to perform
	 * custom painting and tool tip handling.
	 * 
	 * @param extension
	 *            The extension
	 * @since jEdit 4.0pre4
	 */
	public void removeExtension(TextAreaExtension extension)
	{
		extensionMgr.removeExtension(extension);
		repaint();
	} // }}}

	/**
	 * As of jEdit 4.3pre4, a new JDK 1.6 subpixel antialias mode is supported.
	 * 
	 * @since jEdit 4.2pre4
	 */
	public void setAntiAlias(AntiAlias newValue)
	{
		this.antiAlias = newValue;
		updateRenderingHints();
	} // }}}
		// {{{ setAntiAliasEnabled() method

	/**
	 * @deprecated use setAntiAlias(AntiAlias newMode)
	 */
	@Deprecated
	public void setAntiAliasEnabled(boolean isEnabled)
	{

		setAntiAlias(new AntiAlias(isEnabled));
	}

	// {{{ setBlockCaretEnabled() method
	/**
	 * Sets if the caret should be drawn as a block, false otherwise.
	 * 
	 * @param blockCaret
	 *            True if the caret should be drawn as a block, false otherwise.
	 */
	public final void setBlockCaretEnabled(boolean blockCaret)
	{
		this.blockCaret = blockCaret;
		extensionMgr.removeExtension(caretExtension);
		if (blockCaret)
			addExtension(BLOCK_CARET_LAYER, caretExtension);
		else
			addExtension(CARET_LAYER, caretExtension);
		if (textArea.getBuffer() != null)
			textArea.invalidateLine(textArea.getCaretLine());
	} // }}}
		// {{{ setBounds() method

	/**
	 * It is a bad idea to override this, but we need to get the component event
	 * before the first repaint.
	 */
	@Override
	public void setBounds(int x, int y, int width, int height)
	{
		if (x == getX() && y == getY() && width == getWidth() && height == getHeight())
		{
			return;
		}

		super.setBounds(x, y, width, height);

		textArea.recalculateVisibleLines();
		if (!textArea.getBuffer().isLoading())
			textArea.recalculateLastPhysicalLine();
		textArea.propertiesChanged();
		textArea.updateMaxHorizontalScrollWidth();
		textArea.scrollBarsInitialized = true;
	} // }}}
		// {{{ setCaretColor() method

	/**
	 * Sets the caret color.
	 * 
	 * @param caretColor
	 *            The caret color
	 */
	public final void setCaretColor(Color caretColor)
	{
		this.caretColor = caretColor;
		if (textArea.getBuffer() != null)
			textArea.invalidateLine(textArea.getCaretLine());
	} // }}}

	// {{{ setEOLMarkerColor() method
	/**
	 * Sets the EOL marker color.
	 * 
	 * @param eolMarkerColor
	 *            The EOL marker color
	 */
	public final void setEOLMarkerColor(Color eolMarkerColor)
	{
		this.eolMarkerColor = eolMarkerColor;
		repaint();
	} // }}}

	// {{{ setEOLMarkersPainted() method
	/**
	 * Sets if EOL markers are to be drawn.
	 * 
	 * @param eolMarkers
	 *            True if EOL markers should be drawn, false otherwise
	 */
	public final void setEOLMarkersPainted(boolean eolMarkers)
	{
		this.eolMarkers = eolMarkers;
		repaint();
	} // }}}

	// }}}

	// {{{ Private members

	// {{{ setFoldLineStyle() method
	/**
	 * Sets the fold line style. The first element is the style for lines with a
	 * fold level greater than 3. The remaining elements are for fold levels 1
	 * to 3.
	 * 
	 * @param foldLineStyle
	 *            The fold line style
	 */
	public final void setFoldLineStyle(SyntaxStyle[] foldLineStyle)
	{
		this.foldLineStyle = foldLineStyle;
		repaint();
	} // }}}
		// {{{ setFont() method

	/**
	 * Sets the font for this component. This is overridden to update the cached
	 * font metrics and to recalculate which lines are visible.
	 * 
	 * @param font
	 *            The font
	 */
	@Override
	public void setFont(Font font)
	{
		super.setFont(font);
		fm = getFontMetrics(font);
		textArea.recalculateVisibleLines();
		if (textArea.getBuffer() != null && !textArea.getBuffer().isLoading())
			textArea.recalculateLastPhysicalLine();
		// textArea.propertiesChanged();
	} // }}}
		// {{{ setFractionalFontMetricsEnabled() method

	/**
	 * Sets if fractional font metrics should be enabled. Has no effect when
	 * running on Java 1.1.
	 * 
	 * @since jEdit 3.2pre6
	 */
	public void setFractionalFontMetricsEnabled(boolean fracFontMetrics)
	{
		this.fracFontMetrics = fracFontMetrics;
		updateRenderingHints();
	} // }}}
		// {{{ setLineHighlightColor() method

	/**
	 * Sets the line highlight color.
	 * 
	 * @param lineHighlightColor
	 *            The line highlight color
	 */
	public final void setLineHighlightColor(Color lineHighlightColor)
	{
		this.lineHighlightColor = lineHighlightColor;
		if (textArea.getBuffer() != null)
			textArea.invalidateLine(textArea.getCaretLine());
	} // }}}
		// {{{ setLineHighlightEnabled() method

	/**
	 * Enables or disables current line highlighting.
	 * 
	 * @param lineHighlight
	 *            True if current line highlight should be enabled, false
	 *            otherwise
	 */
	public final void setLineHighlightEnabled(boolean lineHighlight)
	{
		this.lineHighlight = lineHighlight;
		textArea.repaint();
	} // }}}

	// {{{ setMultipleSelectionColor() method
	/**
	 * Sets the multiple selection color.
	 * 
	 * @param multipleSelectionColor
	 *            The multiple selection color
	 * @since jEdit 4.2pre1
	 */
	public final void setMultipleSelectionColor(Color multipleSelectionColor)
	{
		this.multipleSelectionColor = multipleSelectionColor;
		textArea.repaint();
	} // }}}
		// {{{ setSelectionColor() method

	/**
	 * Sets the selection color.
	 * 
	 * @param selectionColor
	 *            The selection color
	 */
	public final void setSelectionColor(Color selectionColor)
	{
		this.selectionColor = selectionColor;
		textArea.repaint();
	} // }}}

	// {{{ setStructureHighlightColor() method
	/**
	 * Sets the structure highlight color.
	 * 
	 * @param structureHighlightColor
	 *            The bracket highlight color
	 * @since jEdit 4.2pre3
	 */
	public final void setStructureHighlightColor(Color structureHighlightColor)
	{
		this.structureHighlightColor = structureHighlightColor;
		textArea.invalidateStructureMatch();
	} // }}}
		// {{{ setStructureHighlightEnabled() method

	/**
	 * Enables or disables structure highlighting.
	 * 
	 * @param structureHighlight
	 *            True if structure highlighting should be enabled, false
	 *            otherwise
	 * @since jEdit 4.2pre3
	 */
	public final void setStructureHighlightEnabled(boolean structureHighlight)
	{
		this.structureHighlight = structureHighlight;
		textArea.invalidateStructureMatch();
	} // }}}

	// }}}

	// {{{ Inner classes

	// {{{ setStyles() method
	/**
	 * Sets the syntax styles used to paint colorized text. Entry <i>n</i> will
	 * be used to paint tokens with id = <i>n</i>.
	 * 
	 * @param styles
	 *            The syntax styles
	 * @see bsh.syntax.Token
	 */
	public final void setStyles(SyntaxStyle[] styles)
	{
		// assumed this is called after a font render context is set up.
		// changing font render context settings without a setStyles()
		// call will not reset cached monospaced font info.
		fonts.clear();

		this.styles = styles;
		styles[Token.NULL] = new SyntaxStyle(getForeground(), null, getFont());
		repaint();
	} // }}}

	// {{{ setThickCaretEnabled() method
	/**
	 * Sets if the caret should be drawn with a thick line.
	 * 
	 * @param thickCaret
	 *            True if the caret should be drawn as a block, false otherwise.
	 * @since jEdit 4.3pre15
	 */
	public final void setThickCaretEnabled(boolean thickCaret)
	{
		this.thickCaret = thickCaret;
		if (textArea.getBuffer() != null)
			textArea.invalidateLine(textArea.getCaretLine());
	} // }}}

	// {{{ setWrapGuideColor() method
	/**
	 * Sets the wrap guide color.
	 * 
	 * @param wrapGuideColor
	 *            The wrap guide color
	 */
	public final void setWrapGuideColor(Color wrapGuideColor)
	{
		this.wrapGuideColor = wrapGuideColor;
		repaint();
	} // }}}

	// {{{ setWrapGuidePainted() method
	/**
	 * Sets if the wrap guide is to be drawn.
	 * 
	 * @param wrapGuide
	 *            True if the wrap guide should be drawn, false otherwise
	 */
	public final void setWrapGuidePainted(boolean wrapGuide)
	{
		this.wrapGuide = wrapGuide;
		repaint();
	} // }}}

	// {{{ update() method
	/**
	 * Repaints the text.
	 * 
	 * @param _gfx
	 *            The graphics context
	 */
	@Override
	public void update(Graphics _gfx)
	{
		paint(_gfx);
	} // }}}

	// }}}
}
