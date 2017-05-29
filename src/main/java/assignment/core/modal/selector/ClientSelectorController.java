package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.Client;
import assignment.model.Motorhome;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class ClientSelectorController extends SelectorBaseController {
    private static final String TITLE = "client_select";

    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    private FilteredList<Client> filteredData = new FilteredList<>(clientList, p -> true);
    private EntryValidator<Client> entryValidator;

    @FXML
    private TableView<Client> tableView;

    public ClientSelectorController(ModalDispatcher modalDispatcher, Stage stage,
                    boolean canCreate, EntryValidator<Client> entryValidator) {
        super(modalDispatcher, stage, canCreate);

        this.entryValidator = entryValidator;
    }

    @Override
    public void initialize() {
        super.initialize();

        TableColumn<Client, String> firstNameColumn = new TableColumn("First name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstName);

        TableColumn<Client, String> lastNameColumn = new TableColumn("Last name");
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastName);

        TableColumn<Client, String> emailColumn = new TableColumn("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().email);

        TableColumn<Client, String> dateOfBirthColumn = new TableColumn("Date of birth");
        dateOfBirthColumn.setCellValueFactory(cellData -> cellData.getValue().dateOfBirth.asString());

        tableView.getColumns().addAll(firstNameColumn, lastNameColumn, emailColumn, dateOfBirthColumn);

        populateTableView();
    }

    @Override
    public Client result() {
        Client selectedClient = tableView.getSelectionModel().getSelectedItem();
        if (super.isOKClicked && selectedClient != null) {
            return selectedClient;
        }
        return null;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @FXML
    protected void handleCreateAction(ActionEvent event) {
        Client client = modalDispatcher.showCreateClientModal(null);
        if (client != null) {
            CacheEngine.markForUpdate("clients");
        }
    }

    /*
     *  Helpers
     */
    private void populateTableView() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTableView(newValue);
        });

        SortedList<Client> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

        CacheEngine.get("clients", new DBOperation<>(() ->
            Client.dbGetAll(), (List<Client> clients) -> {

            clientList.clear();
            clients.forEach(entry -> {
                clientList.add(entry);
                filterTableView(searchField.getText());
            });
        }));
    }

    private void filterTableView(String searchValue) {
        filteredData.setPredicate(client -> {
            if (!entryValidator.isValid(client)) {
                return false;
            }

            // No search term
            if (searchValue == null || searchValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchValue.toLowerCase();

            if (client.firstName.getValue().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (client.lastName.getValue().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (client.email.getValue().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (client.dateOfBirth.getValue().toString().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
    }
}
