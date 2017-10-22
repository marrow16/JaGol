package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class Bacteria extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Bacteria";
	public static final String RULE = "B34/S456";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 4 || adjacentsAlive == 5 || adjacentsAlive == 6);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 4);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "4,5,6";
	}

	@Override
	public String getDeadsBornString() {
		return "3,4";
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
