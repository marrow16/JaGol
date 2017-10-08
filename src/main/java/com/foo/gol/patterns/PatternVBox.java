package com.foo.gol.patterns;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class PatternVBox extends VBox {
	private IPattern pattern;
	PatternVBox(IPattern pattern) {
		super();
		this.pattern = pattern;
		paddingProperty().setValue(new Insets(4,4,4,4));
		borderProperty().setValue(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(4,4,4,4))));
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
