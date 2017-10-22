package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class BlinkerLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Blinker Life";
	public static final String RULE = "B36/S235";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 5);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 6);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3,5";
	}

	@Override
	public String getDeadsBornString() {
		return "3,6";
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
