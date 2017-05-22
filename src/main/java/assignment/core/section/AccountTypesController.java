package assignment.core.section;

import assignment.model.AccessType;
import assignment.model.AccountType;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class AccountTypesController implements UISection {
    private static final String ACCESS_TYPE_NAME = "account_types";
    private static final String TEMPLATE_PATH = "templates/section/account_types.fxml";

    @FXML
    private TableView<AccountType> tableView;

    public AccountTypesController() {

    }

    @FXML
    private void initialize() {
        TableColumn<AccountType, String> nameColumn = new TableColumn("Name");
        TableColumn permissionsColumn = new TableColumn("Permissions");
        tableView.getColumns().addAll(nameColumn, permissionsColumn);

        List<AccessType> accessTypes = AccessType.dbGetAll();
        accessTypes.forEach(accessType -> {
            TableColumn<AccessType, String> accessTypeColumn = new TableColumn(accessType.name.getValue());
            permissionsColumn.getColumns().add(accessTypeColumn);
        });
    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }
}
