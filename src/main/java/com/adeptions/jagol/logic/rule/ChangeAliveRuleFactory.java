package com.adeptions.jagol.logic.rule;

import java.lang.reflect.Constructor;
import java.util.*;

public class ChangeAliveRuleFactory {
	public static final String CUSTOM_RULE_LABEL = "Custom";

	private static final String PERM_DIGITS = "012345678";

	private static final Map<String,Class> displayNameLookup = new LinkedHashMap<>();
	private static final Map<String,Class> ruleLookup = new HashMap<>();
	private static final Map<Class,String> clazzLookup = new HashMap<>();
	private static final Map<String,IChangeAliveRule> registeredCustoms = new HashMap<>();
	private static final Map<Integer,String> rulePermutationIndices = new LinkedHashMap<>();
	private static final Map<String,Integer> rulePermutations = new HashMap<>();
	static {
		displayNameLookup.put(StandardConways.LABEL, StandardConways.class);
		ruleLookup.put(StandardConways.RULE, StandardConways.class);
		displayNameLookup.put(TwoXTwo.LABEL, TwoXTwo.class);
		ruleLookup.put(TwoXTwo.RULE, TwoXTwo.class);
		displayNameLookup.put(ThirtyFourLife.LABEL, ThirtyFourLife.class);
		ruleLookup.put(ThirtyFourLife.RULE, ThirtyFourLife.class);
		displayNameLookup.put(Amoeba.LABEL, Amoeba.class);
		ruleLookup.put(Amoeba.RULE, Amoeba.class);
		displayNameLookup.put(AntiLife.LABEL, AntiLife.class);
		ruleLookup.put(AntiLife.RULE, AntiLife.class);
		displayNameLookup.put(Assimilation.LABEL, Assimilation.class);
		ruleLookup.put(Assimilation.RULE, Assimilation.class);
		displayNameLookup.put(Bacteria.LABEL, Bacteria.class);
		ruleLookup.put(Bacteria.RULE, Bacteria.class);
		displayNameLookup.put(BlinkerLife.LABEL, BlinkerLife.class);
		ruleLookup.put(BlinkerLife.RULE, BlinkerLife.class);
		displayNameLookup.put(Blinkers.LABEL, Blinkers.class);
		ruleLookup.put(Blinkers.RULE, Blinkers.class);
		displayNameLookup.put(Bugs.LABEL, Bugs.class);
		ruleLookup.put(Bugs.RULE, Bugs.class);
		displayNameLookup.put(Coagulations.LABEL, Coagulations.class);
		ruleLookup.put(Coagulations.RULE, Coagulations.class);
		displayNameLookup.put(Coral.LABEL, Coral.class);
		ruleLookup.put(Coral.RULE, Coral.class);
		displayNameLookup.put(CorrosionOfConformity.LABEL, CorrosionOfConformity.class);
		ruleLookup.put(CorrosionOfConformity.RULE, CorrosionOfConformity.class);
		displayNameLookup.put(DayAndNight.LABEL, DayAndNight.class);
		ruleLookup.put(DayAndNight.RULE, DayAndNight.class);
		displayNameLookup.put(Diamoeba.LABEL, Diamoeba.class);
		ruleLookup.put(Diamoeba.RULE, Diamoeba.class);
		displayNameLookup.put(DotLife.LABEL, DotLife.class);
		ruleLookup.put(DotLife.RULE, DotLife.class);
		displayNameLookup.put(DryLife.LABEL, DryLife.class);
		ruleLookup.put(DryLife.RULE, DryLife.class);
		displayNameLookup.put(EightLife.LABEL, EightLife.class);
		ruleLookup.put(EightLife.RULE, EightLife.class);
		displayNameLookup.put(ElectrifiedMaze.LABEL, ElectrifiedMaze.class);
		ruleLookup.put(ElectrifiedMaze.RULE, ElectrifiedMaze.class);
		displayNameLookup.put(Flock.LABEL, Flock.class);
		ruleLookup.put(Flock.RULE, Flock.class);
		displayNameLookup.put(Fredkin.LABEL, Fredkin.class);
		ruleLookup.put(Fredkin.RULE, Fredkin.class);
		displayNameLookup.put(Gems.LABEL, Gems.class);
		ruleLookup.put(Gems.RULE, Gems.class);
		displayNameLookup.put(GemsMinor.LABEL, GemsMinor.class);
		ruleLookup.put(GemsMinor.RULE, GemsMinor.class);
		displayNameLookup.put(Gnarl.LABEL, Gnarl.class);
		ruleLookup.put(Gnarl.RULE, Gnarl.class);
		displayNameLookup.put(HTrees.LABEL, HTrees.class);
		ruleLookup.put(HTrees.RULE, HTrees.class);
		displayNameLookup.put(HiLife.LABEL, HiLife.class);
		ruleLookup.put(HiLife.RULE, HiLife.class);
		displayNameLookup.put(Holstein.LABEL, Holstein.class);
		ruleLookup.put(Holstein.RULE, Holstein.class);
		displayNameLookup.put(HoneyLife.LABEL, HoneyLife.class);
		ruleLookup.put(HoneyLife.RULE, HoneyLife.class);
		displayNameLookup.put(Iceballs.LABEL, Iceballs.class);
		ruleLookup.put(Iceballs.RULE, Iceballs.class);
		displayNameLookup.put(InverseLife.LABEL, InverseLife.class);
		ruleLookup.put(InverseLife.RULE, InverseLife.class);
		displayNameLookup.put(LandRush.LABEL, LandRush.class);
		ruleLookup.put(LandRush.RULE, LandRush.class);
		displayNameLookup.put(LifeWithoutDeath.LABEL, LifeWithoutDeath.class);
		ruleLookup.put(LifeWithoutDeath.RULE, LifeWithoutDeath.class);
		displayNameLookup.put(LiveFreeOrDie.LABEL, LiveFreeOrDie.class);
		ruleLookup.put(LiveFreeOrDie.RULE, LiveFreeOrDie.class);
		displayNameLookup.put(LongLife.LABEL, LongLife.class);
		ruleLookup.put(LongLife.RULE, LongLife.class);
		displayNameLookup.put(LowDeath.LABEL, LowDeath.class);
		ruleLookup.put(LowDeath.RULE, LowDeath.class);
		displayNameLookup.put(LowLife.LABEL, LowLife.class);
		ruleLookup.put(LowLife.RULE, LowLife.class);
		displayNameLookup.put(Maze.LABEL, Maze.class);
		ruleLookup.put(Maze.RULE, Maze.class);
		displayNameLookup.put(MazeWithMice.LABEL, MazeWithMice.class);
		ruleLookup.put(MazeWithMice.RULE, MazeWithMice.class);
		displayNameLookup.put(Mazectric.LABEL, Mazectric.class);
		ruleLookup.put(Mazectric.RULE, Mazectric.class);
		displayNameLookup.put(MazectricWithMice.LABEL, MazectricWithMice.class);
		ruleLookup.put(MazectricWithMice.RULE, MazectricWithMice.class);
		displayNameLookup.put(Move.LABEL, Move.class);
		ruleLookup.put(Move.RULE, Move.class);
		displayNameLookup.put(PedestrianLife.LABEL, PedestrianLife.class);
		ruleLookup.put(PedestrianLife.RULE, PedestrianLife.class);
		displayNameLookup.put(PlowWorld.LABEL, PlowWorld.class);
		ruleLookup.put(PlowWorld.RULE, PlowWorld.class);
		displayNameLookup.put(PseudoLife.LABEL, PseudoLife.class);
		ruleLookup.put(PseudoLife.RULE, PseudoLife.class);
		displayNameLookup.put(Replicator.LABEL, Replicator.class);
		ruleLookup.put(Replicator.RULE, Replicator.class);
		displayNameLookup.put(Seeds.LABEL, Seeds.class);
		ruleLookup.put(Seeds.RULE, Seeds.class);
		displayNameLookup.put(Serviettes.LABEL, Serviettes.class);
		ruleLookup.put(Serviettes.RULE, Serviettes.class);
		displayNameLookup.put(SlowBlob.LABEL, SlowBlob.class);
		ruleLookup.put(SlowBlob.RULE, SlowBlob.class);
		displayNameLookup.put(SnowLife.LABEL, SnowLife.class);
		ruleLookup.put(SnowLife.RULE, SnowLife.class);
		displayNameLookup.put(Stains.LABEL, Stains.class);
		ruleLookup.put(Stains.RULE, Stains.class);
		displayNameLookup.put(Vote.LABEL, Vote.class);
		ruleLookup.put(Vote.RULE, Vote.class);
		displayNameLookup.put(Vote45.LABEL, Vote45.class);
		ruleLookup.put(Vote45.RULE, Vote45.class);
		displayNameLookup.put(WalledCities.LABEL, WalledCities.class);
		ruleLookup.put(WalledCities.RULE, WalledCities.class);
		displayNameLookup.put(WaterSurface.LABEL, WaterSurface.class);
		ruleLookup.put(WaterSurface.RULE, WaterSurface.class);
		for (Map.Entry<String,Class> entry: displayNameLookup.entrySet()) {
			clazzLookup.put(entry.getValue(), entry.getKey());
		}
		int aliveCounts = (int)Math.pow(2, 9);
		Set<String> stayAlivePerms = new LinkedHashSet<>();
		for (int alive = 0; alive < aliveCounts; alive++) {
			stayAlivePerms.add("S" + getPermString(alive));
		}
		Set<String> bornPerms = new LinkedHashSet<>();
		int bornCounts = (int)Math.pow(2, 9);
		for (int born = 0; born < bornCounts; born++) {
			bornPerms.add("B" + getPermString(born));
		}
		for (String bornPerm: bornPerms) {
			for (String alivePerm: stayAlivePerms) {
				Integer index = rulePermutationIndices.size();
				rulePermutationIndices.put(index, bornPerm + "/" + alivePerm);
				rulePermutations.put(bornPerm + "/" + alivePerm, index);
			}
		}
	}

