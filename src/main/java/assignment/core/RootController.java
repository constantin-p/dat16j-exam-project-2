package assignment.core;


import assignment.core.auth.AuthManager;
import assignment.core.modal.ModalDispatcher;
import assignment.core.section.*;
import assignment.model.Order;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import assignment.util.ScheduleManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RootController {

    private AuthManager authManager;
    public ModalDispatcher modalDispatcher;

    @FXML
    private TabPane tabPane;

    @FXML
    private Label usernameLabel;

    public RootController(AuthManager authManager, Stage primaryStage) {
        this.authManager = authManager;
        modalDispatcher = new ModalDispatcher(primaryStage);
    }

    @FXML
    private void initialize() {

        usernameLabel.textProperty().bind(authManager.currentUser.username);

        loadSections();
        initScheduleManager();
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
        loadSection(PricesController.getAccessTypeName(),
                () -> new PricesController(this));
        loadSection(SeasonsController.getAccessTypeName(),
                () -> new SeasonsController(this));
        loadSection(CancellationPeriodsController.getAccessTypeName(),
                () -> new CancellationPeriodsController(this));
        loadSection(PaymentsController.getAccessTypeName(),
                () -> new PaymentsController(this));
        loadSection(RefundsController.getAccessTypeName(),
                () -> new RefundsController(this));
        loadSection(RepairsController.getAccessTypeName(),
                () -> new RepairsController(this));
        loadSection(ServiceController.getAccessTypeName(),
                () -> new ServiceController(this));
        loadSection(ExtrasController.getAccessTypeName(),
                () -> new ExtrasController(this));
    }

    private void loadSection(String accessTypeName, Callable<UISection> createSectionControllerRequest) {
        // The section is visible only if the current user has access to it
        if (authManager.currentUser.hasAccess(accessTypeName)) {
            try {
                // Initialize the controller
                UISection controller = createSectionControllerRequest.call();
                Tab sectionTab = new Tab(getDisplayString(accessTypeName));

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
            FXMLLoader loader = new FXMLLoader(classLoader
                    .getResource(controller.getTemplatePath()));

            loader.setController(controller);
            Parent layout = loader.load();

            return layout;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void initScheduleManager() {
        CacheEngine.get("orders", new DBOperation<>(() ->
            Order.dbGetAll(), (List<Order> orders) -> {

            List<Order> orderList = new ArrayList<>();
            orders.forEach(entry -> {
                if (!entry.isCancelled) {
                    orderList.add(entry);
                }
            });
            ScheduleManager.update(orderList);
        }));
    }

    @FXML
    public void handleSignOutAction(ActionEvent event) {
        authManager.signOut();
    }

    /*
     *  Helpers
     */
    private String getDisplayString(String string){

        string = string.replaceAll("_", " ");
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
