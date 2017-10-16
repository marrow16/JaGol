package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

public class Replicator extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Replicator";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 1 || adjacentsAlive == 3 || adjacentsAlive == 5 || adjacentsAlive == 7);
		} else {
			changes = (adjacentsAlive == 1 || adjacentsAlive == 3 || adjacentsAlive == 5 || adjacentsAlive == 7);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "1,3,5,7";
	}

	@Override
	public String getDeadsBornString() {
		return "1,3,5,7";
	}

	@Override
	public String getRleString() {
		return "B1357/S1357";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
