package com.foo.gol.logic;

import java.util.*;

public class FullScanGenerationController implements IGenerationController {
	private List<Cell> cells;;
	private GenerationState state;
	private IChangeAliveRule changeAliveRule;

	public FullScanGenerationController(IChangeAliveRule changeAliveRule) {
		if (changeAliveRule == null) {
			throw new IllegalArgumentException("Change alive rule may not be null");
		}
		this.changeAliveRule = changeAliveRule;
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
		List<ICell> changedCells = new ArrayList<>(cells.size());
		for (ICell cell: cells) {
			if (changeAliveRule.evaluate(cell)) {
				changedCells.add(cell);
			}
		}
		state = changedCells.size() > 0 ? GenerationState.READY : GenerationState.STABLE;
		for (ICell cell: changedCells) {
			cell.isAlive(!cell.isAlive());
		}
		return changedCells;
	}

	@Override
	public IChangeAliveRule getChangeAliveRule() {
		return changeAliveRule;
	}

	@Override
	public void setChangeAliveRule(IChangeAliveRule changeAliveRule) {
		this.changeAliveRule = changeAliveRule;
	}
}
