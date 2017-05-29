package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Motorhome;
import assignment.model.Order;
import assignment.util.CacheEngine;
import assignment.util.DBOperation;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.time.LocalDate;
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
        TableColumn<Order, String> actionColumn = new TableColumn("Actions");
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().id));
        actionColumn.setCellFactory(getActionCellFactory());
        actionColumn.getStyleClass().add("align-center");

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


        tableView.getColumns().addAll(durationColumn, motorhomeColumn, clientColumn, priceColumn, actionColumn);
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
            CacheEngine.markForUpdate("orders");
        }
    }
    /*
     *  Helpers
     */
    private void populateTableView() {

        CacheEngine.get("orders", new DBOperation<>(() ->
            Order.dbGetAll(), (List<Order> orders) -> {

            orderList.clear();
            orders.forEach(entry -> {
                if (!entry.isCancelled) {
                    orderList.add(entry);
                }
            });
        }));
    }

    private Callback<TableColumn<Order, String>, TableCell<Order, String>> getActionCellFactory() {
        return new Callback<TableColumn<Order, String>, TableCell<Order, String>>() {
            @Override
            public TableCell call( final TableColumn<Order, String> param) {
                final TableCell<Order, String> cell = new TableCell<Order, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            Order order = getTableView().getItems().get(getIndex());

                            if (order.startDate.getValue().isAfter(LocalDate.now().minusDays(1))) {
                                Button cancel = new Button("Cancel");
                                cancel.setOnAction((ActionEvent event) -> {
                                    rootController.modalDispatcher.showCancelOrderModal(null, order);
                                });
                                setGraphic(cancel);
                                setText(null);
                            } else {
                                setGraphic(null);
                                setText("-");
                            }
                        }
                    }
                };
                return cell;
            }
        };
    }
}
