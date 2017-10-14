package com.foo.gol.ui;

import com.foo.gol.logic.BoardWrappingMode;
import javafx.scene.paint.Color;

public class BoardDrawingConfig {
	private int columns = 60;
	private int rows = 60;
	private BoardWrappingMode wrappingMode = BoardWrappingMode.NONE;
	private boolean deadCellEdges = false;
	private int cellSize = 6;
	private int cellSpace = 1;
	private Color cellActiveColor = Color.DODGERBLUE;
	private Color cellInactiveColor = Color.WHITE;
	private Color cellGridColor = Color.LIGHTGRAY;

	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}

	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCellSize() {
		return cellSize;
	}
	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

	public int getCellSpace() {
		return cellSpace;
	}
	public void setCellSpace(int cellSpace) {
		this.cellSpace = cellSpace;
	}

	public Color getCellActiveColor() {
		return cellActiveColor;
	}
	public void setCellActiveColor(Color cellActiveColor) {
		this.cellActiveColor = cellActiveColor;
	}

	public Color getCellInactiveColor() {
		return cellInactiveColor;
	}
	public void setCellInactiveColor(Color cellInactiveColor) {
		this.cellInactiveColor = cellInactiveColor;
	}

	public Color getCellGridColor() {
		return cellGridColor;
	}
	public void setCellGridColor(Color cellGridColor) {
		this.cellGridColor = cellGridColor;
	}

	public BoardWrappingMode getWrappingMode() {
		return wrappingMode;
	}
	public void setWrappingMode(BoardWrappingMode wrappingMode) {
		this.wrappingMode = wrappingMode;
	}

	public boolean isDeadCellEdges() {
		return deadCellEdges;
	}
	public void setDeadCellEdges(boolean deadCellEdges) {
		this.deadCellEdges = deadCellEdges;
	}
}
