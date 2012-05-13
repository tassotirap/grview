package org.grview.canvas.state;

public class Preferences extends CanvasSerializableElement {
	
	private String moveStrategy;
	private String connectionStrategy;
	private int showStatus;
	
	/**
	 * @return the showLines
	 */
	public boolean isShowLines() {
		return showLines;
	}
	/**
	 * @param showLines the showLines to set
	 */
	public void setShowLines(boolean showLines) {
		this.showLines = showLines;
	}
	/**
	 * @return the showGuide
	 */
	public boolean isShowGuide() {
		return showGuide;
	}
	/**
	 * @param showGuide the showGuide to set
	 */
	public void setShowGuide(boolean showGuide) {
		this.showGuide = showGuide;
	}
	/**
	 * @return the showGrid
	 */
	public boolean isShowGrid() {
		return showGrid;
	}
	/**
	 * @param showGrid the showGrid to set
	 */
	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}
	private boolean showLines;
	private boolean showGuide;
	private boolean showGrid;
	/**
	 * @return the moveStrategy
	 */
	public String getMoveStrategy() {
		return moveStrategy;
	}
	/**
	 * @param moveStrategy the moveStrategy to set
	 */
	public void setMoveStrategy(String moveStrategy) {
		this.moveStrategy = moveStrategy;
	}
	/**
	 * @return the connectionStrategy
	 */
	public String getConnectionStrategy() {
		return connectionStrategy;
	}
	/**
	 * @param connectionStrategy the connectionStrategy to set
	 */
	public void setConnectionStrategy(String connectionStrategy) {
		this.connectionStrategy = connectionStrategy;
	}
	/**
	 * @return the showStatus
	 */
	public int getShowStatus() {
		return showStatus;
	}
	/**
	 * @param showStatus the showStatus to set
	 */
	public void setShowStatus(int showStatus) {
		this.showStatus = showStatus;
	}

}
