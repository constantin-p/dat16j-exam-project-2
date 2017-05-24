package assignment.core.modal;


import assignment.core.modal.selector.AccountTypeSelectorController;
import assignment.model.Account;
import assignment.model.AccountType;
import assignment.model.Fleet;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ModalDispatcher {

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private interface ControllerCreator {
        ModalController create(Stage modalStage);
    };

    private Stage primaryStage;
    private IntegerProperty openModalsCount = new SimpleIntegerProperty(0);
    public BooleanProperty isModalOpen = new SimpleBooleanProperty(false);

    public ModalDispatcher(Stage primaryStage) {
        this.primaryStage = primaryStage;
        isModalOpen.bind(openModalsCount.isNotEqualTo(0));
    }

    private Object showModal(Stage stage, ControllerCreator controllerCreator) {
        Stage parentStage = (stage != null)
                ? stage
                : primaryStage;

        try {
            // Create the modal Stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(parentStage);

            ModalController controller = controllerCreator.create(modalStage);
            modalStage.setTitle(controller.getTitle());

            FXMLLoader loader = new FXMLLoader(classLoader.getResource(controller.getTemplatePath()));
            loader.setController(controller);

            Parent layout = loader.load();
            modalStage.setScene(new Scene(layout));

            // Show the modal, wait until the user closes it
            // and then return the result
            openModalsCount.setValue(openModalsCount.getValue() + 1);
            modalStage.showAndWait();
            openModalsCount.setValue(openModalsCount.getValue() - 1);

            return controller.result();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountType showCreateAccountTypeModal(Stage stage) {
        return (AccountType) showModal(stage, (Stage modalStage) -> {
            return new AccountTypeFormController(this, modalStage,
                     true, new AccountType());
        });
    }

    public AccountType showSelectAccountTypeModal(Stage stage) {
        return (AccountType) showModal(stage, (Stage modalStage) -> {
            return new AccountTypeSelectorController(this, modalStage,
                    false);
        });
    }

    public Account showCreateAccountModal(Stage stage) {
        return (Account) showModal(stage, (Stage modalStage) -> {
            return new AccountFormController(this, modalStage,
                    true, new Account());
        });
    }
//    public Fleet showCreateVehicleModal(Stage stage) {
//        return (Fleet) showModal(stage, (Stage modalStage) -> {
//            return new FleetFormController(this, modalStage,
//                    true, new Fleet());
//        });
//    }
}
