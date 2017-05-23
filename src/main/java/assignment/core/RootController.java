package assignment.core;


import assignment.core.auth.AuthManager;
import assignment.core.modal.ModalDispatcher;
import assignment.core.section.*;
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

    private AuthManager authManager;
    public ModalDispatcher modalDispatcher;

    @FXML
    private TabPane tabPane;

    public RootController(AuthManager authManager, Stage primaryStage) {
        this.authManager = authManager;
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
        loadSection(CleaningController.getAccessTypeName(),
                () -> new CleaningController(this));
        loadSection(ClientsController.getAccessTypeName(),
                () -> new ClientsController(this));
        loadSection(FleetController.getAccessTypeName(),
                () -> new FleetController(this));
        loadSection(OrdersController.getAccessTypeName(),
                () -> new OrdersController(this));
        loadSection(PaymentsController.getAccessTypeName(),
                () -> new PaymentsController(this));
        loadSection(RefundsController.getAccessTypeName(),
                () -> new RefundsController(this));
        loadSection(RepairsController.getAccessTypeName(),
                () -> new RepairsController(this));
        loadSection(ServiceController.getAccessTypeName(),
                () -> new ServiceController(this));
    }

    private void loadSection(String accessTypeName, Callable<UISection> createSectionControllerRequest) {
        // The section is visible only if the current user has access to it
        if (authManager.currentUser.hasAccess(accessTypeName)) {
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
