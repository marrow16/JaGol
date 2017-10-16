package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class Assimilation extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Assimilation";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 4 || adjacentsAlive == 5 || adjacentsAlive == 6 || adjacentsAlive == 7);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 5);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "4,5,6,7";
	}

	@Override
	public String getDeadsBornString() {
		return "3,4,5";
	}

	@Override
	public String getRleString() {
		return "B345/S4567";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
