package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.AccountType;
import assignment.model.Price;
import assignment.model.PriceType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class PriceSelectorController extends SelectorBaseController {
    private static final String TITLE = "price_type_select";

    private ObservableList<Price> priceList = FXCollections.observableArrayList();

    @FXML
    private TableView<Price> tableView;

    public PriceSelectorController(ModalDispatcher modalDispatcher, Stage stage, boolean canCreate) {
        super(modalDispatcher, stage, canCreate);
    }

    @Override
    public void initialize() {
        super.initialize();

        TableColumn<Price, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        TableColumn<Price, String> valueColumn = new TableColumn("Amount");
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().value);
        tableView.getColumns().addAll(nameColumn, valueColumn);
        tableView.setItems(priceList);

        populateTableView();
    }

    @Override
    public Price result() {
        Price selectedPrice = tableView.getSelectionModel().getSelectedItem();
        if (super.isOKClicked && selectedPrice != null) {
            return selectedPrice;
        }
        return null;
    }

    @Override
    public String getTitle() {
        return TITLE;
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
