package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.Client;
import assignment.model.Extra;
import assignment.model.Motorhome;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class ExtraSelectorController extends SelectorBaseController {
    private static final String TITLE = "Select extra";

    private ObservableList<Extra> extraList = FXCollections.observableArrayList();
    private FilteredList<Extra> filteredData = new FilteredList<>(extraList, p -> true);
    private EntryValidator<Extra> entryValidator;

    @FXML
    private TableView<Extra> tableView;

    public ExtraSelectorController(ModalDispatcher modalDispatcher, Stage stage,
               boolean canCreate, EntryValidator<Extra> entryValidator) {
        super(modalDispatcher, stage, canCreate);

        this.entryValidator = entryValidator;
    }

    @Override
    public void initialize() {
        super.initialize();

        TableColumn<Extra, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        TableColumn<Extra, String> priceColumn = new TableColumn("Price");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().price.getValue().value.getValue() +
                        " / " + cellData.getValue().price.getValue().type.getValue().name.getValue())
        );
        priceColumn.getStyleClass().add("align-center");

        tableView.getColumns().addAll(nameColumn, priceColumn);
        tableView.setItems(extraList);

        populateTableView();
    }

    @Override
    public Extra result() {
        Extra selectedExtra = tableView.getSelectionModel().getSelectedItem();
        if (super.isOKClicked && selectedExtra != null) {
            return selectedExtra;
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

        SortedList<Extra> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
        CacheEngine.get("extras", new DBOperation<>(() ->
                Extra.dbGetAll(), (List<Extra> extras) -> {

            extraList.clear();
            extras.forEach(entry -> {
                extraList.add(entry);
                filterTableView(searchField.getText());
            });
        }));
    }

    private void filterTableView(String searchValue) {
        filteredData.setPredicate(extra -> {
            if (!entryValidator.isValid(extra)) {
                return false;
            }

            // No search term
            if (searchValue == null || searchValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchValue.toLowerCase();

            if (extra.name.getValue().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (extra.price.getValue().value.toString()
                    .toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
    }
}
