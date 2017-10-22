package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class LiveFreeOrDie extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Live Free or Die";
	public static final String RULE = "B2/S0";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
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
		return RULE;
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
