package assignment.core.section;


import assignment.core.RootController;
import assignment.model.Extra;
import assignment.model.Motorhome;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class FleetController implements UISection {
    private static final String ACCESS_TYPE_NAME = "fleet";
    private static final String TEMPLATE_PATH = "templates/section/fleet.fxml";

    private RootController rootController;
    private ObservableList<Motorhome> motorhomeList = FXCollections.observableArrayList();

    @FXML
    private TableView<Motorhome> tableView;

    public FleetController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Motorhome, String> brandColumn = new TableColumn("Brand");
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().brand);

        TableColumn<Motorhome, String> modelColumn = new TableColumn("Model");
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().model);

        TableColumn<Motorhome, String> capacityColumn = new TableColumn("Capacity");
        capacityColumn.setCellValueFactory(cellData -> cellData.getValue().capacity);

        TableColumn<Motorhome, String> priceColumn = new TableColumn("Base price");
        priceColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().price.getValue().value.getValue() +
                        " / " + cellData.getValue().price.getValue().type.getValue().name.getValue())
        );
        tableView.getColumns().addAll(brandColumn, modelColumn, capacityColumn, priceColumn);
        tableView.setItems(motorhomeList);

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
        Motorhome motorhome = rootController.modalDispatcher.showCreateMotorhomeModal(null);
        if (motorhome != null) {
            populateTableView();
        }
    }

    /*
     *  Helpers
     */
    private void populateTableView() {

        List<Motorhome> motorhome = Motorhome.dbGetAll();
        motorhomeList.clear();
        motorhome.forEach(entry -> {
            motorhomeList.add(entry);
        });
    }
}


