package com.adeptions.jagol.logic;

import com.adeptions.jagol.patterns.IPattern;
import com.sun.istack.internal.NotNull;

public class Board implements IBoard {
	private ICell[][] cells;
	private int width;
	private int height;
	private IGenerationController generationController;
	private BoardWrappingMode wrappingMode = BoardWrappingMode.NONE;
	private boolean deadCellEdges = false;

	public Board(int width, int height, BoardWrappingMode wrappingMode, boolean deadCellEdges, @NotNull IGenerationController generationController) {
		this(width, height, wrappingMode, deadCellEdges, generationController, null);
	}

	public Board(int width, int height, BoardWrappingMode wrappingMode, boolean deadCellEdges, @NotNull IGenerationController generationController, int[] initialCells) {
		if (generationController == null) {
			throw new IllegalArgumentException("Generation controller may not be null");
		}
		this.generationController = generationController;
		this.wrappingMode = (wrappingMode == null ? BoardWrappingMode.NONE : wrappingMode);
		this.deadCellEdges = deadCellEdges;
		if (width <= 0) {
			throw new IllegalArgumentException("Board cannot have a columns of less than or equal to zero");
		}
		if (height <= 0) {
			throw new IllegalArgumentException("Board cannot have a rows of less than or equal to zero");
		}
		this.width = width;
		this.height = height;
		int[] initialCellStates = initialCells;
		if (initialCellStates == null) {
			initialCellStates = new int[width * height];
		} else if (initialCells.length != (width * height)) {
			throw new IllegalArgumentException("Initial cells must be of length " + (width * height) + " for grid size " + width + " X " + height);
		}
		initialise(initialCellStates);
	}

