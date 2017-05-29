package assignment.core.modal;


import assignment.model.Price;
import assignment.model.PriceType;
import assignment.util.Response;
import assignment.util.ValidationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ui.control.CPDecimalField;
import ui.control.CPTextField;

public class PriceFormController extends ModalBaseController {
    private static final String TITLE_CREATE = "price_create";
    private static final String TITLE_EDIT = "price_edit";
    private static final String TEMPLATE_PATH = "templates/modal/price.fxml";

    private Price price;
    private boolean create;

    @FXML
    private Label errorLabel;

    @FXML
    private CPTextField nameTextField;
    private BooleanProperty isNameValid = new SimpleBooleanProperty(false);

    @FXML
    private CPDecimalField valueTextField;
    private BooleanProperty isValueValid = new SimpleBooleanProperty(false);

    @FXML
    private Button selectPriceTypeButton;
    private BooleanProperty isPriceTypeValid = new SimpleBooleanProperty(false);

    public PriceFormController(ModalDispatcher modalDispatcher, Stage stage, boolean create,
                               Price price) {
        super(modalDispatcher, stage);
        this.price = price;
        this.create = create;
    }

    @Override
    public void initialize() {
        super.initialize();

        super.isDisabled.bind(
            isNameValid.not().or(
                isValueValid.not().or(
                    isPriceTypeValid.not()
                )
            )
        );


        nameTextField.textProperty().bindBidirectional(price.name);
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isNameValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validatePriceName(newValue)));
        });

        valueTextField.valueProperty().bindBidirectional(price.value);
        valueTextField.valueProperty().addListener((observable, oldValue, newValue) -> {
            isValueValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validatePriceValue(newValue.doubleValue())));
        });
    }

    @Override
    public void handleOKAction(ActionEvent event) {
        if (create) {
            boolean success = ValidationHandler.showError(errorLabel,
                    ValidationHandler.validatePriceDBOperation(Price.dbInsert(price)));
            if (success) {
                price = Price.dbGetByName(price.name.getValue());

                super.handleOKAction(event);
            }
        }
    }

    @Override
    public Price result() {
        if (super.isOKClicked && !super.isDisabled.getValue()) {
            return price;
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
    public void handleSelectPriceTypeAction(ActionEvent event) {
        PriceType priceType = modalDispatcher.showSelectPriceTypeModal(super.stage);

        Response validation = ValidationHandler.validatePricePriceType(priceType);
        if (validation.success) {
            price.type.setValue(priceType);
            selectPriceTypeButton.setText(priceType.name.getValue());
        } else {
            selectPriceTypeButton.setText("Select type");
        }

        isPriceTypeValid.set(ValidationHandler.showError(errorLabel, validation));
    }
}
