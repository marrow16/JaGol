package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class Seeds extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Seeds";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = true;
		} else {
			changes = adjacentsAlive == 2;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "";
	}

	@Override
	public String getDeadsBornString() {
		return "2";
	}

	@Override
	public String getRleString() {
		return "B2/S";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
