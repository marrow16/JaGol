package com.foo.gol.logic;

import com.foo.gol.logic.rule.StandardConways;
import junit.framework.TestCase;

public class BoardWrappingTests extends TestCase {
	public void testVerticalWrapping() {
		IBoard board = new Board(5, 5, BoardWrappingMode.VERTICAL, false, new FullScanGenerationController(new StandardConways()));
		int[][] expectedCounts = new int[][] {
				new int[] {5,8,8,8,5},
				new int[] {5,8,8,8,5},
				new int[] {5,8,8,8,5},
				new int[] {5,8,8,8,5},
				new int[] {5,8,8,8,5}
		};
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				int expected = expectedCounts[row][column];
				assertEquals("Cell (row=" + row + " column=" + column + ") should have " + expected + " adjacent cells", expected, board.cell(row, column).adjacentCells().size());
			}
		}
	}

	public void testHorizontalWrapping() {
		IBoard board = new Board(5, 5, BoardWrappingMode.HORIZONTAL, false, new FullScanGenerationController(new StandardConways()));
		int[][] expectedCounts = new int[][] {
				new int[] {5,5,5,5,5},
				new int[] {8,8,8,8,8},
				new int[] {8,8,8,8,8},
				new int[] {8,8,8,8,8},
				new int[] {5,5,5,5,5}
		};
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				int expected = expectedCounts[row][column];
				assertEquals("Cell (row=" + row + " column=" + column + ") should have " + expected + " adjacent cells", expected, board.cell(row, column).adjacentCells().size());
			}
		}
	}

	public void testFullWrapping() throws Exception {
		IBoard board = new Board(5, 5, BoardWrappingMode.BOTH, true, new FullScanGenerationController(new StandardConways()));
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				assertEquals("Cell (row=" + row + " column=" + column + ") should have 8 adjacent cells",8, board.cell(row, column).adjacentCells().size());
			}
		}
	}

	public void testAllAroundDeadCells() throws Exception {
		IBoard board = new Board(5, 5, BoardWrappingMode.NONE, true, new FullScanGenerationController(new StandardConways()));
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				assertEquals("Cell (row=" + row + " column=" + column + ") should have 8 adjacent cells",8, board.cell(row, column).adjacentCells().size());
			}
		}
	}

	public void testDeadCellsWithVerticalWrapping() throws Exception {
		IBoard board = new Board(5, 5, BoardWrappingMode.VERTICAL, true, new FullScanGenerationController(new StandardConways()));
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				assertEquals("Cell (row=" + row + " column=" + column + ") should have 8 adjacent cells",8, board.cell(row, column).adjacentCells().size());
			}
		}
	}

	public void testDeadCellsWithHorizontalWrapping() throws Exception {
		IBoard board = new Board(5, 5, BoardWrappingMode.HORIZONTAL, true, new FullScanGenerationController(new StandardConways()));
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				assertEquals("Cell (row=" + row + " column=" + column + ") should have 8 adjacent cells",8, board.cell(row, column).adjacentCells().size());
			}
		}
	}
}
