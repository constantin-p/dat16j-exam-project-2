package assignment.core.modal;


import assignment.model.Extra;
import assignment.model.Price;
import assignment.util.Response;
import assignment.util.ValidationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ui.control.CPTextField;

public class ExtraFormController extends ModalBaseController {
    private static final String TITLE_CREATE = "extras_create";
    private static final String TITLE_EDIT = "extras_edit";
    private static final String TEMPLATE_PATH = "templates/modal/extras.fxml";

    private Extra extra;
    private boolean create;

    @FXML
    private Label errorLabel;

    @FXML
    private CPTextField nameTextField;
    private BooleanProperty isNameValid = new SimpleBooleanProperty(false);

    @FXML
    private Button selectPriceButton;
    private BooleanProperty isPriceValid = new SimpleBooleanProperty(false);

    public ExtraFormController(ModalDispatcher modalDispatcher, Stage stage, boolean create,
                               Extra extra) {
        super(modalDispatcher, stage);
        this.extra = extra;
        this.create = create;
    }

    @Override
    public void initialize() {
        super.initialize();

        super.isDisabled.bind(
            isNameValid.not().or(
                isPriceValid.not()
            )
        );

        nameTextField.textProperty().bindBidirectional(extra.name);
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isNameValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateExtraName(newValue)));
        });
    }

    @Override
    public void handleOKAction(ActionEvent event) {
        if (create) {
            boolean success = ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateExtraDBOperation(Extra.dbInsert(extra)));

            if (success) {
                super.handleOKAction(event);
            }
        } else {

        }
    }

    @Override
    public Extra result() {
        if (super.isOKClicked && !super.isDisabled.getValue()) {
            return extra;
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

    @FXML
    public void handleSelectPriceAction(ActionEvent event) {
        Price price = modalDispatcher.showSelectPriceModal(super.stage);

        Response validation = ValidationHandler.validateExtraPrice(price);
        if (validation.success) {
            extra.price.setValue(price);
            selectPriceButton.setText(price.name.getValue() + " - [" + price.value.getValue() +
                    " / " + price.type.getValue().name.getValue() + "]");
        } else {
            selectPriceButton.setText("Select price");
        }

        isPriceValid.set(ValidationHandler.showError(errorLabel, validation));
    }
}

