package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class ElectrifiedMaze extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Electrified Maze";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 1 || adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 5);
		} else {
			changes = (adjacentsAlive == 4 || adjacentsAlive == 5);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1,2,3,4,5";
	}

	@Override
	public String getDeadsBornString() {
		return "4,5";
	}

	@Override
	public String getRleString() {
		return "B45/S12345";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
