package com.foo.gol.ui;

import com.foo.gol.util.GifSequenceWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.io.IOException;

public class AnimationSaver {
	private Window ownerWindow;
	private Canvas canvas;
	private int frameInterval;
	private boolean saving = false;
	private File animationOutputFile;
	private File initialDirectory;
	private FileChooser fileChooser;

	private GifSequenceWriter gifSequenceWriter;
	private ImageOutputStream imageOutputStream;

	public AnimationSaver(Window ownerWindow, Canvas canvas, int frameInterval) {
		this.ownerWindow = ownerWindow;
		this.canvas = canvas;
		this.frameInterval = frameInterval;
		hookupShutdownListener();
	}

	private void hookupShutdownListener() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			end();
		}));
	}

	public void start() {
		end();
		File file = showFileChooser();
		if (file != null) {
			animationOutputFile = file;
			initialDirectory = file.getParentFile();
			initializeAnimationOutput();
			step();
		}
	}

	private File showFileChooser() {
		if (fileChooser == null) {
			fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Animated image (*.gif)", "*.gif"));
			fileChooser.setTitle("Save animation to");
			initialDirectory = initialDirectory == null ? new File(".") : initialDirectory;
		}
		fileChooser.setInitialDirectory(initialDirectory);
		return fileChooser.showSaveDialog(ownerWindow);
	}

	private void initializeAnimationOutput() {
		try {
			imageOutputStream = new FileImageOutputStream(animationOutputFile);
			gifSequenceWriter = new GifSequenceWriter(imageOutputStream, 1, frameInterval, true);
			saving = true;
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
			alert.showAndWait();
		}
	}

	public void end() {
		if (saving) {
			try {
				gifSequenceWriter.close();
			} catch (IOException e) {
				// close silently
			}
			try {
				imageOutputStream.close();
			} catch (IOException e) {
				// close silently
			}
			gifSequenceWriter = null;
			imageOutputStream = null;
			saving = false;
		}
	}

	public void step() {
		if (saving) {
			try {
				WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
				canvas.snapshot(null, writableImage);
				gifSequenceWriter.writeToSequence(SwingFXUtils.fromFXImage(writableImage, null));
			} catch (IOException e) {
				// we'll have to swallow this!
			}
		}
	}

	public boolean isSaving() {
		return saving;
	}

	public void setFrameInterval(int frameInterval) {
		this.frameInterval = frameInterval;
	}

	public File getAnimationOutputFile() {
		return animationOutputFile;
	}
}
