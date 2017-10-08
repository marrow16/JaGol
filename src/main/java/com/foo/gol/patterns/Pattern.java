package com.foo.gol.patterns;

import com.foo.gol.logic.Cell;
import com.foo.gol.logic.ICell;
import com.foo.gol.ui.BoardDrawingConfig;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Pattern implements IPattern {
	protected String name;
	protected int columns;
	protected int rows;
	protected List<ICell> cells;

	Pattern(String name, int columns, int[] pattern) {
		this.name = name;
		if (columns < 0) {
			throw new IllegalArgumentException("Pattern cannot have zero columns");
		}
		this.columns = columns;
		if (pattern.length < this.columns || (pattern.length % this.columns) != 0) {
			throw new IllegalArgumentException("Pattern initializer has incorrect length");
		}
		this.columns = columns;
		this.rows = pattern.length / this.columns;
		cells = new ArrayList<>();
		for (int row = 0; row < this.rows; row++) {
			for (int column = 0; column < this.columns; column++) {
				cells.add(Cell.createCell(row, column, pattern[(row * this.columns) + column] != 0));
			}
		}
	}

	public String name() {
		return name;
	}

	public int columns() {
		return columns;
	}

	public int rows() {
		return rows;
	}

	public List<ICell> cells() {
		return cells;
	}

	public ICell cell(int row, int column) {
		if (row < 0 || row >= rows || column < 0 || column >= columns) {
			throw new IndexOutOfBoundsException("Row/column is out of bounds");
		}
		return cells.get((row * columns) + column);
	}

	public PatternVBox generateDisplay(BoardDrawingConfig drawingConfig) {
		PatternVBox result = new PatternVBox(this);
		int cellSize = drawingConfig.getCellSize();
		int cellSpace = drawingConfig.getCellSpace();
		int cellSpacing = cellSize + cellSpace;
		int canvasWidth = 2 + (columns * cellSize) + ((columns - 1) * cellSpace);
		int canvasHeight = 2 + (rows * cellSize) + ((rows - 1) * cellSpace);
		Canvas canvas = new Canvas(
				2 + (columns * cellSize) + ((columns - 1) * cellSpace),
				2 + (rows * cellSize) + ((rows - 1) * cellSpace)
		);
		result.getChildren().add(canvas);
		Label label = new Label(name);
		result.getChildren().add(label);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(drawingConfig.getCellInactiveColor());
		gc.fillRect(0,0, canvasWidth, canvasHeight);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		gc.strokeRect(0,0, canvasWidth, canvasHeight);
		if (cellSpace > 0) {
			gc.setStroke(drawingConfig.getCellGridColor());
			for (int r = 1; r < rows; r++) {
				gc.strokeLine(1.5d, (r * cellSpacing) + 0.5d, canvasWidth - 1.5d, (r * cellSpacing) + 0.5d);
			}
			for (int c = 1; c < columns; c++) {
				gc.strokeLine((c * cellSpacing) + 0.5d, 1.5d, (c * cellSpacing) + 0.5d, canvasHeight - 1.5d);
			}
		}
		gc.setFill(drawingConfig.getCellActiveColor());
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				ICell cell = cells.get((row * columns) + column);
				if (cell.isAlive()) {
					gc.fillRect((column * cellSpacing) + 1, (row * cellSpacing) + 1, cellSize, cellSize);
				}
			}
		}
		return result;
	}
}
