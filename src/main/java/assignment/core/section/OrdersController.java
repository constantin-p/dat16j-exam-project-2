package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Motorhome;
import assignment.model.Order;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdersController implements UISection {
    private static final String ACCESS_TYPE_NAME = "orders";
    private static final String TEMPLATE_PATH = "templates/section/orders.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private RootController rootController;
    private ObservableList<Order> orderList = FXCollections.observableArrayList();

    @FXML
    private TableView<Order> tableView;

    public OrdersController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Order, String> durationColumn = new TableColumn("Duration");
        TableColumn<Order, String> motorhomeColumn = new TableColumn("Motorhome");
        TableColumn<Order, String> clientColumn = new TableColumn("Client");
        TableColumn<Order, String> priceColumn = new TableColumn("Price");

        TableColumn<Order, String> startColumn = new TableColumn("Start");
        startColumn.setCellValueFactory(cellData -> {
            return Bindings.createStringBinding(() ->
                formatter.format(cellData.getValue().startDate.getValue()),
                     cellData.getValue().startDate);
        });
        TableColumn<Order, String> endColumn = new TableColumn("End");
        endColumn.setCellValueFactory(cellData -> {
            return Bindings.createStringBinding(() ->
                            formatter.format(cellData.getValue().endDate.getValue()),
                    cellData.getValue().endDate);
        });
        durationColumn.getColumns().addAll(startColumn, endColumn);


        TableColumn<Order, String> brandColumn = new TableColumn("Brand");
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().motorhome.getValue().brand);

        TableColumn<Order, String> modelColumn = new TableColumn("Model");
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().motorhome.getValue().model);
        motorhomeColumn.getColumns().addAll(brandColumn, modelColumn);

        TableColumn<Order, String> firstNameColumn = new TableColumn("First name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().client.getValue().firstName);

        TableColumn<Order, String> lastnameColumn = new TableColumn("Last name");
        lastnameColumn.setCellValueFactory(cellData -> cellData.getValue().client.getValue().lastName);
        clientColumn.getColumns().addAll(firstNameColumn, lastnameColumn);


        tableView.getColumns().addAll(durationColumn, motorhomeColumn, clientColumn, priceColumn);
        tableView.setItems(orderList);

        populateTableView();
    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    @FXML
    public void handleAddAction(ActionEvent event) {
        Order order = rootController.modalDispatcher.showCreateOrderModal(null);
        if (order != null) {
            populateTableView();
        }
    }
    /*
     *  Helpers
     */
    private void populateTableView() {

        List<Order> orders = Order.dbGetAll();
        orderList.clear();
        orders.forEach(entry -> {
            orderList.add(entry);
        });
    }
}
