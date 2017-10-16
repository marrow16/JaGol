package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class Gnarl extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Gnarl";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = adjacentsAlive != 1;
		} else {
			changes = adjacentsAlive == 1;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1";
	}

	@Override
	public String getDeadsBornString() {
		return "1";
	}

	@Override
	public String getRleString() {
		return "B1/S1";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
