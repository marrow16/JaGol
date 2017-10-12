package com.foo.gol.logic;

import java.util.*;

public class CustomChangeAliveRule implements IChangeAliveRule {
	private Set<Integer> survivesWithCounts;
	private Set<Integer> bornWithCounts;
	private String survivesString;
	private String bornString;

	public CustomChangeAliveRule(String survivesString, String bornString) {
		this.survivesString = survivesString;
		this.bornString = bornString;
		survivesWithCounts = new HashSet<>();
		bornWithCounts = new HashSet<>();
		for (String count: survivesString.split("[, ]")) {
			try {
				survivesWithCounts.add(Integer.parseInt(count));
			} catch (NumberFormatException nfe) {
				// swallow
			}
		}
		for (String count: bornString.split("[, ]")) {
			try {
				bornWithCounts.add(Integer.parseInt(count));
			} catch (NumberFormatException nfe) {
				// swallow
			}
		}
	}

	public CustomChangeAliveRule(Set<Integer> survivesWithCounts, Set<Integer> bornWithCounts) {
		this.survivesWithCounts = new HashSet<>(survivesWithCounts);
		this.bornWithCounts = new HashSet<>(bornWithCounts);
		List<Integer> survives = new ArrayList<>(survivesWithCounts);
		survives.sort((o1, o2) -> o1.compareTo(o2));
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < survives.size(); i++) {
			builder.append(i == 0 ? "" : ",").append(survives.get(i));
		}
		survivesString = builder.toString();
		List<Integer> borns = new ArrayList<>(bornWithCounts);
		borns.sort((o1, o2) -> o1.compareTo(o2));
		builder = new StringBuilder();
		for (int i = 0; i < borns.size(); i++) {
			builder.append(i == 0 ? "" : ",").append(borns.get(i));
		}
		bornString = builder.toString();
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

	@Override
	public String getAlivesSurviveString() {
		return survivesString;
	}

	@Override
	public String getDeadsBornString() {
		return bornString;
	}
}
