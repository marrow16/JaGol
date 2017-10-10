package com.foo.gol.patterns;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatternRLELoader {
	public static IPattern load(File rleFile) throws IOException, InvalidRLEFormatException {
		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(rleFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		return decodeRle(rleFile.getName(), lines);
	}

	public static IPattern load(String name, String rleData) throws InvalidRLEFormatException {
		return decodeRle(name, Arrays.asList(rleData.split("\n")));
	}

	private static IPattern decodeRle(String defaultName, List<String> lines) throws InvalidRLEFormatException {
		String name = defaultName == null || defaultName.isEmpty() ? "(RLE Pattern)" : defaultName;
		boolean dataStarted = false;
		StringBuilder dataBuilder = new StringBuilder();
		int columns = 0;
		int rows = 0;
		boolean endSeen = false;
		for (String line: lines) {
			if (dataStarted) {
				dataBuilder.append(line);
				if (line.endsWith("!")) {
					endSeen = true;
					break;
				}
			} else if (line.startsWith("#N")) {
				name = line.substring(2).trim();
			} else if (line.startsWith("x")) {
				String[] parts = line.split(",");
				if (parts.length < 2) {
					throw new InvalidRLEFormatException("Invalid RLE format - bad dimension line");
				}
				for (String part: parts) {
					if (part.trim().startsWith("x")) {
						columns = parseDimension(part) + 2;
					} else if (part.trim().startsWith("y")) {
						rows = parseDimension(part) + 2;
					}
				}
				dataStarted = true;
			}
		}
		if (!endSeen) {
			throw new InvalidRLEFormatException("Invalid RLE format - no terminating '!'");
		} else if (columns <= 0 || rows <= 0) {
			throw new InvalidRLEFormatException("Invalid RLE format - invalid or unspecified dimensions");
		}
		String rle = dataBuilder.toString().trim();
		if (!rle.endsWith("!")) {
			throw new InvalidRLEFormatException("Invalid RLE format - data does not terminate with '!'");
		}
		rle = rle.substring(0, rle.length() - 1);
		int[] pattern = new int[rows * columns];
		String[] rleLines = rle.split("\\$");
		if (rleLines.length > (rows - 2)) {
			throw new InvalidRLEFormatException("Invalid RLE format - too many rows");
		}
		int actualRow = 1;
		for (int rowLine = 0; rowLine < rleLines.length; rowLine++) {
			int addRow = decodeRleRow(columns, actualRow, rleLines[rowLine], pattern);
			actualRow += addRow;
		}
		return new Pattern(name, columns, pattern);
	}

	private static int decodeRleRow(int columns, int row, String rle, int[] pattern) throws InvalidRLEFormatException {
		int runCount = 0;
		int column = 1;
		for (int c = 0; c < rle.length();) {
			char ch = rle.charAt(c);
			if (ch == 'b') {
				// dead
				runCount = Math.max(runCount, 1);
				column += runCount;
				runCount = 0;
				c++;
			} else if (ch == 'o') {
				// alive
				runCount = Math.max(runCount, 1);
				for (int cOn = 0; cOn < runCount; cOn++) {
					pattern[(row * columns) + column + cOn] = 1;
				}
				column += runCount;
				runCount = 0;
				c++;
			} else if (ch >= 48 && ch <= 57) {
				runCount = ch - 48;
				c++;
				for (int d = c; d < rle.length(); d++) {
					ch = rle.charAt(d);
					if (ch >= 48 && ch <= 57) {
						c++;
						runCount = (runCount * 10) + (ch - 48);
					} else {
						break;
					}
				}
			} else if (ch == ' ' || ch == '\t') {
				c++;
			} else {
				throw new InvalidRLEFormatException("Invalid RLE format - invalid character token '" + ch + "' in row " + (row - 1));
			}
		}
		// row increment is 1 - unless row ended with a count...
		return Math.max(1, runCount);
	}

	private static int parseDimension(String token) throws InvalidRLEFormatException {
		String[] bits = token.split("=");
		if (bits.length != 2) {
			throw new InvalidRLEFormatException("Invalid RLE format - dimension invalid");
		}
		try {
			return Integer.parseInt(bits[1].trim());
		} catch (NumberFormatException nfe) {
			throw new InvalidRLEFormatException("Invalid RLE format - dimension invalid");
		}
	}
}
