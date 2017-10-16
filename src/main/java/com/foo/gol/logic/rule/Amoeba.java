package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class Amoeba extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Amoeba";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 1 || adjacentsAlive == 3 || adjacentsAlive == 5 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 5 || adjacentsAlive == 7);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1,3,5,8";
	}

	@Override
	public String getDeadsBornString() {
		return "3,5,7";
	}

	@Override
	public String getRleString() {
		return "B357/S1358";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
