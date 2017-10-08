package com.foo.gol.ui;

import com.foo.gol.logic.*;
import com.foo.gol.patterns.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
	private final DataFormat patternDragFormat = new DataFormat("com.foo.gol.ui.pattern");

	private BoardDrawingConfig boardDrawingConfig;
	private IBoard board;
	private IGenerationController generationController;
	private Timeline animationLoop = null;
	private PatternLibrary patternLibrary;

	private boolean running = false;

	private PatternVBox draggingPattern = null;
	private PatternVBox selectedPattern = null;

	private FileChooser fileChooser;
	private File initialDirectory = new File(".");

	private CanvasDrawingMode canvasDrawingMode = CanvasDrawingMode.NONE;
	private GridPosition canvasStartPosition = null;
	private GridRectangle canvasMarkedRectangle = null;

	@FXML
	private SplitPane mainPane;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private Canvas canvas;
	@FXML
	private Button playButton;
	@FXML
	private Button stepButton;
	@FXML
	private Button stopButton;
	@FXML
	private Slider animationSpeedSlider;
	@FXML
	private Button randomizeButton;
	@FXML
	private Button clearButton;
	@FXML
	private Slider randomDensitySlider;
	@FXML
	private Spinner boardWidthSpinner;
	@FXML
	private Spinner boardHeightSpinner;
	@FXML
	private Spinner cellSizeSpinner;
	@FXML
	private ColorPicker activeCellColorPicker;
	@FXML
	private ColorPicker inActiveCellColorPicker;
	@FXML
	private CheckBox drawGridCheckbox;
	@FXML
	private ColorPicker gridColorPicker;
	@FXML
	private VBox patternsContainer;
	@FXML
	private Accordion accordion;
	@FXML
	private TitledPane accordionPatterns;
	@FXML
	private ScrollPane patternsScrollPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game of Life pattern (*.rle)", "*.rle"));
		fileChooser.setTitle("Load Pattern");
		fileChooser.setInitialDirectory(initialDirectory);
		patternLibrary = new PatternLibrary();
		boardDrawingConfig = new BoardDrawingConfig();
		boardWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 500, boardDrawingConfig.getColumns()));
		boardWidthSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
			resizeBoard();
		});
		boardHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 500, boardDrawingConfig.getRows()));
		boardHeightSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
			resizeBoard();
		});
		cellSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, boardDrawingConfig.getCellSize()));
		cellSizeSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
			boardDrawingConfig.setCellSize((Integer)newValue);
			drawBoard();
			rebuildPatterns();
		});
		activeCellColorPicker.setValue(boardDrawingConfig.getCellActiveColor());
		inActiveCellColorPicker.setValue(boardDrawingConfig.getCellInactiveColor());
		drawGridCheckbox.setSelected(boardDrawingConfig.getCellSpace() > 0);
		gridColorPicker.setValue(boardDrawingConfig.getCellGridColor());
		updateControls();
		createBoard(true);
		drawBoard();
		rebuildPatterns();
	}

	private void createBoard(boolean randomize) {
		generationController = new FullScanGenerationController();
		board = new Board(boardDrawingConfig.getColumns(), boardDrawingConfig.getRows(), generationController);
		if (randomize) {
			double randomDensity = randomDensitySlider.getValue() / 100d;
			for (int row = 0; row < boardDrawingConfig.getRows(); row++) {
				for (int column = 0; column < boardDrawingConfig.getColumns(); column++) {
					board.cell(row, column).isAlive(Math.random() < randomDensity);
				}
			}
		}
	}

	private void resizeBoard() {
		int newWidth = (Integer)boardWidthSpinner.getValue();
		int newHeight = (Integer)boardHeightSpinner.getValue();
		int oldWidth = boardDrawingConfig.getColumns();
		int oldHeight = boardDrawingConfig.getRows();
		boardDrawingConfig.setRows(newHeight);
		boardDrawingConfig.setColumns(newWidth);
		IBoard oldBoard = board;
		createBoard(false);
		for (int row = 0, rowMax = Math.min(newHeight, oldHeight), columnMax = Math.min(newWidth, oldWidth); row < rowMax; row++) {
			for (int column = 0; column < columnMax; column++) {
				board.cell(row, column).isAlive(oldBoard.cellAlive(row, column));
			}
		}
		drawBoard();
	}

	private void drawBoard() {
		int rows = boardDrawingConfig.getRows();
		int columns = boardDrawingConfig.getColumns();
		int cellSize = boardDrawingConfig.getCellSize();
		int cellSpace = boardDrawingConfig.getCellSpace();
		int cellSpacing = cellSize + cellSpace;
		int canvasWidth = 2 + (columns * cellSize) + ((columns - 1) * cellSpace);
		int canvasHeight = 2 + (rows * cellSize) + ((rows - 1) * cellSpace);
		canvas.setWidth(canvasWidth);
		canvas.setHeight(canvasHeight);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(boardDrawingConfig.getCellInactiveColor());
		gc.fillRect(0,0, canvasWidth, canvasHeight);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		gc.strokeRect(0,0, canvasWidth, canvasHeight);
		if (cellSpace > 0) {
			gc.setStroke(boardDrawingConfig.getCellGridColor());
			for (int r = 1; r < rows; r++) {
				gc.strokeLine(1.5d, (r * cellSpacing) + 0.5d, canvasWidth - 1.5d, (r * cellSpacing) + 0.5d);
			}
			for (int c = 1; c < columns; c++) {
				gc.strokeLine((c * cellSpacing) + 0.5d, 1.5d, (c * cellSpacing) + 0.5d, canvasHeight - 1.5d);
			}
		}
		gc.setFill(boardDrawingConfig.getCellActiveColor());
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				if (board.cellAlive(row, column)) {
					gc.fillRect((column * cellSpacing) + 1, (row * cellSpacing) + 1, cellSize, cellSize);
				}
			}
		}
	}

	private void rebuildPatterns() {
		selectedPattern = null;
		draggingPattern = null;
		patternsContainer.getChildren().clear();
		for (IPattern pattern: patternLibrary.getPatterns()) {
			PatternVBox vbox = pattern.generateDisplay(boardDrawingConfig);
			patternsContainer.getChildren().add(vbox);
			makePatternVBoxInteractive(vbox);
/*
			vbox.setOnDragDetected(event -> {
				if (selectedPattern != null && selectedPattern != pattern) {
					selectedPattern.clearSelectedBorder();
					selectedPattern = null;
				}
				selectedPattern = vbox;
				selectedPattern.showSelectedBorder();
				if (!running) {
					Dragboard db = vbox.startDragAndDrop(TransferMode.MOVE);
					vbox.prepareForDrag();
					db.setDragView(vbox.snapshot(null, null));
					ClipboardContent cc = new ClipboardContent();
					cc.put(patternDragFormat, pattern.name());
					db.setContent(cc);
					draggingPattern = vbox;
					selectedPattern.showSelectedBorder();
				}
			});
			vbox.setOnDragDone(event -> {
				draggingPattern = null;
			});
			vbox.setOnMouseClicked(event -> {
				if (selectedPattern != null) {
					selectedPattern.clearSelectedBorder();
					selectedPattern = null;
				}
				selectedPattern = vbox;
				vbox.showSelectedBorder();
			});
*/
		}
	}

	private void makePatternVBoxInteractive(PatternVBox patternVBox) {
		IPattern pattern = patternVBox.getPattern();
		patternVBox.setOnDragDetected(event -> {
			if (selectedPattern != null && selectedPattern != pattern) {
				selectedPattern.clearSelectedBorder();
				selectedPattern = null;
			}
			selectedPattern = patternVBox;
			selectedPattern.showSelectedBorder();
			if (!running) {
				Dragboard db = patternVBox.startDragAndDrop(TransferMode.MOVE);
				patternVBox.prepareForDrag();
				db.setDragView(patternVBox.snapshot(null, null));
				ClipboardContent cc = new ClipboardContent();
				cc.put(patternDragFormat, pattern.name());
				db.setContent(cc);
				draggingPattern = patternVBox;
				selectedPattern.showSelectedBorder();
			}
		});
		patternVBox.setOnDragDone(event -> {
			draggingPattern = null;
		});
		patternVBox.setOnMouseClicked(event -> {
			if (selectedPattern != null) {
				selectedPattern.clearSelectedBorder();
				selectedPattern = null;
			}
			selectedPattern = patternVBox;
			patternVBox.showSelectedBorder();
		});
	}

	private void run() {
		running = true;
		updateControls();
		animationLoop = new Timeline(new KeyFrame(Duration.millis(animationSpeedSlider.getValue()), e -> {
			generation();
		}));
		animationLoop.setCycleCount(-1);
		animationLoop.play();
	}

	private void stop() {
		animationLoop.stop();
		running = false;
		updateControls();
	}

	private void generation() {
		List<ICell> changes = generationController.nextGeneration();
		GraphicsContext gc = canvas.getGraphicsContext2D();
		Color activeColor = boardDrawingConfig.getCellActiveColor();
		Color inactiveColor = boardDrawingConfig.getCellInactiveColor();
		int cellSize = boardDrawingConfig.getCellSize();
		int cellSpacing = cellSize + boardDrawingConfig.getCellSpace();
		for (ICell cell: changes) {
			gc.setFill(cell.isAlive() ? activeColor : inactiveColor);
			gc.fillRect((cell.column() * cellSpacing) + 1, (cell.row() * cellSpacing) + 1, cellSize, cellSize);
		}
		if (changes.size() == 0 && running) {
			stop();
		}
	}

	private void updateControls() {
		playButton.setDisable(running);
		stepButton.setDisable(running);
		stopButton.setDisable(!running);
		animationSpeedSlider.setDisable(running);
		randomizeButton.setDisable(running);
		clearButton.setDisable(running);
		randomDensitySlider.setDisable(running);
		boardWidthSpinner.setDisable(running);
		boardHeightSpinner.setDisable(running);
		cellSizeSpinner.setDisable(running);
		activeCellColorPicker.setDisable(running);
		inActiveCellColorPicker.setDisable(running);
		drawGridCheckbox.setDisable(running);
		gridColorPicker.setDisable(running);
	}

	@FXML
	public void onPlayButtonClick(ActionEvent actionEvent) {
		run();
	}

	@FXML
	public void onStepButtonClick(ActionEvent actionEvent) {
		generation();
	}

	@FXML
	public void onStopButtonClick(ActionEvent actionEvent) {
		stop();
	}

	@FXML
	public void onRandomizeButtonClick(ActionEvent actionEvent) {
		createBoard(true);
		drawBoard();
	}

	@FXML
	public void onClearButtonClick(ActionEvent actionEvent) {
		createBoard(false);
		drawBoard();
	}

	@FXML
	public void onActiveCellColorPickerChange(ActionEvent actionEvent) {
		boardDrawingConfig.setCellActiveColor(activeCellColorPicker.getValue());
		drawBoard();
		rebuildPatterns();
	}

	@FXML
	public void OnInActiveCellColorPicker(ActionEvent actionEvent) {
		boardDrawingConfig.setCellInactiveColor(inActiveCellColorPicker.getValue());
		drawBoard();
		rebuildPatterns();
	}

	@FXML
	public void onDrawGridCheckboxChange(ActionEvent actionEvent) {
		boardDrawingConfig.setCellSpace(drawGridCheckbox.isSelected() ? 1 : 0);
		drawBoard();
		rebuildPatterns();
	}

	@FXML
	public void onGridColorPicker(ActionEvent actionEvent) {
		boardDrawingConfig.setCellGridColor(gridColorPicker.getValue());
		drawBoard();
		rebuildPatterns();
	}

	@FXML
	public void onCanvasDragOver(DragEvent dragEvent) {
		Dragboard db = dragEvent.getDragboard();
		if (db.hasContent(patternDragFormat) && draggingPattern != null) {
			dragEvent.acceptTransferModes(TransferMode.MOVE);
		}
	}

	@FXML
	public void onCanvasDragDropped(DragEvent dragEvent) {
		Dragboard db = dragEvent.getDragboard();
		if (db.hasContent(patternDragFormat) && draggingPattern != null) {
			dragEvent.setDropCompleted(true);
			GridPosition position = GridPosition.fromXYCoordinate(boardDrawingConfig, dragEvent.getX(), dragEvent.getY());
			int row = position.getRow();
			int column = position.getColumn();
			IPattern pattern = draggingPattern.getPattern();
			GraphicsContext gc = canvas.getGraphicsContext2D();
			Color activeColor = boardDrawingConfig.getCellActiveColor();
			Color inactiveColor = boardDrawingConfig.getCellInactiveColor();
			for (int r = 0; r < pattern.rows() && (row + r) < boardDrawingConfig.getRows(); r++) {
				for (int c = 0; c < pattern.columns() && (column + c) < boardDrawingConfig.getColumns(); c++) {
					ICell cell = board.cell(row + r, column + c);
					cell.isAlive(pattern.cell(r, c).isAlive());
					drawCell(cell, gc);
				}
			}
		}
	}

	private void drawCell(ICell cell) {
		drawCell(cell, canvas.getGraphicsContext2D());
	}

	private void drawCell(ICell cell, GraphicsContext gc) {
		gc.setFill(cell.isAlive() ? boardDrawingConfig.getCellActiveColor() : boardDrawingConfig.getCellInactiveColor());
		int cellSize = boardDrawingConfig.getCellSize();
		int cellSpacing = cellSize + boardDrawingConfig.getCellSpace();
		gc.fillRect((cell.column() * cellSpacing) + 1, (cell.row() * cellSpacing) + 1, cellSize, cellSize);
	}

	@FXML
	public void onLoadPatternButtonClicked(ActionEvent actionEvent) {
		fileChooser.setInitialDirectory(initialDirectory);
		List<File> files = fileChooser.showOpenMultipleDialog(mainPane.getScene().getWindow());
		if (files != null) {
			PatternVBox lastPatternVbox = null;
			for (File file: files) {
				updateInitialDirectory(file.getParentFile());
				try {
					IPattern pattern = PatternRLELoader.load(file);
					PatternVBox vbox = pattern.generateDisplay(boardDrawingConfig);
					lastPatternVbox = vbox;
					patternsContainer.getChildren().add(vbox);
					makePatternVBoxInteractive(vbox);
				} catch (IOException | InvalidRLEFormatException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
					alert.showAndWait();
				}
			}
			if (lastPatternVbox != null) {
				accordion.setExpandedPane(accordionPatterns);
				patternsScrollPane.setHvalue(0);
				patternsScrollPane.setVvalue(patternsScrollPane.getVmax() + 1000);
				if (selectedPattern != null) {
					selectedPattern.clearSelectedBorder();
					selectedPattern = null;
				}
				selectedPattern = lastPatternVbox;
				lastPatternVbox.showSelectedBorder();
			}
		}
	}

	@FXML
	public void onEnterRlePatternButtonClicked(ActionEvent actionEvent) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("enterRleDialog.fxml"));
		try {
			Parent root = (Parent)loader.load();
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.initOwner(mainPane.getScene().getWindow());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);
			stage.setTitle("Enter RLE Pattern");
			EnterRleDialogController controller = (EnterRleDialogController)loader.getController();
			controller.setInitialDirectory(initialDirectory);
			controller.setStage(stage);
			stage.showAndWait();
			updateInitialDirectory(controller.getInitialDirectory());
			if (controller.isModalResultOK()) {
				IPattern pattern = PatternRLELoader.load(controller.getPatternName(), controller.getPatternRle());
				PatternVBox vbox = pattern.generateDisplay(boardDrawingConfig);
				patternsContainer.getChildren().add(vbox);
				makePatternVBoxInteractive(vbox);
			}
		} catch (IOException | InvalidRLEFormatException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
			alert.showAndWait();
		}
	}

	private void updateInitialDirectory(File newInitialDirectory) {
		if (newInitialDirectory != null) {
			initialDirectory = newInitialDirectory;
		}
	}

	@FXML
	public void onCanvasMousePressed(MouseEvent mouseEvent) {
		if (!running) {
			canvasMarkedRectangle = null;
			if (mouseEvent.isPrimaryButtonDown() && (mouseEvent.isControlDown() || mouseEvent.isMetaDown())) {
				canvasStartPosition = GridPosition.fromXYCoordinate(boardDrawingConfig, mouseEvent.getX(), mouseEvent.getY());
				canvasDrawingMode = CanvasDrawingMode.MARK_AREA;
			} else if (mouseEvent.isPrimaryButtonDown()) {
				canvasDrawingMode = CanvasDrawingMode.SET_ALIVE;
				onCanvasMouseReleased(mouseEvent);
			} else if (mouseEvent.isSecondaryButtonDown()) {
				canvasDrawingMode = CanvasDrawingMode.SET_DEAD;
				onCanvasMouseReleased(mouseEvent);
			} else {
				canvasDrawingMode = CanvasDrawingMode.NONE;
			}
		}
	}

	@FXML
	public void onCanvasMouseReleased(MouseEvent mouseEvent) {
		if (!running) {
			ICell cell;
			GridPosition position = GridPosition.fromXYCoordinate(boardDrawingConfig, mouseEvent.getX(), mouseEvent.getY());
			switch (canvasDrawingMode) {
				case SET_ALIVE:
					cell = board.cell(position.getRow(), position.getColumn());
					cell.isAlive(true);
					drawCell(cell);
					break;
				case SET_DEAD:
					cell = board.cell(position.getRow(), position.getColumn());
					cell.isAlive(false);
					drawCell(cell);
					break;
				case MARK_AREA:
					if (canvasMarkedRectangle != null) {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("enterRleDialog.fxml"));
						try {
							GridRectangle markedArea = canvasMarkedRectangle;
							clearCanvasMarking();
							markedArea.setCellsFromBoard(board);
							String rle = PatternRLEEncoder.encode(markedArea.getColumns(), markedArea.getCells());
							Parent root = (Parent)loader.load();
							Scene scene = new Scene(root);
							Stage stage = new Stage();
							stage.initOwner(mainPane.getScene().getWindow());
							stage.initModality(Modality.APPLICATION_MODAL);
							stage.setScene(scene);
							stage.setTitle("Enter RLE Pattern");
							EnterRleDialogController controller = (EnterRleDialogController)loader.getController();
							controller.setInitialDirectory(initialDirectory);
							controller.setStage(stage);
							controller.setRlePattern(rle);
							stage.showAndWait();
							updateInitialDirectory(controller.getInitialDirectory());
							if (controller.isModalResultOK()) {
								IPattern pattern = PatternRLELoader.load(controller.getPatternName(), controller.getPatternRle());
								PatternVBox vbox = pattern.generateDisplay(boardDrawingConfig);
								patternsContainer.getChildren().add(vbox);
								makePatternVBoxInteractive(vbox);
							}
						} catch (IOException | InvalidRLEFormatException e) {
							Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
							alert.showAndWait();
						}
					}
					break;
			}
		}
	}

	@FXML
	public void onCanvasDragged(MouseEvent mouseEvent) {
		if (!running) {
			ICell cell;
			GridPosition position = GridPosition.fromXYCoordinate(boardDrawingConfig, mouseEvent.getX(), mouseEvent.getY());
			switch (canvasDrawingMode) {
				case SET_ALIVE:
					cell = board.cell(position.getRow(), position.getColumn());
					cell.isAlive(true);
					drawCell(cell);
					break;
				case SET_DEAD:
					cell = board.cell(position.getRow(), position.getColumn());
					cell.isAlive(false);
					drawCell(cell);
					break;
				case MARK_AREA:
					if (!position.equals(canvasStartPosition)) {
						clearCanvasMarking();
						canvasMarkedRectangle = new GridRectangle(canvasStartPosition, position);
						drawCanvasMarking();
					} else if (canvasMarkedRectangle != null) {
						clearCanvasMarking();
					}
					break;
			}
		}
	}

	private void clearCanvasMarking() {
		if (canvasMarkedRectangle != null) {
			GraphicsContext gc = canvas.getGraphicsContext2D();
			for (int row = canvasMarkedRectangle.getStartRow(); row <= canvasMarkedRectangle.getEndRow(); row++) {
				for (int column = canvasMarkedRectangle.getStartColumn(); column <= canvasMarkedRectangle.getEndColumn(); column++) {
					drawCell(board.cell(row, column), gc);
				}
			}
			canvasMarkedRectangle = null;
		}
	}

	private void drawCanvasMarking() {
		Color restoreActiveColor = boardDrawingConfig.getCellActiveColor();
		Color restoreInactiveColor = boardDrawingConfig.getCellInactiveColor();
		boardDrawingConfig.setCellActiveColor(Color.RED);
		boardDrawingConfig.setCellInactiveColor(Color.YELLOW);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		for (int row = canvasMarkedRectangle.getStartRow(); row <= canvasMarkedRectangle.getEndRow(); row++) {
			for (int column = canvasMarkedRectangle.getStartColumn(); column <= canvasMarkedRectangle.getEndColumn(); column++) {
				drawCell(board.cell(row, column), gc);
			}
		}
		boardDrawingConfig.setCellActiveColor(restoreActiveColor);
		boardDrawingConfig.setCellInactiveColor(restoreInactiveColor);
	}
}
