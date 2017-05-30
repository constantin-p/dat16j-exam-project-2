package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.Client;
import assignment.model.Motorhome;
import assignment.model.Price;
import assignment.model.PriceType;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class PriceSelectorController extends SelectorBaseController {
    private static final String TITLE = "Select price";

    private ObservableList<Price> priceList = FXCollections.observableArrayList();
    private FilteredList<Price> filteredData = new FilteredList<>(priceList, p -> true);
    private EntryValidator<Price> entryValidator;

    @FXML
    private TableView<Price> tableView;

    public PriceSelectorController(ModalDispatcher modalDispatcher, Stage stage,
                                   boolean canCreate, EntryValidator<Price> entryValidator) {
        super(modalDispatcher, stage, canCreate);

        this.entryValidator = entryValidator;
    }

    @Override
    public void initialize() {
        super.initialize();

        TableColumn<Price, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        TableColumn<Price, String> valueColumn = new TableColumn("Amount");
        valueColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().value.getValue() +
                " / " + cellData.getValue().type.getValue().name.getValue()));


        valueColumn.getStyleClass().add("align-center");
        tableView.getColumns().addAll(nameColumn, valueColumn);

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

    @FXML
    protected void handleCreateAction(ActionEvent event) {
        Price price = modalDispatcher.showCreatePriceModal(super.stage);
        if (price != null) {
            CacheEngine.markForUpdate("prices");
        }
    }

    /*
     *  Helpers
     */
    private void populateTableView() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTableView(newValue);
        });

        SortedList<Price> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);


        CacheEngine.get("prices", new DBOperation<>(() ->
                Price.dbGetAll(), (List<Price> prices) -> {
            priceList.clear();
            prices.forEach(entry -> {
                priceList.add(entry);
            });
            filterTableView(searchField.getText());
        }));
    }

    private void filterTableView(String searchValue) {
        filteredData.setPredicate(price -> {
            if (!entryValidator.isValid(price)) {
                return false;
            }

            // No search term
            if (searchValue == null || searchValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchValue.toLowerCase();

            if (price.name.getValue().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (price.value.toString().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
    }
}
