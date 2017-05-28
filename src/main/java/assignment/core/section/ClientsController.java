package assignment.core.section;


import assignment.core.RootController;
import assignment.model.Client;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientsController implements UISection {
    private static final String ACCESS_TYPE_NAME = "clients";
    private static final String TEMPLATE_PATH = "templates/section/clients.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private RootController rootController;
    private ObservableList<Client> clientList = FXCollections.observableArrayList();

    @FXML
    private TableView<Client> tableView;

    public ClientsController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Client, String> firstNameColumn = new TableColumn("First name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstName);

        TableColumn<Client, String> lastNameColumn = new TableColumn("Last name");
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastName);

        TableColumn<Client, String> emailColumn = new TableColumn("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().email);

        TableColumn<Client, String> dateOfBirthColumn = new TableColumn("Date of birth");
        dateOfBirthColumn.setCellValueFactory(cellData -> {
            return Bindings.createStringBinding(() ->
                formatter.format(cellData.getValue().dateOfBirth.getValue()),
                    cellData.getValue().dateOfBirth);
        });

        tableView.getColumns().addAll(firstNameColumn, lastNameColumn, emailColumn, dateOfBirthColumn);
        tableView.setItems(clientList);

        populateTableView();
    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    @FXML
    public void handleAddAction(ActionEvent event) {
        Client client = rootController.modalDispatcher.showCreateClientModal(null);
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