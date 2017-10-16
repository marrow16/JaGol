package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class HiLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "HiLife";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			if (adjacentsAlive < 2 || adjacentsAlive > 3) {
				changes = true;
			}
		} else if (adjacentsAlive == 3 || adjacentsAlive == 6) {
			changes = true;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3";
	}

	@Override
	public String getDeadsBornString() {
		return "3,6";
	}

	@Override
	public String getRleString() {
		return "B36/S23";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
