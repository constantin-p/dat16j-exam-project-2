package assignment.core.modal.selector;


import assignment.core.modal.ModalDispatcher;
import assignment.model.AccountType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class AccountTypeSelectorController extends SelectorBaseController {
    private static final String TITLE = "account_type_select";

    private ObservableList<AccountType> accountTypeList = FXCollections.observableArrayList();

    @FXML
    private TableView<AccountType> tableView;

    public AccountTypeSelectorController(ModalDispatcher modalDispatcher, Stage stage, boolean canCreate) {
        super(modalDispatcher, stage, canCreate);

        populateTableView();
    }

    @Override
    public void initialize() {
        super.initialize();

        TableColumn<AccountType, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        tableView.getColumns().addAll(nameColumn);
        tableView.setItems(accountTypeList);
    }

    @Override
    public AccountType result() {
        AccountType selectedAccountType = tableView.getSelectionModel().getSelectedItem();
        if (super.isOKClicked && selectedAccountType != null) {
            return selectedAccountType;
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
        // Load account types
        List<AccountType> accountTypes = AccountType.dbGetAll();
        accountTypeList.clear();
        accountTypes.forEach(entry -> {
            accountTypeList.add(entry);
        });
    }
}
