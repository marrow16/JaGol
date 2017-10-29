package com.adeptions.jagol.ui;

import java.util.List;

public interface IController {
	String SETTING_NAME_RUNNING = "running";
	String SETTING_NAME_WIDTH = "width";
	String SETTING_NAME_HEIGHT = "height";
	String SETTING_NAME_DELAY = "delay";
	String SETTING_NAME_RULE = "rule";
	String SETTING_NAME_PERMUTATION = "permutation";
	String SETTING_NAME_RANDOMIZATION = "randomization";
	String SETTING_NAME_WRAPPING = "wrapping";
	String SETTING_NAME_SIZE = "size";
	String SETTING_NAME_ACTIVE_COLOR = "active-color";
	String SETTING_NAME_INACTIVE_COLOR = "inactive-color";
	String SETTING_NAME_SHOW_GRID = "grid";
	String SETTING_NAME_GRID_COLOR = "grid-color";

	void run();
	void stop();
	void step();

	void clear();
	void randomize(Double density);

	String set(String settingName, Object settingValue);
	String permutationIncrement(Integer increment);
	String position(GridPosition newPosition);
	void live(GridPosition position);
	void die(GridPosition position);
	void drawPatternStrings(List<String> lines);
	void print(String str);
}
