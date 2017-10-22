package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class LowLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "LowLife";
	public static final String RULE = "B3/S13";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
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
		return RULE;
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
