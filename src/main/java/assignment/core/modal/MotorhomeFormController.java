package assignment.core.modal;


import assignment.model.Motorhome;
import assignment.model.Price;
import assignment.util.Response;
import assignment.util.ValidationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MotorhomeFormController extends ModalBaseController {
    private static final String TITLE_CREATE = "motorhome_create";
    private static final String TITLE_EDIT = "motorhome_edit";
    private static final String TEMPLATE_PATH = "templates/modal/motorhome.fxml";

    private Motorhome motorhome;
    private boolean create;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField modelTextField;
    private BooleanProperty isModelValid = new SimpleBooleanProperty(false);

    @FXML
    private TextField brandTextField;
    private BooleanProperty isBrandValid = new SimpleBooleanProperty(false);

    // TODO: use numeric TextField
    @FXML
    private TextField capacityTextField;
    private BooleanProperty isCapacityValid = new SimpleBooleanProperty(false);

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
                        isPriceValid.not()
                    )
                )
            )
        );

        modelTextField.textProperty().bindBidirectional(motorhome.model);
        modelTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isModelValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateMotorhomeModel(newValue)));
        });

        brandTextField.textProperty().bindBidirectional(motorhome.brand);
        brandTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isBrandValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateMotorhomeBrand(newValue)));
        });

        capacityTextField.textProperty().bindBidirectional(motorhome.capacity);
        capacityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isCapacityValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateMotorhomeCapacity(newValue)));
        });
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
        Price price = modalDispatcher.showSelectPriceModal(super.stage);

        Response validation = ValidationHandler.validateExtraPrice(price);
        if (validation.success) {
            motorhome.price.setValue(price);
            selectPriceButton.setText(price.name.getValue() + " - [" + price.value.getValue() +
                " / " + price.type.getValue().name.getValue() + "]");
        } else {
            selectPriceButton.setText("Select price");
        }

        isPriceValid.set(ValidationHandler.showError(errorLabel, validation));
    }
}
