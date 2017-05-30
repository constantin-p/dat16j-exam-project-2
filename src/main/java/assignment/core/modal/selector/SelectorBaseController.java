package assignment.core.modal.selector;


import assignment.core.modal.ModalBaseController;
import assignment.core.modal.ModalDispatcher;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ui.control.CPSearchField;

public class SelectorBaseController extends ModalBaseController {
    private static final String TEMPLATE_PATH = "templates/modal/selector.fxml";

    private boolean canCreate = false;

    public interface EntryValidator<U> {
        boolean isValid(U u);
    };

    @FXML
    private TableView tableView;

    @FXML
    private Button createButton;

    @FXML
    protected CPSearchField searchField;

    public SelectorBaseController(ModalDispatcher modalDispatcher, Stage stage, boolean canCreate) {
        super(modalDispatcher, stage);
        this.canCreate = canCreate;
    }

    @Override
    public void initialize() {
        super.initialize();

        createButton.managedProperty().bind(createButton.visibleProperty());
        createButton.setVisible(canCreate);

        super.isDisabled.bind(
            Bindings.isNull(tableView.getSelectionModel().selectedItemProperty())
        );
    }

    @Override
    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    @FXML
    protected void handleCreateAction(ActionEvent event) { }
}
