package assignment.core.modal;


import assignment.core.modal.selector.SelectorBaseController;
import assignment.model.*;
import assignment.util.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.LocalDateStringConverter;
import store.db.TableHandler;
import ui.control.CPIntegerField;
import ui.control.CPTextField;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.DAYS;

public class OrderFormController extends ModalBaseController {
    private static final Logger LOGGER = Logger.getLogger(OrderFormController.class.getName());

    private static final String TITLE_CREATE = "Create order";
    private static final String TITLE_EDIT = "Edit order";
    private static final String TEMPLATE_PATH = "templates/modal/order.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private DecimalFormat decimalFormatter = new DecimalFormat(".##");

    private Order order;
    private boolean create;

    @FXML
    private Label errorLabel;

    List<Season> seasons = Season.dbGetAll();

    @FXML
    private DatePicker startDatePicker;
    private BooleanProperty isStartDateValid = new SimpleBooleanProperty(false);

    @FXML
    private DatePicker endDatePicker;
    private BooleanProperty isEndDateValid = new SimpleBooleanProperty(false);

    @FXML
    private CPTextField pickUpTextField;
    private BooleanProperty isPickUpTextValid = new SimpleBooleanProperty(false);

    @FXML
    private CPIntegerField pickUpDistanceTextField;
    private BooleanProperty isPickUpDistanceValid = new SimpleBooleanProperty(false);

    @FXML
    private CPTextField dropOffTextField;
    private BooleanProperty isDropOffTextValid = new SimpleBooleanProperty(false);

    @FXML
    private CPIntegerField dropOffDistanceTextField;
    private BooleanProperty isDropOffDistanceValid = new SimpleBooleanProperty(false);

    @FXML
    private Button selectMotorhomeButton;
    private BooleanProperty isMotorhomeValid = new SimpleBooleanProperty(false);

    @FXML
    private Button selectClientButton;
    private BooleanProperty isClientValid = new SimpleBooleanProperty(false);

    @FXML
    private TableView<Map.Entry<Extra, Double>> tableView;

    @FXML
    private Button addExtraButton;


    // Invoice
    @FXML
    private VBox invoiceVBox;

    @FXML
    private Label companyNameLabel;
    @FXML
    private Label companyAddressLabel;
    @FXML
    private Label companyEmailLabel;

    @FXML
    private Label dateLabel;
    @FXML
    private Label dueDateLabel;

    @FXML
    private Label clientNameLabel;
    @FXML
    private Label clientAddressLabel;
    @FXML
    private Label clientEmailLabel;

    @FXML
    private Label startDateLabel;
    @FXML
    private Label pickUpLabel;

    @FXML
    private Label endDateLabel;
    @FXML
    private Label dropOffLabel;

    @FXML
    private Label motorhomeLabel;
    @FXML
    private Label motorhomeMileageStartLabel;
    @FXML
    private Label motorhomeMileageEndLabel;

    @FXML
    private Label daysLabel;
    @FXML
    private Label daysTotalLabel;

    @FXML
    private Label seasonModifierLabel;
    @FXML
    private Label seasonTotalLabel;
    @FXML
    private Label daysSeasonTotalLabel;

    @FXML
    private VBox extrasVBox;
    @FXML
    private Label extrasTotalLabel;

    @FXML
    private Label fuelLabel;
    @FXML
    private Label fuelTotalLabel;
    @FXML
    private Label transportLabel;
    @FXML
    private Label transportTotalLabel;
    @FXML
    private Label fuelTransportTotalLabel;

    @FXML
    private Label withoutVATTotalLabel;

    @FXML
    private Label VATLabel;
    @FXML
    private Label VATTotalLabel;

    @FXML
    private Label totalLabel;

