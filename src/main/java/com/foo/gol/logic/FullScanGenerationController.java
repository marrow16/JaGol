package com.foo.gol.logic;

import java.util.*;

public class FullScanGenerationController implements IGenerationController {
	private List<Cell> cells;;
	private GenerationState state;

	public FullScanGenerationController() {
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
		List<ICell> originalCells = new ArrayList<>(cells.size());
		for (ICell cell: cells) {
			int adjacentsAlive = countAdjacentsAlive(cell);
			if (cell.isAlive()) {
				if (adjacentsAlive < 2 || adjacentsAlive > 3) {
					originalCells.add(cell);
					changedCells.add(cell.clone(false));
				}
			} else if (adjacentsAlive == 3) {
				originalCells.add(cell);
				changedCells.add(cell.clone(true));
			}
		}
		state = changedCells.size() > 0 ? GenerationState.READY : GenerationState.STABLE;
		for (int i = 0, imax = changedCells.size(); i < imax; i++) {
			originalCells.get(i).isAlive(changedCells.get(i).isAlive());
		}
		return changedCells;
	}

	private int countAdjacentsAlive(ICell cell) {
		int result = 0;
		for (ICell adjacentCell: cell.adjacentCells()) {
			result += (adjacentCell.isAlive() ? 1 : 0);
		}
		return result;
	}
}
