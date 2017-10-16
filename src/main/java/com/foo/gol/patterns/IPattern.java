package com.foo.gol.patterns;

import com.foo.gol.logic.ICell;
import com.foo.gol.logic.rule.IChangeAliveRule;
import com.foo.gol.config.GameConfig;

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
}
