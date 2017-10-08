package com.foo.gol.logic;

import java.util.ArrayList;
import java.util.List;

public class ChangeOnlyGenerationController implements IGenerationController {
	private List<Cell> cells;;
	private GenerationState state;

	public ChangeOnlyGenerationController() {
		state = GenerationState.UNKNOWN;
	}

	@Override
	public void startInitialisation() {
		state = GenerationState.INITIALISING;
		cells = new ArrayList<>();
	}

	@Override
	public void addControlledCell(Cell cell) {
		if (!GenerationState.INITIALISING.equals(state)) {
			throw new IllegalStateException("Cannot add cells to controller when controller is not initialising");
		}
		cells.add(cell);
	}

	@Override
	public void endInitialisation() {
		state = GenerationState.READY;
	}

	@Override
	public GenerationState state() {
		return state;
	}

	@Override
	public List<ICell> nextGeneration() {
		state = GenerationState.GENERATING;
		List<ICell> changedCells = new ArrayList<>(cells.size());
		// TODO

		state = changedCells.size() > 0 ? GenerationState.READY : GenerationState.STABLE;
		return changedCells;
	}
}
