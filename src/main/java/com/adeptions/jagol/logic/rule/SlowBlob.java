package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class SlowBlob extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Slow Blob";
	public static final String RULE = "B367/S125678";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 1 || adjacentsAlive == 2 || adjacentsAlive == 5 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 6 || adjacentsAlive == 7);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1,2,5,6,7,8";
	}

	@Override
	public String getDeadsBornString() {
		return "3,6,7";
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
