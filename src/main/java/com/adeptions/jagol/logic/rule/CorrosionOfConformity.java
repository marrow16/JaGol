package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class CorrosionOfConformity extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Corrosion of Conformity";
	public static final String RULE = "B3/S124";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 1 || adjacentsAlive == 2 || adjacentsAlive == 4);
		} else {
			changes = adjacentsAlive == 3;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1,2,4";
	}

	@Override
	public String getDeadsBornString() {
		return "3";
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
