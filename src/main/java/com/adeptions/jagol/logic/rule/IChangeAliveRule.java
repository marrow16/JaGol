package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public interface IChangeAliveRule {
	/**
	 * Evaluates whether the state of a cell (alive/dead) should changed
	 *
	 * @param cell the cell to be evaluated
	 * @return whether the state of the cell changes
	 */
	boolean evaluate(ICell cell);

	String getAlivesSurviveString();
	String getDeadsBornString();

	String getRleString();
	String getType();

	boolean isCustom();

	public static int countAdjacentsAlive(ICell cell) {
		int result = 0;
		for (ICell adjacentCell: cell.adjacentCells()) {
			result += (adjacentCell.isAlive() ? 1 : 0);
		}
		return result;
	}
}
