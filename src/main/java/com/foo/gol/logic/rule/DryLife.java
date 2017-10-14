package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class DryLife implements IChangeAliveRule {
	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 3);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 7);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3";
	}

	@Override
	public String getDeadsBornString() {
		return "3,7";
	}

	@Override
	public String getRleString() {
		return "B37/S23";
	}
}