    private DoubleProperty invoiceDaysTotal = new SimpleDoubleProperty(0.00);
    private DoubleProperty invoiceSeasonModifier = new SimpleDoubleProperty(0.00);
    private NumberBinding invoiceSeasonTotal = invoiceDaysTotal.multiply(invoiceSeasonModifier);
    private NumberBinding invoiceDaysSeasonSubtotal = invoiceDaysTotal.add(invoiceSeasonTotal);
    private DoubleProperty invoiceExtrasSubtotal = new SimpleDoubleProperty(0.00);
    private DoubleProperty invoiceFuelTotal = new SimpleDoubleProperty(0.00);
    private DoubleProperty invoiceTransportTotal = new SimpleDoubleProperty(0.00);
    private NumberBinding invoiceFuelTransportSubtotal = invoiceFuelTotal.add(invoiceTransportTotal);
    private NumberBinding invoiceWithoutVATTotal = invoiceDaysSeasonSubtotal
            .add(invoiceExtrasSubtotal)
            .add(invoiceFuelTransportSubtotal);
    private DoubleProperty invoiceVATModifier = new SimpleDoubleProperty(0.00);
    private NumberBinding invoiceVATTotal = invoiceWithoutVATTotal.multiply(invoiceVATModifier);
    private NumberBinding invoiceTotal = invoiceWithoutVATTotal
            .add(invoiceVATTotal);

    public OrderFormController(ModalDispatcher modalDispatcher, Stage stage, boolean create,
                               Order order) {
        super(modalDispatcher, stage);
        this.order = order;
        this.create = create;
    }

