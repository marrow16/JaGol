package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class Move extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Move";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 4 || adjacentsAlive == 5);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 6 || adjacentsAlive == 8);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,4,5";
	}

	@Override
	public String getDeadsBornString() {
		return "3,6,8";
	}

	@Override
	public String getRleString() {
		return "B368/S245";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
