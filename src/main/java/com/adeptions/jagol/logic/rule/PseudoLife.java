package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class PseudoLife extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Pseudo Life";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 8);
		} else {
			changes = (adjacentsAlive == 3 || adjacentsAlive == 5 || adjacentsAlive == 7);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3,8";
	}

	@Override
	public String getDeadsBornString() {
		return "3,5,7";
	}

	@Override
	public String getRleString() {
		return "B357/S238";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
