package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class LowLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "LowLife";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 1 || adjacentsAlive == 3);
		} else {
			changes = adjacentsAlive == 3;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1,3";
	}

	@Override
	public String getDeadsBornString() {
		return "3";
	}

	@Override
	public String getRleString() {
		return "B3/S13";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
