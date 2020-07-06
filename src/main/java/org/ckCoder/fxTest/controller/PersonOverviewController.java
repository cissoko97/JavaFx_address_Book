package org.ckCoder.fxTest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.ckCoder.fxTest.Main;
import org.ckCoder.fxTest.model.Person;
import org.ckCoder.fxTest.util.DateUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class PersonOverviewController implements Initializable {

    ObservableList<Person> people = FXCollections.observableArrayList();
    public TableView personTable;
    public TableColumn<Person, String> firstNameColumn;
    public TableColumn<Person, String> lastNameColumn;

    public Label birthDayLabel;
    public Label streetLabel;
    public Label cityLabel;
    public Label firstNameLabel;
    public Label lastNameLabel;

    private Main main;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PersonOverviewController() {
    }

    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader loader = new FXMLLoader();
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        // Clear person details.
        showPersonDetails(null);

        // Listen for selection changes and show the person details when changed.
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails((Person) newValue));
    }


    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setMainApp(Main main) {
        this.main = main;
        // Add observable list data to the table
        personTable.setItems(main.getPeople());
    }

    private void showPersonDetails(Person person) {
        if (person != null) {
            setTextFields(person);
        } else {
            clearTextFields();
        }
    }

    private void setTextFields(Person person) {
        birthDayLabel.setText(DateUtil.format(person.getBirthday()));
        streetLabel.setText(person.getStreet());
        cityLabel.setText(person.getCity());
        firstNameLabel.setText(person.getFirstName());
        lastNameLabel.setText(person.getLastName());
    }

    private void clearTextFields() {
        birthDayLabel.setText("");
        streetLabel.setText("");
        cityLabel.setText("");
        firstNameLabel.setText("");
        lastNameLabel.setText("");
    }

    public void handleDeletePerson(ActionEvent actionEvent) {
        int selectedIndex = this.personTable.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0)
            personTable.getItems().remove(selectedIndex);
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }

    public void handleNewPerson(ActionEvent actionEvent) {
        Person tempPerson = new Person();
        boolean okClicked = main.showPersonEditDialog(tempPerson);
        if (okClicked) {
            main.getPeople().add(tempPerson);
        }
    }

    public void handleEditPerson(ActionEvent actionEvent) {
        Person selectedPerson = (Person) personTable.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            boolean okClicked = main.showPersonEditDialog(selectedPerson);
            if (okClicked) {
                showPersonDetails(selectedPerson);
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }
}
