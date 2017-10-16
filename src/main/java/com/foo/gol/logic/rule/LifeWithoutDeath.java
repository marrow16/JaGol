package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class LifeWithoutDeath extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Life without death";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (!cell.isAlive()) {
			changes = adjacentsAlive == 3;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "0,1,2,3,4,5,6,7,8";
	}

	@Override
	public String getDeadsBornString() {
		return "3";
	}

	@Override
	public String getRleString() {
		return "B3/S012345678";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
