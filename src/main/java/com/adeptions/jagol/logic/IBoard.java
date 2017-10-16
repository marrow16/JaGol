package com.adeptions.jagol.logic;

import com.adeptions.jagol.patterns.IPattern;

public interface IBoard {
	IGenerationController generationController();

	ICell cell(int row, int column);
	boolean cellAlive(int row, int column);

	void drawPattern(int row, int column, IPattern pattern);
}
