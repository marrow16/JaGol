package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class LandRush extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Land Rush";
	public static final String RULE = "B35/S234578";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 5 || adjacentsAlive == 7 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 5);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3,4,5,7,8";
	}

	@Override
	public String getDeadsBornString() {
		return "3,5";
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
