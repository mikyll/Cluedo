package it.mikyll.cluedo.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.model.settings.Settings;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ControllerLoading implements IController {
    @FXML private AnchorPane anchorPaneRoot;

    @FXML public ProgressBar progressBarLoading;
    @FXML private Label labelLoading;

    private double increment = -1.0;

    public ControllerLoading()
    {
        long fileCount;
        if (this.runningInJAR())
            fileCount = loadFromJAR();
        else
            fileCount = loadFromIDE();

        if (fileCount > 0)
            increment = 1.0 / fileCount;
    }

    public void initialize()
    {
        this.progressBarLoading.setProgress(increment > 0 ? increment : ProgressBar.INDETERMINATE_PROGRESS);
        this.labelLoading.setText("Loaded scene 'views/ViewMenuLoading.fxml'");
    }

    public void incrementProgressBar(String text)
    {
        if (increment > 0)
        {
            double newProgress = progressBarLoading.getProgress() + increment;
            Platform.runLater(() -> {
                progressBarLoading.setProgress(newProgress);
                labelLoading.setText(text);
            });
        }
    }

    private boolean runningInJAR()
    {
        URL url = ControllerLoading.class.getResource("ControllerLoading.class");

        if (url != null)
        {
            return "jar".equals(url.getProtocol());
        }
        return false;
    }

    private long loadFromIDE()
    {
        try {
            Path dirPath = Paths.get(getClass().getResource(Settings.RESOURCES_PATH + "views/").toURI());

            try (Stream<Path> paths = Files.walk(dirPath, 1)) {
                return paths
                        .filter(Files::isRegularFile) // Filter to include only files
                        .count(); // Count the files

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return -1;
    }

    private long loadFromJAR()
    {
        try {
            String jarPath = ControllerLoading.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8.name());

            try (JarFile jar = new JarFile(decodedPath)){

                long fileCount = 0;
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements())
                {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    String prefix = Settings.RESOURCES_PATH.replaceFirst("/", "") + "views/";
                    if (name.startsWith(prefix)
                            && !name.equals(prefix)
                            && !name.startsWith(prefix + "extra/")
                            && name.endsWith(".fxml"))
                    {
                        fileCount++;
                    }
                }

                return fileCount;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return -1;
    }
}