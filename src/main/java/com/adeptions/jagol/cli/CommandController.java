package com.adeptions.jagol.cli;

import com.adeptions.jagol.config.GameConfig;
import com.adeptions.jagol.logic.BoardWrappingMode;
import com.adeptions.jagol.logic.rule.ChangeAliveRuleFactory;
import com.adeptions.jagol.logic.rule.Custom;
import com.adeptions.jagol.logic.rule.IChangeAliveRule;
import com.adeptions.jagol.ui.GridPosition;
import com.adeptions.jagol.ui.IController;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.adeptions.jagol.ui.IController.*;

public class CommandController {
	private static final Pattern ARGS_SPLIT_PATTERN = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
	private static final Set<String> SETTING_NAMES = new HashSet<String>(Arrays.asList(new String[] {
			SETTING_NAME_RUNNING,
			SETTING_NAME_WIDTH,
			SETTING_NAME_HEIGHT,
			SETTING_NAME_DELAY,
			SETTING_NAME_RULE,
			SETTING_NAME_PERMUTATION,
			SETTING_NAME_RANDOMIZATION,
			SETTING_NAME_WRAPPING,
			SETTING_NAME_SIZE,
			SETTING_NAME_ACTIVE_COLOR,
			SETTING_NAME_INACTIVE_COLOR,
			SETTING_NAME_SHOW_GRID,
			SETTING_NAME_GRID_COLOR
	}));
	private static final String HELP_STRING = "jaGol commands:-\n" +
			"  help\n" +
			"       Print this help\n" +
			"  quit | exit\n" +
			"       Quit jaGol\n" +
			"  play | run | start\n" +
			"       Start the Game Of Life\n" +
			"  step [steps]\n" +
			"       Step the game (optional by a specified number of steps)\n" +
			"  stop\n" +
			"       Stop the game\n" +
			"  clear | cls\n" +
			"       Clear the board\n" +
			"  randomize [value]\n" +
			"       Randomize the board - optionally with randomization density\n" +
			"       where:-\n" +
			"         [value] - is the randomization density (0.0 to 100.0)\n" +
			"  set [setting [value]]\n" +
			"       Sets a jaGol setting\n" +
			"       (if the setting is omitted all current settings are listed)\n" +
			"       (if the value is omitted the current value for the setting is displayed)\n" +
			"       where setting is one of:-\n" +
			"         " + SETTING_NAME_RUNNING + "         - whether the game is running (yes | no)\n" +
			"         " + SETTING_NAME_WIDTH + "           - board width (5 to 500)\n" +
			"         " + SETTING_NAME_HEIGHT + "          - board height (5 to 500)\n" +
			"         " + SETTING_NAME_DELAY + "           - generation delay ms (50 to 1000)\n" +
			"         " + SETTING_NAME_RULE + "            - generation rule name or B/S notation\n" +
			"         " + SETTING_NAME_PERMUTATION + "     - rule permutation index (0 to 262143)\n" +
			"         " + SETTING_NAME_RANDOMIZATION + "   - randomization density (0,0 to 100.0)\n" +
			"         " + SETTING_NAME_WRAPPING + "        - board wrapping mode\n" +
			"                           (none|horizontal|vertical|both)\n" +
			"         " + SETTING_NAME_SIZE + "            - cell size (1 to 100)\n" +
			"         " + SETTING_NAME_ACTIVE_COLOR + "    - active cell color (#rrggbb)\n" +
			"         " + SETTING_NAME_INACTIVE_COLOR + "  - inactive cell color (#rrggbb)\n" +
			"         " + SETTING_NAME_SHOW_GRID + "            - show grid (on | off)\n" +
			"         " + SETTING_NAME_GRID_COLOR + "      - grid lines color (#rrggbb)\n" +
			"  permutation [+|-][amount]\n" +
			"       Increment or decerement rule permutation index\n" +
			"       (default is to increment by 1)\n" +
			"  position [row column]\n" +
			"       Set the current board drawing position\n" +
			"       (shows current position if no row & column specified)\n" +
			"  live [row column]\n" +
			"       Set cell at row/column as alive\n" +
			"       (if row/column omitted the current position is used\n" +
			"  die [row column]\n" +
			"       Set cell at row/position as dead\n" +
			"       (if row/column omitted the current position is used\n" +
			"  10101... [10101...]\n" +
			"       Draws pattern of alive (1) and dead (0) cells starting at\n" +
			"       the current position\n" +
			"  print [string]\n" +
			"       Print a string on the board at the current drawing position\n";

