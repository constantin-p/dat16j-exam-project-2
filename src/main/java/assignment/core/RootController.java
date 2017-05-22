package assignment.core;


import assignment.core.modal.ModalDispatcher;
import assignment.core.section.AccountTypesController;
import assignment.core.section.AccountsController;
import assignment.core.section.UISection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.util.concurrent.Callable;

public class RootController {

    public ModalDispatcher modalDispatcher;

    @FXML
    private TabPane tabPane;

    public RootController(Stage primaryStage) {
        modalDispatcher = new ModalDispatcher(primaryStage);
    }

    @FXML
    private void initialize() {

        loadSections();
    }

    private void loadSections() {

        // TODO: Use Reflection, pass only the class
        loadSection(AccountsController.getAccessTypeName(),
                () -> new AccountsController(this));
        loadSection(AccountTypesController.getAccessTypeName(),
                () -> new AccountTypesController(this));
    }

    private void loadSection(String accessTypeName, Callable<UISection> createSectionControllerRequest) {
        // TODO: Check permission
        if (true) {
            try {
                // Initialize the controller
                UISection controller = createSectionControllerRequest.call();
                Tab sectionTab = new Tab(accessTypeName);

                // Load the template
                Node layout = loadSectionContent(controller);
                sectionTab.setContent(layout);

                tabPane.getTabs().add(sectionTab);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Node loadSectionContent(UISection controller) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            FXMLLoader loader = new FXMLLoader(classLoader.getResource(controller.getTemplatePath()));

            loader.setController(controller);
            Parent layout = loader.load();

            return layout;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