	private void initialise(int[] initialCells) {
		generationController.startInitialisation();
		cells = new Cell[height][width];

		int maxColIndex = width - 1;
		for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				ICell cell = Cell.createCell(row, column, initialCells[(row * width) + column] != 0);
				generationController.addControlledCell(cell);
				cells[row][column] = cell;
				if (row > 0) {
					Cell.makeTwoCellsAdjacent(cell, cells[row - 1][column]);
					if (column > 0) {
						Cell.makeTwoCellsAdjacent(cell, cells[row][column - 1]);
						Cell.makeTwoCellsAdjacent(cell, cells[row - 1][column - 1]);
					}
					if (column < maxColIndex) {
						Cell.makeTwoCellsAdjacent(cell, cells[row - 1][column + 1]);
					}
				} else if (column > 0) {
					Cell.makeTwoCellsAdjacent(cell, cells[row][column - 1]);
				}
			}
		}
		switch (wrappingMode) {
			case VERTICAL:
				adjacentTopAndBottomRows();
				break;
			case HORIZONTAL:
				adjacentLeftAndRightColumns();
				break;
			case BOTH:
				adjacentFullWrapping();
		}
		if (deadCellEdges) {
			ICell deadCell;
			switch (wrappingMode) {
				case VERTICAL:
					addDeadCellsLeftAndRight();
					deadCell = new DeadCell();
					Cell.makeTwoCellsAdjacent(cells[0][0], deadCell);
					Cell.makeTwoCellsAdjacent(cells[0][width - 1], deadCell);
					Cell.makeTwoCellsAdjacent(cells[height - 1][0], deadCell);
					Cell.makeTwoCellsAdjacent(cells[height - 1][width - 1], deadCell);
					break;
				case HORIZONTAL:
					addDeadCellsTopAndBottom();
					deadCell = new DeadCell();
					Cell.makeTwoCellsAdjacent(cells[0][0], deadCell);
					Cell.makeTwoCellsAdjacent(cells[0][width - 1], deadCell);
					Cell.makeTwoCellsAdjacent(cells[height - 1][0], deadCell);
					Cell.makeTwoCellsAdjacent(cells[height - 1][width - 1], deadCell);
					break;
				case NONE:
					addDeadCellsAllAround();
					break;
			}
		}
		generationController.endInitialisation();
	}

	private void adjacentTopAndBottomRows() {
		int lastRow = height - 1;
		int lastColumn = width - 1;
		for (int column = 0; column < width; column++) {
			Cell.makeTwoCellsAdjacent(cells[0][column], cells[lastRow][column]);
			if (column > 0) {
				Cell.makeTwoCellsAdjacent(cells[0][column], cells[lastRow][column - 1]);
			}
			if (column < lastColumn) {
				Cell.makeTwoCellsAdjacent(cells[0][column], cells[lastRow][column + 1]);
			}
		}
	}

	private void adjacentLeftAndRightColumns() {
		int lastColumn = width - 1;
		int lastRow = height - 1;
		for (int row = 0; row < height; row++) {
			Cell.makeTwoCellsAdjacent(cells[row][0], cells[row][lastColumn]);
			if (row > 0) {
				Cell.makeTwoCellsAdjacent(cells[row][0], cells[row - 1][lastColumn]);
			}
			if (row < lastRow) {
				Cell.makeTwoCellsAdjacent(cells[row][0], cells[row + 1][lastColumn]);
			}
		}
	}

	private void adjacentFullWrapping() {
		adjacentTopAndBottomRows();
		adjacentLeftAndRightColumns();
		int lastColumn = width - 1;
		int lastRow = height - 1;
		Cell.makeTwoCellsAdjacent(cells[0][0], cells[lastRow][lastColumn]);
		Cell.makeTwoCellsAdjacent(cells[0][lastColumn], cells[lastRow][0]);
	}

	private void addDeadCellsLeftAndRight() {
		ICell deadCell = new DeadCell();
		int lastColumn = width - 1;
		int lastRow = height - 1;
		for (int row = 0; row < height; row++) {
			Cell.makeTwoCellsAdjacent(cells[row][0], deadCell);
			Cell.makeTwoCellsAdjacent(cells[row][0], deadCell);
			Cell.makeTwoCellsAdjacent(cells[row][lastColumn], deadCell);
			Cell.makeTwoCellsAdjacent(cells[row][lastColumn], deadCell);
			if (row != 0 && row != lastRow) {
				Cell.makeTwoCellsAdjacent(cells[row][0], deadCell);
				Cell.makeTwoCellsAdjacent(cells[row][lastColumn], deadCell);
			}
		}
	}

	private void addDeadCellsTopAndBottom() {
		ICell deadCell = new DeadCell();
		int lastColumn = width - 1;
		int lastRow = height - 1;
		for (int column = 0; column < height; column++) {
			Cell.makeTwoCellsAdjacent(cells[0][column], deadCell);
			Cell.makeTwoCellsAdjacent(cells[0][column], deadCell);
			Cell.makeTwoCellsAdjacent(cells[lastRow][column], deadCell);
			Cell.makeTwoCellsAdjacent(cells[lastRow][column], deadCell);
			if (column != 0 && column != lastColumn) {
				Cell.makeTwoCellsAdjacent(cells[0][column], deadCell);
				Cell.makeTwoCellsAdjacent(cells[lastRow][column], deadCell);
			}
		}
	}

	private void addDeadCellsAllAround() {
		addDeadCellsLeftAndRight();
		addDeadCellsTopAndBottom();
		ICell deadCell = new DeadCell();
		Cell.makeTwoCellsAdjacent(cells[0][0], deadCell);
		Cell.makeTwoCellsAdjacent(cells[0][width - 1], deadCell);
		Cell.makeTwoCellsAdjacent(cells[height - 1][0], deadCell);
		Cell.makeTwoCellsAdjacent(cells[height - 1][width - 1], deadCell);
	}

	@Override
	public IGenerationController generationController() {
		return generationController;
	}

	@Override
	public ICell cell(int row, int column) {
		if (row < 0 || row >= height || column < 0 || column >= width) {
			throw new IndexOutOfBoundsException("Row/column is out of bounds");
		}
		return cells[row][column];
	}

	@Override
	public boolean cellAlive(int row, int column) {
		return cell(row, column).isAlive();
	}

	@Override
	public void drawPattern(int atRow, int atColumn, IPattern pattern) {
		if (atRow < 0 || atRow >= height || atColumn < 0 || atColumn >= width) {
			throw new IndexOutOfBoundsException("Row/column is out of bounds");
		}
		for (int row = 0; row < pattern.rows(); row++) {
			if ((row + atRow) < height) {
				for (int column = 0; column < pattern.columns(); column++) {
					if ((column + atColumn) < width) {
						cells[row + atRow][column + atColumn].isAlive(pattern.cell(row, column).isAlive());
					}
				}
			}
		}
	}
}
