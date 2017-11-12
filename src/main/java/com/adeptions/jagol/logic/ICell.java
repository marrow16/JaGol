package com.adeptions.jagol.logic;

import java.util.Collection;

public interface ICell {
	boolean isAlive();
	boolean isAlive(boolean newAlive);

	long getAge();
	void age();

	Collection<ICell> adjacentCells();

	int row();
	int column();

	ICell clone(boolean newAliveState);

	void addAdjacentCell(ICell adjacent);
}
