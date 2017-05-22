package assignment.core.modal;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ModalBaseController implements ModalController {

    protected ModalDispatcher modalDispatcher;
    protected Stage stage;

    protected boolean isOKClicked = false;
    protected BooleanProperty isDisabled = new SimpleBooleanProperty(false);

    @FXML
    private Button submitButton;

    public ModalBaseController(ModalDispatcher modalDispatcher, Stage stage) {
        this.modalDispatcher = modalDispatcher;
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        submitButton.disableProperty().bind(isDisabled);
    }

    @FXML
    protected void handleCancelAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    protected void handleOKAction(ActionEvent event) {
        isOKClicked = true;
        stage.close();
    }

    public Object result() {
        return null;
    }

    public String getTemplatePath() {
        return null;
    }
    public String getTitle() {
        return null;
    }
}
