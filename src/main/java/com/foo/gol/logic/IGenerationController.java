package com.foo.gol.logic;

import java.util.List;

public interface IGenerationController {
	void startInitialisation();
	void addControlledCell(Cell cell);
	void endInitialisation();

	GenerationState state();

	/**
	 * Processes the next generation for all cells
	 * @return the cells whose state changed
	 */
	List<ICell> nextGeneration();
}
