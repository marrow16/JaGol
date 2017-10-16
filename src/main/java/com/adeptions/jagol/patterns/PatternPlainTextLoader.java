package com.adeptions.jagol.patterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatternPlainTextLoader {
	public static IPattern load(String name, String plainTextData) throws InvalidPlainTextFormatException {
		return decodePlainText(name, Arrays.asList(plainTextData.split("\n")));
	}

	private static IPattern decodePlainText(String defaultName, List<String> lines) throws InvalidPlainTextFormatException {
		String name = defaultName == null || defaultName.isEmpty() ? "(Plain Text Pattern)" : defaultName;
		List<String> dataLines = new ArrayList<>();
		List<String> comments = new ArrayList<>();
		String origination = null;
		Integer width = null;
		for (String line: lines) {
			String actualLine = line.trim();
			if (actualLine.startsWith("!")) {
				if (actualLine.startsWith("!Name:")) {
					name = actualLine.substring(6).trim();
				} else if (actualLine.startsWith("!Author:")) {
					origination = actualLine.substring(8).trim();
				} else {
					comments.add(actualLine.trim());
				}
			} else if (actualLine.startsWith(".") || actualLine.startsWith("O")) {
				if (width != null && !width.equals(actualLine.length())) {
					throw new InvalidPlainTextFormatException("Invalid plain text format - all data lines must be equal width");
				}
				width = actualLine.length();
				dataLines.add(actualLine);
			}
		}
		if (width == null || width.equals(0) || dataLines.size() == 0) {
			throw new InvalidPlainTextFormatException("Invalid plain text format - does not contain any pattern");
		}
		int columns = width + 2;
		int rows = dataLines.size() + 2;
		int[] pattern = new int[rows * columns];
		for (int rowLine = 0; rowLine < dataLines.size(); rowLine++) {
			decodePlainTextRow(columns, rowLine + 1, dataLines.get(rowLine), pattern);
		}
		IPattern result = new Pattern(name, columns, pattern);
		result.getComments().addAll(comments);
		if (origination != null) {
			result.setOrigination(origination);
		}
		return result;
	}

	private static void decodePlainTextRow(int columns, int row, String rowString, int[] pattern) throws InvalidPlainTextFormatException {
		for (int c = 0; c < rowString.length(); c++) {
			char ch = rowString.charAt(c);
			switch (ch) {
				case '.':
					break;
				case 'O':
					pattern[(row * columns) + c + 1] = 1;
					break;
				default:
					throw new InvalidPlainTextFormatException("Invalid plain text format - data line can contain only '.' or 'O' characters");
			}
		}
	}

}
