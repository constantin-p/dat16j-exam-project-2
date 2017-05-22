package assignment.core.modal;


import assignment.model.Account;
import assignment.model.AccountType;
import assignment.util.ValidationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountFormController extends ModalBaseController {
    private static final String TITLE_CREATE = "account_create";
    private static final String TITLE_EDIT = "account_create";
    private static final String TEMPLATE_PATH = "templates/modal/account.fxml";

    private Account account;
    private boolean create;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField usernameTextField;
    private BooleanProperty isUsernameValid = new SimpleBooleanProperty(false);

    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private PasswordField repeatPasswordPasswordField;
    private BooleanProperty isPasswordValid = new SimpleBooleanProperty(false);

    public AccountFormController(ModalDispatcher modalDispatcher, Stage stage, boolean create,
                                     Account account) {
        super(modalDispatcher, stage);
        this.account = account;
        this.create = create;
    }

    @Override
    public void initialize() {
        super.initialize();

        super.isDisabled.bind(
            isUsernameValid.not().or(
                isPasswordValid.not()
            )
        );


        usernameTextField.textProperty().bindBidirectional(account.username);
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isUsernameValid.set(ValidationHandler.showError(errorLabel,
                ValidationHandler.validateAccountUsername(newValue)));
        });

        passwordPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            isPasswordValid.set(ValidationHandler.showError(errorLabel,
                ValidationHandler.validateAccountPassword(passwordPasswordField.getText(), newValue)));
        });

        repeatPasswordPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            isPasswordValid.set(ValidationHandler.showError(errorLabel,
                ValidationHandler.validateAccountPassword(passwordPasswordField.getText(), newValue)));
        });
    }

    @Override
    public void handleOKAction(ActionEvent event) {
        if (create) {

        }
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
