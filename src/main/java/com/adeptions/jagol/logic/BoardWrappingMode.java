package com.adeptions.jagol.logic;

public enum BoardWrappingMode {
	NONE("None"),
	VERTICAL("Vertical"),
	HORIZONTAL("Horizontal"),
	BOTH("Both");

	private String label;

	BoardWrappingMode(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public static BoardWrappingMode fromString(String str) {
		BoardWrappingMode result = null;
		for (BoardWrappingMode value: values()) {
			if (value.name().equalsIgnoreCase(str)) {
				result = value;
				break;
			}
		}
		return result;
	}
}
