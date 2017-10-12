package com.foo.gol.logic;

public class StandardLifeChangeAliveRule implements IChangeAliveRule {
	@Override
	public boolean evaluate(ICell cell) {
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		boolean changes = false;
		if (cell.isAlive()) {
			if (adjacentsAlive < 2 || adjacentsAlive > 3) {
				changes = true;
			}
		} else if (adjacentsAlive == 3) {
			changes = true;
		}
		return changes;
	}

}