	private static final class Lock { }
	private final Object lock = new Lock();

	private IController uiController;
	private InputHandler inputHandler;
	private Thread inputHandlerThread;

	private Map<String, Function<List<String>,String>> commandDelegates = new HashMap<>();


	public CommandController(IController uiController) {
		this.uiController = uiController;
		registerCommandDelegates();
		inputHandler = new InputHandler(this);
		inputHandlerThread = new Thread(inputHandler);
		inputHandlerThread.start();
	}

	private void registerCommandDelegates() {
		commandDelegates.put("help", this::help);
		commandDelegates.put("?", this::help);
		commandDelegates.put("start", this::start);
		commandDelegates.put("play", this::start);
		commandDelegates.put("run", this::start);
		commandDelegates.put("stop", this::stop);
		commandDelegates.put("step", this::step);
		commandDelegates.put("clear", this::clear);
		commandDelegates.put("cls", this::clear);
		commandDelegates.put("randomize", this::randomize);
		commandDelegates.put("rand", this::randomize);
		commandDelegates.put("rnd", this::randomize);
		commandDelegates.put("quit", this::quit);
		commandDelegates.put("exit", this::quit);
		commandDelegates.put("set", this::setting);
		commandDelegates.put("permutation", this::permutationIncrementDecrement);
		commandDelegates.put("perm", this::permutationIncrementDecrement);
		commandDelegates.put("position", this::position);
		commandDelegates.put("pos", this::position);
		commandDelegates.put("live", this::live);
		commandDelegates.put("die", this::die);
		commandDelegates.put("print", this::print);
	}

	void processCommand(String command) {
		List<String> args = commandArgsSplit(command);
		if (args.size() > 0) {
			String actualCommand = args.get(0).trim();
			try {
				if (commandDelegates.containsKey(actualCommand)) {
					String output = commandDelegates.get(actualCommand).apply(args);
					if (output != null) {
						System.out.println(output);
					}
				} else if (actualCommand.startsWith("0") || actualCommand.startsWith("1") || actualCommand.startsWith(".") || actualCommand.startsWith("O")) {
					drawPattern(args);
				} else {
					throw new BadCommandException("Unknown command \"" + actualCommand + "\"");
				}
			} catch (BadCommandException bce) {
				System.out.println("ERROR: " + bce.getMessage());
			}
		}
	}

	private List<String> commandArgsSplit(String command) {
		List<String> result = new ArrayList<>();
		Matcher matcher = ARGS_SPLIT_PATTERN.matcher(command);
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result;
	}

	private String help(List<String> args) {
		return HELP_STRING;
	}

	private String start(List<String> args) {
		uiController.run();
		return null;
	}

	private String stop(List<String> args) {
		uiController.stop();
		return null;
	}

	private void stopInBatch(List<String> args) {
		uiController.stop();
	}

	private String step(List<String> args) {
		if (args.size() > 1) {
			Integer steps = getIntegerArgValue(args.get(1));
			if (steps == null || steps < 0) {
				throw new BadCommandException("Number of steps must be an integer greater than zero");
			}
			for (int i = 0; i < steps; i++) {
				uiController.step();
			}
		} else {
			uiController.step();
		}
		return null;
	}

	private String clear(List<String> args) {
		uiController.clear();
		return null;
	}

	private String randomize(List<String> args) {
		Double density = null;
		if (args.size() > 1) {
			density = getDoubleArgValue(args.get(1));
			if (density == null) {
				throw new BadCommandException("Density argument must be numeric");
			} else if (density < 0 || density > 100) {
				throw new BadCommandException("Density argument must be numeric");
			}
		}
		uiController.randomize(density);
		return null;
	}

