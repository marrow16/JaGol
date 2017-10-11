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

		String dimensionsLine = "x=" + columns + ",y=" + rows + "\n";
		if (blankLineIndices.size() == rows) {
			return dimensionsLine + "b!";
		}

		LimitedLineLengthStringBuilder linesBuilder = new LimitedLineLengthStringBuilder(70, rows);
		StringBuilder rowBuilder;
		for (int row = 0; row < rows; row++) {
			rowBuilder = new StringBuilder(columns + 1);
			int runLength = 0;
			Boolean lastOn = null;
			for (int column = 0; column < columns; column++) {
				Boolean isOn = pattern[(row * columns) + column] != 0;
				if (lastOn == null) {
					runLength = 1;
					lastOn = isOn;
				} else if (isOn != lastOn) {
					rowBuilder.append(runLength > 1 ? "" + runLength : "").append(lastOn ? "o" : "b");
					runLength = 1;
					lastOn = isOn;
					if (isOn && column == (columns - 1)) {
						// last column and on, so just add it now (otherwise it won't get added at all)
						rowBuilder.append("o");
					}
				} else {
					runLength++;
					if (column == (columns - 1) && isOn) {
						rowBuilder.append(runLength > 1 ? "" + runLength : "").append("o");
					}
				}
			}
			int blankLinesAfterThisRow = checkBlankLinesAfterRow(row, blankLineIndices);
			if (blankLinesAfterThisRow > 0) {
				row += blankLinesAfterThisRow;
				if (row < rows) {
					// only add row skip if we haven't skipped to the end
					rowBuilder.append(blankLinesAfterThisRow + 1);
				}
			}
			rowBuilder.append(row == (rows - 1) ? "!" : "$");
			linesBuilder.append(rowBuilder.toString());
		}
		return dimensionsLine + linesBuilder.toString();
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
