package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class WaterSurface extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Water Surface";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 3);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 4);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3";
	}

	@Override
	public String getDeadsBornString() {
		return "3,4";
	}

	@Override
	public String getRleString() {
		return "B34/S23";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
