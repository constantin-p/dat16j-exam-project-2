package assignment.core.modal;


import assignment.model.*;
import assignment.util.Config;
import assignment.util.Response;
import assignment.util.ValidationHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.LocalDateStringConverter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.time.temporal.ChronoUnit.DAYS;

public class OrderFormController extends ModalBaseController {
    private static final String TITLE_CREATE = "order_create";
    private static final String TITLE_EDIT = "order_edit";
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
    private Button selectMotorhomeButton;
    private BooleanProperty isMotorhomeValid = new SimpleBooleanProperty(false);

    @FXML
    private Button selectClientButton;
    private BooleanProperty isClientValid = new SimpleBooleanProperty(false);

    @FXML
    private ListView<Map.Entry<Extra, Double>> listView;

    @FXML
    private Button addExtraButton;


    // Invoice
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
                        isMotorhomeValid.not()
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


        // 2. Editable fields
        startDatePicker.setConverter(new LocalDateStringConverter(formatter, formatter));
        startDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item.isBefore(LocalDate.now())) {
                            // Not in the future
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        } else if (endDatePicker.getValue() != null) {
                            if (item.isAfter(endDatePicker.getValue().minusDays(1))) {
                                // Not before endDate
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            } else if (startDatePicker.getValue() != null) {
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

                        if (item.isBefore(LocalDate.now().plusDays(1))) {
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
            setInvoiceDays();
        });
        isEndDateValid.set(ValidationHandler.showError(errorLabel,
                ValidationHandler.validateOrderEndDate(endDatePicker.getValue())));
    }

    @Override
    public void handleOKAction(ActionEvent event) {
        if (create) {
            System.out.println("\n\n " + order);
            System.out.println("\n\n " + order.season);
            boolean success = ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateOrderDBOperation(Order.dbInsert(order)));
            if (success) {
                order = Order.dbGetByDateMotorhomeClient(order.startDate.getValue(),
                        order.endDate.getValue(), order.motorhome.getValue().id,
                        order.motorhome.getValue().id);
                System.out.println(order);
                // 1. Add the extras
                // 2. Schedule payment
                // 3. Schedule cleaning job
                // 4. Schedule service job

                // Add the extras
//                extraMap.forEach(entry -> {
//                    if (entry.getValue().getValue() == true) {
//                        accountType.addPermission(entry.getKey());
//                    }
//                });

                order.schedulePayment();
                order.scheduleCleaningJob();
                order.scheduleServiceJob();
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
        Motorhome motorhome = modalDispatcher.showSelectMotorhomeModal(super.stage);

        Response validation = ValidationHandler.validateOrderMotorhome(motorhome);
        if (validation.success) {
            order.motorhome.setValue(motorhome);
            order.motorhomeValue.setValue(motorhome.price.getValue().value.getValue());
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
        Client client = modalDispatcher.showSelectClientModal(super.stage, true);

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
        Extra extra = modalDispatcher.showExtraClientModal(super.stage);

//        Response validation = ValidationHandler.validateOrderClient(client);
//        if (validation.success) {
//            order.client.setValue(client);
//            selectClientButton.setText(client.firstName.getValue() +
//                    " " + client.lastName.getValue());
//        } else {
//            selectClientButton.setText("Select client");
//        }

//        isClientValid.set(ValidationHandler.showError(errorLabel, validation));
    }

    /*
     *  Helpers
     */
    private void setSeasonPrice() {
        if (isStartDateValid.getValue()) {
            MonthDay startDate = MonthDay.of(startDatePicker.getValue().getMonth(),
                    startDatePicker.getValue().getDayOfMonth());
            // TODO: sort seasons by date
            boolean found = false;
            for (int i = 1; i < seasons.size(); i++) {
                if (startDate.isBefore(seasons.get(i).start.getValue())) {
                    found = true;
                    order.season.setValue(seasons.get(i - 1));
                    order.seasonModifier.setValue(order.season.getValue().
                            price.getValue().value.getValue());
                    break;
                }
            }

            if (!found) {
                order.season.setValue(seasons.get(0));
                order.seasonModifier.setValue(order.season.getValue().
                        price.getValue().value.getValue());
            }
        }
        setInvoiceSeason();
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

}
