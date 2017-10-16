package com.foo.gol.logic.rule;

import com.foo.gol.logic.Cell;
import com.foo.gol.logic.ICell;
import junit.framework.TestCase;

public class RuleTests extends TestCase {
	public void testRuleFactory() throws Exception {
		IChangeAliveRule rule = ChangeAliveRuleFactory.createFromLabel("Standard");
		assertEquals(StandardConways.class, rule.getClass());
	}

	public void testRuleLabel() throws Exception {
		assertEquals("Standard", ChangeAliveRuleFactory.instanceToLabel(new StandardConways()));
	}

	public void testStandardConwayAlive() throws Exception {
		IChangeAliveRule rule = new StandardConways();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testStandardConwayDead() throws Exception {
		IChangeAliveRule rule = new StandardConways();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testHiLifeAlive() throws Exception {
		IChangeAliveRule rule = new HiLife();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testHiLfeDead() throws Exception {
		IChangeAliveRule rule = new HiLife();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testReplicatorAlive() throws Exception {
		IChangeAliveRule rule = new Replicator();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testReplicatorDead() throws Exception {
		IChangeAliveRule rule = new Replicator();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testFredkinAlive() throws Exception {
		IChangeAliveRule rule = new Fredkin();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testFredkinDead() throws Exception {
		IChangeAliveRule rule = new Fredkin();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testSeedsAlive() throws Exception {
		IChangeAliveRule rule = new Seeds();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testSeedsDead() throws Exception {
		IChangeAliveRule rule = new Seeds();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testLiveFreeOrDieAlive() throws Exception {
		IChangeAliveRule rule = new LiveFreeOrDie();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testLiveFreeOrDieDead() throws Exception {
		IChangeAliveRule rule = new LiveFreeOrDie();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testLifeWithoutDeathAlive() throws Exception {
		IChangeAliveRule rule = new LifeWithoutDeath();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testLifeWithoutDeathDead() throws Exception {
		IChangeAliveRule rule = new LifeWithoutDeath();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testFlockAlive() throws Exception {
		IChangeAliveRule rule = new Flock();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testFlockDead() throws Exception {
		IChangeAliveRule rule = new Flock();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testMazectricAlive() throws Exception {
		IChangeAliveRule rule = new Mazectric();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testMazectricDead() throws Exception {
		IChangeAliveRule rule = new Mazectric();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testMazeAlive() throws Exception {
		IChangeAliveRule rule = new Maze();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testMazeDead() throws Exception {
		IChangeAliveRule rule = new Maze();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testTwoXTwoAlive() throws Exception {
		IChangeAliveRule rule = new TwoXTwo();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testTwoXTwoDead() throws Exception {
		IChangeAliveRule rule = new TwoXTwo();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testMoveAlive() throws Exception {
		IChangeAliveRule rule = new Move();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testMoveDead() throws Exception {
		IChangeAliveRule rule = new Move();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testDayAndNightAlive() throws Exception {
		IChangeAliveRule rule = new DayAndNight();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testDayAndNightDead() throws Exception {
		IChangeAliveRule rule = new DayAndNight();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testDryLifeAlive() throws Exception {
		IChangeAliveRule rule = new DryLife();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testDryLifeDead() throws Exception {
		IChangeAliveRule rule = new DryLife();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testPedestrianLifeAlive() throws Exception {
		IChangeAliveRule rule = new PedestrianLife();
		boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, true);
			addAdjacents(testCell, i);
			assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testPedestrianLifeDead() throws Exception {
		IChangeAliveRule rule = new PedestrianLife();
		boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
		for (int i = 0; i < expectedChanges.length; i++) {
			ICell testCell = Cell.createCell(0,0, false);
			addAdjacents(testCell, i);
			assertEquals("Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
		}
	}

	public void testAllRulesAlive() throws Exception {
		for (String type: ChangeAliveRuleFactory.getLabelsList()) {
			IChangeAliveRule rule = ChangeAliveRuleFactory.createFromLabel(type);
			boolean[] expectedChanges = generateExpectedAlivesChangesFromRuleString(rule.getRleString());
			for (int i = 0; i < expectedChanges.length; i++) {
				ICell testCell = Cell.createCell(0, 0, true);
				addAdjacents(testCell, i);
				assertEquals("Alive with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
			}
		}
	}


	public void testAllRulesDead() throws Exception {
		for (String type: ChangeAliveRuleFactory.getLabelsList()) {
			IChangeAliveRule rule = ChangeAliveRuleFactory.createFromLabel(type);
			boolean[] expectedChanges = generateExpectedDeadsChangesFromRuleString(rule.getRleString());
			for (int i = 0; i < expectedChanges.length; i++) {
				ICell testCell = Cell.createCell(0,0, false);
				addAdjacents(testCell, i);
				assertEquals(type + ": Dead with " + i + " adjacents should " + (!expectedChanges[i] ? "not" : "") + " change", expectedChanges[i], rule.evaluate(testCell));
			}
		}
	}

	private void addAdjacents(ICell cell, int aliveAdjacents) {
		for (int i = 0; i < aliveAdjacents; i++) {
			Cell.makeTwoCellsAdjacent(cell, Cell.createCell(-1, -1, true));
		}
		for (int i = 0; i < (8 - aliveAdjacents); i++) {
			Cell.makeTwoCellsAdjacent(cell, Cell.createCell(-1, -1, false));
		}
	}

	private boolean[] generateExpectedAlivesChangesFromRuleString(String str) throws Exception {
		String[] bornAliveParts = str.split("/");
		if (bornAliveParts.length != 2) {
			throw new IllegalArgumentException("Rule string must have '/' separator");
		}
		boolean[] result = new boolean[] {true,true,true,true,true,true,true,true,true};
		for (int ch = 1; ch < bornAliveParts[1].length(); ch++) {
			result[bornAliveParts[1].charAt(ch) - 48] = false;
		}
		return result;
	}

	private boolean[] generateExpectedDeadsChangesFromRuleString(String str) throws Exception {
		String[] bornAliveParts = str.split("/");
		if (bornAliveParts.length != 2) {
			throw new IllegalArgumentException("Rule string must have '/' separator");
		}
		boolean[] result = new boolean[9];
		for (int ch = 1; ch < bornAliveParts[0].length(); ch++) {
			result[bornAliveParts[0].charAt(ch) - 48] = true;
		}
		return result;
	}
}
