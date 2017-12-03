package com.adeptions.jagol.logic;

import com.adeptions.jagol.logic.rule.IChangeAliveRule;

import java.util.*;

public class FullScanGenerationController implements IGenerationController {
	private List<ICell> cells;
	private GenerationState state;
	private IChangeAliveRule changeAliveRule;
	private boolean cellsAge;
	private long maximumCellAge;

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
	public void addControlledCell(ICell cell) {
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
		if (cellsAge) {
			for (ICell cell : cells) {
				if (changeAliveRule.evaluate(cell)) {
					changedCells.add(cell);
				} else if (cell.isAlive()) {
					cell.age();
					if (cell.getAge() > maximumCellAge) {
						changedCells.add(cell);
					}
				}
			}
		} else {
			for (ICell cell : cells) {
				if (changeAliveRule.evaluate(cell)) {
					changedCells.add(cell);
				}
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

	@Override
	public boolean getCellsAge() {
		return cellsAge;
	}

	@Override
	public void setCellsAge(boolean cellsAge) {
		this.cellsAge = cellsAge;
	}

	@Override
	public long getMaximumCellAge() {
		return maximumCellAge;
	}

	@Override
	public void setMaximumCellAge(long maximumCellAge) {
		this.maximumCellAge = maximumCellAge;
	}
}
