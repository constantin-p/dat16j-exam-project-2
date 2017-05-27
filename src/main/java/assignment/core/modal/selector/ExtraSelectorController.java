package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.Extra;
import assignment.model.Motorhome;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class ExtraSelectorController extends SelectorBaseController {
    private static final String TITLE = "extra_select";

    private ObservableList<Extra> extraList = FXCollections.observableArrayList();

    @FXML
    private TableView<Extra> tableView;

    public ExtraSelectorController(ModalDispatcher modalDispatcher, Stage stage, boolean canCreate) {
        super(modalDispatcher, stage, canCreate);
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
        // Load extras
        List<Extra> extras = Extra.dbGetAll();
        extraList.clear();
        extras.forEach(entry -> {
            extraList.add(entry);
        });
    }
}
