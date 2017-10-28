package com.adeptions.jagol.logic.rule;

import com.adeptions.jagol.logic.ICell;

import java.util.*;

public class Custom implements IChangeAliveRule {
	public static final String LABEL = "Custom";

	private String type;
	private Set<Integer> survivesWithCounts = new HashSet<>();
	private Set<Integer> bornWithCounts = new HashSet<>();
	private String survivesString = "";
	private String bornString = "";
	private String rleString = "";

	public static Custom createFromRuleString(String rule) {
		Custom result = new Custom(rule);
		String[] parts = rule.split("/");
		if (parts.length == 2) {
			if (!parts[0].startsWith("B") && !parts[0].startsWith("S") && !parts[1].startsWith("B") && !parts[1].startsWith("S")) {
				// it encocded with 'B' and 'S' prefixes
				// so the first part is S and the second part is B...
				result.setRleString("B" + parts[1] + "/S" + parts[0]);
				result.setType("B" + parts[1] + "/S" + parts[0]);
			} else {
				result.setRleString(rule);
			}
		}
		return result;
	}

	public Custom(String type) {
		this.type = type;
	}

	public Custom() {
		this.type = type;
	}

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
					if (val >= 0 && val < 9) {
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
					if (val >= 0 && val < 9) {
						bornWithCounts.add(val);
					}
				} catch(NumberFormatException nfe){
					// swallow
				}
			}
		}
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

	private void buildStrings() {
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
			changes = survivesWithCounts.size() == 0 || !survivesWithCounts.contains(adjacentsAlive);
		} else if (bornWithCounts.size() == 0 || bornWithCounts.contains(adjacentsAlive)) {
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

	public void setRleString(String rule) {
		if (rule != null) {
			String[] parts = rule.split("/");
			if (parts.length == 2) {
				String alivesSurvivePart = parts[0].startsWith("S") ? parts[0] : (parts[1].startsWith("S") ? parts[1] : null);
				String deadsBornPart = parts[0].startsWith("B") ? parts[0] : (parts[1].startsWith("B") ? parts[1] : null);
				if (alivesSurvivePart != null && deadsBornPart != null) {
					survivesWithCounts = new HashSet<>();
					bornWithCounts = new HashSet<>();
					for (int c = 1; c < alivesSurvivePart.length(); c++) {
						char ch = alivesSurvivePart.charAt(c);
						if (ch >= '0' && ch < '9') {
							survivesWithCounts.add(ch - 48);
						}
					}
					for (int c = 1; c < deadsBornPart.length(); c++) {
						char ch = deadsBornPart.charAt(c);
						if (ch >= '0' && ch < '9') {
							bornWithCounts.add(ch - 48);
						}
					}
					rleString = rule;
					buildStrings();
				}
			}
		}
	}

	@Override
	public String getType() {
		return type != null ? type : LABEL;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean isCustom() {
		return true;
	}
}
