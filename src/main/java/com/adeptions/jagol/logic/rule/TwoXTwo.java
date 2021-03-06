package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class TwoXTwo extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "2X2";
	public static final String RULE = "B36/S125";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 1 || adjacentsAlive == 2 || adjacentsAlive == 5);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 6);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1,2,5";
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
