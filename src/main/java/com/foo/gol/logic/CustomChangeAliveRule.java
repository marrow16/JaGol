package com.foo.gol.logic;

import java.util.HashSet;
import java.util.Set;

public class CustomChangeAliveRule implements IChangeAliveRule {
	private Set<Integer> survivesWithCounts;
	private Set<Integer> bornWithCounts;

	public CustomChangeAliveRule(Set<Integer> survivesWithCounts, Set<Integer> bornWithCounts) {
		this.survivesWithCounts = new HashSet<>(survivesWithCounts);
		this.bornWithCounts = new HashSet<>(bornWithCounts);
	}

	@Override
	public boolean evaluate(ICell cell) {
		boolean changes = false;
		int adjacentsAlive = IChangeAliveRule.countAdjacentsAlive(cell);
		if (cell.isAlive()) {
			changes = !survivesWithCounts.contains(adjacentsAlive);
		} else if (bornWithCounts.contains(adjacentsAlive)) {
			changes = true;
		}
		return changes;
	}
}
