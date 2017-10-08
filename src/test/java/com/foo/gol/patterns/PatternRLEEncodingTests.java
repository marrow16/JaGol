package com.foo.gol.patterns;

import junit.framework.TestCase;

public class PatternRLEEncodingTests extends TestCase {
	public void testLWSSEncoding() throws Exception {
		int[] pattern = new int[] {
				0,1,0,0,1,
				1,0,0,0,0,
				1,0,0,0,1,
				1,1,1,1,0
		};
		String rleEncoded = PatternRLEEncoder.encode(5, pattern);
		String[] lines = rleEncoded.split("\n");
		assertEquals("x=5,y=4", lines[0]);
		StringBuilder dataBuilder = new StringBuilder();
		for (int i = 1; i < lines.length; i++) {
			dataBuilder.append(lines[i]);
		}
		String data = dataBuilder.toString();
		//assertEquals("bo2bo$o4b$o3bo$4o!", data);
		assertEquals("bo2bo$o$o3bo$4o!", data);
		IPattern decodedPattern = PatternRLELoader.load("Test", rleEncoded);
		assertEquals(7, decodedPattern.columns());
		assertEquals(6, decodedPattern.rows());
		int[][] checkPattern = new int[][] {
				new int[] {0,0,0,0,0,0,0},
				new int[] {0,0,1,0,0,1,0},
				new int[] {0,1,0,0,0,0,0},
				new int[] {0,1,0,0,0,1,0},
				new int[] {0,1,1,1,1,0,0},
				new int[] {0,0,0,0,0,0,0}
		};
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 7; column++) {
				assertEquals("Row " + row + ", column " + column + " should be " + (checkPattern[row][column] != 0 ? "ALIVE" : "DEAD"), checkPattern[row][column] != 0, decodedPattern.cell(row, column).isAlive());
			}
		}
	}

	public void testGosperEncoding() throws Exception {
		int[] pattern = new int[] {
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,
				0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,
				1,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				1,1,0,0,0,0,0,0,0,0,1,0,0,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
		};
		String rleEncoded = PatternRLEEncoder.encode(36, pattern);
		String[] lines = rleEncoded.split("\n");
		assertEquals("x=36,y=9", lines[0]);
		int[][] checkPattern = new int[][] {
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0},
				new int[] {0,1,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,1,1,0,0,0,0,0,0,0,0,1,0,0,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
		};
		IPattern decodedPattern = PatternRLELoader.load("Test", rleEncoded);
		assertEquals(38, decodedPattern.columns());
		assertEquals(11, decodedPattern.rows());
		for (int row = 0; row < 11; row++) {
			for (int column = 0; column < 38; column++) {
				assertEquals("Row " + row + ", column " + column + " should be " + (checkPattern[row][column] != 0 ? "ALIVE" : "DEAD"), checkPattern[row][column] != 0, decodedPattern.cell(row, column).isAlive());
			}
		}

	}
}
