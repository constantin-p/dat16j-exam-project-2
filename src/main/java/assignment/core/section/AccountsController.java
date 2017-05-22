package assignment.core.section;


import assignment.core.RootController;
import assignment.model.Account;
import assignment.model.AccountType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AccountsController implements UISection {
    public static final String ACCESS_TYPE_NAME = "accounts";
    private static final String TEMPLATE_PATH = "templates/section/accounts.fxml";

    private RootController rootController;
    private ObservableList<Account> accountList = FXCollections.observableArrayList();

    @FXML
    private TableView<Account> tableView;

    public AccountsController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Account, String> usernameColumn = new TableColumn("Username");
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().username);

        TableColumn<Account, String> accountTypeColumn = new TableColumn("Account type");
        accountTypeColumn.setCellValueFactory(cellData -> cellData.getValue().type.getValue().name);
        tableView.getColumns().addAll(usernameColumn, accountTypeColumn);
    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    @FXML
    public void handleAddAction(ActionEvent event) {
        Account account = rootController.modalDispatcher.showCreateAccountModal(null);
//        Account account = rootController.modalDispatcher.showCreateAccountModal(null);
//        if (accountType != null) {
//            // Load account types
//            List<AccountType> accountTypes = AccountType.dbGetAll();
//            accountTypeMap.clear();
//            accountTypes.forEach(entry -> {
//                accountTypeMap.add(entry);
//            });
//        }
    }
}
