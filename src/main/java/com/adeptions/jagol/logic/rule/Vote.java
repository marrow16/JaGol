package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class Vote extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Vote";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 4 || adjacentsAlive == 5 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 5 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "4,5,6,7,8";
	}

	@Override
	public String getDeadsBornString() {
		return "5,6,7,8";
	}

	@Override
	public String getRleString() {
		return "B5678/S45678";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
