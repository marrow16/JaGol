package com.foo.gol.logic;

import com.foo.gol.logic.rule.IChangeAliveRule;

import java.util.List;

public interface IGenerationController {
	void startInitialisation();
	void addControlledCell(ICell cell);
	void endInitialisation();

	GenerationState state();

	/**
	 * Processes the next generation for all cells
	 * @return the cells whose state changed
	 */
	List<ICell> nextGeneration();

	IChangeAliveRule getChangeAliveRule();
	void setChangeAliveRule(IChangeAliveRule changeAliveRule);
}
