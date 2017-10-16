package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

public class WalledCities extends AbstractPredefinedRule implements IChangeAliveRule {
	public static final String LABEL = "Walled cities";

	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			changes = !(adjacentsAlive == 2 || adjacentsAlive == 3 || adjacentsAlive == 4 || adjacentsAlive == 5);
		} else {
			changes = (adjacentsAlive == 4 || adjacentsAlive == 5 || adjacentsAlive == 6 || adjacentsAlive == 7 || adjacentsAlive == 8);
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3,4,5";
	}

	@Override
	public String getDeadsBornString() {
		return "4,5,6,7,8";
	}

	@Override
	public String getRleString() {
		return "B45678/S2345";
	}

	@Override
	public String getType() {
		return LABEL;
	}
}
