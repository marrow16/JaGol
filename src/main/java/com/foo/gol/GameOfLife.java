package com.foo.gol;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class GameOfLife extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		URL url = getClass().getResource("ui.fxml");
		Parent parent = FXMLLoader.load(getClass().getResource("ui.fxml"));
		primaryStage.setTitle("Conway's Game of Life");
		primaryStage.setScene(new Scene(parent));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
