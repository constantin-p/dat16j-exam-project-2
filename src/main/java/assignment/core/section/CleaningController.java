package assignment.core.section;

import assignment.core.RootController;
import assignment.model.CleaningJob;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import javafx.beans.binding.Bindings;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CleaningController implements UISection {
    private static final String ACCESS_TYPE_NAME = "cleaning";
    private static final String TEMPLATE_PATH = "templates/section/cleaning.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private RootController rootController;
    private ObservableList<CleaningJob> cleaningJobList = FXCollections.observableArrayList();

    @FXML
    private TableView<CleaningJob> tableView;

    public CleaningController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<CleaningJob, String> startColumn = new TableColumn("Starting from");
        startColumn.setCellValueFactory(cellData -> {
            return Bindings.createStringBinding(() ->
                formatter.format(cellData.getValue().date.getValue()),
                    cellData.getValue().date);
        });
        startColumn.getStyleClass().add("align-center");

        TableColumn<CleaningJob, String> motorhomeColumn = new TableColumn("Motorhome");

        TableColumn<CleaningJob, String> brandColumn = new TableColumn("Brand");
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().order.getValue()
                .motorhome.getValue().brand);

        TableColumn<CleaningJob, String> modelColumn = new TableColumn("Model");
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().order.getValue()
                .motorhome.getValue().model);
        motorhomeColumn.getColumns().addAll(brandColumn, modelColumn);

        TableColumn<CleaningJob, String> actionColumn = new TableColumn("Actions");
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().id));
        actionColumn.setCellFactory(getActionCellFactory());
        actionColumn.getStyleClass().add("align-center");

        tableView.getColumns().addAll(startColumn, motorhomeColumn, actionColumn);
        tableView.setItems(cleaningJobList);

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

        CacheEngine.get("cleaning", new DBOperation<>(() ->
            CleaningJob.dbGetAll(), (List<CleaningJob> cleaningJobs) -> {

            cleaningJobList.clear();
            cleaningJobs.forEach(entry -> {
                if (!entry.order.getValue().isCancelled) {
                    // The order is not canceled
                    if (!entry.done.getValue()) {
                        // The order is not done
                        cleaningJobList.add(entry);
                    }
                }
            });
        }));
    }

    private Callback<TableColumn<CleaningJob, String>, TableCell<CleaningJob, String>> getActionCellFactory() {
        return new Callback<TableColumn<CleaningJob, String>, TableCell<CleaningJob, String>>() {
            @Override
            public TableCell call( final TableColumn<CleaningJob, String> param) {
                final TableCell<CleaningJob, String> cell = new TableCell<CleaningJob, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            CleaningJob cleaningJob = getTableView().getItems().get(getIndex());

                            if (!cleaningJob.date.getValue().isAfter(LocalDate.now())) {
                                Button done = new Button("Done");
                                done.setOnAction((ActionEvent event) -> {
                                    CleaningJob.dbUpdate(cleaningJob.id, true);
                                    CacheEngine.markForUpdate("cleaning");
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
