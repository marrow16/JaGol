package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class HTrees extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "H-trees";
	public static final String RULE = "B1/S012345678";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 0 || adjacentsAlive == 1 || adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 5 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		} else {
			changes = adjacentsAlive == 1;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "0,1,2,3,4,5,6,7,8";
	}

	@Override
	public String getDeadsBornString() {
		return "1";
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
