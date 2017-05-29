package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Payment;
import assignment.model.Price;
import assignment.model.Refund;
import assignment.util.CacheEngine;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RefundsController implements UISection {
    private static final String ACCESS_TYPE_NAME = "refunds";
    private static final String TEMPLATE_PATH = "templates/section/refunds.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private RootController rootController;
    private ObservableList<Refund> refundList = FXCollections.observableArrayList();

    @FXML
    private TableView<Refund> tableView;

    public RefundsController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Refund, String> clientColumn = new TableColumn("Client");
        TableColumn<Refund, String> priceColumn = new TableColumn("Amount");
        TableColumn<Refund, String> statusColumn = new TableColumn("Status");
        TableColumn<Refund, String> actionColumn = new TableColumn("Action");
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().id));
        actionColumn.setCellFactory(getActionCellFactory());

        TableColumn<Refund, String> firstNameColumn = new TableColumn("First name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().invoice.getValue()
                .order.getValue().client.getValue().firstName);

        TableColumn<Refund, String> lastnameColumn = new TableColumn("Last name");
        lastnameColumn.setCellValueFactory(cellData -> cellData.getValue().invoice.getValue()
                .order.getValue().client.getValue().lastName);
        clientColumn.getColumns().addAll(firstNameColumn, lastnameColumn);


        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                (cellData.getValue().date.getValue() == null)
                        ? "Awaiting"
                        : "Paid on: "+ formatter.format(cellData.getValue().date.getValue())
        ));

        tableView.getColumns().addAll(clientColumn, priceColumn, statusColumn, actionColumn);
        tableView.setItems(refundList);

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

        CacheEngine.get("refunds", new DBOperation<>(() ->
            Refund.dbGetAll(), (List<Refund> refunds) -> {

            refundList.clear();
            refunds.forEach(entry -> {
                refundList.add(entry);
            });
        }));
    }

    private Callback<TableColumn<Refund, String>, TableCell<Refund, String>> getActionCellFactory() {
        return new Callback<TableColumn<Refund, String>, TableCell<Refund, String>>() {
            @Override
            public TableCell call( final TableColumn<Refund, String> param) {
                final TableCell<Refund, String> cell = new TableCell<Refund, String>() {


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
                                    Refund.dbUpdate(item, LocalDate.now());
                                    CacheEngine.markForUpdate("refunds");
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
