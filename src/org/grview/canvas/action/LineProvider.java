package org.grview.canvas.action;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.grview.canvas.Canvas;
import org.grview.canvas.widget.GuideLineWidget;
import org.grview.canvas.widget.LineWidget;


/**
 * Responsible for creating and positioning instances of LineWidget.
 * @author Gustavo H. Braga
 *
 */
public class LineProvider implements PropertyChangeListener {

	private Canvas canvas;
	private HashMap<Integer, LineWidget> lines;
	private int lastYPos;
	private LineWidget lastLine;
	private LineWidget guideLine;
	private boolean populated = false;
	
	private static HashMap<Canvas, LineProvider> lineProviders = new HashMap<Canvas, LineProvider>();
	
	/** the distance between lines **/
	public final static int LINE_OFFSET = 25;
	
	private LineProvider (Canvas canvas) {
		this.canvas = canvas;
		lines = new HashMap<Integer, LineWidget>();
	}
	
	public static LineProvider getInstance(Canvas canvas) {
		if (!lineProviders.containsKey(canvas)) {
			lineProviders.put(canvas, new LineProvider(canvas));
		}
		return lineProviders.get(canvas);
	}
	/**
	 * Insert a new guide line
	 * @param xPos the x position of the guide line, if null, uses the mouse x position
	 */
	public LineWidget insertGuideLine(Integer xPos) {
		LineWidget lWidget = (LineWidget)canvas.addNode(GuideLineWidget.class.getCanonicalName());
		if (xPos == null) {
			lWidget.setPreferredLocation(new Point(GuideLineWidget.DEFAULT_X_POS, 0));
		}
		else {
			lWidget.setPreferredLocation(new Point(xPos,0));
		}
		guideLine = lWidget;
		canvas.repaint();
		return guideLine;
	}
	
	public void setGuideVisible(boolean visible) {
		if (guideLine != null) {
			guideLine.setVisible(visible);
		}
		else if (visible == true) {
			guideLine = insertGuideLine(null);
		}
		canvas.setShowingGuide(visible);
		canvas.repaint();
	}
	
	public boolean isGuideVisible() {
		if (guideLine == null) {
			return false;
		}
		else
			return guideLine.isVisible();
	}
	/**
	 * Insert a new line. The position on the x-axis is assumed to be 0.
	 * @param yPos the position on the y-axis. 
	 * @param lineNumber the number of the line
	 */
	public synchronized LineWidget insertLine(Integer yPos, Integer lineNumber) {
		if (yPos == null) {
			yPos = lastYPos;
		}
		if (lineNumber == null) {
			lineNumber = lines.size() + 1;
		}
		LineWidget lWidget = (LineWidget)canvas.addNode(LineWidget.class.getCanonicalName() + lineNumber);
		lWidget.setPreferredLocation(new Point(0,yPos));
		lWidget.setNumber(lineNumber);
		lines.put(yPos,lWidget);
		lastYPos = yPos + LineWidget.DEFAULT_HEIGHT + LINE_OFFSET;
		if (lastLine != null) {
			lastLine.getMonitor().removePropertyChangeListener(this);
		}
		lWidget.getMonitor().addPropertyChangeListener(this);
		lastLine = lWidget;
		return lastLine;
	}
	
	/**
	 * Remove all lines exhibited on the canvas
	 */
	public void removeAllLines() {
		for (LineWidget lw : lines.values()) {
			lw.getMonitor().removePropertyChangeListener(this);
			canvas.removeNodeSafely(LineWidget.class.getCanonicalName() + lw.getNumber());
		}
		lines.clear();
		populated = false;
	}
	
	/**
	 * remove the guide line
	 */
	public void removeGuideLine() {
		canvas.removeNodeSafely(GuideLineWidget.class.getCanonicalName());
		guideLine = null;
	}
	
	/**
	 * Insert new lines to cover the entire canvas
	 */
	public void populateCanvas() {
		int yPos = LINE_OFFSET;
		int lineNumber = 1;
		int linesToInsert = calculateCanvasHeight() / (LineWidget.DEFAULT_HEIGHT + LINE_OFFSET);
		removeAllLines();
		while (linesToInsert > 0) {
			insertLine(yPos, lineNumber);
			lineNumber++;
			yPos += LineWidget.DEFAULT_HEIGHT + LINE_OFFSET;
			linesToInsert--;
		}
		populated = true;
		canvas.repaint();
	}
	
	/**
	 * Calculate the actual canvas height. If the canvas doesn't have the bounds set yet,
	 * this method deduces the canvas height from the height the Container that holds it.
	 * @return the height of the canvas
	 */
	private int calculateCanvasHeight() {
		int height = (canvas.getBounds() == null)?
				canvas.getView().getParent().getHeight():
				canvas.getBounds().height;
		return height;
	}
	
	/**
	 * estimates the number of lines inserted in the canvas
	 * @return the number of lines in the canvas
	 */
	public int lineCnt() {
		return lines.size();
	}
	
	/**
	 * estimates the number of additional lines necessary to fill the entire canvas
	 * @return the number of lines to fill the canvas
	 */
	public int linesToFillCanvas() {
		return (calculateCanvasHeight() - (lines.size() * (LineWidget.DEFAULT_HEIGHT + LINE_OFFSET))) / (LineWidget.DEFAULT_HEIGHT + LINE_OFFSET);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (populated) {
			if (evt.getSource() instanceof LineWidget) {
				if (evt.getPropertyName().equals(LineWidget.LINE_PAINTED_EVENT)) {
					int lineNumber = (Integer)evt.getNewValue();
					if (lineNumber == lineCnt()) {
						Thread t1 = new Thread() {
							@Override
							public void run() {
								while (linesToFillCanvas() > 0) {
									insertLine(null, null);
								}
								canvas.repaint();
								canvas.validate();
							}
						};
						t1.start();
					}
				}
			}
		}
	}
	
	public LineWidget getGuideLine() {
		return guideLine;
	}
	
	/**
	 * calculates the closest line to the given y position
	 * @param y the position close to the line
	 * @return the closest line to the y coordinate
	 */
	public LineWidget getLine(int y) {
		LineWidget ltop;
		LineWidget ldown;
		int diff1 = 0;
		int diff2 = 0;
		if (y > LINE_OFFSET) {
			if (y <= lastYPos - (LineWidget.DEFAULT_HEIGHT + LINE_OFFSET)) {
				while(!lines.containsKey(y - diff1) && y - diff1 >= LINE_OFFSET) {
					diff1++;
				}
				ltop = 	lines.get(y - diff1);
				while(!lines.containsKey(y + diff2) 
						&& y + diff2 <= lastYPos - (LineWidget.DEFAULT_HEIGHT + LINE_OFFSET) && diff2 < diff1) {
					diff2++;
				}
				ldown = lines.get(y + diff2);
				if (diff2 >= diff1){
					return ltop;
				}
				else {
					return ldown;
				}
			}
			else {
				if (y >= lastYPos) {
					insertLine(null,null);
					canvas.repaint();
					return lastLine;
				}
				else if (lines.containsKey(lastYPos - (LineWidget.DEFAULT_HEIGHT + LINE_OFFSET))) {
					return lines.get(lastYPos - (LineWidget.DEFAULT_HEIGHT + LINE_OFFSET));
				}
			}
		}
		else {
			if (lines.containsKey(LINE_OFFSET)) {
				return lines.get(LINE_OFFSET);
			}
		}
		return null;
	}
}
