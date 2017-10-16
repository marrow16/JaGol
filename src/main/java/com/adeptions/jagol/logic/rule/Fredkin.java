package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class Fredkin extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Fredkin";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 0 || adjacentsAlive == 2 || adjacentsAlive == 4 || adjacentsAlive == 6 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 1 || adjacentsAlive == 3 || adjacentsAlive == 5 || adjacentsAlive == 7);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "0,2,4,6,8";
	}

	@Override
	public String getDeadsBornString() {
		return "1,3,5,7";
	}

	@Override
	public String getRleString() {
		return "B1357/S02468";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
