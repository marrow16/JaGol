package com.foo.gol.patterns;

public class LimitedLineLengthStringBuilder {
	private int maxLineLength;
	private StringBuilder mainBuilder;
	private StringBuilder currentLineBuilder;

	LimitedLineLengthStringBuilder(int maxLineLength, int estimatedNumberOfLines) {
		this.maxLineLength = maxLineLength;
		// create the main builder with capacity (the +2 is for CRLF)
		mainBuilder = new StringBuilder(estimatedNumberOfLines * (maxLineLength + 2));
		currentLineBuilder = new StringBuilder(maxLineLength);
	}

	LimitedLineLengthStringBuilder append(String str) {
		if (str != null && !str.isEmpty()) {
			if ((currentLineBuilder.length() + str.length()) > maxLineLength) {
				String currentLine = currentLineBuilder.toString();
				mainBuilder.append(currentLine).append(str.substring(0, maxLineLength - currentLine.length())).append("\n");
				currentLineBuilder = new StringBuilder(maxLineLength);
				currentLineBuilder.append(str.substring(maxLineLength - currentLine.length()));
			} else {
				currentLineBuilder.append(str);
			}
		}
		return this;
	}

	@Override
	public String toString() {
		return mainBuilder.toString() + currentLineBuilder.toString();
	}
}
