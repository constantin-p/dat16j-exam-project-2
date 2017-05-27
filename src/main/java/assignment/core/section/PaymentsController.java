package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Motorhome;
import assignment.model.Order;
import assignment.model.Payment;
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

public class PaymentsController implements UISection {
    private static final String ACCESS_TYPE_NAME = "payments";
    private static final String TEMPLATE_PATH = "templates/section/payments.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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

        TableColumn<Payment, String> firstNameColumn = new TableColumn("First name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().invoice.getValue()
                .order.getValue().client.getValue().firstName);

        TableColumn<Payment, String> lastnameColumn = new TableColumn("Last name");
        lastnameColumn.setCellValueFactory(cellData -> cellData.getValue().invoice.getValue()
                .order.getValue().client.getValue().lastName);
        clientColumn.getColumns().addAll(firstNameColumn, lastnameColumn);


        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                (cellData.getValue().date.getValue() == null)
                    ? "Awaiting"
                    : "Paid on: " + formatter.format(cellData.getValue().date.getValue())
        ));

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

        List<Payment> payments = Payment.dbGetAll();
        paymentList.clear();
        payments.forEach(entry -> {
            paymentList.add(entry);
        });
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
                                    Payment.dbUpdate(item, LocalDate.now());
                                    populateTableView();
                                });
                                setGraphic(pay);
                                setText(null);
                            }
                        }
                    }
                };
                return cell;
            }
        };
    }
}
