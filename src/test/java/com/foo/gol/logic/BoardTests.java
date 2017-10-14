package com.foo.gol.logic;

import com.foo.gol.logic.rule.StandardConways;
import junit.framework.TestCase;

public class BoardTests extends TestCase {
	public void testBoardCreateFails() {
		boolean failed = false;
		try {
			IBoard board = new Board(0,1, null, new FullScanGenerationController(new StandardConways()));
		} catch (IllegalArgumentException e) {
			failed = true;
		}
		assertTrue("Board creation should have failed", failed);
	}

	public void testBoardCreateFails2() {
		boolean failed = false;
		try {
			IBoard board = new Board(1,0, null, new FullScanGenerationController(new StandardConways()));
		} catch (IllegalArgumentException e) {
			failed = true;
		}
		assertTrue("Board creation should have failed", failed);
	}

	public void testBoardCreateFails3() {
		boolean failed = false;
		try {
			IBoard board = new Board(3,3, null, new FullScanGenerationController(new StandardConways()), new int[4]);
		} catch (IllegalArgumentException e) {
			failed = true;
		}
		assertTrue("Board creation should have failed", failed);
	}

	public void testBoardCreateWithActiveCells() {
		IBoard board = new Board(3,3, null, new FullScanGenerationController(new StandardConways()), new int[] {
				1,1,1,
				1,1,1,
				1,1,1
		});
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				assertTrue("Cell should be initialized active", board.cell(row, column).isAlive());
			}
		}
	}

	public void testBoardCreationCellAdjacentCounts() {
		IBoard board = new Board(5,5, null, new FullScanGenerationController(new StandardConways()));
		int[][] expectedAdjacentCounts = new int[][] {
				new int[] {3, 5, 5, 5, 3},
				new int[] {5, 8, 8, 8, 5},
				new int[] {5, 8, 8, 8, 5},
				new int[] {5, 8, 8, 8, 5},
				new int[] {3, 5, 5, 5, 3}
		};
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				assertEquals(expectedAdjacentCounts[row][column], board.cell(row, column).adjacentCells().size());
			}
		}
	}
}
