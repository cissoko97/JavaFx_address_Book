package org.ckCoder.fxTest;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ckCoder.fxTest.controller.PersonEditcontroller;
import org.ckCoder.fxTest.controller.PersonOverviewController;
import org.ckCoder.fxTest.controller.RootLayoutController;
import org.ckCoder.fxTest.model.Person;
import org.ckCoder.fxTest.model.PersonListWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Main extends Application {

    private ObservableList<Person> people = FXCollections.observableArrayList();
    private Stage primaryStage;
    private BorderPane rootLayout;


    public Main() {
        this.people.add(new Person("Hans", "Muster"));
        this.people.add(new Person("Ruth", "Mueller"));
        this.people.add(new Person("Heinz", "Kurz"));
        this.people.add(new Person("Cornelia", "Meier"));
        this.people.add(new Person("Werner", "Meyer"));
        this.people.add(new Person("Lydia", "Kunz"));
        this.people.add(new Person("Anna", "Best"));
        this.people.add(new Person("Stefan", "Meier"));
        this.people.add(new Person("Martin", "Mueller"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        // init the array af person with data
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Address App");

        initRootLayout();

        showPersonOverview();
    }

    public void initRootLayout() throws IOException {
        // load the Rootlayout from view package
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/view/RootLayout.fxml"));
        this.rootLayout = loader.load();

        this.primaryStage.setScene(new Scene(this.rootLayout));

        RootLayoutController controller = loader.getController();
        controller.setMainApp(this);

        this.primaryStage.show();

        // Try to load last opened person file.
        File file = getPersonFilePath();
        if (file != null) {
            loadPeopleFromFile(file);
        }
    }

    public void showPersonOverview() throws IOException {
        //Load the PersonOverView
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/view/PersonOverview.fxml"));
        AnchorPane personAnchorPane = loader.load();

        //Set PersonOverview at the center of the rootlayout
        this.rootLayout.setCenter(personAnchorPane);

        // give the controller to acces to the main App
        PersonOverviewController personController = loader.getController();
        personController.setMainApp(this);
    }

    /**
     * Returns the main stage.
     *
     * @return Stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<Person> getPeople() {
        return this.people;
    }

    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/view/PersonEdit.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PersonEditcontroller controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("AddressApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("AddressApp");
        }
    }

    /**
     * Loads person data from the specified file. The current person data will
     * be replaced.
     *
     * @param file
     */
    public void loadPeopleFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(PersonListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

            people.clear();
            people.addAll(wrapper.getPersons());

            // Save the file path to the registry.
            setPersonFilePath(file);

        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Saves the current person data to the specified file.
     *
     * @param file
     */
    public void savePeopleToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our person data.
            PersonListWrapper wrapper = new PersonListWrapper();
            wrapper.setPersons(people);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            setPersonFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
}
