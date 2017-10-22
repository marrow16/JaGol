package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class LongLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Long Life";
	public static final String RULE = "B345/S5";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
		if (cell.isAlive()) {
			changes = adjacentsAlive != 5;
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 5);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "5";
	}

	@Override
	public String getDeadsBornString() {
		return "3,4,5";
	}

	@Override
	public String getRleString() {
		return RULE;
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
