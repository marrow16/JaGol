package com.foo.gol.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;
import java.io.IOException;

public class Loader {
	private Stage primaryStage;

	public Loader(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void load() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("ui.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Conway's Game of Life");
		// wait for scene to show and then tell it that it's shown...
		primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
			Controller.getInstance().shown();
		});
		primaryStage.show();
	}
}
