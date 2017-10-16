package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class ThirtyFourLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "34 Life";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 3 || adjacentsAlive == 4);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 4);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "3,4";
	}

	@Override
	public String getDeadsBornString() {
		return "3,4";
	}

	@Override
	public String getRleString() {
		return "B34/S34";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
