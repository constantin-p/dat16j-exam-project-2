package assignment.core.modal;


import assignment.model.AccessType;
import assignment.model.Client;
import assignment.util.Response;
import assignment.util.ValidationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.LocalDateStringConverter;
import ui.control.CPTextField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ClientFormController extends ModalBaseController {
    private static final String TITLE_CREATE = "client_create";
    private static final String TITLE_EDIT = "client_edit";
    private static final String TEMPLATE_PATH = "templates/modal/client.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private Client client;
    private boolean create;

    @FXML
    private Label errorLabel;

    @FXML
    private CPTextField firstNameTextField;
    private BooleanProperty isFirstNameValid = new SimpleBooleanProperty(false);

    @FXML
    private CPTextField lastNameTextField;
    private BooleanProperty isLastNameValid = new SimpleBooleanProperty(false);
    @FXML
    private CPTextField emailTextField;
    private BooleanProperty isEmailValid = new SimpleBooleanProperty(false);
    @FXML
    private DatePicker dateOfBirthDatePicker;
    private BooleanProperty isDateOfBirthValid = new SimpleBooleanProperty(false);

    @FXML
    private TableView<Map.Entry<AccessType, BooleanProperty>> tableView;

    public ClientFormController(ModalDispatcher modalDispatcher, Stage stage, boolean create,
                                     Client client) {
        super(modalDispatcher, stage);
        this.client = client;
        this.create = create;
}

    @Override
    public void initialize() {
        super.initialize();

        super.isDisabled.bind(isFirstNameValid.not().or(
            isLastNameValid.not()).or(
                isEmailValid.not().or(
                    isDateOfBirthValid.not()
                )
            )
        );

        firstNameTextField.textProperty().bindBidirectional(client.firstName);
        firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isFirstNameValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateClientFirstName(newValue)));
        });

        lastNameTextField.textProperty().bindBidirectional(client.lastName);
        lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isLastNameValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateClientLastName(newValue)));
        });

        emailTextField.textProperty().bindBidirectional(client.email);
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isEmailValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateClientEmail(newValue)));
        });


        dateOfBirthDatePicker.setConverter(new LocalDateStringConverter(formatter, formatter));
        dateOfBirthDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item.isAfter(LocalDate.now().minusYears(16))) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        });

        dateOfBirthDatePicker.valueProperty().bindBidirectional(client.dateOfBirth);
        dateOfBirthDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            isDateOfBirthValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateClientDateOfBirth(newValue)));
        });

    }

    @Override
    public void handleOKAction(ActionEvent event) {
        if (create) {
            boolean success = ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateClientDBOperation(Client.dbInsert(client)));

            if (success) {
                client = Client.dbGetByEmail(client.email.getValue());
                System.out.println(client);
                super.handleOKAction(event);
            }
        } else {

        }
    }

    @Override
    public Client result() {
        if (super.isOKClicked && !super.isDisabled.getValue()) {
            return client;
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
