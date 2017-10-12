package com.foo.gol;

import com.foo.gol.ui.Loader;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameOfLife extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		new Loader(primaryStage).load();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
