package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Extra;
import assignment.model.Motorhome;
import assignment.model.Order;
import assignment.model.Payment;
import assignment.util.CacheEngine;
import assignment.util.Config;
import assignment.util.DBOperation;
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

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.time.temporal.ChronoUnit.DAYS;

public class PaymentsController implements UISection {
    private static final String ACCESS_TYPE_NAME = "payments";
    private static final String TEMPLATE_PATH = "templates/section/payments.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private DecimalFormat decimalFormatter = new DecimalFormat(".##");

    private RootController rootController;
    private ObservableList<Payment> paymentList = FXCollections.observableArrayList();

    @FXML
    private TableView<Payment> tableView;

    public PaymentsController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Payment, String> clientColumn = new TableColumn("Client");
        TableColumn<Payment, String> priceColumn = new TableColumn("Amount");
        TableColumn<Payment, String> statusColumn = new TableColumn("Status");
        TableColumn<Payment, String> actionColumn = new TableColumn("Action");
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().id));
        actionColumn.setCellFactory(getActionCellFactory());
        actionColumn.getStyleClass().add("align-center");

        TableColumn<Payment, String> firstNameColumn = new TableColumn("First name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().invoice.getValue()
                .order.getValue().client.getValue().firstName);

        TableColumn<Payment, String> lastnameColumn = new TableColumn("Last name");
        lastnameColumn.setCellValueFactory(cellData -> cellData.getValue().invoice.getValue()
                .order.getValue().client.getValue().lastName);
        clientColumn.getColumns().addAll(firstNameColumn, lastnameColumn);

        priceColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(decimalFormatter.format(getTotal(cellData.getValue())) + "kr"));
        priceColumn.getStyleClass().add("align-right");


        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                (cellData.getValue().date.getValue() == null)
                    ? "..."
                    : "Paid on: " + formatter.format(cellData.getValue().date.getValue())
        ));
        statusColumn.getStyleClass().add("align-center");

        tableView.getColumns().addAll(clientColumn, priceColumn, statusColumn, actionColumn);
        tableView.setItems(paymentList);

        populateTableView();
    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    /*
    *  Helpers
    */
    private void populateTableView() {

        CacheEngine.get("payments", new DBOperation<>(() ->
            Payment.dbGetAll(), (List<Payment> payments) -> {

            paymentList.clear();
            payments.forEach(entry -> {
                if (!entry.invoice.getValue().order.getValue().isCancelled) {
                    paymentList.add(entry);
                }
            });
        }));
    }

    private double getTotal(Payment payment) {
        double total;

        Properties invoiceProperties = Config.getConfig("invoice");
        Order order = payment.invoice.getValue().order.getValue();
        long days = DAYS.between(order.startDate.getValue(), order.endDate.getValue());

        total = days * order.motorhomeValue.getValue();
        // Add season modifier
        total += total * order.seasonModifier.getValue();
        // Add extras
        for (Map.Entry<Extra, Double> entry: order.extras) {
            total += entry.getValue().doubleValue();
        }
        // Add fuel
        int distance = order.pickUpDistance.getValue() + order.dropOffDistance.getValue();
        if (distance > 0) {
            double kmPrice = Double.valueOf(invoiceProperties.getProperty("INVOICE_PRICE_PER_KM"));
            total += distance * kmPrice;
        }
        // Add VAT
        double VATModifier = Double.valueOf(invoiceProperties.getProperty("INVOICE_VAT_MODIFIER"));
        total += total * VATModifier;

        return total;
    }

    private Callback<TableColumn<Payment, String>, TableCell<Payment, String>> getActionCellFactory() {
        return new Callback<TableColumn<Payment, String>, TableCell<Payment, String>>() {
            @Override
            public TableCell call( final TableColumn<Payment, String> param) {
                final TableCell<Payment, String> cell = new TableCell<Payment, String>() {


                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (getTableView().getItems().get(getIndex()).date.getValue() == null) {
                                Button pay = new Button("Pay");
                                pay.setOnAction((ActionEvent event) -> {
                                    Payment.dbUpdateDate(item, LocalDate.now());
                                    CacheEngine.markForUpdate("payments");
                                });
                                setGraphic(pay);
                                setText("...");
                            }
                        }
                    }
                };
                return cell;
            }
        };
    }
}
