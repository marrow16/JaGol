package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class Replicator extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Replicator";
	public static final String RULE = "B1357/S1357";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes;
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
		return RULE;
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