    @Override
    public void initialize() {
        super.initialize();

        super.isDisabled.bind(
            isStartDateValid.not().or(
                isEndDateValid.not().or(
                    isClientValid.not().or(
                        isMotorhomeValid.not().or(
                            isPickUpTextValid.not().or(
                                isPickUpDistanceValid.not().or(
                                    isDropOffTextValid.not().or(
                                        isDropOffDistanceValid.not()
                                    )
                                )
                            )
                        )
                    )
                )
            )
        );

        // 1. Set invoice values
        Properties invoiceConfig = Config.getConfig("invoice");
        companyNameLabel.setText(invoiceConfig.getProperty("INVOICE_COMPANY_NAME"));
        companyAddressLabel.setText(invoiceConfig.getProperty("INVOICE_COMPANY_EMAIL"));
        companyEmailLabel.setText(invoiceConfig.getProperty("INVOICE_COMPANY_ADDRESS"));
        dateLabel.setText(formatter.format(LocalDate.now()));
        dueDateLabel.setText(formatter.format(LocalDate.now().plusDays(
                Integer.valueOf(invoiceConfig.getProperty("INVOICE_PAYMENT_PERIOD"))
        )));
        double VATModifier = Double.valueOf(invoiceConfig.getProperty("INVOICE_VAT_MODIFIER"));
        VATLabel.setText((VATModifier * 100) + "%");
        invoiceVATModifier.setValue(VATModifier);


        startDateLabel.textProperty().bind(Bindings.createStringBinding(() ->
                formatter.format(order.startDate.getValue()), order.startDate));
        endDateLabel.textProperty().bind(Bindings.createStringBinding(() ->
                formatter.format(order.endDate.getValue()), order.endDate));
        pickUpLabel.textProperty().bind(order.pickUp);
        dropOffLabel.textProperty().bind(order.dropOff);


        daysTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceDaysTotal.getValue()), invoiceDaysTotal));
        seasonTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceSeasonTotal.getValue()), invoiceSeasonTotal));
        daysSeasonTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceDaysSeasonSubtotal.getValue()), invoiceDaysSeasonSubtotal));
        extrasTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceExtrasSubtotal.getValue()), invoiceExtrasSubtotal));
        fuelTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceFuelTotal.getValue()), invoiceFuelTotal));
        transportTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceTransportTotal.getValue()), invoiceTransportTotal));
        fuelTransportTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceFuelTransportSubtotal.getValue()), invoiceFuelTransportSubtotal));
        withoutVATTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceWithoutVATTotal.getValue()), invoiceWithoutVATTotal));
        VATTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceVATTotal.getValue()), invoiceVATTotal));
        totalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceTotal.getValue()), invoiceTotal));



        order.extras.addListener((ListChangeListener
              .Change<? extends Map.Entry<Extra, Double>> c) -> {

            double totalExtras = 0.0;
            extrasVBox.getChildren().clear();
            for (Map.Entry<Extra, Double> entry: c.getList()) {
                totalExtras += entry.getValue();

                setInvoiceExtra(entry.getKey().name.getValue(),
                        decimalFormatter.format(entry.getValue()));
            }

            if (c.getList().isEmpty()) {
               setInvoiceExtra("...", "...");
            }
            invoiceExtrasSubtotal.setValue(totalExtras);
        });

        // 2. Editable fields
        startDatePicker.setConverter(new LocalDateStringConverter(formatter, formatter));
        startDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!item.isAfter(LocalDate.now())) {
                            // Not in the future
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        } else if (endDatePicker.getValue() != null) {
                            if (startDatePicker.getValue() != null) {
                                if (item.isAfter(startDatePicker.getValue())
                                        && item.isBefore(endDatePicker.getValue())) {
                                    setStyle("-fx-background-color: #c0ffcb;");
                                }
                            }
                        }
                    }
                };
            }
        });

        startDatePicker.valueProperty().bindBidirectional(order.startDate);
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            isStartDateValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateOrderStartDate(newValue)));

            if (newValue.isAfter(endDatePicker.getValue().minusDays(1))) {
                endDatePicker.setValue(newValue.plusDays(1));
            }

            resetScheduleDependentFields();
            setSeasonPrice();
            setInvoiceDays();
        });
        isStartDateValid.set(ValidationHandler.showError(errorLabel,
                ValidationHandler.validateOrderStartDate(startDatePicker.getValue())));
        setSeasonPrice();
        setInvoiceDays();

        endDatePicker.setConverter(new LocalDateStringConverter(formatter, formatter));
        endDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!item.isAfter(LocalDate.now().plusDays(1))) {
                            // Not in the future
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        } else if (startDatePicker.getValue() != null) {
                            if (item.isBefore(startDatePicker.getValue().plusDays(1))) {
                                // Not after startDate
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            } else if (endDatePicker.getValue() != null) {
                                if (item.isAfter(startDatePicker.getValue())
                                        && item.isBefore(endDatePicker.getValue())) {
                                    setStyle("-fx-background-color: #c0ffcb;");
                                }
                            }
                        }
                    }
                };
            }
        });

        endDatePicker.valueProperty().bindBidirectional(order.endDate);
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            isEndDateValid.set(ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateOrderEndDate(newValue)));

            resetScheduleDependentFields();
            setInvoiceDays();
        });
        isEndDateValid.set(ValidationHandler.showError(errorLabel,
                ValidationHandler.validateOrderEndDate(endDatePicker.getValue())));

        pickUpTextField.textProperty().bindBidirectional(order.pickUp);
        pickUpTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isPickUpTextValid.set(ValidationHandler.validateControl(pickUpTextField, errorLabel,
                    ValidationHandler.validateOrderPickUp(newValue)));
        });

        pickUpDistanceTextField.valueProperty().bindBidirectional(order.pickUpDistance);
        pickUpDistanceTextField.valueProperty().addListener((observable, oldValue, newValue) -> {
            isPickUpDistanceValid.set(ValidationHandler.validateControl(pickUpDistanceTextField,
                    errorLabel, ValidationHandler.validateOrderPickUpDistance(newValue.intValue())));
            setInvoiceTransport();
        });

        dropOffTextField.textProperty().bindBidirectional(order.dropOff);
        dropOffTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            isDropOffTextValid.set(ValidationHandler.validateControl(dropOffTextField, errorLabel,
                    ValidationHandler.validateOrderDropOff(newValue)));
        });

        dropOffDistanceTextField.valueProperty().bindBidirectional(order.dropOffDistance);
        dropOffDistanceTextField.valueProperty().addListener((observable, oldValue, newValue) -> {
            isDropOffDistanceValid.set(ValidationHandler.validateControl(dropOffDistanceTextField,
                    errorLabel, ValidationHandler.validateOrderDropOffDistance(newValue.intValue())));
            setInvoiceTransport();
        });


        TableColumn<Map.Entry<Extra, Double>, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getKey().name);

        TableColumn<Map.Entry<Extra, Double>, String> actionColumn = new TableColumn("Action");
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .getKey().id));
        actionColumn.setCellFactory(getActionCellFactory());
        actionColumn.getStyleClass().add("align-center");

        tableView.getColumns().addAll(nameColumn, actionColumn);
        tableView.setItems(order.extras);
    }

    @Override
    public void handleOKAction(ActionEvent event) {
        if (create) {
            List<Map.Entry<Extra, Double>> extras = order.extras;
            boolean success = ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateOrderDBOperation(Order.dbInsert(order)));
            if (success) {
                order = Order.dbGetByDateMotorhomeClient(order.startDate.getValue(),
                        order.endDate.getValue(), order.motorhome.getValue().id,
                        order.client.getValue().id);

                // 1. Add the extras
                // 2. Schedule payment
                // 3. Schedule cleaning job
                // 4. Schedule service job

                // Add the extras
                for (Map.Entry<Extra, Double> entry: extras) {
                    Order.dbInsertExtra(order.id, entry.getKey());
                }

                order.schedulePayment();
                order.scheduleCleaningJob();
                order.scheduleServiceJob();

                CacheEngine.markForUpdate("payments");
                CacheEngine.markForUpdate("cleaning");
                CacheEngine.markForUpdate("service");

                super.handleOKAction(event);
            }
        }
    }

    @Override
    public Order result() {
        if (super.isOKClicked && !super.isDisabled.getValue()) {
            return order;
        }
        return null;
    }

    @Override
    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    @Override
    public String getTitle() {
        return create
                ?   TITLE_CREATE
                :   TITLE_EDIT;
    }

    @FXML
    public void handleSelectMotorhomeAction(ActionEvent event) {
        Motorhome motorhome = modalDispatcher.showSelectMotorhomeModal(super.stage,
            m -> ScheduleManager.isMotorhomeFree(order.startDate.getValue(),
                order.endDate.getValue(), m.id));

        Response validation = ValidationHandler.validateOrderMotorhome(motorhome);
        if (validation.success) {
            order.motorhome.setValue(motorhome);
            order.motorhomeValue.setValue(motorhome.price.getValue().value.getValue());
            order.motorhomeMileageStart.setValue(motorhome.mileage.getValue());
            selectMotorhomeButton.setText(motorhome.brand.getValue() +
                    " - " + motorhome.model.getValue());
        } else {
            selectMotorhomeButton.setText("Select vehicle");
        }

        isMotorhomeValid.set(ValidationHandler.showError(errorLabel, validation));
        setInvoiceMotorhome();
    }

    @FXML
    public void handleSelectClientAction(ActionEvent event) {
        Client client = modalDispatcher.showSelectClientModal(super.stage, true,
                c -> ScheduleManager.isClientFree(order.startDate.getValue(),
                        order.endDate.getValue(), c.id));

        Response validation = ValidationHandler.validateOrderClient(client);
        if (validation.success) {
            order.client.setValue(client);
            selectClientButton.setText(client.firstName.getValue() +
                    " " + client.lastName.getValue());
        } else {
            selectClientButton.setText("Select client");
        }

        isClientValid.set(ValidationHandler.showError(errorLabel, validation));
        setInvoiceClient();
    }

    @FXML
    public void handleAddExtraAction(ActionEvent event) {
        Extra extra = modalDispatcher.showSelectExtraModal(super.stage, e -> {
            boolean isAvailable = ScheduleManager.isClientFree(order.startDate.getValue(),
                            order.endDate.getValue(), e.id);

            if (!isAvailable) {
                return false;
            }
            for(Map.Entry<Extra, Double> entry: order.extras) {
                if (entry.getKey().id.equals(e.id)) {
                    return false;
                }
            }
            return true;
        });
        if (extra != null) {
            order.extras.add(new AbstractMap.SimpleEntry<>(extra,
                    extra.price.getValue().value.getValue()));
        }
    }

    @FXML
    public void handlePrintAction(ActionEvent event) {
        WritableImage image = invoiceVBox.snapshot(new SnapshotParameters(), null);

        File file = new FileChooser().showSaveDialog(stage);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }

    /*
     *  Helpers
     */
    private void resetScheduleDependentFields() {
        // 1. Client
        // 2. Motorhome
        // 3. Extras

        order.motorhome.setValue(null);
        order.motorhomeValue.setValue(null);
        order.motorhomeMileageStart.setValue(0.0);
        selectMotorhomeButton.setText("Select motorhome");
        isMotorhomeValid.set(false);
        setInvoiceMotorhome();

        order.client.setValue(null);
        selectClientButton.setText("Select client");
        isClientValid.set(false);
        setInvoiceClient();

        order.extras.clear();
    }

    private void setSeasonPrice() {
        if (isStartDateValid.getValue()) {
            MonthDay startDate = MonthDay.of(startDatePicker.getValue().getMonth(),
                    startDatePicker.getValue().getDayOfMonth());

            seasons.sort(Comparator.comparing(o -> o.start.getValue()));
            boolean found = false;
            for (int i = 1; i < seasons.size(); i++) {
                if (startDate.isBefore(seasons.get(i).start.getValue())) {
                    found = true;
                    order.seasonModifier.setValue(seasons.get(i - 1).
                            price.getValue().value.getValue());
                    break;
                }
            }

            if (!found) {
                order.seasonModifier.setValue(seasons.get(0).
                        price.getValue().value.getValue());
            }
        }
        setInvoiceSeason();
    }

    private void setInvoiceExtra(String left, String right) {
        HBox hbox = new HBox();
        Label leftLabel = new Label(left);
        leftLabel.setMinWidth(100.0);
        leftLabel.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(leftLabel, Priority.NEVER);

        Label rightLabel = new Label(right);
        rightLabel.setMaxWidth(Double.MAX_VALUE);
        rightLabel.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(rightLabel, Priority.ALWAYS);

        hbox.getChildren().addAll(leftLabel, rightLabel);
        extrasVBox.getChildren().add(hbox);
    }

    private void setInvoiceTransport() {
        int distance = order.pickUpDistance.getValue() + order.dropOffDistance.getValue();

        if (distance > 0) {

            double kmPrice = Double.valueOf(Config.getConfig("invoice")
                    .getProperty("INVOICE_PRICE_PER_KM"));

            transportLabel.setText(distance + "× " +
                    decimalFormatter.format(kmPrice) + "kr");
            invoiceTransportTotal.setValue(distance * kmPrice);
        } else {
            transportLabel.setText("...");
            invoiceTransportTotal.setValue(0.0);
        }
    }

    private void setInvoiceMotorhome() {
        if (isMotorhomeValid.getValue()) {

            motorhomeLabel.setText(order.motorhome.getValue().brand.getValue() +
                    " - " + order.motorhome.getValue().model.getValue());
            motorhomeMileageStartLabel.setText(order.motorhome.getValue().mileage.getValue() + "km");
        } else {
            motorhomeLabel.setText("...");
            motorhomeMileageStartLabel.setText("...");
        }
        setInvoiceDays();
    }

    private void setInvoiceClient() {
        if (isClientValid.getValue()) {

            clientNameLabel.setText(order.client.getValue().firstName.getValue() +
                    " " + order.client.getValue().lastName.getValue());
            clientEmailLabel.setText(order.client.getValue().email.getValue());
        } else {
            clientNameLabel.setText("...");
            clientEmailLabel.setText("...");
        }
    }

    private void setInvoiceDays() {

        if (isMotorhomeValid.getValue()) {
            long days = DAYS.between(order.startDate.getValue(), order.endDate.getValue());
            daysLabel.setText(days + "× " +
                    decimalFormatter.format(order.motorhome.getValue().price.getValue().value.getValue()) + "kr");

            invoiceDaysTotal.setValue(days * order.motorhome.getValue().price.getValue().value.getValue());
        } else {
            daysLabel.setText(DAYS.between(order.startDate.getValue(), order.endDate.getValue()) + "× ");
            invoiceDaysTotal.setValue(0);
        }
    }

    private void setInvoiceSeason() {

        if (isStartDateValid.getValue()) {
            double modifier = order.seasonModifier.getValue();
            seasonModifierLabel.setText((modifier * 100) + "%");

            invoiceSeasonModifier.setValue(modifier);
        } else {
            seasonModifierLabel.setText("...");
            invoiceSeasonModifier.setValue(0);
        }
    }

    private Callback<TableColumn<Map.Entry<Extra, Double>, String>,
            TableCell<Map.Entry<Extra, Double>, String>> getActionCellFactory() {
        return new Callback<TableColumn<Map.Entry<Extra, Double>, String>,
                TableCell<Map.Entry<Extra, Double>, String>>() {
            @Override
            public TableCell call( final TableColumn<Map.Entry<Extra, Double>, String> param) {
                final TableCell<Map.Entry<Extra, Double>, String> cell = new TableCell<Map.Entry<Extra, Double>, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            Map.Entry<Extra, Double> entry = getTableView().getItems()
                                    .get(getIndex());

                            Button remove = new Button("Remove");
                            remove.setOnAction((ActionEvent event) -> {
                                order.extras.remove(entry);
                            });
                            setGraphic(remove);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
    }
}
