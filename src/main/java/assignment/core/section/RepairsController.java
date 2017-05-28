package assignment.core.section;

import assignment.core.RootController;
import assignment.model.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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

        TableColumn<RepairJob, String> detailsColumn = new TableColumn("Details");
        detailsColumn.setCellValueFactory(cellData -> cellData.getValue().details);


        tableView.getColumns().addAll(startColumn, motorhomeColumn, detailsColumn);
        tableView.setItems(repairJobList);
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
        // Load repair jobs
        List<RepairJob> repairJobs = RepairJob.dbGetAll();
        repairJobList.clear();
        repairJobs.forEach(entry -> {
            repairJobList.add(entry);
        });
    }
}
