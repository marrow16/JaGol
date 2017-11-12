package com.adeptions.jagol.ui;

import com.adeptions.jagol.cli.CommandController;
import com.adeptions.jagol.config.GameConfig;
import com.adeptions.jagol.logic.*;
import com.adeptions.jagol.logic.rule.*;
import com.adeptions.jagol.patterns.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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

public class Controller implements IController {
	private static Controller instance = null;
	public static Controller getInstance() {
		return instance;
	}

	private static final DataFormat PATTERN_DRAG_FORMAT = new DataFormat("com.adeptions.jagol.ui.pattern");

	private GameConfig gameConfig;
	private IBoard board;
	private IGenerationController generationController = new FullScanGenerationController(new StandardConways());
	private Timeline animationLoop = null;
	private PatternLibrary patternLibrary;

	private CommandController cliController;
	private boolean running = false;
	private boolean ruleChanging = false;

	private PatternVBox selectedPattern = null;

	private IPattern draggingPattern = null;
	private GridPosition draggingOverlayPosition = null;

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
	private Spinner permutationSpinner;
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
	private CheckBox cellsAgeCheckbox;
	@FXML
	private Spinner maximumCellAgeSpinner;
	@FXML
	private Label maximumCellAgeLabel;

	@FXML
	public void initialize() {
		// store the instance of the controller so that window shown can call .show() method...
		instance = this;

		mainPane.setDisable(true);
	}

