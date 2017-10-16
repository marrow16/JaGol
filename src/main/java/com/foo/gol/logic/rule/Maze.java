package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class Maze extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Maze";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 1 || adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 5);
		} else {
			changes = adjacentsAlive == 3;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1,2,3,4,5";
	}

	@Override
	public String getDeadsBornString() {
		return "3";
	}

	@Override
	public String getRleString() {
		return "B3/S12345";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
