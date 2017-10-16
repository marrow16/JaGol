package com.adeptions.jagol.logic.rule;

import java.lang.reflect.Constructor;
import java.util.*;

public class ChangeAliveRuleFactory {
	private static Map<String,Class> displayNameLookup = new LinkedHashMap<>();
	private static Map<Class,String> clazzLookup = new HashMap<>();
	private static Map<String,IChangeAliveRule> registeredCustoms = new HashMap<>();
	static {
		displayNameLookup.put(StandardConways.LABEL, StandardConways.class);
		displayNameLookup.put(TwoXTwo.LABEL, TwoXTwo.class);
		displayNameLookup.put(ThirtyFourLife.LABEL, ThirtyFourLife.class);
		displayNameLookup.put(Amoeba.LABEL, Amoeba.class);
		displayNameLookup.put(AntiLife.LABEL, AntiLife.class);
		displayNameLookup.put(Assimilation.LABEL, Assimilation.class);
		displayNameLookup.put(Bacteria.LABEL, Bacteria.class);
		displayNameLookup.put(BlinkerLife.LABEL, BlinkerLife.class);
		displayNameLookup.put(Blinkers.LABEL, Blinkers.class);
		displayNameLookup.put(Bugs.LABEL, Bugs.class);
		displayNameLookup.put(Coagulations.LABEL, Coagulations.class);
		displayNameLookup.put(Coral.LABEL, Coral.class);
		displayNameLookup.put(CorrosionOfConformity.LABEL, CorrosionOfConformity.class);
		displayNameLookup.put(DayAndNight.LABEL, DayAndNight.class);
		displayNameLookup.put(Diamoeba.LABEL, Diamoeba.class);
		displayNameLookup.put(DotLife.LABEL, DotLife.class);
		displayNameLookup.put(DryLife.LABEL, DryLife.class);
		displayNameLookup.put(EightLife.LABEL, EightLife.class);
		displayNameLookup.put(ElectrifiedMaze.LABEL, ElectrifiedMaze.class);
		displayNameLookup.put(Flock.LABEL, Flock.class);
		displayNameLookup.put(Fredkin.LABEL, Fredkin.class);
		displayNameLookup.put(Gems.LABEL, Gems.class);
		displayNameLookup.put(GemsMinor.LABEL, GemsMinor.class);
		displayNameLookup.put(Gnarl.LABEL, Gnarl.class);
		displayNameLookup.put(HTrees.LABEL, HTrees.class);
		displayNameLookup.put(HiLife.LABEL, HiLife.class);
		displayNameLookup.put(Holstein.LABEL, Holstein.class);
		displayNameLookup.put(HoneyLife.LABEL, HoneyLife.class);
		displayNameLookup.put(Iceballs.LABEL, Iceballs.class);
		displayNameLookup.put(InverseLife.LABEL, InverseLife.class);
		displayNameLookup.put(LandRush.LABEL, LandRush.class);
		displayNameLookup.put(LifeWithoutDeath.LABEL, LifeWithoutDeath.class);
		displayNameLookup.put(LiveFreeOrDie.LABEL, LiveFreeOrDie.class);
		displayNameLookup.put(LongLife.LABEL, LongLife.class);
		displayNameLookup.put(LowDeath.LABEL, LowDeath.class);
		displayNameLookup.put(LowLife.LABEL, LowLife.class);
		displayNameLookup.put(Maze.LABEL, Maze.class);
		displayNameLookup.put(MazeWithMice.LABEL, MazeWithMice.class);
		displayNameLookup.put(Mazectric.LABEL, Mazectric.class);
		displayNameLookup.put(MazectricWithMice.LABEL, MazectricWithMice.class);
		displayNameLookup.put(Move.LABEL, Move.class);
		displayNameLookup.put(PedestrianLife.LABEL, PedestrianLife.class);
		displayNameLookup.put(PlowWorld.LABEL, PlowWorld.class);
		displayNameLookup.put(PseudoLife.LABEL, PseudoLife.class);
		displayNameLookup.put(Replicator.LABEL, Replicator.class);
		displayNameLookup.put(Seeds.LABEL, Seeds.class);
		displayNameLookup.put(Serviettes.LABEL, Serviettes.class);
		displayNameLookup.put(SlowBlob.LABEL, SlowBlob.class);
		displayNameLookup.put(SnowLife.LABEL, SnowLife.class);
		displayNameLookup.put(Stains.LABEL, Stains.class);
		displayNameLookup.put(Vote.LABEL, Vote.class);
		displayNameLookup.put(Vote45.LABEL, Vote45.class);
		displayNameLookup.put(WalledCities.LABEL, WalledCities.class);
		displayNameLookup.put(WaterSurface.LABEL, WaterSurface.class);
		for (Map.Entry<String,Class> entry: displayNameLookup.entrySet()) {
			clazzLookup.put(entry.getValue(), entry.getKey());
		}
	}

	public static final String CUSTOM_RULE_LABEL = "Custom";

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
}
