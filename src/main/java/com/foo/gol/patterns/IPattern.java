package com.foo.gol.patterns;

import com.foo.gol.logic.ICell;
import com.foo.gol.ui.BoardDrawingConfig;
import javafx.scene.layout.VBox;

import java.util.List;

public interface IPattern {
	String name();
	int columns();
	int rows();
	List<ICell> cells();
	ICell cell(int row, int column);

	PatternVBox generateDisplay(BoardDrawingConfig drawingConfig);
}
