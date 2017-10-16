package com.foo.gol.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foo.gol.logic.BoardWrappingMode;
import com.foo.gol.logic.rule.ChangeAliveRuleFactory;
import com.foo.gol.logic.rule.StandardConways;
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

	private static String colorToHtml(Color color) {
		Double r = color.getRed() * 255d;
		Double g = color.getGreen() * 255d;
		Double b = color.getBlue() * 255d;
		return String.format("#%02x%02x%02x", r.intValue(), g.intValue(), b.intValue());
	}

	private static Color colorFromHtml(String htmlColor, Color defaultValue) {
		Color result = defaultValue;
		if (htmlColor != null && !htmlColor.isEmpty() && htmlColor.startsWith("#")) {
			Integer r,g,b;
			try {
				if (htmlColor.length() == 7) {
					r = Integer.valueOf(htmlColor.substring(1, 3), 16);
					g = Integer.valueOf(htmlColor.substring(3, 5), 16);
					b = Integer.valueOf(htmlColor.substring(5, 7), 16);
					result = new Color(r.doubleValue() / 255d, g.doubleValue() / 255d, b.doubleValue() / 255d, 1.0d);
				} else if (htmlColor.length() == 4) {
					r = Integer.valueOf(htmlColor.substring(1, 2) + htmlColor.substring(1, 2), 16);
					g = Integer.valueOf(htmlColor.substring(2, 3) + htmlColor.substring(2, 3), 16);
					b = Integer.valueOf(htmlColor.substring(3, 4) + htmlColor.substring(3, 4), 16);
					result = new Color(r.doubleValue() / 255d, g.doubleValue() / 255d, b.doubleValue() / 255d, 1.0d);
				}
			} catch (NumberFormatException nfe) {
				// just ignore and return the default value
			}
		}
		return result;
	}

	public String getActiveColor() {
		return colorToHtml(config.getCellActiveColor());
	}
	public void setActiveColor(String htmlColor) {
		config.setCellActiveColor(colorFromHtml(htmlColor, config.getCellActiveColor()));
	}
	public String getInactiveColor() {
		return colorToHtml(config.getCellInactiveColor());
	}
	public void setInactiveColor(String htmlColor) {
		config.setCellInactiveColor(colorFromHtml(htmlColor, config.getCellInactiveColor()));
	}
	public String getGridColor() {
		return colorToHtml(config.getCellGridColor());
	}
	public void setGridColor(String htmlColor) {
		config.setCellGridColor(colorFromHtml(htmlColor, config.getCellGridColor()));
	}
	public String getBorderColor() {
		return colorToHtml(config.getBoardBorderColor());
	}
	public void setBorderColor(String htmlColor) {
		config.setBoardBorderColor(colorFromHtml(htmlColor, config.getBoardBorderColor()));
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
}