	private String setting(List<String> args) {
		String settingName = null;
		Object settingValue = null;
		if (args.size() > 1) {
			settingName = args.get(1);
			if (!SETTING_NAMES.contains(settingName)) {
				throw new BadCommandException("Unknown setting name \"" + settingName + "\"");
			}
			if (args.size() > 2) {
				String settingValueStr = args.get(2);
				switch (settingName) {
					case SETTING_NAME_RUNNING:
						if (settingValueStr.equals("yes") || settingValueStr.equals("no")) {
							settingValue = (Boolean)(settingValueStr.equals("yes"));
						} else {
							throw new BadCommandException("Running must be set to either \"yes\" or \"no\"");
						}
						break;
					case SETTING_NAME_WIDTH:
						settingValue = getIntegerArgValue(settingValueStr);
						if (settingValue == null) {
							throw new BadCommandException("Width setting must be a valid integer");
						} else if ((Integer)settingValue < 5 || (Integer)settingValue > 500) {
							throw new BadCommandException("Width must be between 5 and 500");
						}
						break;
					case SETTING_NAME_HEIGHT:
						settingValue = getIntegerArgValue(settingValueStr);
						if (settingValue == null) {
							throw new BadCommandException("Height setting must be a valid integer");
						} else if ((Integer)settingValue < 5 || (Integer)settingValue > 500) {
							throw new BadCommandException("Height must be between 5 and 500");
						}
						break;
					case SETTING_NAME_DELAY:
						settingValue = getDoubleArgValue(settingValueStr);
						if (settingValue == null) {
							throw new BadCommandException("Delay setting must be a valid number");
						} else if ((Double)settingValue < 50d || (Double)settingValue > 1000d) {
							throw new BadCommandException("Delay must be between 50 and 1000");
						}
						break;
					case SETTING_NAME_RULE:
						String ruleName = getStringArgValue(settingValueStr);
						if (!ruleName.isEmpty()) {
							IChangeAliveRule rule = ChangeAliveRuleFactory.createFromLabel(ruleName);
							if (rule == null) {
								// see if it's in B/S notation...
								if (ruleName.contains("/")) {
									String[] parts = ruleName.split("/");
									if (parts.length == 2) {
										if ((parts[0].startsWith("B") && parts[1].startsWith("S")) ||
												(parts[0].startsWith("S") && parts[1].startsWith("B"))) {
											rule = Custom.createFromRuleString(ruleName);
										}
									}
								}
							}
							if (rule == null) {
								throw new BadCommandException("Unknown rule \"" + ruleName + "\"");
							}
							settingValue = rule;
						} else {
							throw new BadCommandException("Rule name is invalid");
						}
						break;
					case SETTING_NAME_PERMUTATION:
						settingValue = getIntegerArgValue(settingValueStr);
						if (settingValue == null) {
							throw new BadCommandException("Permutation setting must be a valid integer");
						} else if ((Integer)settingValue < 0 || (Integer)settingValue > 262143) {
							throw new BadCommandException("Permutation must be between 0 and 262143");
						}
						break;
					case SETTING_NAME_RANDOMIZATION:
						settingValue = getDoubleArgValue(settingValueStr);
						if (settingValue == null) {
							throw new BadCommandException("Randomization setting must be a valid number");
						} else if ((Double)settingValue < 0.0d || (Double)settingValue > 100d) {
							throw new BadCommandException("Randomization must be between 0.0 and 100.0");
						}
						break;
					case SETTING_NAME_WRAPPING:
						settingValue = BoardWrappingMode.fromString(settingValueStr);
						if (settingValue == null) {
							throw new BadCommandException("Wrapping setting must be \"none\", \"horizontal\", \"vertical\" or \"both\"");
						}
						break;
					case SETTING_NAME_SIZE:
						settingValue = getIntegerArgValue(settingValueStr);
						if (settingValue == null) {
							throw new BadCommandException("Size setting must be a valid integer");
						} else if ((Integer)settingValue < 1 || (Integer)settingValue > 100) {
							throw new BadCommandException("Size must be between 1 and 100");
						}
						break;
					case SETTING_NAME_ACTIVE_COLOR:
					case SETTING_NAME_INACTIVE_COLOR:
					case SETTING_NAME_GRID_COLOR:
						settingValue = GameConfig.colorFromHtml(settingValueStr, null);
						if (settingValue == null) {
							throw new BadCommandException("Color must be a valid HTML color (i.e. #rrggbb)");
						}
						break;
					case SETTING_NAME_SHOW_GRID:
						if (settingValueStr.equals("on") || settingValueStr.equals("off")) {
							settingValue = (Boolean)(settingValueStr.equals("on"));
						} else {
							throw new BadCommandException("Grid must be set to either \"on\" or \"off\"");
						}
						break;
				}
			}
		}
		return uiController.set(settingName, settingValue);
	}

