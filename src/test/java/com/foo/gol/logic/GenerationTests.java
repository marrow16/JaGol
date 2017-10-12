package com.foo.gol.logic;

import com.foo.gol.logic.*;
import junit.framework.TestCase;

import java.util.List;

public class GenerationTests extends TestCase {
	private static boolean LIVE = true;
	private static boolean DEAD = false;

	public void testStableBlock() {
		IBoard board = new Board(4, 4, null, new FullScanGenerationController(new StandardLifeChangeAliveRule()), new int[] {
				0,0,0,0,
				0,1,1,0,
				0,1,1,0,
				0,0,0,0
		});
		List<ICell> changed = board.generationController().nextGeneration();
		assertEquals(0, changed.size());
		assertEquals(GenerationState.STABLE, board.generationController().state());
	}

	public void testStableBeehive() {
		IBoard board = new Board(6, 5, null, new FullScanGenerationController(new StandardLifeChangeAliveRule()), new int[] {
				0,0,0,0,0,0,
				0,0,1,1,0,0,
				0,1,0,0,1,0,
				0,0,1,1,0,0,
				0,0,0,0,0,0,
		});
		List<ICell> changed = board.generationController().nextGeneration();
		assertEquals(0, changed.size());
		assertEquals(GenerationState.STABLE, board.generationController().state());
	}

	public void testStableBoat() {
		IBoard board = new Board(5, 5, null, new FullScanGenerationController(new StandardLifeChangeAliveRule()), new int[] {
				0,0,0,0,0,
				0,1,1,0,0,
				0,1,0,1,0,
				0,0,1,0,0,
				0,0,0,0,0
		});
		List<ICell> changed = board.generationController().nextGeneration();
		assertEquals(0, changed.size());
		assertEquals(GenerationState.STABLE, board.generationController().state());
	}

	public void testStableTub() {
		IBoard board = new Board(5, 5, null, new FullScanGenerationController(new StandardLifeChangeAliveRule()), new int[] {
				0,0,0,0,0,
				0,0,1,0,0,
				0,1,0,1,0,
				0,0,1,0,0,
				0,0,0,0,0
		});
		List<ICell> changed = board.generationController().nextGeneration();
		assertEquals(0, changed.size());
		assertEquals(GenerationState.STABLE, board.generationController().state());
	}

	public void testStableLoaf() {
		IBoard board = new Board(6, 6, null, new FullScanGenerationController(new StandardLifeChangeAliveRule()), new int[] {
				0,0,0,0,0,0,
				0,0,1,1,0,0,
				0,1,0,0,1,0,
				0,0,1,0,1,0,
				0,0,0,1,0,0,
				0,0,0,0,0,0
		});
		List<ICell> changed = board.generationController().nextGeneration();
		assertEquals(0, changed.size());
		assertEquals(GenerationState.STABLE, board.generationController().state());
	}

