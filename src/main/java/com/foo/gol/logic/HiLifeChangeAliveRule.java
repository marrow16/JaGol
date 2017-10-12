package com.foo.gol.logic;

public class HiLifeChangeAliveRule implements IChangeAliveRule {
	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			if (adjacentsAlive < 2 || adjacentsAlive > 3) {
				changes = true;
			}
		} else if (adjacentsAlive == 3 || adjacentsAlive == 6) {
			changes = true;
		}
		return changes;
	}

	@Override
	public String getAlivesSurviveString() {
		return "2,3";
	}

	@Override
	public String getDeadsBornString() {
		return "3,6";
	}
}
