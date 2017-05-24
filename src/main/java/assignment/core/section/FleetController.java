package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Account;
import assignment.model.AccountType;
import assignment.model.Fleet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class FleetController implements UISection {
    public static final String ACCESS_TYPE_NAME = "fleet";
    private static final String TEMPLATE_PATH = "templates/section/fleet.fxml";

    private RootController rootController;
    private ObservableList<Fleet> fleetList = FXCollections.observableArrayList();

    @FXML
    private TableView<Fleet> tableView;

    public FleetController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Fleet, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        TableColumn<Fleet, String> capacityColumn = new TableColumn("Capacity");
        capacityColumn.setCellValueFactory(cellData -> cellData.getValue().capacity.asString());
        tableView.getColumns().addAll(nameColumn, capacityColumn);
        tableView.setItems(fleetList);

        populateTableView();
    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

//    @FXML
//    public void handleAddAction(ActionEvent event) {
//        Fleet fleet = rootController.modalDispatcher.showCreateVehicleModal(null);
//        if (fleet != null) {
//            populateTableView();
//        }
//    }

    private void populateTableView() {
        // Load account types
        List<Fleet> fleet = Fleet.dbGetAll();
        fleetList.clear();
        fleet.forEach(entry -> {
            fleetList.add(entry);
        });
    }
}


