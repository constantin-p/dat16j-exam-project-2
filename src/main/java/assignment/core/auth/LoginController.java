package assignment.core.auth;


import assignment.util.Response;
import assignment.util.ValidationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private AuthManager authManager;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField usernameTextField;
    private BooleanProperty isUsernameValid = new SimpleBooleanProperty(false);

    @FXML
    private PasswordField passwordPasswordField;
    private BooleanProperty isPasswordValid = new SimpleBooleanProperty(false);

    @FXML
    private Button loginButton;

    public LoginController(AuthManager authManager) {
        this.authManager = authManager;
    }

    @FXML
    private void initialize() {

        loginButton.disableProperty().bind(
            isUsernameValid.not().or(
                isPasswordValid.not()
            )
        );

        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isUsernameValid.set(ValidationHandler.validateControl(usernameTextField, errorLabel,
                    ValidationHandler.validateAccountUsername(newValue)));
        });

        passwordPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            isPasswordValid.set(ValidationHandler.validateControl(passwordPasswordField, errorLabel,
                    ValidationHandler.validateAccountPassword(newValue)));
        });
    }

    @FXML
    public void handleLoginAction(ActionEvent event){
        Response validation = authManager.login(usernameTextField.getText(),
                passwordPasswordField.getText());

        if (ValidationHandler.showError(errorLabel, validation)) {
            authManager.initRootLayoutRequest.run();
        }
    }
}

