package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.Price;
import assignment.model.PriceType;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
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

public class PriceTypeSelectorController extends SelectorBaseController {
    private static final String TITLE = "Select price type";

    private ObservableList<PriceType> priceTypeList = FXCollections.observableArrayList();
    private FilteredList<PriceType> filteredData = new FilteredList<>(priceTypeList, p -> true);
    private EntryValidator<PriceType> entryValidator;

    @FXML
    private TableView<PriceType> tableView;

    public PriceTypeSelectorController(ModalDispatcher modalDispatcher, Stage stage,
               boolean canCreate, EntryValidator<PriceType> entryValidator) {
        super(modalDispatcher, stage, canCreate);

        this.entryValidator = entryValidator;
    }

    @Override
    public void initialize() {
        super.initialize();

        TableColumn<PriceType, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        nameColumn.getStyleClass().add("align-center");
        tableView.getColumns().addAll(nameColumn);

        populateTableView();
    }

    @Override
    public PriceType result() {
        PriceType selectedPriceType = tableView.getSelectionModel().getSelectedItem();
        if (super.isOKClicked && selectedPriceType != null) {
            return selectedPriceType;
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
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTableView(newValue);
        });

        SortedList<PriceType> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);


        CacheEngine.get("price_types", new DBOperation<>(() ->
                PriceType.dbGetAll(), (List<PriceType> priceTypes) -> {

            priceTypeList.clear();
            priceTypes.forEach(entry -> {
                priceTypeList.add(entry);
            });
            filterTableView(searchField.getText());
        }));
    }

    private void filterTableView(String searchValue) {
        filteredData.setPredicate(priceType -> {
            if (!entryValidator.isValid(priceType)) {
                return false;
            }

            // No search term
            if (searchValue == null || searchValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchValue.toLowerCase();

            if (priceType.name.getValue().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
    }
}
