package assignment.core.modal;


import assignment.model.AccessType;
import assignment.model.AccountType;
import assignment.util.ValidationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class AccountTypeFormController extends ModalBaseController {
    private static final String TITLE_CREATE = "account_type_create";
    private static final String TITLE_EDIT = "account_type_create";
    private static final String TEMPLATE_PATH = "templates/modal/account_type.fxml";

    private AccountType accountType;
    private ObservableList<Map.Entry<AccessType, BooleanProperty>> accessMap = FXCollections.observableArrayList();
    private boolean create;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField nameTextField;
    private BooleanProperty isNameValid = new SimpleBooleanProperty(false);

    @FXML
    private TableView<Map.Entry<AccessType, BooleanProperty>> tableView;

    public AccountTypeFormController(ModalDispatcher modalDispatcher, Stage stage, boolean create,
                                     AccountType accountType) {
        super(modalDispatcher, stage);
        this.accountType = accountType;
        this.create = create;
    }

    @Override
    public void initialize() {
        super.initialize();

        super.isDisabled.bind(isNameValid.not());

        nameTextField.textProperty().bindBidirectional(accountType.name);
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isNameValid.set(ValidationHandler.showError(errorLabel,
                ValidationHandler.validateAccountTypeName(newValue)));
        });

        // Set the permission's table definition
        TableColumn<Map.Entry<AccessType, BooleanProperty>, Boolean> accessColumn = new TableColumn("");
        accessColumn.setCellValueFactory(cellData -> cellData.getValue().getValue());
        accessColumn.setCellFactory(column -> new CheckBoxTableCell());
        accessColumn.setEditable(true);

        TableColumn<Map.Entry<AccessType, BooleanProperty>, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getKey().name);

        tableView.getColumns().addAll(accessColumn, nameColumn);
        tableView.setItems(accessMap);
        tableView.setEditable(true);
        tableView.setSelectionModel(null);

        // Load access types
        List<AccessType> accessTypes = AccessType.dbGetAll();
        accessTypes.forEach(accessType -> {
            accessMap.add(new AbstractMap.SimpleEntry<>(accessType,
                    new SimpleBooleanProperty(false)));
        });
    }

    @Override
    public void handleOKAction(ActionEvent event) {
        if (create) {
            boolean success = ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateAccountTypeDBOperation(AccountType.dbInsert(accountType)));

            // Add the permissions set
            if (success) {
                accountType = AccountType.dbGetByName(accountType.name.getValue());

                accessMap.forEach(entry -> {
                    if (entry.getValue().getValue() == true) {
                        accountType.addPermission(entry.getKey());
                    }
                });
                super.handleOKAction(event);
            }
        } else {

        }
    }

    @Override
    public AccountType result() {
        if (super.isOKClicked && !super.isDisabled.getValue()) {
            return accountType;
        }
        return null;
    }


    @Override
    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    @Override
    public String getTitle() {
        return create
            ?   TITLE_CREATE
            :   TITLE_EDIT;
    }
}
