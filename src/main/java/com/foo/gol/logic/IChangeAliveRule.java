package com.foo.gol.logic;

@FunctionalInterface
public interface IChangeAliveRule {
	boolean evaluate(ICell cell);

	public static int countAdjacentsAlive(ICell cell) {
		int result = 0;
		for (ICell adjacentCell: cell.adjacentCells()) {
			result += (adjacentCell.isAlive() ? 1 : 0);
		}
		return result;
	}
}
