package com.foo.gol.logic;

import java.util.Collection;

public interface ICell {
	boolean isAlive();
	boolean isAlive(boolean newAlive);

	Collection<ICell> adjacentCells();

	int row();
	int column();

	ICell clone(boolean newAliveState);

	void addAdjacentCell(ICell adjacent);
}