	public void shown() {
		gameConfig = GameConfig.load();
		permutationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, ChangeAliveRuleFactory.getPermutationsCount()));
		generationController.setCellsAge(gameConfig.getCellsAge());
		generationController.setMaximumCellAge(gameConfig.getMaximumCellAge());
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
			animationSaver.step();
			rebuildPatterns();
		});
		permutationSpinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!running && !ruleChanging) {
				Integer newIntValue = (Integer)newValue;
				if (newValue == null || newIntValue.compareTo(0) < 0) {
					newIntValue = 0;
					permutationSpinner.getValueFactory().setValue(0);
				} else if (newIntValue.compareTo(ChangeAliveRuleFactory.getPermutationsCount()) >= 0) {
					newIntValue = ChangeAliveRuleFactory.getPermutationsCount();
					permutationSpinner.getValueFactory().setValue(ChangeAliveRuleFactory.getPermutationsCount());
				}
				ruleChange(ChangeAliveRuleFactory.getPermutationRule(newIntValue));
			}
		});
		maximumCellAgeSpinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!running) {
				Integer newIntValue = (Integer)newValue;
				if (newIntValue != null && newIntValue >= 0) {
					gameConfig.setMaximumCellAge(newIntValue);
					generationController.setMaximumCellAge(newIntValue);
				}
			}
		});
		permutationSpinner.getEditor().setOnKeyPressed(event -> {
			if (!running && !ruleChanging) {
				Integer currentValue = (Integer) permutationSpinner.getValue();
				boolean changed = false;
				switch (event.getCode()) {
					case UP:
						if (currentValue < (ChangeAliveRuleFactory.getPermutationsCount() - 1)) {
							changed = true;
							currentValue++;
						}
						break;
					case DOWN:
						if (currentValue > 0) {
							changed = true;
							currentValue--;
						}
						break;
					case PAGE_UP:
						changed = true;
						currentValue = Math.min(ChangeAliveRuleFactory.getPermutationsCount(), currentValue + 100);
						break;
					case PAGE_DOWN:
						changed = true;
						currentValue = Math.max(0, currentValue - 100);
						break;
					case HOME:
						if (event.isControlDown()) {
							changed = true;
							currentValue = 0;
						}
						break;
					case END:
						if (event.isControlDown()) {
							changed = true;
							currentValue = ChangeAliveRuleFactory.getPermutationsCount() - 1;
						}
						break;
				}
				if (changed) {
					event.consume();
					permutationSpinner.getValueFactory().setValue(currentValue);
				}
			}
		});
		alivesSurviveTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!running && !ruleChanging) {
				ruleChange(new Custom(newValue, deadsBornTextField.textProperty().getValue()));
			}
		});
		deadsBornTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!running && !ruleChanging) {
				ruleChange(new Custom(alivesSurviveTextField.textProperty().getValue(), newValue));
			}
		});
		activeCellColorPicker.setValue(gameConfig.getCellActiveColor());
		inActiveCellColorPicker.setValue(gameConfig.getCellInactiveColor());
		drawGridCheckbox.setSelected(gameConfig.getCellSpace() > 0);
		gridColorPicker.setValue(gameConfig.getCellGridColor());

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

		// start the command line controller...
		cliController = new CommandController(this);
		mainPane.getScene().getWindow().setOnCloseRequest(event -> {
			cliController.close();
		});
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
		ruleChange(gameConfig.getChangeAliveRule());
		activeCellColorPicker.setValue(gameConfig.getCellActiveColor());
		inActiveCellColorPicker.setValue(gameConfig.getCellInactiveColor());
		drawGridCheckbox.setSelected(gameConfig.getCellSpace() != 0);
		gridColorPicker.setValue(gameConfig.getCellGridColor());
		cellsAgeCheckbox.setSelected(gameConfig.getCellsAge());
		maximumCellAgeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, gameConfig.getMaximumCellAge()));
		maximumCellAgeSpinner.setDisable(!gameConfig.getCellsAge());
		maximumCellAgeLabel.setDisable(!gameConfig.getCellsAge());
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
		ruleCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!running && !ruleChanging) {
				IChangeAliveRule newRule = ChangeAliveRuleFactory.createFromLabel(newValue);
				if (newRule == null) {
					newRule = new Custom(alivesSurviveTextField.textProperty().getValue(), deadsBornTextField.textProperty().getValue());
				}
				ruleChange(newRule);
			}
		});
	}

	private void ruleChange(IChangeAliveRule newRule) {
		gameConfig.setChangeAliveRule(newRule);
		generationController.setChangeAliveRule(newRule);
		if (!ruleChanging) {
			ruleChanging = true;
			ruleCombo.setValue(gameConfig.getChangeAliveRule().getType());
			alivesSurviveTextField.textProperty().setValue(gameConfig.getChangeAliveRule().getAlivesSurviveString());
			deadsBornTextField.textProperty().setValue(gameConfig.getChangeAliveRule().getDeadsBornString());
			if (permutationSpinner.getValueFactory() != null) {
				permutationSpinner.getValueFactory().setValue(ChangeAliveRuleFactory.getPermutationIndex(gameConfig.getChangeAliveRule().getRleString()));
			}
			alivesSurviveTextField.setDisable(!gameConfig.getChangeAliveRule().isCustom());
			deadsBornTextField.setDisable(!gameConfig.getChangeAliveRule().isCustom());
			ruleChanging = false;
		}
	}

	private void drawBoardMessage() {
		IPattern[] lines = new IPattern[] {
				AlphabetPatterns.stringToPattern("<<< JaGol >>>"),
				AlphabetPatterns.stringToPattern("Conway's"),
				AlphabetPatterns.stringToPattern("Game"),
				AlphabetPatterns.stringToPattern("Of"),
				AlphabetPatterns.stringToPattern("Life")
		};
		int atRow = Math.max(0, (gameConfig.getRows() - ((AlphabetPatterns.CHAR_HEIGHT + 1) * lines.length)) / 2);
		for (IPattern line: lines) {
			if (atRow < gameConfig.getRows()) {
				board.drawPattern(atRow, Math.max(0, (gameConfig.getColumns() - line.columns()) / 2), line);
				atRow += (AlphabetPatterns.CHAR_HEIGHT + 1);
			}
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
				Dragboard dragboard = patternVBox.startDragAndDrop(TransferMode.COPY_OR_MOVE);
				patternVBox.prepareForDrag();
				dragboard.setDragView(patternVBox.snapshot(null, null));
				ClipboardContent clipboardContent = new ClipboardContent();
				clipboardContent.put(PATTERN_DRAG_FORMAT, pattern.getName());
				dragboard.setContent(clipboardContent);
				draggingPattern = pattern;
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

	private void doRun() {
		running = true;
		updateControls();
		clearBlinkCell();
		animationLoop = new Timeline(new KeyFrame(Duration.millis(gameConfig.getGenerationDelay()), e -> {
			generation();
		}));
		animationLoop.setCycleCount(-1);
		animationLoop.play();
	}

	private void doStop() {
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
			doStop();
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
		doRun();
	}

	@FXML
	public void onStepButtonClick(ActionEvent actionEvent) {
		generation();
	}

	@FXML
	public void onStopButtonClick(ActionEvent actionEvent) {
		doStop();
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
		if (!running && draggingPattern != null) {
			dragEvent.acceptTransferModes(TransferMode.COPY);
			drawDraggingOverlay(GridPosition.fromXYCoordinate(gameConfig, dragEvent.getX(), dragEvent.getY()));
		}
	}

	@FXML
	public void onCanvasDragEntered(DragEvent dragEvent) {
		draggingOverlayPosition = null;
		if (!running) {
			Dragboard db = dragEvent.getDragboard();
			if (!db.hasContent(PATTERN_DRAG_FORMAT) && db.hasContent(DataFormat.PLAIN_TEXT)) {
				draggingPattern = null;
				// attempt to build the dragging pattern...
				String draggingString = db.getString().trim();
				if (draggingString.startsWith(".") || draggingString.startsWith("O") || draggingString.startsWith("!")) {
					// possibly a plain text pattern encoding...
					try {
						draggingPattern = PatternPlainTextLoader.load(null, draggingString);
					} catch (InvalidPlainTextFormatException e) {
						//e.printStackTrace();
					}
				} else if (draggingString.startsWith("#")) {
					// possibly an rle encoded string...
					try {
						draggingPattern = PatternRLELoader.load(null, draggingString);
					} catch (InvalidRLEFormatException e) {
						//e.printStackTrace();
					}
				}
				if (draggingPattern == null) {
					// not a pattern format that we could recognize - so just use the string as a pattern...
					String[] lines = draggingString.split("\n");
					List<IPattern> linePatterns = new ArrayList<>();
					int maxWidth = 0;
					for (String line : lines) {
						IPattern linePattern = AlphabetPatterns.stringToPattern(line);
						maxWidth = Math.max(maxWidth, linePattern.columns());
						linePatterns.add(linePattern);
					}
					draggingPattern = new Pattern(null, (linePatterns.size() * AlphabetPatterns.CHAR_HEIGHT), maxWidth);
					int atRow = 0;
					for (IPattern linePattern : linePatterns) {
						draggingPattern.drawPattern(linePattern, atRow, 0);
						atRow += AlphabetPatterns.CHAR_HEIGHT;
					}
				}
			}
			if (draggingPattern != null) {
				drawDraggingOverlay(GridPosition.fromXYCoordinate(gameConfig, dragEvent.getX(), dragEvent.getY()));
			}
		}
	}

	public void onCanvasDragExited(DragEvent dragEvent) {
		if (!running) {
			if (!dragEvent.isDropCompleted()) {
				clearDraggingOverlay();
			}
			draggingPattern = null;
		}
	}

	@FXML
	public void onCanvasDragDropped(DragEvent dragEvent) {
		if (!running && draggingPattern != null) {
			dragEvent.setDropCompleted(true);
			clearDraggingOverlay();
			Dragboard db = dragEvent.getDragboard();
			GridPosition position = GridPosition.fromXYCoordinate(gameConfig, dragEvent.getX(), dragEvent.getY());
			board.drawPattern(position.getRow(), position.getColumn(), draggingPattern);
			redrawBoardArea(position.getRow(), position.getColumn(), draggingPattern.rows(), draggingPattern.columns());
			animationSaver.step();
			changeCurrentCellPosition(position);
			canvas.requestFocus();
		}
	}

	private void clearDraggingOverlay() {
		if (draggingPattern != null && draggingOverlayPosition != null) {
			redrawBoardArea(draggingOverlayPosition.getRow(), draggingOverlayPosition.getColumn(), draggingPattern.rows(), draggingPattern.columns());
		}
	}

	private void drawDraggingOverlay(GridPosition position) {
		if (draggingOverlayPosition == null || !draggingOverlayPosition.equals(position)) {
			clearDraggingOverlay();
			draggingOverlayPosition = position;
			int cellSize = gameConfig.getCellSize();
			int cellSpacing = cellSize + gameConfig.getCellSpace();
			Color aliveColor = gameConfig.getCellActiveColor();
			Color deadColor = gameConfig.getCellInactiveColor();
			if (deadColor.getBrightness() > 0.5d) {
				deadColor = deadColor.deriveColor(0, 1.0, 0.9, 1.0);
				aliveColor = aliveColor.deriveColor(0, 1.0, 1.0 / 0.9, 1.0);
			} else {
				deadColor = deadColor.deriveColor(0, 1.0, 1.0 / 0.9, 1.0);
				aliveColor = aliveColor.deriveColor(0, 1.0, 0.9, 1.0);
			}
			for (int row = 0; row < draggingPattern.rows() && (row + position.getRow()) < gameConfig.getRows(); row++) {
				for (int column = 0; column < draggingPattern.columns() && (column + position.getColumn()) < gameConfig.getColumns(); column++) {
					ICell cell = draggingPattern.cell(row, column);
					canvasGraphicsContext.setFill(cell.isAlive() ? aliveColor : deadColor);
					canvasGraphicsContext.fillRect(((column + position.getColumn()) * cellSpacing) + 1, ((row + position.getRow()) * cellSpacing) + 1, cellSize, cellSize);
				}
			}
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
					synchronized (canvasGraphicsContext) {
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

	@Override
	public void run() {
		if (!running) {
			doRun();
		}
	}

	@Override
	public void stop() {
		if (running) {
			doStop();
		}
	}

	@Override
	public void step() {
		if (!running) {
			generation();
		}
	}

	@Override
	public void clear() {
		if (!running) {
			createBoard(false);
			drawBoard();
			animationSaver.step();
		}
	}

	@Override
	public void randomize(Double density) {
		if (!running) {
			if (density != null) {
				gameConfig.setRandomizationDensity(density);
				randomDensitySlider.valueProperty().setValue(gameConfig.getRandomizationDensity());
			}
			createBoard(true);
			drawBoard();
			animationSaver.step();
		}
	}

	@Override
	public String set(String settingName, Object settingValue) {
		String result = null;
		if (settingName != null) {
			if (settingValue != null && SETTING_NAME_RUNNING.equals(settingName)) {
				if (running && !(Boolean)settingValue) {
					doStop();
				} else if (!running && (Boolean)settingValue) {
					doRun();
				}
			} else if (settingValue != null && !running) {
				switch (settingName) {
					case SETTING_NAME_WIDTH:
						Platform.runLater(() -> {
							boardWidthSpinner.getValueFactory().setValue(settingValue);
							resizeBoard();
						});
						return SETTING_NAME_WIDTH + " = " + settingValue;
					case SETTING_NAME_HEIGHT:
						Platform.runLater(() -> {
							boardHeightSpinner.getValueFactory().setValue(settingValue);
							resizeBoard();
						});
						return SETTING_NAME_HEIGHT + " = " + settingValue;
					case SETTING_NAME_DELAY:
						gameConfig.setGenerationDelay((Double)settingValue);
						Platform.runLater(() -> {
							animationSpeedSlider.valueProperty().setValue(gameConfig.getGenerationDelay());
						});
						break;
					case SETTING_NAME_RULE:
						Platform.runLater(() -> {
							ruleChange((IChangeAliveRule)settingValue);
						});
						return SETTING_NAME_RULE + " = " + ((IChangeAliveRule)settingValue).getType();
					case SETTING_NAME_PERMUTATION:
						Platform.runLater(() -> {
							permutationSpinner.getValueFactory().setValue((Integer)settingValue);
							ruleChange(ChangeAliveRuleFactory.getPermutationRule((Integer)settingValue));
						});
						return SETTING_NAME_PERMUTATION + " = " + settingValue;
					case SETTING_NAME_RANDOMIZATION:
						gameConfig.setRandomizationDensity((Double)settingValue);
						Platform.runLater(() -> {
							randomDensitySlider.valueProperty().setValue(gameConfig.getRandomizationDensity());
						});
						break;
					case SETTING_NAME_WRAPPING:
						gameConfig.setWrappingMode((BoardWrappingMode)settingValue);
						Platform.runLater(() -> {
							wrappingCombo.setValue(gameConfig.getWrappingMode().getLabel());
						});
						break;
					case SETTING_NAME_SIZE:
						gameConfig.setCellSize((Integer)settingValue);
						Platform.runLater(() -> {
							cellSizeSpinner.getValueFactory().setValue(gameConfig.getCellSize());
							drawBoard();
							animationSaver.step();
							rebuildPatterns();
						});
						break;
					case SETTING_NAME_ACTIVE_COLOR:
						gameConfig.setCellActiveColor((Color)settingValue);
						Platform.runLater(() -> {
							activeCellColorPicker.setValue(gameConfig.getCellActiveColor());
							drawBoard();
							animationSaver.step();
							rebuildPatterns();
						});
						break;
					case SETTING_NAME_INACTIVE_COLOR:
						gameConfig.setCellInactiveColor((Color)settingValue);
						Platform.runLater(() -> {
							inActiveCellColorPicker.setValue(gameConfig.getCellInactiveColor());
							drawBoard();
							animationSaver.step();
							rebuildPatterns();
						});
						break;
					case SETTING_NAME_SHOW_GRID:
						gameConfig.setCellSpace((Boolean)settingValue ? 1 : 0);
						Platform.runLater(() -> {
							drawGridCheckbox.setSelected(gameConfig.getCellSpace() > 0);
							drawBoard();
							animationSaver.step();
							rebuildPatterns();
						});
						break;
					case SETTING_NAME_GRID_COLOR:
						gameConfig.setCellGridColor((Color)settingValue);
						Platform.runLater(() -> {
							gridColorPicker.setValue(gameConfig.getCellGridColor());
							drawBoard();
							animationSaver.step();
							rebuildPatterns();
						});
						break;
				}
			}
			switch (settingName) {
				case SETTING_NAME_RUNNING:
					return SETTING_NAME_RUNNING + " = " + (running ? "yes" : "no");
				case SETTING_NAME_WIDTH:
					return SETTING_NAME_WIDTH + " = " + gameConfig.getColumns();
				case SETTING_NAME_HEIGHT:
					return SETTING_NAME_HEIGHT + " = " + gameConfig.getRows();
				case SETTING_NAME_DELAY:
					return SETTING_NAME_DELAY + " = " + gameConfig.getGenerationDelay();
				case SETTING_NAME_RULE:
					return SETTING_NAME_RULE + " = " + gameConfig.getChangeAliveRule().getType();
				case SETTING_NAME_PERMUTATION:
					return SETTING_NAME_PERMUTATION + " = " + permutationSpinner.getValueFactory().getValue();
				case SETTING_NAME_RANDOMIZATION:
					return SETTING_NAME_RANDOMIZATION + " = " + gameConfig.getRandomizationDensity();
				case SETTING_NAME_WRAPPING:
					return SETTING_NAME_WRAPPING + " = " + gameConfig.getWrappingMode().getLabel();
				case SETTING_NAME_SIZE:
					return SETTING_NAME_SIZE + " = " + gameConfig.getCellSize();
				case SETTING_NAME_ACTIVE_COLOR:
					return SETTING_NAME_ACTIVE_COLOR + " = " + GameConfig.colorToHtml(gameConfig.getCellActiveColor());
				case SETTING_NAME_INACTIVE_COLOR:
					return SETTING_NAME_INACTIVE_COLOR + " = " + GameConfig.colorToHtml(gameConfig.getCellInactiveColor());
				case SETTING_NAME_SHOW_GRID:
					return SETTING_NAME_SHOW_GRID + " = " + (gameConfig.getCellSpace() > 0 ? "on" : "off");
				case SETTING_NAME_GRID_COLOR:
					return SETTING_NAME_GRID_COLOR + " = " + GameConfig.colorToHtml(gameConfig.getCellGridColor());
			}
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append(SETTING_NAME_RUNNING).append(" = ").append(running ? "yes" : "no").append("\n")
					.append(SETTING_NAME_WIDTH).append(" = ").append(gameConfig.getColumns()).append("\n")
					.append(SETTING_NAME_HEIGHT).append(" = ").append(gameConfig.getRows()).append("\n")
					.append(SETTING_NAME_DELAY).append(" = ").append(gameConfig.getGenerationDelay()).append("\n")
					.append(SETTING_NAME_RULE).append(" = ").append(gameConfig.getChangeAliveRule().getType()).append("\n")
					.append(SETTING_NAME_PERMUTATION).append(" = ").append(permutationSpinner.getValueFactory().getValue()).append("\n")
					.append(SETTING_NAME_RANDOMIZATION).append(" = ").append(gameConfig.getRandomizationDensity()).append("\n")
					.append(SETTING_NAME_WRAPPING).append(" = ").append(gameConfig.getWrappingMode().getLabel()).append("\n")
					.append(SETTING_NAME_SIZE).append(" = ").append(gameConfig.getCellSize()).append("\n")
					.append(SETTING_NAME_ACTIVE_COLOR).append(" = ").append(GameConfig.colorToHtml(gameConfig.getCellActiveColor())).append("\n")
					.append(SETTING_NAME_INACTIVE_COLOR).append(" = ").append(GameConfig.colorToHtml(gameConfig.getCellInactiveColor())).append("\n")
					.append(SETTING_NAME_SHOW_GRID).append(" = ").append(gameConfig.getCellSpace() > 0 ? "on" : "off").append("\n")
					.append(SETTING_NAME_GRID_COLOR).append(" = ").append(GameConfig.colorToHtml(gameConfig.getCellGridColor()));
			result = builder.toString();
		}
		return result;
	}

	@Override
	public String permutationIncrement(Integer increment) {
		if (!running) {
			Integer currentValue = (Integer)permutationSpinner.getValue();
			Integer newValue = currentValue + increment;
			if (newValue >= 0 && newValue < 262144) {
				Platform.runLater(() -> {
					permutationSpinner.getValueFactory().setValue(newValue);
					ruleChange(ChangeAliveRuleFactory.getPermutationRule(newValue));
				});
				return newValue.toString();
			} else {
				return currentValue.toString();
			}
		}
		return null;
	}

	@Override
	public String position(GridPosition newPosition) {
		if (newPosition != null && !currentCellPosition.equals(newPosition)) {
			changeCurrentCellPosition(new GridPosition(Math.min(newPosition.getRow(), gameConfig.getRows() - 1), Math.min(newPosition.getColumn(), gameConfig.getColumns() - 1)));
		}
		return "row = " + currentCellPosition.getRow() + ", column = " + currentCellPosition.getColumn();
	}

	@Override
	public void live(GridPosition position) {
		if (!running) {
			GridPosition atPosition = position != null ? position : currentCellPosition;
			if (atPosition.getRow() >= 0 && atPosition.getRow() < gameConfig.getRows() &&
					atPosition.getColumn() >= 0 && atPosition.getColumn() < gameConfig.getColumns()) {
				board.cell(atPosition.getRow(), atPosition.getColumn()).isAlive(true);
				drawCell(board.cell(atPosition.getRow(), atPosition.getColumn()));
				if (position == null) {
					changeCurrentCellPosition(incrementCurrentPosition());
				} else {
					changeCurrentCellPosition(atPosition);
				}
			}
		}
	}

	@Override
	public void die(GridPosition position) {
		if (!running) {
			GridPosition atPosition = position != null ? position : currentCellPosition;
			if (atPosition.getRow() >= 0 && atPosition.getRow() < gameConfig.getRows() &&
					atPosition.getColumn() >= 0 && atPosition.getColumn() < gameConfig.getColumns()) {
				board.cell(atPosition.getRow(), atPosition.getColumn()).isAlive(false);
				drawCell(board.cell(atPosition.getRow(), atPosition.getColumn()));
				if (position == null) {
					changeCurrentCellPosition(incrementCurrentPosition());
				} else {
					changeCurrentCellPosition(atPosition);
				}
			}
		}
	}

	@Override
	public void drawPatternStrings(List<String> lines) {
		int startColumn = currentCellPosition.getColumn();
		int startRow = currentCellPosition.getRow();
		int maxWidth = -1;
		for (int row = 0; row < lines.size() && (row + startRow) < gameConfig.getRows(); row++) {
			String line = lines.get(row).trim();
			maxWidth = Math.max(maxWidth, line.length());
			for (int column = 0; column < line.length() && (column + startColumn) < gameConfig.getColumns(); column++) {
				board.cell(row + startRow, column + startColumn).isAlive(line.charAt(column) == '1' || line.charAt(column) == 'O');
				drawCell(board.cell(row + startRow, column + startColumn));
			}
		}
		changeCurrentCellPosition(new GridPosition(
				Math.min(gameConfig.getRows() - 1, currentCellPosition.getRow() + lines.size()),
				Math.min(gameConfig.getColumns() - 1, startColumn)
		));
	}

	private GridPosition incrementCurrentPosition() {
		if (currentCellPosition.getColumn() < (gameConfig.getColumns() - 1)) {
			return new GridPosition(currentCellPosition.getRow(), currentCellPosition.getColumn() + 1);
		} else if (currentCellPosition.getRow() < (gameConfig.getRows() - 1)) {
			return new GridPosition(currentCellPosition.getRow() + 1, 0);
		} else {
			return new GridPosition(0,0);
		}
	}

	@Override
	public void print(String str) {
		if (!running) {
			IPattern stringPattern = AlphabetPatterns.stringToPattern(str);
			if (stringPattern != null) {
				synchronized (canvasGraphicsContext) {
					int top = Math.max(0, currentCellPosition.getRow() - (stringPattern.rows() - 2));
					board.drawPattern(top, currentCellPosition.getColumn(), stringPattern);
					redrawBoardArea(top, currentCellPosition.getColumn(), stringPattern.rows(), stringPattern.columns());
					changeCurrentCellPosition(new GridPosition(top + (stringPattern.rows() - 2), Math.min(gameConfig.getColumns() - 1, currentCellPosition.getColumn() + stringPattern.columns() + 1)));
					animationSaver.step();
				}
			}
		}
	}

	public void onCellsAgeCheckboxChanged(ActionEvent actionEvent) {
		maximumCellAgeSpinner.setDisable(!cellsAgeCheckbox.isSelected());
		maximumCellAgeLabel.setDisable(!cellsAgeCheckbox.isSelected());
		gameConfig.setCellsAge(cellsAgeCheckbox.isSelected());
		generationController.setCellsAge(cellsAgeCheckbox.isSelected());
	}
}
