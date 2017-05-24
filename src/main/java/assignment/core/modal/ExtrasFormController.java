package assignment.core.modal;

import assignment.model.AccountType;
import assignment.model.Extras;
import assignment.model.Motorhome;
import assignment.util.ValidationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ExtrasFormController extends ModalBaseController {
    private static final String TITLE_CREATE = "extras_create";
    private static final String TITLE_EDIT = "extras_create";
    private static final String TEMPLATE_PATH = "templates/modal/extras.fxml";

    private Extras extras;
    private boolean create;

    @FXML
    private Label errorLabel;


    @FXML
    private TextField nameTextField;
    private BooleanProperty isNameValid = new SimpleBooleanProperty(false);

    public ExtrasFormController(ModalDispatcher modalDispatcher, Stage stage, boolean create,
                                   Extras extras) {
        super(modalDispatcher, stage);
        this.extras = extras;
        this.create = create;
    }

    @Override
    public void initialize() {
        super.initialize();

        super.isDisabled.bind(
                isNameValid.not()


        );

        nameTextField.textProperty().bindBidirectional(extras.name);
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isNameValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateMotorhomeModel(newValue)));
        });

    }

    @Override
    public void handleOKAction(ActionEvent event) {
        if (create) {
            boolean success = ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateExtrasDBOperation(Extras.dbInsert(extras)));

            if (success) {

                super.handleOKAction(event);
            }
        } else {

        }
    }

    @Override
    public Extras result() {
        if (super.isOKClicked && !super.isDisabled.getValue()) {
            return extras;
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

