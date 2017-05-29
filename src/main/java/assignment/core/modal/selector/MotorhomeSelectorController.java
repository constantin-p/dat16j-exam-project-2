package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.Motorhome;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class MotorhomeSelectorController extends SelectorBaseController {
    private static final String TITLE = "motorhome_select";

    private ObservableList<Motorhome> motorhomeList = FXCollections.observableArrayList();
    private FilteredList<Motorhome> filteredData = new FilteredList<>(motorhomeList, p -> true);
    private EntryValidator<Motorhome> entryValidator;

    @FXML
    private TableView<Motorhome> tableView;

    public MotorhomeSelectorController(ModalDispatcher modalDispatcher, Stage stage,
                   boolean canCreate, EntryValidator<Motorhome> entryValidator) {
        super(modalDispatcher, stage, canCreate);

        this.entryValidator = entryValidator;
    }

    @Override
    public void initialize() {
        super.initialize();

        TableColumn<Motorhome, String> brandColumn = new TableColumn("Brand");
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().brand);

        TableColumn<Motorhome, String> modelColumn = new TableColumn("Model");
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().model);

        TableColumn<Motorhome, String> capacityColumn = new TableColumn("Capacity");
        capacityColumn.setCellValueFactory(cellData -> cellData.getValue().capacity.asString());

        TableColumn<Motorhome, String> priceColumn = new TableColumn("Base price");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().price.getValue().value.getValue() +
                        " / " + cellData.getValue().price.getValue().type.getValue().name.getValue())
        );
        tableView.getColumns().addAll(brandColumn, modelColumn, capacityColumn, priceColumn);

        populateTableView();
    }

    @Override
    public Motorhome result() {
        Motorhome selectedMotorhome = tableView.getSelectionModel().getSelectedItem();
        if (super.isOKClicked && selectedMotorhome != null) {
            return selectedMotorhome;
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

        SortedList<Motorhome> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);


        CacheEngine.get("fleet", new DBOperation<>(() ->
            Motorhome.dbGetAll(), (List<Motorhome> motorhomes) -> {

            motorhomeList.clear();
            motorhomes.forEach(entry -> {
                motorhomeList.add(entry);
            });
            filterTableView(searchField.getText());
        }));
    }


    private void filterTableView(String searchValue) {
        filteredData.setPredicate(motorhome -> {
            if (!entryValidator.isValid(motorhome)) {
                return false;
            }

            // No search term
            if (searchValue == null || searchValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchValue.toLowerCase();

            if (motorhome.brand.getValue().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (motorhome.model.getValue().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (motorhome.price.getValue().value.toString().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
    }
}