	public void testOscillatorBlinker() {
		boolean[][] initialStates = new boolean[][] {
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,LIVE,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,LIVE,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,LIVE,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD}
		};
		int[] initialCells = new int[5 * 5];
		for (int i = 0; i < (5*5); i++) {
			initialCells[i] = initialStates[i / 5][i % 5] ? 1 : 0;
		}
		IBoard board = new Board(5, 5, null, new FullScanGenerationController(new StandardLifeChangeAliveRule()), initialCells);
		boolean[][] afterStates = new boolean[][] {
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,LIVE,LIVE,LIVE,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD}
		};
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				assertEquals("Row " + row + ", Column " + column + " incorrect state at start", initialStates[row][column], board.cell(row,column).isAlive());
			}
		}
		List<ICell> changed = board.generationController().nextGeneration();
		assertEquals(4, changed.size());
		assertEquals(GenerationState.READY, board.generationController().state());
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				assertEquals("Row " + row + ", Column " + column + " incorrect state (after 1st generation)", afterStates[row][column], board.cell(row,column).isAlive());
			}
		}
		changed = board.generationController().nextGeneration();
		assertEquals(4, changed.size());
		assertEquals(GenerationState.READY, board.generationController().state());
		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 5; column++) {
				assertEquals("Row " + row + ", Column " + column + " incorrect state (after 2nd generation)", initialStates[row][column], board.cell(row,column).isAlive());
			}
		}
	}

	public void testOscillatorToad() {
		boolean[][] initialStates = new boolean[][] {
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,LIVE,DEAD,DEAD},
				new boolean[] {DEAD,LIVE,DEAD,DEAD,LIVE,DEAD},
				new boolean[] {DEAD,LIVE,DEAD,DEAD,LIVE,DEAD},
				new boolean[] {DEAD,DEAD,LIVE,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD}
		};
		int[] initialCells = new int[6 * 6];
		for (int i = 0; i < (6*6); i++) {
			initialCells[i] = initialStates[i / 6][i % 6] ? 1 : 0;
		}
		IBoard board = new Board(6, 6, null, new FullScanGenerationController(new StandardLifeChangeAliveRule()), initialCells);
		boolean[][] afterStates = new boolean[][] {
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,LIVE,LIVE,LIVE,DEAD},
				new boolean[] {DEAD,LIVE,LIVE,LIVE,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD}
		};
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 6; column++) {
				assertEquals("Row " + row + ", Column " + column + " incorrect state at start", initialStates[row][column], board.cell(row,column).isAlive());
			}
		}
		List<ICell> changed = board.generationController().nextGeneration();
		assertEquals(8, changed.size());
		assertEquals(GenerationState.READY, board.generationController().state());
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 6; column++) {
				assertEquals("Row " + row + ", Column " + column + " incorrect state (after 1st generation)", afterStates[row][column], board.cell(row,column).isAlive());
			}
		}
		changed = board.generationController().nextGeneration();
		assertEquals(8, changed.size());
		assertEquals(GenerationState.READY, board.generationController().state());
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 6; column++) {
				assertEquals("Row " + row + ", Column " + column + " incorrect state (after 2nd generation)", initialStates[row][column], board.cell(row,column).isAlive());
			}
		}
	}

	public void testOscillatorBeacon() {
		boolean[][] initialStates = new boolean[][] {
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,LIVE,LIVE,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,LIVE,LIVE,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,LIVE,LIVE,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,LIVE,LIVE,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD}
		};
		int[] initialCells = new int[6 * 6];
		for (int i = 0; i < (6*6); i++) {
			initialCells[i] = initialStates[i / 6][i % 6] ? 1 : 0;
		}
		IBoard board = new Board(6, 6, null, new FullScanGenerationController(new StandardLifeChangeAliveRule()), initialCells);
		boolean[][] afterStates = new boolean[][] {
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,LIVE,LIVE,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,LIVE,DEAD,DEAD,DEAD,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,LIVE,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,LIVE,LIVE,DEAD},
				new boolean[] {DEAD,DEAD,DEAD,DEAD,DEAD,DEAD}
		};
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 6; column++) {
				assertEquals("Row " + row + ", Column " + column + " incorrect state at start", initialStates[row][column], board.cell(row,column).isAlive());
			}
		}
		List<ICell> changed = board.generationController().nextGeneration();
		assertEquals(2, changed.size());
		assertEquals(GenerationState.READY, board.generationController().state());
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 6; column++) {
				assertEquals("Row " + row + ", Column " + column + " incorrect state (after 1st generation)", afterStates[row][column], board.cell(row,column).isAlive());
			}
		}
		changed = board.generationController().nextGeneration();
		assertEquals(2, changed.size());
		assertEquals(GenerationState.READY, board.generationController().state());
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 6; column++) {
				assertEquals("Row " + row + ", Column " + column + " incorrect state (after 2nd generation)", initialStates[row][column], board.cell(row,column).isAlive());
			}
		}
	}

}
