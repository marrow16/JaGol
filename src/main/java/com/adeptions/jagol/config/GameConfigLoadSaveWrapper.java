package com.adeptions.jagol.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.adeptions.jagol.logic.BoardWrappingMode;
import com.adeptions.jagol.logic.rule.ChangeAliveRuleFactory;
import com.adeptions.jagol.logic.rule.StandardConways;
import javafx.scene.paint.Color;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameConfigLoadSaveWrapper {
	private GameConfig config;

	public GameConfigLoadSaveWrapper() {
		config = new GameConfig();
	}

	public GameConfigLoadSaveWrapper(GameConfig config) {
		this.config = config;
	}

	@JsonIgnore
	GameConfig getActualConfig() {
		return config;
	}

	public String getActiveColor() {
		return GameConfig.colorToHtml(config.getCellActiveColor());
	}
	public void setActiveColor(String htmlColor) {
		config.setCellActiveColor(GameConfig.colorFromHtml(htmlColor, config.getCellActiveColor()));
	}
	public String getInactiveColor() {
		return GameConfig.colorToHtml(config.getCellInactiveColor());
	}
	public void setInactiveColor(String htmlColor) {
		config.setCellInactiveColor(GameConfig.colorFromHtml(htmlColor, config.getCellInactiveColor()));
	}
	public String getGridColor() {
		return GameConfig.colorToHtml(config.getCellGridColor());
	}
	public void setGridColor(String htmlColor) {
		config.setCellGridColor(GameConfig.colorFromHtml(htmlColor, config.getCellGridColor()));
	}
	public String getBorderColor() {
		return GameConfig.colorToHtml(config.getBoardBorderColor());
	}
	public void setBorderColor(String htmlColor) {
		config.setBoardBorderColor(GameConfig.colorFromHtml(htmlColor, config.getBoardBorderColor()));
	}

	public int getColumns() {
		return config.getColumns();
	}
	public void setColumns(int columns) {
		config.setColumns(columns);
	}

	public int getRows() {
		return config.getRows();
	}
	public void setRows(int rows) {
		config.setRows(rows);
	}

	public BoardWrappingMode getWrappingMode() {
		return config.getWrappingMode();
	}
	public void setWrappingMode(BoardWrappingMode wrappingMode) {
		config.setWrappingMode(wrappingMode);
	}

	public boolean isDeadCellEdges() {
		return config.isDeadCellEdges();
	}
	public void setDeadCellEdges(boolean deadCellEdges) {
		config.setDeadCellEdges(deadCellEdges);
	}

	public double getGenerationDelay() {
		return config.getGenerationDelay();
	}
	public void setGenerationDelay(double generationDelay) {
		config.setGenerationDelay(generationDelay);
	}

	public double getRandomizationDensity() {
		return config.getRandomizationDensity();
	}
	public void setRandomizationDensity(double randomizationDensity) {
		config.setRandomizationDensity(randomizationDensity);
	}

	public int getCellSize() {
		return config.getCellSize();
	}
	public void setCellSize(int cellSize) {
		config.setCellSize(cellSize);
	}

	public int getCellSpace() {
		return config.getCellSpace();
	}
	public void setCellSpace(int cellSpace) {
		config.setCellSpace(cellSpace);
	}

	public ChangeRuleLoadSaveWrapper getGameRule() {
		return new ChangeRuleLoadSaveWrapper(config.getChangeAliveRule());
	}
	public void setGameRule(ChangeRuleLoadSaveWrapper wrapper) {
		if (wrapper == null) {
			config.setChangeAliveRule(new StandardConways());
			return;
		}
		config.setChangeAliveRule(wrapper.getActualRule());
		if (wrapper.getActualRule().isCustom() && !ChangeAliveRuleFactory.CUSTOM_RULE_LABEL.equals(wrapper.getActualRule().getType())) {
			ChangeAliveRuleFactory.registerNamedCustom(wrapper.getActualRule());
		}
	}

	public boolean getCellsAge() {
		return config.getCellsAge();
	}
	public void setCellsAge(boolean cellsAge) {
		config.setCellsAge(cellsAge);
	}

	public int getMaximumCellAge() {
		return config.getMaximumCellAge();
	}
	public void setMaximumCellAge(int maximumCellAge) {
		config.setMaximumCellAge(maximumCellAge);
	}
}
