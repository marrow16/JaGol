package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class MazectricWithMice extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Mazectric with Mice";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 1 || adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 4);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 7);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1,2,3,4";
	}

	@Override
	public String getDeadsBornString() {
		return "3,7";
	}

	@Override
	public String getRleString() {
		return "B37/S1234";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}