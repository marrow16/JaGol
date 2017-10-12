package com.foo.gol.logic;

@FunctionalInterface
public interface IChangeAliveRule {
	/**
	 * Evaluates whether the state of a cell (alive/dead) should changed
	 *
	 * @param cell the cell to be evaluated
	 * @return whether the state of the cell changes
	 */
	boolean evaluate(ICell cell);

	public static int countAdjacentsAlive(ICell cell) {
		int result = 0;
		for (ICell adjacentCell: cell.adjacentCells()) {
			result += (adjacentCell.isAlive() ? 1 : 0);
		}
		return result;
	}
}
