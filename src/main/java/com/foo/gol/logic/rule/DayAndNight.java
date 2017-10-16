package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class DayAndNight extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Day & Night";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "3,4,6,7,8";
	}

	@Override
	public String getDeadsBornString() {
		return "3,6,7,8";
	}

	@Override
	public String getRleString() {
		return "B3678/S34678";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
