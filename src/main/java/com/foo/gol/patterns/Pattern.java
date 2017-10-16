package com.foo.gol.patterns;

import com.foo.gol.logic.Cell;
import com.foo.gol.logic.ICell;
import com.foo.gol.logic.rule.IChangeAliveRule;
import com.foo.gol.config.GameConfig;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Pattern implements IPattern {
	protected String name;
	protected int columns;
	protected int rows;
	protected List<ICell> cells;

	protected List<String> comments = new ArrayList<>();
	protected String origination;
	protected String coordinates;
	protected IChangeAliveRule rule;

	public Pattern(String name, int columns, int[] pattern) {
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

	@Override
	public int columns() {
		return columns;
	}

	@Override
	public int rows() {
		return rows;
	}

	@Override
	public List<ICell> cells() {
		return cells;
	}

	@Override
	public ICell cell(int row, int column) {
		if (row < 0 || row >= rows || column < 0 || column >= columns) {
			throw new IndexOutOfBoundsException("Row/column is out of bounds");
		}
		return cells.get((row * columns) + column);
	}

	@Override
	public PatternVBox generateDisplay(GameConfig drawingConfig) {
		return new PatternVBox(this, drawingConfig);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<String> getComments() {
		return comments;
	}

	@Override
	public String getOrigination() {
		return origination;
	}

	@Override
	public void setOrigination(String origination) {
		this.origination = origination;
	}

	@Override
	public String getCoordinates() {
		return coordinates;
	}

	@Override
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public IChangeAliveRule getRule() {
		return rule;
	}

	@Override
	public void setRule(IChangeAliveRule rule) {
		this.rule = rule;
	}

	@Override
	public void rotate90Clockwise() {
		List<ICell> newCells = new ArrayList<>();
		for (int column = 0; column < columns; column++) {
			for (int row = rows - 1; row >= 0; row--) {
				newCells.add(cells.get((row * columns) + column));
			}
		}
		cells = newCells;
		int wasRows = rows;
		rows = columns;
		columns = wasRows;
	}

	@Override
	public void rotate90AntiClockwise() {
		List<ICell> newCells = new ArrayList<>();
		for (int column = columns - 1; column >= 0; column--) {
			for (int row = 0; row < rows; row++) {
				newCells.add(cells.get((row * columns) + column));
			}
		}
		cells = newCells;
		int wasRows = rows;
		rows = columns;
		columns = wasRows;
	}

	@Override
	public void flipVertically() {
		List<ICell> newCells = new ArrayList<>();
		for (int row = rows - 1; row >= 0; row--) {
			for (int column = 0; column < columns; column++) {
				newCells.add(cells.get((row * columns) + column));
			}
		}
		cells = newCells;
	}

	@Override
	public void flipHorizontally() {
		List<ICell> newCells = new ArrayList<>();
		for (int row = 0; row < rows; row++) {
			for (int column = columns - 1; column >= 0; column--) {
				newCells.add(cells.get((row * columns) + column));
			}
		}
		cells = newCells;
	}
}
