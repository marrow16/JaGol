package com.foo.gol.logic;

public enum BoardWrappingMode {
	NONE,
	VERTICAL,
	HORIZONTAL,
	BOTH;

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
