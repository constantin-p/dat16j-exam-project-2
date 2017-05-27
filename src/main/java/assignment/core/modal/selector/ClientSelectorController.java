package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.Client;
import assignment.model.Motorhome;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class ClientSelectorController extends SelectorBaseController {
    private static final String TITLE = "client_select";

    private ObservableList<Client> clientList = FXCollections.observableArrayList();

    @FXML
    private TableView<Client> tableView;

    public ClientSelectorController(ModalDispatcher modalDispatcher, Stage stage, boolean canCreate) {
        super(modalDispatcher, stage, canCreate);
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
        tableView.setItems(clientList);

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
            populateTableView();
        }
    }

    /*
     *  Helpers
     */
    private void populateTableView() {
        // Load clients
        List<Client> clients = Client.dbGetAll();
        clientList.clear();
        clients.forEach(entry -> {
            clientList.add(entry);
        });
    }
}
