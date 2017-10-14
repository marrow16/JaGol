package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class LiveFreeOrDie implements IChangeAliveRule {

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = adjacentsAlive != 0;
		} else {
			changes = adjacentsAlive == 2;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "0";
	}

	@Override
	public String getDeadsBornString() {
		return "2";
	}

	@Override
	public String getRleString() {
		return "B2/S0";
	}
}
