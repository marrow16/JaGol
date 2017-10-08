package com.foo.gol.logic;

public interface IBoard {
	IGenerationController generationController();

	ICell cell(int row, int column);
	boolean cellAlive(int row, int column);
}
