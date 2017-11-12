package com.adeptions.jagol.logic;

import java.util.*;

public class Cell implements ICell {
	private int row;
	private int column;
	private boolean alive;
	private long age;
	private List<ICell> adjacents = new ArrayList<>();

	private Cell(int row, int column) {
		this(row, column, false);
	}

	private Cell(int row, int column, boolean alive) {
		this.row = row;
		this.column = column;
		this.alive = alive;
	}

	public static Cell createCell(int row, int column) {
		return new Cell(row, column);
	}

	public static Cell createCell(int row, int column, boolean alive) {
		return new Cell(row, column, alive);
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public boolean isAlive(boolean newAlive) {
		boolean changed = alive != newAlive;
		alive = newAlive;
		age = 0L;
		return changed;
	}

	@Override
	public long getAge() {
		return age;
	}

	@Override
	public void age() {
		age++;
	}

	@Override
	public Collection<ICell> adjacentCells() {
		return Collections.unmodifiableCollection(adjacents);
	}

	@Override
	public int row() {
		return row;
	}

	@Override
	public int column() {
		return column;
	}

	@Override
	public ICell clone(boolean newAliveState) {
		return new Cell(row, column, newAliveState);
	}

	@Override
	public void addAdjacentCell(ICell adjacent) {
		adjacents.add(adjacent);
	}

	public static void makeTwoCellsAdjacent(ICell firstCell, ICell secondCell) {
		firstCell.addAdjacentCell(secondCell);
		secondCell.addAdjacentCell(firstCell);
	}
}
