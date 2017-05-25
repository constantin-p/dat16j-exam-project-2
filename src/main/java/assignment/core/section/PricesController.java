package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Account;
import assignment.model.Price;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class PricesController implements UISection {
    private static final String ACCESS_TYPE_NAME = "prices";
    private static final String TEMPLATE_PATH = "templates/section/prices.fxml";

    private RootController rootController;
    private ObservableList<Price> priceList = FXCollections.observableArrayList();

    @FXML
    private TableView<Price> tableView;

    public PricesController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Price, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        TableColumn<Price, String> valueColumn = new TableColumn("Amount");
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().value);

        TableColumn<Price, String> priceTypeColumn = new TableColumn("Price type");
        priceTypeColumn.setCellValueFactory(cellData -> cellData.getValue().type.getValue().name);
        tableView.getColumns().addAll(nameColumn, valueColumn, priceTypeColumn);
        tableView.setItems(priceList);

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
        Price price = rootController.modalDispatcher.showCreatePriceModal(null);
        if (price != null) {
            populateTableView();
        }
    }

    /*
     *  Helpers
     */
    private void populateTableView() {
        // Load prices
        List<Price> prices = Price.dbGetAll();
        priceList.clear();
        prices.forEach(entry -> {
            priceList.add(entry);
        });
    }
}
