package com.foo.gol.patterns;

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

public class PatternRLELoaderTests extends TestCase {
	public void testLoadLWSS() throws Exception {
		URL url = this.getClass().getResource("lwss.rle");
		File file = new File(url.toURI());
		IPattern pattern = PatternRLELoader.load(file);
		assertEquals("Lightweight spaceship", pattern.getName());
		assertEquals(6, pattern.rows());
		assertEquals(7, pattern.columns());
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
				assertEquals("Row " + row + ", column " + column + " should be " + (checkPattern[row][column] != 0 ? "ALIVE" : "DEAD"), checkPattern[row][column] != 0, pattern.cell(row, column).isAlive());
			}
		}
	}

	public void testLoadGosper() throws Exception {
		URL url = this.getClass().getResource("gosper.rle");
		File file = new File(url.toURI());
		IPattern pattern = PatternRLELoader.load(file);
		assertEquals("Gosper glider gun", pattern.getName());
		assertEquals(11, pattern.rows());
		assertEquals(38, pattern.columns());
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
		for (int row = 0; row < 11; row++) {
			for (int column = 0; column < 38; column++) {
				assertEquals("Row " + row + ", column " + column + " should be " + (checkPattern[row][column] != 0 ? "ALIVE" : "DEAD"), checkPattern[row][column] != 0, pattern.cell(row, column).isAlive());
			}
		}
	}

	public void testLoadLWSSFromString() throws Exception {
		String lwssRle = "#N Lightweight spaceship\n" +
				"#O John Conway\n" +
				"#C A very well-known period 4 c/2 orthogonal spaceship.\n" +
				"#C www.conwaylife.com/wiki/index.php?title=Lightweight_spaceship\n" +
				"x = 5, y = 4, rule = B3/S23\n" +
				"bo2bo$o4b$o3bo$4o!";
		IPattern pattern = PatternRLELoader.load("Test", lwssRle);
		assertEquals("Lightweight spaceship", pattern.getName());
		assertEquals(6, pattern.rows());
		assertEquals(7, pattern.columns());
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
				assertEquals("Row " + row + ", column " + column + " should be " + (checkPattern[row][column] != 0 ? "ALIVE" : "DEAD"), checkPattern[row][column] != 0, pattern.cell(row, column).isAlive());
			}
		}
	}

	public void testLoadDartFromString() throws Exception {
		String dartRle = "#N Dart\n" +
				"#O David Bell\n" +
				"#C An orthogonal period 3 spaceship with speed c/3 that was found in May 1992.\n" +
				"#C www.conwaylife.com/wiki/index.php?title=Dart\n" +
				"x = 15, y = 10, rule = B3/S23\n" +
				"7bo7b$" +
				"6bobo6b$" +
				"5bo3bo5b$" +
				"6b3o6b2$" + // ends with extra line
				"4b2o3b2o4b$" +
				"2bo3bobo3bo2b$" +
				"b2o3bobo3b2ob$" +
				"o5bobo5bo$" +
				"bob2obobob2obo!";
		IPattern pattern = PatternRLELoader.load("Test", dartRle);
		assertEquals("Dart", pattern.getName());
		assertEquals(12, pattern.rows());
		assertEquals(17, pattern.columns());
		int[][] checkPattern = new int[][] {
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				new int[] {0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0},
				new int[] {0,0,0,1,0,0,0,1,0,1,0,0,0,1,0,0,0},
				new int[] {0,0,1,1,0,0,0,1,0,1,0,0,0,1,1,0,0},
				new int[] {0,1,0,0,0,0,0,1,0,1,0,0,0,0,0,1,0},
				new int[] {0,0,1,0,1,1,0,1,0,1,0,1,1,0,1,0,0},
				new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
		};
		for (int row = 0; row < 12; row++) {
			for (int column = 0; column < 17; column++) {
				assertEquals("Row " + row + ", column " + column + " should be " + (checkPattern[row][column] != 0 ? "ALIVE" : "DEAD"), checkPattern[row][column] != 0, pattern.cell(row, column).isAlive());
			}
		}
	}
}
