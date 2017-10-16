package com.foo.gol.patterns;

import com.foo.gol.config.GameConfig;
import com.foo.gol.logic.ICell;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class PatternVBox extends VBox {
	private IPattern pattern;
	private Canvas canvas;
	private Label label;

	PatternVBox(IPattern pattern, GameConfig drawingConfig) {
		super();
		this.pattern = pattern;
		paddingProperty().setValue(new Insets(4,4,4,4));
		borderProperty().setValue(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(4,4,4,4))));
		createControls();
		redrawPattern(drawingConfig);
	}

	private void createControls() {
		canvas = new Canvas(0, 0);
		label = new Label(pattern.getName());
		getChildren().addAll(canvas, label);
	}

	public void redrawPattern(GameConfig drawingConfig) {
		int rows = pattern.rows();
		int columns = pattern.columns();
		int cellSize = drawingConfig.getCellSize();
		int cellSpace = drawingConfig.getCellSpace();
		int cellSpacing = cellSize + cellSpace;
		int canvasWidth = 2 + (columns * cellSize) + ((columns - 1) * cellSpace);
		int canvasHeight = 2 + (rows * cellSize) + ((rows - 1) * cellSpace);
		canvas.setHeight(canvasHeight);
		canvas.setWidth(canvasWidth);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(drawingConfig.getCellInactiveColor());
		gc.fillRect(0,0, canvasWidth, canvasHeight);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		gc.strokeRect(0,0, canvasWidth, canvasHeight);
		if (cellSpace > 0) {
			gc.setStroke(drawingConfig.getCellGridColor());
			for (int r = 1; r < rows; r++) {
				gc.strokeLine(1.5d, (r * cellSpacing) + 0.5d, canvasWidth - 1.5d, (r * cellSpacing) + 0.5d);
			}
			for (int c = 1; c < columns; c++) {
				gc.strokeLine((c * cellSpacing) + 0.5d, 1.5d, (c * cellSpacing) + 0.5d, canvasHeight - 1.5d);
			}
		}
		gc.setFill(drawingConfig.getCellActiveColor());
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				ICell cell = pattern.cell(row, column);
				if (cell.isAlive()) {
					gc.fillRect((column * cellSpacing) + 1, (row * cellSpacing) + 1, cellSize, cellSize);
				}
			}
		}
	}

	public IPattern getPattern() {
		return pattern;
	}

	public void showSelectedBorder() {
		borderProperty().setValue(new Border(new BorderStroke(Color.DODGERBLUE, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(4,4,4,4))));
	}

	public void clearSelectedBorder() {
		borderProperty().setValue(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(4,4,4,4))));
	}

	public void prepareForDrag() {
		paddingProperty().setValue(new Insets(0,0,0,0));
		borderProperty().setValue(null);
	}
}
