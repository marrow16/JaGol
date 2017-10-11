package com.foo.gol;

import com.foo.gol.ui.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameOfLife extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
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

	public static void main(String[] args) {
		launch(args);
	}

}
