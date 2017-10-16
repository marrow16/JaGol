package com.foo.gol.ui;

import com.foo.gol.config.GameConfig;

public class GridPosition {
	private int row;
	private int column;

	public GridPosition(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public static GridPosition fromXYCoordinate(GameConfig drawingConfig, double x, double y) {
		int cellSize = drawingConfig.getCellSize();
		int cellSpacing = cellSize + drawingConfig.getCellSpace();
		int row = Math.min((int)(y / cellSpacing), drawingConfig.getRows() - 1);
		int column = Math.min((int) (x / cellSpacing), drawingConfig.getColumns() - 1);
		return new GridPosition(row, column);
	}

	public boolean equals(Object other) {
		if (other != null && other instanceof GridPosition) {
			GridPosition otherPosition = (GridPosition)other;
			if (row == otherPosition.row && column == otherPosition.column) {
				return true;
			}
		}
		return false;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
}
