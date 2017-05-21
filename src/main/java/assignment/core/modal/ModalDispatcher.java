package assignment.core.modal;


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

    private Object showModal(Stage stage, ControllerCreator controllerCreator,
                             String templatePath, String title) {
        Stage parentStage = (stage != null)
                ? stage
                : primaryStage;

        try {
            FXMLLoader loader = new FXMLLoader(classLoader.getResource(templatePath));

            // Create the modal Stage
            Stage modalStage = new Stage();
            modalStage.setTitle(title);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(parentStage);

            ModalController controller = controllerCreator.create(modalStage);
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
}
