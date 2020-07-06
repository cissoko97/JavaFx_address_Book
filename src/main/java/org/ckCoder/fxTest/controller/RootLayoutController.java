package org.ckCoder.fxTest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import org.ckCoder.fxTest.Main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RootLayoutController implements Initializable {

    // Reference to the main application
    private Main mainApp;

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Creates an empty address book.
     */
    public void handleNew(ActionEvent actionEvent) {
        mainApp.getPeople().clear();
        mainApp.setPersonFilePath(null);
    }

    /**
     * Saves the file to the person file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    public void handleSave(ActionEvent actionEvent) {
        File personFile = mainApp.getPersonFilePath();
        if (personFile != null) {
            mainApp.savePeopleToFile(personFile);
        } else {
            handleSaveAs(actionEvent);
        }
    }

    public void handleSaveAs(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.savePeopleToFile(file);
        }
    }

    public void handleExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * Opens a FileChooser to let the user select an address book to load.
     */
    public void handleOpen(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadPeopleFromFile(file);
        }
    }
}
