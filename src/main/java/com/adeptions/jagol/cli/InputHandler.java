package com.adeptions.jagol.cli;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public class InputHandler implements Runnable {
	private static final String PROMPT = "jaGol> ";

	private boolean running = true;
	private CommandController commandController;
	LineReader lineReader;

	InputHandler(CommandController commandController) {
		this.commandController = commandController;
		System.out.println("<jaGol> Command Line Interface (enter \"help\" or \"?\" for help)");
		lineReader = LineReaderBuilder.builder()
				.appName("jaGol")
				.build();
	}

	@Override
	public void run() {
		while (running) {
			String line = lineReader.readLine(PROMPT);
			commandController.processCommand(line);
		}
	}

	public void stop() {
		running = false;
	}
}
