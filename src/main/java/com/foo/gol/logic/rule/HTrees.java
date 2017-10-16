package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class HTrees extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "H-trees";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
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
		return "B1/S012345678";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
