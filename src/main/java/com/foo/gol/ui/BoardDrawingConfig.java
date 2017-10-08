package com.foo.gol.ui;

import javafx.scene.paint.Color;

public class BoardDrawingConfig {
	private int columns = 50;
	private int rows = 50;
	private int cellSize = 8;
	private int cellSpace = 1;
	private Color cellActiveColor = Color.DODGERBLUE;
	private Color cellInactiveColor = Color.WHITE;
	private Color cellGridColor = Color.SLATEGRAY;

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
}
