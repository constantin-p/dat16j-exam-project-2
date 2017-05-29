package assignment.core.modal;


import assignment.model.Motorhome;
import assignment.model.Price;
import assignment.util.Response;
import assignment.util.ValidationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ui.control.CPIntegerField;
import ui.control.CPTextField;

import java.util.Arrays;

public class MotorhomeFormController extends ModalBaseController {
    private static final String TITLE_CREATE = "Create motorhome";
    private static final String TITLE_EDIT = "Edit motorhome";
    private static final String TEMPLATE_PATH = "templates/modal/motorhome.fxml";

    private Motorhome motorhome;
    private boolean create;

    @FXML
    private Label errorLabel;

    @FXML
    private CPTextField modelTextField;
    private BooleanProperty isModelValid = new SimpleBooleanProperty(false);

    @FXML
    private CPTextField brandTextField;
    private BooleanProperty isBrandValid = new SimpleBooleanProperty(false);

    @FXML
    private CPIntegerField capacityTextField;
    private BooleanProperty isCapacityValid = new SimpleBooleanProperty(false);

    @FXML
    private CPIntegerField mileageTextField;
    private BooleanProperty isMileageValid = new SimpleBooleanProperty(false);

    @FXML
    private Button selectPriceButton;
    private BooleanProperty isPriceValid = new SimpleBooleanProperty(false);

    public MotorhomeFormController(ModalDispatcher modalDispatcher, Stage stage, boolean create,
                                   Motorhome motorhome) {
        super(modalDispatcher, stage);
        this.motorhome = motorhome;
        this.create = create;
    }

    @Override
    public void initialize() {
        super.initialize();

        super.isDisabled.bind(
            isModelValid.not().or(
                isBrandValid.not().or(
                    isCapacityValid.not().or(
                        isPriceValid.not().or(
                            isMileageValid.not()
                        )
                    )
                )
            )
        );

        modelTextField.textProperty().bindBidirectional(motorhome.model);
        modelTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isModelValid.set(ValidationHandler.validateControl(modelTextField, errorLabel,
                    ValidationHandler.validateMotorhomeModel(newValue)));
        });

        brandTextField.textProperty().bindBidirectional(motorhome.brand);
        brandTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isBrandValid.set(ValidationHandler.validateControl(brandTextField, errorLabel,
                    ValidationHandler.validateMotorhomeBrand(newValue)));
        });

        capacityTextField.valueProperty().bindBidirectional(motorhome.capacity);
        capacityTextField.valueProperty().addListener((observable, oldValue, newValue) -> {
            isCapacityValid.set(ValidationHandler.validateControl(capacityTextField, errorLabel,
                    ValidationHandler.validateMotorhomeCapacity(newValue.intValue())));
        });
        isCapacityValid.set(ValidationHandler.validateControl(capacityTextField, errorLabel,
                ValidationHandler.validateMotorhomeCapacity(capacityTextField.getValue())));

        mileageTextField.valueProperty().bindBidirectional(motorhome.mileage);
        mileageTextField.valueProperty().addListener((observable, oldValue, newValue) -> {
            isMileageValid.set(ValidationHandler.validateControl(mileageTextField, errorLabel,
                    ValidationHandler.validateMotorhomeMileage(newValue.intValue())));
        });
        isMileageValid.set(ValidationHandler.validateControl(mileageTextField, errorLabel,
                ValidationHandler.validateMotorhomeMileage(mileageTextField.getValue())));
    }

    @Override
    public void handleOKAction(ActionEvent event) {
        if (create) {
            boolean success = ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateMotorhomeDBOperation(Motorhome.dbInsert(motorhome)));

            if (success) {
                super.handleOKAction(event);
            }
        } else {

        }
    }

    @Override
    public Motorhome result() {
        if (super.isOKClicked && !super.isDisabled.getValue()) {
            return motorhome;
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
        Price price = modalDispatcher.showSelectPriceModal(super.stage, true, p ->
                Arrays.asList(Motorhome.ALLOWED_PRICE_TYPES).contains(p.type
                        .getValue().name.getValue())
        );

        Response validation = ValidationHandler.validateMotorhomePrice(price);
        if (validation.success) {
            motorhome.price.setValue(price);
            selectPriceButton.setText(price.name.getValue() + " - " +
                    price.value.getValue() + "kr");
        } else {
            selectPriceButton.setText("Select price");
        }

        isPriceValid.set(ValidationHandler.showError(errorLabel, validation));
    }
}
