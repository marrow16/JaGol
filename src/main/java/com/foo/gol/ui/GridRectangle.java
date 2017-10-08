package com.foo.gol.ui;

import com.foo.gol.logic.Cell;
import com.foo.gol.logic.IBoard;
import com.foo.gol.logic.ICell;

import java.util.ArrayList;
import java.util.List;

public class GridRectangle {
	private GridPosition startPosition;
	private GridPosition endPosition;
	private List<ICell> cells;

	public GridRectangle(GridPosition startPosition, GridPosition endPosition) {
		int minRow = Math.min(startPosition.getRow(), endPosition.getRow());
		int minColumn = Math.min(startPosition.getColumn(), endPosition.getColumn());
		int maxRow = Math.max(startPosition.getRow(), endPosition.getRow());
		int maxColumn = Math.max(startPosition.getColumn(), endPosition.getColumn());
		this.startPosition = new GridPosition(minRow, minColumn);
		this.endPosition = new GridPosition(maxRow, maxColumn);
	}

	public void setCellsFromBoard(IBoard board) {
		cells = new ArrayList<>();
		for (int row = startPosition.getRow(); row <= endPosition.getRow(); row++) {
			for (int column = startPosition.getColumn(); column <= endPosition.getColumn(); column++) {
				cells.add(Cell.createCell(row - startPosition.getRow(), column - startPosition.getColumn(), board.cellAlive(row, column)));
			}
		}
	}

	public GridPosition getStartPosition() {
		return startPosition;
	}

	public GridPosition getEndPosition() {
		return endPosition;
	}

	public int getStartRow() {
		return startPosition.getRow();
	}

	public int getEndRow() {
		return endPosition.getRow();
	}

	public int getStartColumn() {
		return startPosition.getColumn();
	}

	public int getEndColumn() {
		return endPosition.getColumn();
	}

	public int getRows() {
		return (endPosition.getRow() - startPosition.getRow()) + 1;
	}

	public int getColumns() {
		return (endPosition.getColumn() - startPosition.getColumn()) + 1;
	}

	public List<ICell> getCells() {
		return cells;
	}
}
