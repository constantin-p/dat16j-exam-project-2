package assignment.core.section;

import assignment.core.RootController;
import assignment.model.CleaningJob;
import assignment.model.Client;
import assignment.model.Motorhome;
import assignment.model.ServiceJob;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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

        TableColumn<ServiceJob, String> motorhomeColumn = new TableColumn("Motorhome");

        TableColumn<ServiceJob, String> brandColumn = new TableColumn("Brand");
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().order.getValue()
                .motorhome.getValue().brand);

        TableColumn<ServiceJob, String> modelColumn = new TableColumn("Model");
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().order.getValue()
                .motorhome.getValue().model);
        motorhomeColumn.getColumns().addAll(brandColumn, modelColumn);


        tableView.getColumns().addAll(startColumn, motorhomeColumn);
        tableView.setItems(serviceJobList);
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
        // Load service jobs
        List<ServiceJob> serviceJobs = ServiceJob.dbGetAll();
        serviceJobList.clear();
        serviceJobs.forEach(entry -> {
            serviceJobList.add(entry);
        });
    }
}
