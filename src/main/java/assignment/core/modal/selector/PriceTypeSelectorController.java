package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.AccountType;
import assignment.model.PriceType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class PriceTypeSelectorController extends SelectorBaseController {
    private static final String TITLE = "price_type_select";

    private ObservableList<PriceType> priceTypeList = FXCollections.observableArrayList();

    @FXML
    private TableView<PriceType> tableView;

    public PriceTypeSelectorController(ModalDispatcher modalDispatcher, Stage stage, boolean canCreate) {
        super(modalDispatcher, stage, canCreate);

        populateTableView();
    }

    @Override
    public void initialize() {
        super.initialize();

        TableColumn<PriceType, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        tableView.getColumns().addAll(nameColumn);
        tableView.setItems(priceTypeList);
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
        // Load price types
        List<PriceType> priceTypes = PriceType.dbGetAll();
        priceTypeList.clear();
        priceTypes.forEach(entry -> {
            priceTypeList.add(entry);
        });
    }
}
