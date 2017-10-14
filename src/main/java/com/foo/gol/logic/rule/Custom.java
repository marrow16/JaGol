package com.foo.gol.logic.rule;

import com.foo.gol.logic.ICell;

import java.util.*;

public class Custom implements IChangeAliveRule {
	private Set<Integer> survivesWithCounts;
	private Set<Integer> bornWithCounts;
	private String survivesString;
	private String bornString;
	private String rleString;

	public Custom(String survivesString, String bornString) {
		this.survivesString = survivesString;
		this.bornString = bornString;
		survivesWithCounts = new HashSet<>();
		bornWithCounts = new HashSet<>();
		Integer val;
		for (String count: survivesString.split("[, ]")) {
			if (!count.isEmpty()) {
				try {
					val = Integer.parseInt(count);
					if (val >= 0 && val < 10) {
						survivesWithCounts.add(val);
					}
				} catch (NumberFormatException nfe) {
					// swallow
				}
			}
		}
		for (String count: bornString.split("[, ]")) {
			if (!count.isEmpty()) {
				try {
					val = Integer.parseInt(count);
					if (val >= 0 && val < 10) {
						bornWithCounts.add(val);
					}
				} catch(NumberFormatException nfe){
					// swallow
				}
			}
		}
		buildRleString();
	}

	public Custom(Set<Integer> survivesWithCounts, Set<Integer> bornWithCounts) {
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
		buildRleString();
	}

	private void buildRleString() {
		StringBuilder builder = new StringBuilder(3 + survivesWithCounts.size() + bornWithCounts.size());
		builder.append("B");
		List<Integer> sorted = new ArrayList<>(bornWithCounts);
		sorted.sort((o1, o2) -> o1.compareTo(o2));
		for (Integer val: sorted) {
			builder.append(val);
		}
		builder.append("/S");
		sorted = new ArrayList<>(survivesWithCounts);
		sorted.sort((o1, o2) -> o1.compareTo(o2));
		for (Integer val: sorted) {
			builder.append(val);
		}
		rleString = builder.toString();
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

	@Override
	public String getRleString() {
		return rleString;
	}
}
