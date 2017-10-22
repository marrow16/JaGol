package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class HoneyLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "HoneyLife";
	public static final String RULE = "B38/S238";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
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
		return RULE;
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
