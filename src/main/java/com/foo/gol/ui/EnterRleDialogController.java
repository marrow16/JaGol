package com.foo.gol.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EnterRleDialogController implements Initializable {
	private static final String EXAMPLE_HEADER = "#N (enter the name of pattern)\n" +
			"#O (enter name and date of pattern creator)\n" +
			"#C (enter any additional comments)\n";

	@FXML
	private BorderPane mainPane;
	@FXML
	private TextField nameField;
	@FXML
	private TextArea rlePatternField;

	private Stage dialog;
	private boolean modalResult = false;
	private String patternName;
	private String patternRle;

	private File initialDirectory = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		modalResult = false;
	}

	public void setStage(Stage stage) {
		this.dialog = stage;
	}

	@FXML
	public void onOkButtonClicked(ActionEvent actionEvent) {
		modalResult = true;
		patternName = nameField.getText();
		patternRle = rlePatternField.getText();
		dialog.close();
	}

	@FXML
	public void onCancelButtonClicked(ActionEvent actionEvent) {
		dialog.close();
	}

	@FXML
	public void onSaveAsButtonClicked(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game of Life pattern (*.rle)", "*.rle"));
		fileChooser.setTitle("Save Pattern as");
		fileChooser.setInitialDirectory(initialDirectory == null ? new File(".") : initialDirectory);
		File file = fileChooser.showSaveDialog(dialog);
		if (file != null) {
			initialDirectory = file.getParentFile();
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(rlePatternField.getText());
				writer.close();
			} catch (IOException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
				alert.showAndWait();
			}
		}
	}

	public boolean isModalResultOK() {
		return modalResult;
	}

	public String getPatternName() {
		return patternName;
	}

	public String getPatternRle() {
		return patternRle;
	}

	public void setRlePattern(String patternData) {
		rlePatternField.setText(EXAMPLE_HEADER + patternData);
	}

	public void setInitialDirectory(File initialDirectory) {
		this.initialDirectory = initialDirectory;
	}

	public File getInitialDirectory() {
		return initialDirectory;
	}
}
