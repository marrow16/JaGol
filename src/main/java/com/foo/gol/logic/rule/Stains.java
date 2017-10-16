package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class Stains extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Stains";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 5 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3,5,6,7,8";
	}

	@Override
	public String getDeadsBornString() {
		return "3,6,7,8";
	}

	@Override
	public String getRleString() {
		return "B3678/S235678";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
