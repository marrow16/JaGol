package com.adeptions.jagol.patterns;

import com.adeptions.jagol.logic.ICell;
import com.adeptions.jagol.logic.rule.IChangeAliveRule;
import com.adeptions.jagol.config.GameConfig;

import java.util.List;

public interface IPattern {
	int columns();
	int rows();
	List<ICell> cells();
	ICell cell(int row, int column);

	PatternVBox generateDisplay(GameConfig drawingConfig);

	String getName();
	void setName(String name);
	List<String> getComments();
	String getOrigination();
	void setOrigination(String origination);
	String getCoordinates();
	void setCoordinates(String coordinates);
	IChangeAliveRule getRule();
	void setRule(IChangeAliveRule rule);

	void rotate90Clockwise();
	void rotate90AntiClockwise();
	void flipVertically();
	void flipHorizontally();
}
