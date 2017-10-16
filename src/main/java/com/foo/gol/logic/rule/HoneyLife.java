package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class HoneyLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "HoneyLife";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 8);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3,8";
	}

	@Override
	public String getDeadsBornString() {
		return "3,8";
	}

	@Override
	public String getRleString() {
		return "B38/S238";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
