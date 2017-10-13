package com.foo.gol.ui;

import com.foo.gol.logic.*;
import com.foo.gol.patterns.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
	private static Controller instance = null;
	public static Controller getInstance() {
		return instance;
	}

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

	private GridPosition currentCellPosition = new GridPosition(0, 0);
	private boolean currentCellBlinkOn = false;
	private Timeline focusCellBlinker = null;

	private volatile GraphicsContext canvasGraphicsContext;

	private AnimationSaver animationSaver;

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
	@FXML
	private ComboBox<String> ruleCombo;
	@FXML
	private ComboBox<String> wrappingCombo;
	@FXML
	private GridPane lifeControlsGrid;
	@FXML
	private GridPane boardDrawingControlsGrid;
	@FXML
	private GridPane loadSaveControlsGrid;
	@FXML
	private TextField alivesSurviveTextField;
	@FXML
	private TextField deadsBornTextField;
	@FXML
	private CheckBox saveAnimationCheckbox;
	@FXML
	private TextField saveAnimationToTextField;

	@FXML
	public void initialize() {
		// store the instance of the controller so that window shown can call .show() method...
		instance = this;

		mainPane.setDisable(true);
	}

	public void shown() {
		mainPane.setDisable(false);
		canvasGraphicsContext = canvas.getGraphicsContext2D();
		animationSaver = new AnimationSaver(mainPane.getScene().getWindow(), canvas, (int)animationSpeedSlider.getValue());
		animationSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			animationSaver.setFrameInterval((int)newValue);
		});
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

		alivesSurviveTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			onAlivesSurviveTextFieldChanged();
		});
		deadsBornTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			onDeadsBornTextFieldChanged();
		});

		focusCellBlinker = new Timeline(new KeyFrame(Duration.millis(500), e -> {
			focusCellBlink();
		}));
		focusCellBlinker.setCycleCount(-1);

		canvas.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				currentCellBlinkOn = true;
				drawBlinkCell();
				focusCellBlinker.play();
			} else {
				focusCellBlinker.stop();
				clearBlinkCell();
			}
		});
		updateControls();
		createBoard(false);
		drawBoardMessage();
		drawBoard();
		loadPatterns();
	}

	private void drawBoardMessage() {
		IPattern[] lines = new IPattern[] {
				AlphabetPatterns.stringToPattern("Conway's"),
				AlphabetPatterns.stringToPattern("Game"),
				AlphabetPatterns.stringToPattern("Of"),
				AlphabetPatterns.stringToPattern("Life")
		};
		int atRow = (boardDrawingConfig.getRows() - ((AlphabetPatterns.CHAR_HEIGHT + 1) * lines.length)) / 2;
		for (IPattern line: lines) {
			board.drawPattern(atRow, (boardDrawingConfig.getColumns() - line.columns()) / 2, line);
			atRow += (AlphabetPatterns.CHAR_HEIGHT + 1);
		}
	}

	private void createBoard(boolean randomize) {
		generationController = new FullScanGenerationController(new StandardLifeChangeAliveRule());
		board = new Board(boardDrawingConfig.getColumns(), boardDrawingConfig.getRows(), boardDrawingConfig.getWrappingMode(), generationController);
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
		if (currentCellPosition.getRow() >= newHeight || currentCellPosition.getColumn() >= newWidth) {
			currentCellPosition = new GridPosition(Math.min(currentCellPosition.getRow(), newHeight - 1), Math.min(currentCellPosition.getColumn(), newWidth - 1));
		}
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
		synchronized (canvasGraphicsContext) {
			int rows = boardDrawingConfig.getRows();
			int columns = boardDrawingConfig.getColumns();
			int cellSize = boardDrawingConfig.getCellSize();
			int cellSpace = boardDrawingConfig.getCellSpace();
			int cellSpacing = cellSize + cellSpace;
			int canvasWidth = 2 + (columns * cellSize) + ((columns - 1) * cellSpace);
			int canvasHeight = 2 + (rows * cellSize) + ((rows - 1) * cellSpace);
			canvas.setWidth(canvasWidth);
			canvas.setHeight(canvasHeight);
			canvasGraphicsContext.setFill(boardDrawingConfig.getCellInactiveColor());
			canvasGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
			canvasGraphicsContext.setStroke(Color.BLACK);
			canvasGraphicsContext.setLineWidth(1);
			canvasGraphicsContext.strokeRect(0, 0, canvasWidth, canvasHeight);
			if (cellSpace > 0) {
				canvasGraphicsContext.setStroke(boardDrawingConfig.getCellGridColor());
				for (int r = 1; r < rows; r++) {
					canvasGraphicsContext.strokeLine(1.5d, (r * cellSpacing) + 0.5d, canvasWidth - 1.5d, (r * cellSpacing) + 0.5d);
				}
				for (int c = 1; c < columns; c++) {
					canvasGraphicsContext.strokeLine((c * cellSpacing) + 0.5d, 1.5d, (c * cellSpacing) + 0.5d, canvasHeight - 1.5d);
				}
			}
			canvasGraphicsContext.setFill(boardDrawingConfig.getCellActiveColor());
			for (int row = 0; row < rows; row++) {
				for (int column = 0; column < columns; column++) {
					if (board.cellAlive(row, column)) {
						canvasGraphicsContext.fillRect((column * cellSpacing) + 1, (row * cellSpacing) + 1, cellSize, cellSize);
					}
				}
			}
		}
	}

	private void loadPatterns() {
		selectedPattern = null;
		draggingPattern = null;
		patternsContainer.getChildren().clear();
		for (IPattern pattern: patternLibrary.getPatterns()) {
			PatternVBox vbox = pattern.generateDisplay(boardDrawingConfig);
			patternsContainer.getChildren().add(vbox);
			makePatternVBoxInteractive(vbox);
		}
	}

	private void rebuildPatterns() {
		List<IPattern> patterns = new ArrayList<>();
		for (Node node: patternsContainer.getChildren()) {
			if (node instanceof PatternVBox) {
				patterns.add(((PatternVBox)node).getPattern());
			}
		}
		selectedPattern = null;
		draggingPattern = null;
		patternsContainer.getChildren().clear();
		for (IPattern pattern: patterns) {
			PatternVBox vbox = pattern.generateDisplay(boardDrawingConfig);
			patternsContainer.getChildren().add(vbox);
			makePatternVBoxInteractive(vbox);
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
			if (!running && event.getClickCount() == 2) {
				int row = currentCellPosition.getRow();
				int column = currentCellPosition.getColumn();
				for (int r = 0; r < pattern.rows() && (row + r) < boardDrawingConfig.getRows(); r++) {
					for (int c = 0; c < pattern.columns() && (column + c) < boardDrawingConfig.getColumns(); c++) {
						ICell cell = board.cell(row + r, column + c);
						cell.isAlive(pattern.cell(r, c).isAlive());
						drawCell(cell);
					}
				}
				animationSaver.step();
				canvas.requestFocus();
			}
		});
	}

	private void run() {
		running = true;
		updateControls();
		clearBlinkCell();
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
		Color activeColor = boardDrawingConfig.getCellActiveColor();
		Color inactiveColor = boardDrawingConfig.getCellInactiveColor();
		int cellSize = boardDrawingConfig.getCellSize();
		int cellSpacing = cellSize + boardDrawingConfig.getCellSpace();
		for (ICell cell: changes) {
			canvasGraphicsContext.setFill(cell.isAlive() ? activeColor : inactiveColor);
			canvasGraphicsContext.fillRect((cell.column() * cellSpacing) + 1, (cell.row() * cellSpacing) + 1, cellSize, cellSize);
		}
		animationSaver.step();
		if (changes.size() == 0 && running) {
			stop();
		}
	}

	private void updateControls() {
		playButton.setDisable(running);
		stepButton.setDisable(running);
		stopButton.setDisable(!running);
		randomizeButton.setDisable(running);
		clearButton.setDisable(running);
		lifeControlsGrid.setDisable(running);
		boardDrawingControlsGrid.setDisable(running);
		loadSaveControlsGrid.setDisable(running);
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
		animationSaver.step();
	}

	@FXML
	public void onClearButtonClick(ActionEvent actionEvent) {
		createBoard(false);
		drawBoard();
		animationSaver.step();
	}

	@FXML
	public void onActiveCellColorPickerChange(ActionEvent actionEvent) {
		boardDrawingConfig.setCellActiveColor(activeCellColorPicker.getValue());
		drawBoard();
		animationSaver.step();
		rebuildPatterns();
	}

	@FXML
	public void OnInActiveCellColorPicker(ActionEvent actionEvent) {
		boardDrawingConfig.setCellInactiveColor(inActiveCellColorPicker.getValue());
		drawBoard();
		animationSaver.step();
		rebuildPatterns();
	}

	@FXML
	public void onDrawGridCheckboxChange(ActionEvent actionEvent) {
		boardDrawingConfig.setCellSpace(drawGridCheckbox.isSelected() ? 1 : 0);
		drawBoard();
		animationSaver.step();
		rebuildPatterns();
	}

	@FXML
	public void onGridColorPicker(ActionEvent actionEvent) {
		boardDrawingConfig.setCellGridColor(gridColorPicker.getValue());
		drawBoard();
		animationSaver.step();
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
			for (int r = 0; r < pattern.rows() && (row + r) < boardDrawingConfig.getRows(); r++) {
				for (int c = 0; c < pattern.columns() && (column + c) < boardDrawingConfig.getColumns(); c++) {
					ICell cell = board.cell(row + r, column + c);
					cell.isAlive(pattern.cell(r, c).isAlive());
					drawCell(cell);
				}
			}
			animationSaver.step();
		}
	}

	private void drawCell(ICell cell) {
		synchronized (canvasGraphicsContext) {
			canvasGraphicsContext.setFill(cell.isAlive() ? boardDrawingConfig.getCellActiveColor() : boardDrawingConfig.getCellInactiveColor());
			int cellSize = boardDrawingConfig.getCellSize();
			int cellSpacing = cellSize + boardDrawingConfig.getCellSpace();
			canvasGraphicsContext.fillRect((cell.column() * cellSpacing) + 1, (cell.row() * cellSpacing) + 1, cellSize, cellSize);
		}
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
		mouseEvent.consume();
		if (!canvas.isFocused()) {
			canvas.requestFocus();
		}
		changeCurrentCellPosition(GridPosition.fromXYCoordinate(boardDrawingConfig, mouseEvent.getX(), mouseEvent.getY()));
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
			for (int row = canvasMarkedRectangle.getStartRow(); row <= canvasMarkedRectangle.getEndRow(); row++) {
				for (int column = canvasMarkedRectangle.getStartColumn(); column <= canvasMarkedRectangle.getEndColumn(); column++) {
					drawCell(board.cell(row, column));
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
		for (int row = canvasMarkedRectangle.getStartRow(); row <= canvasMarkedRectangle.getEndRow(); row++) {
			for (int column = canvasMarkedRectangle.getStartColumn(); column <= canvasMarkedRectangle.getEndColumn(); column++) {
				drawCell(board.cell(row, column));
			}
		}
		boardDrawingConfig.setCellActiveColor(restoreActiveColor);
		boardDrawingConfig.setCellInactiveColor(restoreInactiveColor);
	}

	public void onRuleComboChanged(ActionEvent actionEvent) {
		if (!running) {
			switch (ruleCombo.getValue()) {
				case "HiLife":
					generationController.setChangeAliveRule(new HiLifeChangeAliveRule());
					alivesSurviveTextField.setDisable(true);
					deadsBornTextField.setDisable(true);
					alivesSurviveTextField.textProperty().setValue(generationController.getChangeAliveRule().getAlivesSurviveString());
					deadsBornTextField.textProperty().setValue(generationController.getChangeAliveRule().getDeadsBornString());
					break;
				case "Custom":
					alivesSurviveTextField.setDisable(false);
					deadsBornTextField.setDisable(false);
					generationController.setChangeAliveRule(new CustomChangeAliveRule(alivesSurviveTextField.textProperty().getValue(), deadsBornTextField.textProperty().getValue()));
					break;
				default:
					// Standard
					generationController.setChangeAliveRule(new StandardLifeChangeAliveRule());
					alivesSurviveTextField.setDisable(true);
					deadsBornTextField.setDisable(true);
					alivesSurviveTextField.textProperty().setValue(generationController.getChangeAliveRule().getAlivesSurviveString());
					deadsBornTextField.textProperty().setValue(generationController.getChangeAliveRule().getDeadsBornString());
					break;
			}
		}
	}

	public void onAlivesSurviveTextFieldChanged() {
		if (!running && "Custom".equals(ruleCombo.getValue()) && !alivesSurviveTextField.isDisabled()) {
			generationController.setChangeAliveRule(new CustomChangeAliveRule(alivesSurviveTextField.textProperty().getValue(), deadsBornTextField.textProperty().getValue()));
		}
	}

	public void onDeadsBornTextFieldChanged() {
		if (!running && "Custom".equals(ruleCombo.getValue()) && !deadsBornTextField.isDisabled()) {
			generationController.setChangeAliveRule(new CustomChangeAliveRule(alivesSurviveTextField.textProperty().getValue(), deadsBornTextField.textProperty().getValue()));
		}
	}

	public void onRuleTextFieldKeyTyped(KeyEvent keyEvent) {
		String typedChar = keyEvent.getCharacter();
		if (!"0123456789 ,".contains(typedChar)) {
			keyEvent.consume();
		}
	}

	public void onCanvasKeyPressed(KeyEvent keyEvent) {
		if (!running) {
			ICell cell;
			switch (keyEvent.getCode()) {
				case UP:
					if (keyEvent.isControlDown()) {
						cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
						cell.isAlive(!keyEvent.isAltDown() || !cell.isAlive());
						drawCell(cell);
						animationSaver.step();
					}
					changeCurrentCellPosition(new GridPosition(currentCellPosition.getRow() == 0 ? boardDrawingConfig.getRows() - 1 : currentCellPosition.getRow() - 1, currentCellPosition.getColumn()));
					break;
				case DOWN:
					if (keyEvent.isControlDown()) {
						cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
						cell.isAlive(!keyEvent.isAltDown() || !cell.isAlive());
						drawCell(cell);
						animationSaver.step();
					}
					changeCurrentCellPosition(new GridPosition(currentCellPosition.getRow() == (boardDrawingConfig.getRows() - 1) ? 0 : currentCellPosition.getRow() + 1, currentCellPosition.getColumn()));
					break;
				case LEFT:
					if (keyEvent.isControlDown()) {
						cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
						cell.isAlive(!keyEvent.isAltDown() || !cell.isAlive());
						drawCell(cell);
						animationSaver.step();
					}
					changeCurrentCellPosition(new GridPosition(
							currentCellPosition.getColumn() == 0 ? (currentCellPosition.getRow() == 0 ? boardDrawingConfig.getRows() - 1 : currentCellPosition.getRow() - 1) : currentCellPosition.getRow(),
							currentCellPosition.getColumn() == 0 ? boardDrawingConfig.getColumns() - 1 : currentCellPosition.getColumn() - 1));
					break;
				case RIGHT:
					if (keyEvent.isControlDown()) {
						cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
						cell.isAlive(!keyEvent.isAltDown() || !cell.isAlive());
						drawCell(cell);
						animationSaver.step();
					}
					changeCurrentCellPosition(new GridPosition(
							currentCellPosition.getColumn() == boardDrawingConfig.getColumns() - 1 ? (currentCellPosition.getRow() == boardDrawingConfig.getRows() - 1 ? 0 : currentCellPosition.getRow() + 1) : currentCellPosition.getRow(),
							currentCellPosition.getColumn() == (boardDrawingConfig.getColumns() - 1) ? 0 : currentCellPosition.getColumn() + 1));
					break;
				case HOME:
					if (keyEvent.isControlDown()) {
						changeCurrentCellPosition(new GridPosition(0,0));
					} else {
						changeCurrentCellPosition(new GridPosition(currentCellPosition.getRow(), 0));
					}
					break;
				case END:
					if (keyEvent.isControlDown()) {
						changeCurrentCellPosition(new GridPosition(boardDrawingConfig.getRows() - 1,boardDrawingConfig.getColumns() - 1));
					} else {
						changeCurrentCellPosition(new GridPosition(currentCellPosition.getRow(), boardDrawingConfig.getColumns() - 1));
					}
					break;
				case PAGE_UP:
					changeCurrentCellPosition(new GridPosition(0, currentCellPosition.getColumn()));
					break;
				case PAGE_DOWN:
					changeCurrentCellPosition(new GridPosition(boardDrawingConfig.getRows() - 1, currentCellPosition.getColumn()));
					break;
				case SPACE:
					cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
					cell.isAlive(!keyEvent.isControlDown());
					drawCell(cell);
					animationSaver.step();
					changeCurrentCellPosition(new GridPosition(
							currentCellPosition.getColumn() == boardDrawingConfig.getColumns() - 1 ? (currentCellPosition.getRow() == boardDrawingConfig.getRows() - 1 ? 0 : currentCellPosition.getRow() + 1) : currentCellPosition.getRow(),
							currentCellPosition.getColumn() == (boardDrawingConfig.getColumns() - 1) ? 0 : currentCellPosition.getColumn() + 1));
					break;
				case DELETE:
					cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
					cell.isAlive(false);
					drawCell(cell);
					animationSaver.step();
					break;
				case BACK_SPACE:
					cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
					cell.isAlive(keyEvent.isControlDown());
					drawCell(cell);
					animationSaver.step();
					changeCurrentCellPosition(new GridPosition(
							currentCellPosition.getColumn() == 0 ? (currentCellPosition.getRow() == 0 ? boardDrawingConfig.getRows() - 1 : currentCellPosition.getRow() - 1) : currentCellPosition.getRow(),
							currentCellPosition.getColumn() == 0 ? boardDrawingConfig.getColumns() - 1 : currentCellPosition.getColumn() - 1));
					break;
			}
		}
	}

	public void onCanvasKeyTyped(KeyEvent keyEvent) {
		if (!running) {
			String typedChar = keyEvent.getCharacter();
			int codePoint = typedChar.length() > 0 ? typedChar.codePointAt(0) : 0;
			if (codePoint > 32 && codePoint < 127) {
				IPattern characterPattern = AlphabetPatterns.patternForChar(typedChar.charAt(0));
				if (characterPattern != null) {
					synchronized (characterPattern) {
						int top = Math.max(0, currentCellPosition.getRow() - (characterPattern.rows() - 2));
						board.drawPattern(top, currentCellPosition.getColumn(), characterPattern);
						redrawBoardArea(top, currentCellPosition.getColumn(), characterPattern.rows(), characterPattern.columns());
						changeCurrentCellPosition(new GridPosition(top + (characterPattern.rows() - 2), Math.min(boardDrawingConfig.getColumns() - 1, currentCellPosition.getColumn() + characterPattern.columns() + 1)));
						animationSaver.step();
					}
				}
			}
		}
	}

	private void redrawBoardArea(int top, int left, int height, int width) {
		synchronized (canvasGraphicsContext) {
			for (int row = 0; row < height; row++) {
				if ((row + top) < boardDrawingConfig.getRows()) {
					for (int column = 0; column < width; column++) {
						if ((column + left) < boardDrawingConfig.getColumns()) {
							drawCell(board.cell(row + top, column + left));
						}
					}
				}
			}
		}
	}

	private void focusCellBlink() {
		if (!running) {
			synchronized (canvasGraphicsContext) {
				currentCellBlinkOn = !currentCellBlinkOn;
				ICell cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
				if (currentCellBlinkOn) {
					canvasGraphicsContext.setFill((cell.isAlive() ? boardDrawingConfig.getCellActiveColor() : boardDrawingConfig.getCellInactiveColor()).invert());
					int cellSize = boardDrawingConfig.getCellSize();
					int cellSpacing = cellSize + boardDrawingConfig.getCellSpace();
					canvasGraphicsContext.fillRect((cell.column() * cellSpacing) + 1, (cell.row() * cellSpacing) + 1, cellSize, cellSize);
				} else {
					drawCell(cell);
				}
			}
		}
	}

	private void changeCurrentCellPosition(GridPosition newPosition) {
		if (!newPosition.equals(currentCellPosition)) {
			synchronized (canvasGraphicsContext) {
				clearBlinkCell();
				currentCellPosition = newPosition;
				currentCellBlinkOn = true;
				if (!running) {
					drawBlinkCell();
				}
			}
		}
	}

	private void clearBlinkCell() {
		ICell cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
		drawCell(cell);
	}

	private void drawBlinkCell() {
		ICell cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
		canvasGraphicsContext.setFill((cell.isAlive() ? boardDrawingConfig.getCellActiveColor() : boardDrawingConfig.getCellInactiveColor()).invert());
		int cellSize = boardDrawingConfig.getCellSize();
		int cellSpacing = cellSize + boardDrawingConfig.getCellSpace();
		canvasGraphicsContext.fillRect((cell.column() * cellSpacing) + 1, (cell.row() * cellSpacing) + 1, cellSize, cellSize);
	}

	public void onWrappingChanged(ActionEvent actionEvent) {
		if (!running) {
			BoardWrappingMode wrappingMode = BoardWrappingMode.fromString(wrappingCombo.getValue());
			if (wrappingMode != null && boardDrawingConfig.getWrappingMode() != wrappingMode) {
				boardDrawingConfig.setWrappingMode(wrappingMode);
				resizeBoard();
			}
		}
	}

	public void onSaveAnimationCheckboxChanged(ActionEvent actionEvent) {
		if (saveAnimationCheckbox.isSelected()) {
			animationSaver.setFrameInterval((int) animationSpeedSlider.getValue());
			animationSaver.start();
			if (!animationSaver.isSaving()) {
				saveAnimationCheckbox.setSelected(false);
			} else {
				saveAnimationToTextField.textProperty().setValue(animationSaver.getAnimationOutputFile().getAbsolutePath());
			}
		} else {
			animationSaver.end();
		}
		saveAnimationToTextField.setVisible(animationSaver.isSaving());
	}
}
