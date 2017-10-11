package com.foo.gol.patterns;

import com.foo.gol.logic.ICell;

import java.util.*;

public class PatternRLEEncoder {
	public static String encode(int width, List<ICell> cells) throws InvalidRLEFormatException {
		if (cells.size() % width != 0) {
			throw new InvalidRLEFormatException("Cells list must have size consistent with specified width");
		}
		int[] pattern = new int[cells.size()];
		for (int i = 0; i < cells.size(); i++) {
			pattern[i] = cells.get(i).isAlive() ? 1 : 0;
		}
		return encode(width, pattern);
	}

	public static String encode(int width, int[] pattern) throws InvalidRLEFormatException {
		if (pattern.length % width != 0) {
			throw new InvalidRLEFormatException("Cells list must have size consistent with specified width");
		}
		// allocate builder to easily enough space...
		StringBuilder builder = new StringBuilder(20 + pattern.length);
		builder.append("x=").append(width).append(",y=").append(pattern.length / width).append("\n");
		int rows = pattern.length / width;
		int columns = width;
		// look for blank lines...
		Set<Integer> blankLineIndices = new HashSet<>();
		for (int row = 0; row < rows; row++) {
			boolean anyOn = false;
			for (int column = 0; column < columns; column++) {
				if (pattern[(row * columns) + column] != 0) {
					anyOn = true;
					break;
				}
			}
			if (!anyOn) {
				blankLineIndices.add(row);
			}
		}
		String line = "";
		for (int row = 0; row < rows; row++) {
			String rowString = "";
			int runLength = 0;
			Boolean lastOn = null;
			for (int column = 0; column < columns; column++) {
				Boolean isOn = pattern[(row * columns) + column] != 0;
				if (lastOn == null) {
					runLength = 1;
					lastOn = isOn;
				} else if (isOn != lastOn) {
					rowString += (runLength > 1 ? "" + runLength : "") + (lastOn ? "o" : "b");
					runLength = 1;
					lastOn = isOn;
					if (column == (columns - 1) && isOn) {
						rowString += "o";
					}
				} else {
					runLength++;
					if (column == (columns - 1) && isOn) {
						rowString += (runLength > 1 ? "" + runLength : "") + "o";
					}
				}
			}
			if (rowString.equals(columns + "b") || rowString.isEmpty()) {
				rowString = "b";
			}
			int blankLinesAfterThisRow = checkBlankLinesAfterRow(row, blankLineIndices);
			if (blankLinesAfterThisRow > 0) {
				row += blankLinesAfterThisRow;
				if (row < rows) {
					// only add row skip if we haven't skipped to the end
					rowString += (blankLinesAfterThisRow + 1);
				}
			}
			rowString += (row == (rows - 1) ? "!" : "$");
			if ((line.length() + rowString.length()) > 80) {
				builder.append(line);
				line = rowString;
			} else {
				line += rowString;
			}
		}
		builder.append(line);
		return builder.toString();
	}

	private static int checkBlankLinesAfterRow(int row, Set<Integer> blankLineIndices) {
		int result = 0;
		int onRow = row + 1;
		while (blankLineIndices.contains(onRow)) {
			onRow++;
			result++;
		}
		return result;
	}
}
