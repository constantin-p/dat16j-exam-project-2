package assignment.core.section;

import assignment.core.RootController;
import assignment.model.CleaningJob;
import assignment.model.Client;
import assignment.model.Motorhome;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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

        TableColumn<CleaningJob, String> motorhomeColumn = new TableColumn("Motorhome");

        TableColumn<CleaningJob, String> brandColumn = new TableColumn("Brand");
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().order.getValue()
                .motorhome.getValue().brand);

        TableColumn<CleaningJob, String> modelColumn = new TableColumn("Model");
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().order.getValue()
                .motorhome.getValue().model);
        motorhomeColumn.getColumns().addAll(brandColumn, modelColumn);


        tableView.getColumns().addAll(startColumn, motorhomeColumn);
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
                    cleaningJobList.add(entry);
                }
            });
        }));
    }
}