	public static IChangeAliveRule createFromLabel(String label) {
		IChangeAliveRule result = null;
		Class clazz = displayNameLookup.get(label);
		if (clazz != null) {
			try {
				Constructor constructor = clazz.getConstructor();
				result = (IChangeAliveRule)constructor.newInstance();
			} catch (Exception e) {
				// swallow if no constructor found
			}
		} else {
			result = registeredCustoms.get(label);
		}
		return result;
	}

	public static String instanceToLabel(IChangeAliveRule rule) {
		return clazzLookup.get(rule.getClass());
	}

	public static List<String> getLabelsList() {
		List<String> result = new ArrayList<>(displayNameLookup.keySet());
		result.addAll(registeredCustoms.keySet());
		return result;
	}

	public static void registerNamedCustom(IChangeAliveRule rule) {
		if (rule.isCustom() &&
				!CUSTOM_RULE_LABEL.equals(rule.getType()) &&
				!displayNameLookup.containsKey(rule.getType()) &&
				!registeredCustoms.containsKey(rule.getType())) {
			registeredCustoms.put(rule.getType(), rule);
		}
	}

	private static String getPermString(int permNo) {
		StringBuilder result = new StringBuilder();
		int perm = permNo;
		for (int i = 0; i < PERM_DIGITS.length(); i++) {
			if ((perm & 1) == 1) {
				result.append(PERM_DIGITS.charAt(i));
			}
			perm = perm >> 1;
		}
		return result.toString();
	}

	public static int getPermutationsCount() {
		return rulePermutationIndices.size();
	}

	public static String getPermutation(Integer index) {
		return rulePermutationIndices.get(index);
	}

	public static IChangeAliveRule getPermutationRule(int index) {
		String ruleString = rulePermutationIndices.get(index);
		Class clazz = ruleLookup.get(ruleString);
		if (clazz != null) {
			try {
				Constructor constructor = clazz.getConstructor();
				return (IChangeAliveRule)constructor.newInstance();
			} catch (Exception e) {
				// swallow if no constructor found
			}
		}
		return Custom.createFromRuleString(ruleString);
	}

	public static Integer getPermutationIndex(String rule) {
		return rulePermutations.get(rule);
	}
}
