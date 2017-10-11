package com.foo.gol.logic;

import com.foo.gol.patterns.IPattern;

public interface IBoard {
	IGenerationController generationController();

	ICell cell(int row, int column);
	boolean cellAlive(int row, int column);

	void drawPattern(int row, int column, IPattern pattern);
}
