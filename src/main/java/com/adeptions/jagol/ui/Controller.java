package com.adeptions.jagol.ui;

import com.adeptions.jagol.config.GameConfig;
import com.adeptions.jagol.logic.*;
import com.adeptions.jagol.logic.rule.*;
import com.adeptions.jagol.patterns.*;
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
import java.util.List;

public class Controller {
	private static Controller instance = null;
	public static Controller getInstance() {
		return instance;
	}

	private final DataFormat patternDragFormat = new DataFormat("com.adeptions.jagol.ui.pattern");

	private GameConfig gameConfig;
	private IBoard board;
	private IGenerationController generationController = new FullScanGenerationController(new StandardConways());
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

	private ContextMenu patternContextMenu;

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
	private CheckBox deadCellEdgesCheckbox;
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
		gameConfig = GameConfig.load();
		populateRuleCombo();
		syncControlsToSettings();

		buildPatternContextMenu();

		canvasGraphicsContext = canvas.getGraphicsContext2D();
		animationSaver = new AnimationSaver(mainPane.getScene().getWindow(), canvas, (int)gameConfig.getGenerationDelay());
		animationSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			gameConfig.setGenerationDelay(newValue.doubleValue());
			animationSaver.setFrameInterval(newValue.intValue());
		});
		randomDensitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			gameConfig.setRandomizationDensity(newValue.doubleValue());
		});
		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game of Life pattern (*.rle)", "*.rle"));
		fileChooser.setTitle("Load Pattern");
		fileChooser.setInitialDirectory(initialDirectory);
		patternLibrary = new PatternLibrary();
		boardWidthSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
			resizeBoard();
		});
		boardHeightSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
			resizeBoard();
		});
		cellSizeSpinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
			gameConfig.setCellSize((Integer)newValue);
			drawBoard();
			rebuildPatterns();
		});
		activeCellColorPicker.setValue(gameConfig.getCellActiveColor());
		inActiveCellColorPicker.setValue(gameConfig.getCellInactiveColor());
		drawGridCheckbox.setSelected(gameConfig.getCellSpace() > 0);
		gridColorPicker.setValue(gameConfig.getCellGridColor());

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
		mainPane.setDisable(false);
	}

	private void syncControlsToSettings() {
		animationSpeedSlider.valueProperty().setValue(gameConfig.getGenerationDelay());
		randomDensitySlider.valueProperty().setValue(gameConfig.getRandomizationDensity());
		boardWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 500, gameConfig.getColumns()));
		boardHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 500, gameConfig.getRows()));
		cellSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, gameConfig.getCellSize()));
		wrappingCombo.setValue(gameConfig.getWrappingMode().getLabel());
		deadCellEdgesCheckbox.setSelected(gameConfig.isDeadCellEdges());
		deadCellEdgesCheckbox.setDisable(gameConfig.getWrappingMode() == BoardWrappingMode.BOTH);
		ruleCombo.setValue(gameConfig.getChangeAliveRule().getType());
		alivesSurviveTextField.setDisable(!gameConfig.getChangeAliveRule().isCustom());
		deadsBornTextField.setDisable(!gameConfig.getChangeAliveRule().isCustom());
		activeCellColorPicker.setValue(gameConfig.getCellActiveColor());
		inActiveCellColorPicker.setValue(gameConfig.getCellInactiveColor());
		drawGridCheckbox.setSelected(gameConfig.getCellSpace() != 0);
		gridColorPicker.setValue(gameConfig.getCellGridColor());
	}

	private void buildPatternContextMenu() {
		patternContextMenu = new ContextMenu();
		MenuItem editMenuItem = new MenuItem("Edit...");
		editMenuItem.setDisable(true); // not yet supported
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem flipHorizontalMenuItem = new MenuItem("Flip horizontal");
		flipHorizontalMenuItem.setOnAction(event -> {
			if (selectedPattern != null) {
				selectedPattern.getPattern().flipHorizontally();
				selectedPattern.redrawPattern(gameConfig);
			}
		});
		MenuItem flipVerticalMenuItem = new MenuItem("Flip vertical");
		flipVerticalMenuItem.setOnAction(event -> {
			if (selectedPattern != null) {
				selectedPattern.getPattern().flipVertically();
				selectedPattern.redrawPattern(gameConfig);
			}
		});
		MenuItem rotate90ClockwiseMenuItem = new MenuItem("Rotate 90\u00B0 clockwise");
		rotate90ClockwiseMenuItem.setOnAction(event -> {
			if (selectedPattern != null) {
				selectedPattern.getPattern().rotate90Clockwise();
				selectedPattern.redrawPattern(gameConfig);
			}
		});
		MenuItem rotate90AntiClockwiseMenuItem = new MenuItem("Rotate 90\u00B0 anti clockwise");
		rotate90AntiClockwiseMenuItem.setOnAction(event -> {
			if (selectedPattern != null) {
				selectedPattern.getPattern().rotate90AntiClockwise();
				selectedPattern.redrawPattern(gameConfig);
			}
		});
		patternContextMenu.getItems().addAll(editMenuItem, separatorMenuItem,
				flipHorizontalMenuItem, flipVerticalMenuItem,
				rotate90ClockwiseMenuItem, rotate90AntiClockwiseMenuItem);
	}

	private void populateRuleCombo() {
		ruleCombo.getItems().clear();
		String firstLabel = null;
		for (String label: ChangeAliveRuleFactory.getLabelsList()) {
			firstLabel = firstLabel == null ? label : firstLabel;
			ruleCombo.getItems().add(label);
		}
		ruleCombo.getItems().add(ChangeAliveRuleFactory.CUSTOM_RULE_LABEL);
		ruleCombo.setValue(firstLabel);
		IChangeAliveRule selectedRule = ChangeAliveRuleFactory.createFromLabel(firstLabel);
		alivesSurviveTextField.textProperty().setValue(selectedRule.getAlivesSurviveString());
		deadsBornTextField.textProperty().setValue(selectedRule.getDeadsBornString());
	}

	private void drawBoardMessage() {
		IPattern[] lines = new IPattern[] {
				AlphabetPatterns.stringToPattern("<<< JaGol >>>"),
				AlphabetPatterns.stringToPattern("Conway's"),
				AlphabetPatterns.stringToPattern("Game"),
				AlphabetPatterns.stringToPattern("Of"),
				AlphabetPatterns.stringToPattern("Life")
		};
		int atRow = (gameConfig.getRows() - ((AlphabetPatterns.CHAR_HEIGHT + 1) * lines.length)) / 2;
		for (IPattern line: lines) {
			board.drawPattern(atRow, (gameConfig.getColumns() - line.columns()) / 2, line);
			atRow += (AlphabetPatterns.CHAR_HEIGHT + 1);
		}
	}

	private void createBoard(boolean randomize) {
		board = new Board(gameConfig.getColumns(), gameConfig.getRows(), gameConfig.getWrappingMode(), gameConfig.isDeadCellEdges(), generationController);
		if (randomize) {
			double randomDensity = gameConfig.getRandomizationDensity() / 100d;
			for (int row = 0; row < gameConfig.getRows(); row++) {
				for (int column = 0; column < gameConfig.getColumns(); column++) {
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
		int oldWidth = gameConfig.getColumns();
		int oldHeight = gameConfig.getRows();
		gameConfig.setRows(newHeight);
		gameConfig.setColumns(newWidth);
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
			int rows = gameConfig.getRows();
			int columns = gameConfig.getColumns();
			int cellSize = gameConfig.getCellSize();
			int cellSpace = gameConfig.getCellSpace();
			int cellSpacing = cellSize + cellSpace;
			int canvasWidth = 2 + (columns * cellSize) + ((columns - 1) * cellSpace);
			int canvasHeight = 2 + (rows * cellSize) + ((rows - 1) * cellSpace);
			canvas.setWidth(canvasWidth);
			canvas.setHeight(canvasHeight);
			canvasGraphicsContext.setFill(gameConfig.getCellInactiveColor());
			canvasGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
			canvasGraphicsContext.setStroke(gameConfig.getBoardBorderColor());
			canvasGraphicsContext.setLineWidth(1);
			canvasGraphicsContext.strokeRect(0, 0, canvasWidth, canvasHeight);
			if (cellSpace > 0) {
				canvasGraphicsContext.setStroke(gameConfig.getCellGridColor());
				for (int r = 1; r < rows; r++) {
					canvasGraphicsContext.strokeLine(1.5d, (r * cellSpacing) + 0.5d, canvasWidth - 1.5d, (r * cellSpacing) + 0.5d);
				}
				for (int c = 1; c < columns; c++) {
					canvasGraphicsContext.strokeLine((c * cellSpacing) + 0.5d, 1.5d, (c * cellSpacing) + 0.5d, canvasHeight - 1.5d);
				}
			}
			canvasGraphicsContext.setFill(gameConfig.getCellActiveColor());
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
			PatternVBox vbox = pattern.generateDisplay(gameConfig);
			patternsContainer.getChildren().add(vbox);
			makePatternVBoxInteractive(vbox);
		}
	}

	private void rebuildPatterns() {
		for (Node node: patternsContainer.getChildren()) {
			if (node instanceof PatternVBox) {
				((PatternVBox)node).redrawPattern(gameConfig);
			}
		}
	}

	private void makePatternVBoxInteractive(final PatternVBox patternVBox) {
		IPattern pattern = patternVBox.getPattern();
		patternVBox.setFocusTraversable(true);
		patternVBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
			setPatternVBoxSelected(patternVBox);
		});
		patternVBox.setOnDragDetected(event -> {
			setPatternVBoxSelected(patternVBox);
			if (!running) {
				Dragboard dragboard = patternVBox.startDragAndDrop(TransferMode.MOVE);
				patternVBox.prepareForDrag();
				dragboard.setDragView(patternVBox.snapshot(null, null));
				ClipboardContent clipboardContent = new ClipboardContent();
				clipboardContent.put(patternDragFormat, pattern.getName());
				dragboard.setContent(clipboardContent);
				draggingPattern = patternVBox;
				selectedPattern.showSelectedBorder();
			}
		});
		patternVBox.setOnDragDone(event -> {
			draggingPattern = null;
		});
		patternVBox.setOnMouseClicked(event -> {
			setPatternVBoxSelected(patternVBox);
			patternVBox.requestFocus();
			ensurePatternVisible(patternVBox);
			if (!running && event.getClickCount() == 2) {
				board.drawPattern(currentCellPosition.getRow(), currentCellPosition.getColumn(), pattern);
				redrawBoardArea(currentCellPosition.getRow(), currentCellPosition.getColumn(), pattern.rows(), pattern.columns());
				animationSaver.step();
				canvas.requestFocus();
			}
		});
		patternVBox.setOnMousePressed(event -> {
			patternVBox.requestFocus();
			if (event.isSecondaryButtonDown()) {
				setPatternVBoxSelected(patternVBox);
				patternContextMenu.show(patternVBox, event.getScreenX(), event.getScreenY());
			}
		});
		patternVBox.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case UP:
					setPatternVBoxSelectedUpDown(patternVBox, -1);
					event.consume();
					ensurePatternVisible(selectedPattern);
					break;
				case DOWN:
					setPatternVBoxSelectedUpDown(patternVBox, 1);
					event.consume();
					ensurePatternVisible(selectedPattern);
					break;
			}
		});
	}

	private void setPatternVBoxSelected(PatternVBox newSelectedPattern) {
		if (selectedPattern != null && selectedPattern != newSelectedPattern) {
			selectedPattern.clearSelectedBorder();
			selectedPattern = null;
		}
		if (newSelectedPattern != null) {
			selectedPattern = newSelectedPattern;
			selectedPattern.showSelectedBorder();
		}
	}

	private void setPatternVBoxSelectedUpDown(PatternVBox patternVBox, int direction) {
		List<Node> items = patternsContainer.getChildren();
		int index = items.indexOf(patternVBox);
		if (index >= 0) {
			index += direction;
			if (index >= 0 && index < items.size()) {
				items.get(index).requestFocus();
			}
		}
	}

	private void ensurePatternVisible(PatternVBox patternVBox) {
		// TODO - this isn't really working correctly
		/*
		Bounds patternBoxBounds = patternVBox.getBoundsInParent();
		double visibleTop = patternsScrollPane.getVvalue() * patternsContainer.getHeight();
		double visibleBottom = visibleTop + patternsScrollPane.getHeight();
		if (patternBoxBounds.getMinY() < visibleTop) {
			patternsScrollPane.setVvalue(patternBoxBounds.getMinY() / patternsContainer.getHeight());
		} else if (patternBoxBounds.getMaxY() > visibleBottom) {
			patternsScrollPane.setVvalue(patternBoxBounds.getMaxY() / patternsContainer.getHeight());
		}
		*/
	}

	private void run() {
		running = true;
		updateControls();
		clearBlinkCell();
		animationLoop = new Timeline(new KeyFrame(Duration.millis(gameConfig.getGenerationDelay()), e -> {
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
		Color activeColor = gameConfig.getCellActiveColor();
		Color inactiveColor = gameConfig.getCellInactiveColor();
		int cellSize = gameConfig.getCellSize();
		int cellSpacing = cellSize + gameConfig.getCellSpace();
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
		gameConfig.setCellActiveColor(activeCellColorPicker.getValue());
		drawBoard();
		animationSaver.step();
		rebuildPatterns();
	}

	@FXML
	public void OnInActiveCellColorPicker(ActionEvent actionEvent) {
		gameConfig.setCellInactiveColor(inActiveCellColorPicker.getValue());
		drawBoard();
		animationSaver.step();
		rebuildPatterns();
	}

	@FXML
	public void onDrawGridCheckboxChange(ActionEvent actionEvent) {
		gameConfig.setCellSpace(drawGridCheckbox.isSelected() ? 1 : 0);
		drawBoard();
		animationSaver.step();
		rebuildPatterns();
	}

	@FXML
	public void onGridColorPicker(ActionEvent actionEvent) {
		gameConfig.setCellGridColor(gridColorPicker.getValue());
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
			GridPosition position = GridPosition.fromXYCoordinate(gameConfig, dragEvent.getX(), dragEvent.getY());
			IPattern pattern = draggingPattern.getPattern();
			board.drawPattern(position.getRow(), position.getColumn(), pattern);
			redrawBoardArea(position.getRow(), position.getColumn(), pattern.rows(), pattern.columns());
			animationSaver.step();
			changeCurrentCellPosition(position);
			canvas.requestFocus();
		}
	}

	private void drawCell(ICell cell) {
		synchronized (canvasGraphicsContext) {
			canvasGraphicsContext.setFill(cell.isAlive() ? gameConfig.getCellActiveColor() : gameConfig.getCellInactiveColor());
			int cellSize = gameConfig.getCellSize();
			int cellSpacing = cellSize + gameConfig.getCellSpace();
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
					PatternVBox vbox = pattern.generateDisplay(gameConfig);
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
				patternsScrollPane.setVvalue(1.0d);
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
				PatternVBox vbox = pattern.generateDisplay(gameConfig);
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
		changeCurrentCellPosition(GridPosition.fromXYCoordinate(gameConfig, mouseEvent.getX(), mouseEvent.getY()));
		if (!canvas.isFocused()) {
			canvas.requestFocus();
			// if we didn't have focus and we're not about to mark an area - get out
			// this prevents first click on cell without focus from setting cell alive
			if (!(mouseEvent.isPrimaryButtonDown() && (mouseEvent.isControlDown() || mouseEvent.isMetaDown()))) {
				canvasDrawingMode = CanvasDrawingMode.NONE;
				return;
			}
		}
		if (!running) {
			canvasMarkedRectangle = null;
			if (mouseEvent.isPrimaryButtonDown() && (mouseEvent.isControlDown() || mouseEvent.isMetaDown())) {
				canvasStartPosition = GridPosition.fromXYCoordinate(gameConfig, mouseEvent.getX(), mouseEvent.getY());
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
			GridPosition position = GridPosition.fromXYCoordinate(gameConfig, mouseEvent.getX(), mouseEvent.getY());
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
								PatternVBox vbox = pattern.generateDisplay(gameConfig);
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
			GridPosition position = GridPosition.fromXYCoordinate(gameConfig, mouseEvent.getX(), mouseEvent.getY());
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
		Color restoreActiveColor = gameConfig.getCellActiveColor();
		Color restoreInactiveColor = gameConfig.getCellInactiveColor();
		gameConfig.setCellActiveColor(Color.RED);
		gameConfig.setCellInactiveColor(Color.YELLOW);
		for (int row = canvasMarkedRectangle.getStartRow(); row <= canvasMarkedRectangle.getEndRow(); row++) {
			for (int column = canvasMarkedRectangle.getStartColumn(); column <= canvasMarkedRectangle.getEndColumn(); column++) {
				drawCell(board.cell(row, column));
			}
		}
		gameConfig.setCellActiveColor(restoreActiveColor);
		gameConfig.setCellInactiveColor(restoreInactiveColor);
	}

	public void onRuleComboChanged(ActionEvent actionEvent) {
		if (!running) {
			String selectedValue = ruleCombo.getValue();
			IChangeAliveRule newRule = ChangeAliveRuleFactory.createFromLabel(selectedValue);
			if (newRule == null) {
				newRule = new Custom(alivesSurviveTextField.textProperty().getValue(), deadsBornTextField.textProperty().getValue());
			}
			gameConfig.setChangeAliveRule(newRule);
			generationController.setChangeAliveRule(newRule);
			alivesSurviveTextField.setDisable(!newRule.isCustom());
			deadsBornTextField.setDisable(!newRule.isCustom());
			alivesSurviveTextField.textProperty().setValue(generationController.getChangeAliveRule().getAlivesSurviveString());
			deadsBornTextField.textProperty().setValue(generationController.getChangeAliveRule().getDeadsBornString());
		}
	}

	public void onAlivesSurviveTextFieldChanged() {
		if (!running && "Custom".equals(ruleCombo.getValue()) && !alivesSurviveTextField.isDisabled()) {
			generationController.setChangeAliveRule(new Custom(alivesSurviveTextField.textProperty().getValue(), deadsBornTextField.textProperty().getValue()));
		}
	}

	public void onDeadsBornTextFieldChanged() {
		if (!running && "Custom".equals(ruleCombo.getValue()) && !deadsBornTextField.isDisabled()) {
			generationController.setChangeAliveRule(new Custom(alivesSurviveTextField.textProperty().getValue(), deadsBornTextField.textProperty().getValue()));
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
					changeCurrentCellPosition(new GridPosition(currentCellPosition.getRow() == 0 ? gameConfig.getRows() - 1 : currentCellPosition.getRow() - 1, currentCellPosition.getColumn()));
					break;
				case DOWN:
					if (keyEvent.isControlDown()) {
						cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
						cell.isAlive(!keyEvent.isAltDown() || !cell.isAlive());
						drawCell(cell);
						animationSaver.step();
					}
					changeCurrentCellPosition(new GridPosition(currentCellPosition.getRow() == (gameConfig.getRows() - 1) ? 0 : currentCellPosition.getRow() + 1, currentCellPosition.getColumn()));
					break;
				case LEFT:
					if (keyEvent.isControlDown()) {
						cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
						cell.isAlive(!keyEvent.isAltDown() || !cell.isAlive());
						drawCell(cell);
						animationSaver.step();
					}
					changeCurrentCellPosition(new GridPosition(
							currentCellPosition.getColumn() == 0 ? (currentCellPosition.getRow() == 0 ? gameConfig.getRows() - 1 : currentCellPosition.getRow() - 1) : currentCellPosition.getRow(),
							currentCellPosition.getColumn() == 0 ? gameConfig.getColumns() - 1 : currentCellPosition.getColumn() - 1));
					break;
				case RIGHT:
					if (keyEvent.isControlDown()) {
						cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
						cell.isAlive(!keyEvent.isAltDown() || !cell.isAlive());
						drawCell(cell);
						animationSaver.step();
					}
					changeCurrentCellPosition(new GridPosition(
							currentCellPosition.getColumn() == gameConfig.getColumns() - 1 ? (currentCellPosition.getRow() == gameConfig.getRows() - 1 ? 0 : currentCellPosition.getRow() + 1) : currentCellPosition.getRow(),
							currentCellPosition.getColumn() == (gameConfig.getColumns() - 1) ? 0 : currentCellPosition.getColumn() + 1));
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
						changeCurrentCellPosition(new GridPosition(gameConfig.getRows() - 1, gameConfig.getColumns() - 1));
					} else {
						changeCurrentCellPosition(new GridPosition(currentCellPosition.getRow(), gameConfig.getColumns() - 1));
					}
					break;
				case PAGE_UP:
					changeCurrentCellPosition(new GridPosition(0, currentCellPosition.getColumn()));
					break;
				case PAGE_DOWN:
					changeCurrentCellPosition(new GridPosition(gameConfig.getRows() - 1, currentCellPosition.getColumn()));
					break;
				case SPACE:
					cell = board.cell(currentCellPosition.getRow(), currentCellPosition.getColumn());
					cell.isAlive(!keyEvent.isControlDown());
					drawCell(cell);
					animationSaver.step();
					changeCurrentCellPosition(new GridPosition(
							currentCellPosition.getColumn() == gameConfig.getColumns() - 1 ? (currentCellPosition.getRow() == gameConfig.getRows() - 1 ? 0 : currentCellPosition.getRow() + 1) : currentCellPosition.getRow(),
							currentCellPosition.getColumn() == (gameConfig.getColumns() - 1) ? 0 : currentCellPosition.getColumn() + 1));
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
							currentCellPosition.getColumn() == 0 ? (currentCellPosition.getRow() == 0 ? gameConfig.getRows() - 1 : currentCellPosition.getRow() - 1) : currentCellPosition.getRow(),
							currentCellPosition.getColumn() == 0 ? gameConfig.getColumns() - 1 : currentCellPosition.getColumn() - 1));
					break;
				case INSERT:
					if (selectedPattern != null) {
						IPattern pattern = selectedPattern.getPattern();
						board.drawPattern(currentCellPosition.getRow(), currentCellPosition.getColumn(), pattern);
						redrawBoardArea(currentCellPosition.getRow(), currentCellPosition.getColumn(), pattern.rows(), pattern.columns());
						changeCurrentCellPosition(new GridPosition(
								currentCellPosition.getRow(),
								Math.min(currentCellPosition.getColumn() + pattern.columns(), gameConfig.getColumns() - 1)
						));
					}
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
						changeCurrentCellPosition(new GridPosition(top + (characterPattern.rows() - 2), Math.min(gameConfig.getColumns() - 1, currentCellPosition.getColumn() + characterPattern.columns() + 1)));
						animationSaver.step();
					}
				}
			}
		}
	}

	private void redrawBoardArea(int top, int left, int height, int width) {
		synchronized (canvasGraphicsContext) {
			for (int row = 0; row < height; row++) {
				if ((row + top) < gameConfig.getRows()) {
					for (int column = 0; column < width; column++) {
						if ((column + left) < gameConfig.getColumns()) {
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
					canvasGraphicsContext.setFill((cell.isAlive() ? gameConfig.getCellActiveColor() : gameConfig.getCellInactiveColor()).invert());
					int cellSize = gameConfig.getCellSize();
					int cellSpacing = cellSize + gameConfig.getCellSpace();
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
		canvasGraphicsContext.setFill((cell.isAlive() ? gameConfig.getCellActiveColor() : gameConfig.getCellInactiveColor()).invert());
		int cellSize = gameConfig.getCellSize();
		int cellSpacing = cellSize + gameConfig.getCellSpace();
		canvasGraphicsContext.fillRect((cell.column() * cellSpacing) + 1, (cell.row() * cellSpacing) + 1, cellSize, cellSize);
	}

	public void onWrappingChanged(ActionEvent actionEvent) {
		if (!running) {
			BoardWrappingMode wrappingMode = BoardWrappingMode.fromString(wrappingCombo.getValue());
			if (wrappingMode != null && gameConfig.getWrappingMode() != wrappingMode) {
				gameConfig.setWrappingMode(wrappingMode);
				deadCellEdgesCheckbox.setDisable(wrappingMode == BoardWrappingMode.BOTH);
				resizeBoard();
			}
		}
	}

	public void onDeadCellEdgesCheckboxChanged(ActionEvent actionEvent) {
		if (!running) {
			boolean deadCellEdges = deadCellEdgesCheckbox.isSelected();
			if (deadCellEdges != gameConfig.isDeadCellEdges()) {
				gameConfig.setDeadCellEdges(deadCellEdges);
				if (gameConfig.getWrappingMode() != BoardWrappingMode.BOTH) {
					resizeBoard();
				}
			}
		}
	}

	public void onSaveAnimationCheckboxChanged(ActionEvent actionEvent) {
		if (saveAnimationCheckbox.isSelected()) {
			animationSaver.setFrameInterval((int)gameConfig.getGenerationDelay());
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
