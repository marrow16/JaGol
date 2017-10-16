package com.adeptions.jagol.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeadCell implements ICell {
	private List<ICell> adjacentCells = new ArrayList<>();
	@Override
	public boolean isAlive() {
		return false;
	}

	@Override
	public boolean isAlive(boolean newAlive) {
		return false;
	}

	@Override
	public Collection<ICell> adjacentCells() {
		return adjacentCells;
	}

	@Override
	public int row() {
		return -1;
	}

	@Override
	public int column() {
		return -1;
	}

	@Override
	public ICell clone(boolean newAliveState) {
		return new DeadCell();
	}

	@Override
	public void addAdjacentCell(ICell adjacent) {
		// don't need to record adjacnets on dead cells
	}
}
