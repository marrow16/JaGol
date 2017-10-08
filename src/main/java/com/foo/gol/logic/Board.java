package com.foo.gol.logic;

public class Board implements IBoard {
	private Cell[][] cells;
	private int width;
	private int height;
	private IGenerationController generationController;

	public Board(int width, int height) {
		this(width, height, null, null);
	}

	public Board(int width, int height, IGenerationController generationController) {
		this(width, height, null, generationController);
	}

	public Board(int width, int height, int[] initialCells) {
		this(width, height, initialCells, null);
	}

	public Board(int width, int height, int[] initialCells, IGenerationController generationController) {
		if (generationController == null) {
			this.generationController = new ChangeOnlyGenerationController();
		} else {
			this.generationController = generationController;
		}
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
				Cell cell = Cell.createCell(row, column, initialCells[(row * width) + column] != 0);
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

		generationController.endInitialisation();
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
}
