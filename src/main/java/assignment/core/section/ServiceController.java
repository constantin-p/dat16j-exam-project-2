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

public class ServiceController implements UISection {
    private static final String ACCESS_TYPE_NAME = "service";
    private static final String TEMPLATE_PATH = "templates/section/service.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private RootController rootController;
    private ObservableList<ServiceJob> serviceJobList = FXCollections.observableArrayList();

    @FXML
    private TableView<ServiceJob> tableView;

    public ServiceController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<ServiceJob, String> startColumn = new TableColumn("Starting from");
        startColumn.setCellValueFactory(cellData -> {
            return Bindings.createStringBinding(() ->
                            formatter.format(cellData.getValue().date.getValue()),
                    cellData.getValue().date);
        });
        startColumn.getStyleClass().add("align-center");

        TableColumn<ServiceJob, String> motorhomeColumn = new TableColumn("Motorhome");

        TableColumn<ServiceJob, String> brandColumn = new TableColumn("Brand");
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().order.getValue()
                .motorhome.getValue().brand);

        TableColumn<ServiceJob, String> modelColumn = new TableColumn("Model");
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().order.getValue()
                .motorhome.getValue().model);
        motorhomeColumn.getColumns().addAll(brandColumn, modelColumn);

        TableColumn<ServiceJob, String> actionColumn = new TableColumn("Actions");
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().id));
        actionColumn.setCellFactory(getActionCellFactory());
        actionColumn.getStyleClass().add("align-center");

        tableView.getColumns().addAll(startColumn, motorhomeColumn, actionColumn);
        tableView.setItems(serviceJobList);

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

        CacheEngine.get("service", new DBOperation<>(() ->
            ServiceJob.dbGetAll(), (List<ServiceJob> serviceJobs) -> {

            serviceJobList.clear();
            serviceJobs.forEach(entry -> {
                if (!entry.order.getValue().isCancelled) {
                    if (!entry.order.getValue().isCancelled) {
                        // The order is not canceled
                        if (!entry.done.getValue()) {
                            // The order is not done
                            serviceJobList.add(entry);
                        }
                    }
                }
            });
        }));
    }

    private Callback<TableColumn<ServiceJob, String>, TableCell<ServiceJob, String>> getActionCellFactory() {
        return new Callback<TableColumn<ServiceJob, String>, TableCell<ServiceJob, String>>() {
            @Override
            public TableCell call( final TableColumn<ServiceJob, String> param) {
                final TableCell<ServiceJob, String> cell = new TableCell<ServiceJob, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            ServiceJob serviceJob = getTableView().getItems().get(getIndex());

                             if (!serviceJob.date.getValue().isAfter(LocalDate.now())) {
                                 Button done = new Button("Done");
                                 Button reapair = new Button("Mark for repairs");
                                 done.setOnAction((ActionEvent event) -> {
                                    ServiceJob.dbUpdate(serviceJob.id, true);
                                    CacheEngine.markForUpdate("service");
                                 });
                                 reapair.setOnAction((ActionEvent event) -> {
                                     RepairJob.dbInsert(new RepairJob(null,
                                         serviceJob.order.getValue().motorhome.getValue(),
                                         LocalDate.now(), false, ""));

                                     ServiceJob.dbUpdate(serviceJob.id, true);
                                     CacheEngine.markForUpdate("repairs");
                                     CacheEngine.markForUpdate("service");
                                 });
                                 HBox buttons = new HBox(6);
                                 buttons.setAlignment(Pos.CENTER);
                                 buttons.getChildren().addAll(done, reapair);
                                 setGraphic(buttons);
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
