package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class Serviettes extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Serviettes";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = true;
		} else {
			changes = (adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 4);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "";
	}

	@Override
	public String getDeadsBornString() {
		return "2,3,4";
	}

	@Override
	public String getRleString() {
		return "B234/S";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
