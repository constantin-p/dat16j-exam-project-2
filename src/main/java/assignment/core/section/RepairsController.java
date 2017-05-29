package assignment.core.section;

import assignment.core.RootController;
import assignment.model.*;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RepairsController implements UISection {
    private static final String ACCESS_TYPE_NAME = "repairs";
    private static final String TEMPLATE_PATH = "templates/section/repairs.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private RootController rootController;
    private ObservableList<RepairJob> repairJobList = FXCollections.observableArrayList();

    @FXML
    private TableView<RepairJob> tableView;

    public RepairsController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<RepairJob, String> startColumn = new TableColumn("Starting from");
        startColumn.setCellValueFactory(cellData -> {
            return Bindings.createStringBinding(() ->
                            formatter.format(cellData.getValue().date.getValue()),
                    cellData.getValue().date);
        });

        TableColumn<RepairJob, String> motorhomeColumn = new TableColumn("Motorhome");

        TableColumn<RepairJob, String> brandColumn = new TableColumn("Brand");
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().motorhome.getValue().brand);

        TableColumn<RepairJob, String> modelColumn = new TableColumn("Model");
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().motorhome.getValue().model);
        motorhomeColumn.getColumns().addAll(brandColumn, modelColumn);

//        TableColumn<RepairJob, String> detailsColumn = new TableColumn("Details");
//        detailsColumn.setCellValueFactory(cellData -> cellData.getValue().details);

        TableColumn<RepairJob, String> actionColumn = new TableColumn("Actions");
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().id));
        actionColumn.setCellFactory(getActionCellFactory());
        actionColumn.getStyleClass().add("align-center");

        tableView.getColumns().addAll(startColumn, motorhomeColumn, actionColumn);
        tableView.setItems(repairJobList);

        populateTableView();
    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    /*
     *  Helpers
     */
    private void populateTableView() {

        CacheEngine.get("repairs", new DBOperation<>(() ->
            RepairJob.dbGetAll(), (List<RepairJob> repairJobs) -> {

            repairJobList.clear();
            repairJobs.forEach(entry -> {
                if (!entry.done.getValue()) {
                    // The order is not done
                    repairJobList.add(entry);
                }
            });
        }));
    }

    private Callback<TableColumn<RepairJob, String>, TableCell<RepairJob, String>> getActionCellFactory() {
        return new Callback<TableColumn<RepairJob, String>, TableCell<RepairJob, String>>() {
            @Override
            public TableCell call( final TableColumn<RepairJob, String> param) {
                final TableCell<RepairJob, String> cell = new TableCell<RepairJob, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            RepairJob repairJob = getTableView().getItems().get(getIndex());

                            if (!repairJob.date.getValue().isBefore(LocalDate.now())) {
                                Button done = new Button("Done");
                                done.setOnAction((ActionEvent event) -> {
                                    RepairJob.dbUpdate(repairJob.id, true);
                                    CacheEngine.markForUpdate("repairs");
                                });
                                setGraphic(done);
                                setText(null);
                            } else {
                                setGraphic(null);
                                setText("...");
                            }
                        }
                    }
                };
                return cell;
            }
        };
    }
}
