package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class DryLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "DryLife";
	public static final String RULE = "B37/S23";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 3);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 7);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3";
	}

	@Override
	public String getDeadsBornString() {
		return "3,7";
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