	private String permutationIncrementDecrement(List<String> args) {
		Integer increment = 1;
		if (args.size() > 1) {
			String permIncDec = args.get(1);
			if ("+".equals(permIncDec) || "-".equals(permIncDec)) {
				increment = ("+".equals(permIncDec) ? 1 : -1);
			} else {
				increment = getIntegerArgValue(permIncDec);
				if (increment == null) {
					throw new BadCommandException("Permutation increment/decerement value \"" + permIncDec + "\" is invalid");
				}
			}
		}
		return uiController.permutationIncrement(increment);
	}

	private String position(List<String> args) {
		GridPosition newPosition = null;
		if (args.size() > 1) {
			if (args.size() < 3) {
				throw new BadCommandException("You must specify both row and column");
			}
			Integer row = getIntegerArgValue(args.get(1));
			Integer column = getIntegerArgValue(args.get(2));
			if (row == null || row < 0) {
				throw new BadCommandException("Row value \"" + args.get(1) + "\" is invalid");
			}
			if (column == null || column < 0) {
				throw new BadCommandException("Column value \"" + args.get(1) + "\" is invalid");
			}
			newPosition = new GridPosition(row, column);
		}
		return uiController.position(newPosition);
	}

	private String live(List<String> args) {
		GridPosition position = null;
		if (args.size() > 1) {
			if (args.size() < 3) {
				throw new BadCommandException("You must specify both row and column");
			}
			Integer row = getIntegerArgValue(args.get(1));
			Integer column = getIntegerArgValue(args.get(2));
			if (row == null || row < 0) {
				throw new BadCommandException("Row value \"" + args.get(1) + "\" is invalid");
			}
			if (column == null || column < 0) {
				throw new BadCommandException("Column value \"" + args.get(1) + "\" is invalid");
			}
			position = new GridPosition(row, column);
		}
		uiController.live(position);
		return null;
	}

	private String die(List<String> args) {
		GridPosition position = null;
		if (args.size() > 1) {
			if (args.size() < 3) {
				throw new BadCommandException("You must specify both row and column");
			}
			Integer row = getIntegerArgValue(args.get(1));
			Integer column = getIntegerArgValue(args.get(2));
			if (row == null || row < 0) {
				throw new BadCommandException("Row value \"" + args.get(1) + "\" is invalid");
			}
			if (column == null || column < 0) {
				throw new BadCommandException("Column value \"" + args.get(1) + "\" is invalid");
			}
			position = new GridPosition(row, column);
		}
		uiController.die(position);
		return null;
	}

	private void drawPattern(List<String> args) {
		uiController.drawPatternStrings(args);
	}

	private String print(List<String> args) {
		if (args.size() > 1) {
			String printStr = getStringArgValue(args.get(1));
			if (!printStr.isEmpty()) {
				uiController.print(printStr);
			}
		}
		return null;
	}

	private String quit(List<String> args) {
		close();
		return null;
	}

	private String getStringArgValue(String arg) {
		String result = arg;
		if (!arg.isEmpty() && arg.startsWith("\"") && arg.endsWith("\"")) {
			result = arg.substring(1, arg.length() - 1);
		}
		return result;
	}

	private Double getDoubleArgValue(String arg) {
		Double result = null;
		try {
			result = Double.parseDouble(arg);
			if (result.isInfinite() || result.isNaN()) {
				result = null;
			}
		} catch (NumberFormatException nfe) {
			// swallow
		}
		return result;
	}

	private Integer getIntegerArgValue(String arg) {
		Integer result = null;
		try {
			result = Integer.parseInt(arg, 10);
		} catch (NumberFormatException nfe) {
			// swallow
		}
		return result;
	}

	public void close() {
		inputHandler.stop();
		// unfortunately the thread reading on System.in won't stop, so exit the app forcibly...
		System.exit(0);
	}
}
