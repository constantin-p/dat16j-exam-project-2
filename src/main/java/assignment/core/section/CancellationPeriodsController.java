package assignment.core.section;

import assignment.core.RootController;
import assignment.model.CancellationPeriod;
import assignment.model.Extra;
import assignment.model.Season;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class CancellationPeriodsController implements UISection {
    private static final String ACCESS_TYPE_NAME = "cancellation_periods";
    private static final String TEMPLATE_PATH = "templates/section/seasons.fxml";

    private RootController rootController;
    private ObservableList<CancellationPeriod> cancellationPeriodList = FXCollections.observableArrayList();

    @FXML
    private TableView<CancellationPeriod> tableView;

    public CancellationPeriodsController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<CancellationPeriod, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        TableColumn<CancellationPeriod, String> priceColumn = new TableColumn("Price");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().price.getValue().value.getValue() +
                        " / " + cellData.getValue().price.getValue().type.getValue().name.getValue())
        );

        TableColumn<CancellationPeriod, String> minimumPriceColumn = new TableColumn("Minimum price");
        minimumPriceColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty((cellData.getValue().minimumPrice.getValue() == null)
                ? "-"
                : cellData.getValue().minimumPrice.getValue().value.getValue() +
                    " / " + cellData.getValue().minimumPrice.getValue().type.getValue().name.getValue())
        );

        tableView.getColumns().addAll(nameColumn, priceColumn, minimumPriceColumn);
        tableView.setItems(cancellationPeriodList);

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

        List<CancellationPeriod> cancellationPeriods = CancellationPeriod.dbGetAll();
        cancellationPeriodList.clear();
        cancellationPeriods.forEach(entry -> {
            cancellationPeriodList.add(entry);
        });
    }
}
