package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class Seeds extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Seeds";
	public static final String RULE = "B2/S";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
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
		return RULE;
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
