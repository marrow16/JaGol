package com.adeptions.jagol.patterns;

import java.util.ArrayList;
import java.util.List;

public class PatternLibrary {
	private List<IPattern> patterns;

	public PatternLibrary() {
		patterns = new ArrayList<>();
		patterns.add(new Pattern(
				"Toad",
				6,
				new int[] {
						0,0,0,0,0,0,
						0,0,0,0,0,0,
						0,1,1,1,0,0,
						0,0,1,1,1,0,
						0,0,0,0,0,0,
						0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Beacon",
				6,
				new int[] {
						0,0,0,0,0,0,
						0,1,1,0,0,0,
						0,1,1,0,0,0,
						0,0,0,1,1,0,
						0,0,0,1,1,0,
						0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Gilder",
				5,
				new int[] {
						0,0,0,0,0,
						0,0,0,1,0,
						0,1,0,1,0,
						0,0,1,1,0,
						0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Lightweight Spaceship",
				7,
				new int[] {
						0,0,0,0,0,0,0,
						0,1,0,0,1,0,0,
						0,0,0,0,0,1,0,
						0,1,0,0,0,1,0,
						0,0,1,1,1,1,0,
						0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Pulsar",
				17,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,1,1,1,0,0,0,1,1,1,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,1,0,0,0,0,1,0,1,0,0,0,0,1,0,0,
						0,0,1,0,0,0,0,1,0,1,0,0,0,0,1,0,0,
						0,0,1,0,0,0,0,1,0,1,0,0,0,0,1,0,0,
						0,0,0,0,1,1,1,0,0,0,1,1,1,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,1,1,1,0,0,0,1,1,1,0,0,0,0,
						0,0,1,0,0,0,0,1,0,1,0,0,0,0,1,0,0,
						0,0,1,0,0,0,0,1,0,1,0,0,0,0,1,0,0,
						0,0,1,0,0,0,0,1,0,1,0,0,0,0,1,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,1,1,1,0,0,0,1,1,1,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Pentadecathlon",
				11,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,1,1,1,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,1,0,0,0,1,0,0,0,
						0,0,0,1,0,0,0,1,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,1,1,1,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,1,1,1,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,1,0,0,0,1,0,0,0,
						0,0,0,1,0,0,0,1,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,1,1,1,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Gosper's Glider Gun",
				38,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,
						0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,
						0,1,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,1,1,0,0,0,0,0,0,0,0,1,0,0,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"101",
				20,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,
						0,0,0,0,1,0,1,0,0,0,0,0,0,1,0,1,0,0,0,0,
						0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,
						0,1,1,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,
						0,1,1,0,1,0,1,0,0,1,1,0,0,1,0,1,0,1,1,0,
						0,0,0,0,1,0,1,0,1,0,0,1,0,1,0,1,0,0,0,0,
						0,0,0,0,1,0,1,0,1,0,0,1,0,1,0,1,0,0,0,0,
						0,1,1,0,1,0,1,0,0,1,1,0,0,1,0,1,0,1,1,0,
						0,1,1,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,
						0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,
						0,0,0,0,1,0,1,0,0,0,0,0,0,1,0,1,0,0,0,0,
						0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"1-2-3",
				12,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,1,1,0,0,0,0,0,0,0,
						0,1,0,0,1,0,0,0,0,0,0,0,
						0,1,1,0,1,0,1,1,0,0,0,0,
						0,0,1,0,1,0,0,1,0,0,0,0,
						0,0,1,0,0,0,0,1,0,1,1,0,
						0,0,0,1,1,1,0,1,0,1,1,0,
						0,0,0,0,0,0,1,0,0,0,0,0,
						0,0,0,0,0,1,0,0,0,0,0,0,
						0,0,0,0,0,1,1,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"1-2-3-4",
				13,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,1,0,0,0,0,0,0,
						0,0,0,0,0,1,0,1,0,0,0,0,0,
						0,0,0,0,1,0,1,0,1,0,0,0,0,
						0,0,0,0,1,0,0,0,1,0,0,0,0,
						0,1,1,0,1,0,1,0,1,0,1,1,0,
						0,1,0,1,0,0,0,0,0,1,0,1,0,
						0,0,0,0,1,1,1,1,1,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,1,0,0,0,0,0,0,
						0,0,0,0,0,1,0,1,0,0,0,0,0,
						0,0,0,0,0,0,1,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Airforce",
				16,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,
						0,0,0,0,0,1,0,0,0,0,0,1,0,1,1,0,
						0,0,0,0,1,0,1,1,0,0,0,1,0,1,1,0,
						0,0,0,0,1,0,1,0,0,1,0,1,0,0,0,0,
						0,1,1,0,1,0,0,0,1,1,0,1,0,0,0,0,
						0,1,1,0,1,0,0,0,0,0,1,0,0,0,0,0,
						0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Lucas Exploding Gilder Gun",
				7,
				new int[] {
						0,0,0,0,0,0,0,
						0,1,1,1,1,0,0,
						0,1,1,0,0,1,0,
						0,1,0,1,1,1,0,
						0,1,0,1,1,0,0,
						0,0,1,1,0,0,0,
						0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"R-pentomino",
				5,
				new int[] {
						0,0,0,0,0,
						0,0,1,1,0,
						0,1,1,0,0,
						0,0,1,0,0,
						0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Coe ship",
				12,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,1,1,1,1,1,1,0,
						0,0,0,1,1,0,0,0,0,0,1,0,
						0,1,1,0,1,0,0,0,0,0,1,0,
						0,0,0,0,0,1,0,0,0,1,0,0,
						0,0,0,0,0,0,0,1,0,0,0,0,
						0,0,0,0,0,0,0,1,1,0,0,0,
						0,0,0,0,0,0,1,1,1,1,0,0,
						0,0,0,0,0,0,1,1,0,1,1,0,
						0,0,0,0,0,0,0,0,1,1,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Line pulsar",
				41,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,0,0,1,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,0,1,1,1,1,1,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
				}
		));
		patterns.add(new Pattern(
				"Copperhead spaceship",
				10,
				new int[] {
						0,0,0,0,0,0,0,0,0,0,
						0,0,1,1,0,0,1,1,0,0,
						0,0,0,0,1,1,0,0,0,0,
						0,0,0,0,1,1,0,0,0,0,
						0,1,0,1,0,0,1,0,1,0,
						0,1,0,0,0,0,0,0,1,0,
						0,0,0,0,0,0,0,0,0,0,
						0,1,0,0,0,0,0,0,1,0,
						0,0,1,1,0,0,1,1,0,0,
						0,0,0,1,1,1,1,0,0,0,
						0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,1,1,0,0,0,0,
						0,0,0,0,1,1,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0
				}
		));
		try {
			patterns.add(PatternRLELoader.load("Canada goose",
					"#N Canada goose\n" +
							"#O Jason Summers\n" +
							"#C A c/4 period 4 spaceship. At the time of its discovery, the Canada goose was the smallest known diagonal spaceship other than the glider, but this record has since been beaten,\n" +
							"#C first by Orion 2, and more recently by the crab.\n" +
							"#C www.conwaylife.com/wiki/index.php?title=Canada_goose\n" +
							"x = 13, y = 12, rule = B3/S23\n" +
							"3o10b$o9b2ob$bo6b3obo$3b2o2b2o4b$4bo8b$8bo4b$4b2o3bo3b$3bobob2o4b$3bob\n" +
							"o2bob2ob$2bo4b2o4b$2b2o9b$2b2o!"));
			patterns.add(PatternRLELoader.load("Wasp", "#N Wasp\n" +
					"#O David Bell\n" +
					"#C An orthogonal c/3 period 3 spaceship that was found in March 1998.\n" +
					"#C www.conwaylife.com/wiki/index.php?title=Wasp\n" +
					"x = 22, y = 9, rule = 23/3\n" +
					"10bo3bo7b$7bobob2ob3o5b$6bo2bo6b2ob2ob$b2o2b2o2bo3bo2bo2b2ob$b2obob2o\n" +
					"2bo2bo4bo2bo$o3bo4b2o11b$obobo2bo2b2o10b$9bo12b$b3o!"));
		} catch (InvalidRLEFormatException e) {
			e.printStackTrace();
		}
	}

	public List<IPattern> getPatterns() {
		return patterns;
	}
}