package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class AntiLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "AntiLife";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 0 || adjacentsAlive == 1 || adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 0 || adjacentsAlive == 1 || adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 7 || adjacentsAlive == 8);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "0,1,2,3,4,6,7,8";
	}

	@Override
	public String getDeadsBornString() {
		return "0,1,2,3,4,7,8";
	}

	@Override
	public String getRleString() {
		return "B0123478/S01234678";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
