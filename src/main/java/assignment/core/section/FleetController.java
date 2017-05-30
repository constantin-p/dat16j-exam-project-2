package assignment.core.section;


import assignment.core.RootController;
import assignment.model.CleaningJob;
import assignment.model.Motorhome;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import assignment.util.ScheduleManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.time.LocalDate;
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
        capacityColumn.setCellValueFactory(cellData -> cellData.getValue().capacity.asString());
        capacityColumn.getStyleClass().add("align-center");

        TableColumn<Motorhome, String> priceColumn = new TableColumn("Base price");
        priceColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().price.getValue().value.getValue() +
                        " / " + cellData.getValue().price.getValue().type.getValue().name.getValue())
        );
        priceColumn.getStyleClass().add("align-center");

        TableColumn<Motorhome, String> statusColumn = new TableColumn("Status");
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(ScheduleManager.isMotorhomeFree(LocalDate.now(),
                        cellData.getValue().id) ? "..." : "Away")
        );
        statusColumn.getStyleClass().add("align-center");

        TableColumn<Motorhome, String> actionColumn = new TableColumn("Actions");
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().id));
        actionColumn.setCellFactory(getActionCellFactory());
        actionColumn.getStyleClass().add("align-center");

        tableView.getColumns().addAll(brandColumn, modelColumn, capacityColumn,
                priceColumn, statusColumn, actionColumn);
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
            CacheEngine.markForUpdate("fleet");
        }
    }

    /*
     *  Helpers
     */
    private void populateTableView() {

        CacheEngine.get("fleet", new DBOperation<>(() ->
            Motorhome.dbGetAll(), (List<Motorhome> motorhomes) -> {

            motorhomeList.clear();
            motorhomes.forEach(entry -> {
                motorhomeList.add(entry);
            });
        }));
    }

    private Callback<TableColumn<Motorhome, String>, TableCell<Motorhome, String>> getActionCellFactory() {
        return new Callback<TableColumn<Motorhome, String>, TableCell<Motorhome, String>>() {
            @Override
            public TableCell call( final TableColumn<Motorhome, String> param) {
                final TableCell<Motorhome, String> cell = new TableCell<Motorhome, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            Motorhome originalMotorhome = getTableView().getItems().get(getIndex());

                            Button done = new Button("Edit");
                            done.setOnAction((ActionEvent event) -> {
                                Motorhome motorhome = rootController.modalDispatcher
                                        .showEditMotorhomeModal(null, originalMotorhome);
                                if (motorhome != null) {
                                    CacheEngine.markForUpdate("fleet");
                                    CacheEngine.markForUpdate("orders");
                                }
                            });
                            setGraphic(done);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
    }
}


