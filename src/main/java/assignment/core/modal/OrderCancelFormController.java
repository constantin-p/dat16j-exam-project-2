package assignment.core.modal;


import assignment.model.*;
import assignment.util.CacheEngine;
import assignment.util.Config;
import assignment.util.Response;
import assignment.util.ValidationHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.time.temporal.ChronoUnit.DAYS;

public class OrderCancelFormController extends ModalBaseController {
    private static final String TITLE = "order_cancel";
    private static final String TEMPLATE_PATH = "templates/modal/order-cancel.fxml";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private DecimalFormat decimalFormatter = new DecimalFormat(".##");

    private Order order;

    @FXML
    private Label errorLabel;

    List<CancellationPeriod> cancellationPeriods = CancellationPeriod.dbGetAll();

    @FXML
    private CheckBox confirmationCheckBox;

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
    private Label withoutVATCancellationTotalLabel;

    @FXML
    private Label cancellationLabel;
    @FXML
    private Label cancellationTotalLabel;

    @FXML
    private Label cancellationFeeLabel;

    @FXML
    private Label withoutVATTotalLabel;

    @FXML
    private Label VATLabel;
    @FXML
    private Label VATTotalLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private VBox cancellationDiscountVBox;
    @FXML
    private VBox cancellationFeeVBox;

    private BooleanProperty applyCancellationDiscount = new SimpleBooleanProperty(false);

    private DoubleProperty invoiceDaysTotal = new SimpleDoubleProperty(0.00);
    private DoubleProperty invoiceSeasonModifier = new SimpleDoubleProperty(0.00);
    private NumberBinding invoiceSeasonTotal = invoiceDaysTotal.multiply(invoiceSeasonModifier);
    private NumberBinding invoiceDaysSeasonSubtotal = invoiceDaysTotal.add(invoiceSeasonTotal);
    private DoubleProperty invoiceExtrasSubtotal = new SimpleDoubleProperty(0.00);
    private DoubleProperty invoiceFuelTotal = new SimpleDoubleProperty(0.00);
    private DoubleProperty invoiceTransportTotal = new SimpleDoubleProperty(0.00);
    private NumberBinding invoiceFuelTransportSubtotal = invoiceFuelTotal.add(invoiceTransportTotal);
    private NumberBinding invoiceWithoutVATCancellationTotal = invoiceDaysSeasonSubtotal
            .add(invoiceExtrasSubtotal)
            .add(invoiceFuelTransportSubtotal);
    private DoubleProperty invoiceCancellationTotal = new SimpleDoubleProperty(0.00);;
    private DoubleProperty invoiceWithoutVATTotal = new SimpleDoubleProperty(0.00);
    private DoubleProperty invoiceVATModifier = new SimpleDoubleProperty(0.00);
    private NumberBinding invoiceVATTotal = invoiceWithoutVATTotal.multiply(invoiceVATModifier);
    private NumberBinding invoiceTotal = invoiceWithoutVATTotal
            .add(invoiceVATTotal);

    public OrderCancelFormController(ModalDispatcher modalDispatcher, Stage stage,
                                     Order order) {
        super(modalDispatcher, stage);
        this.order = order;
    }

    @Override
    public void initialize() {
        super.initialize();

        super.isDisabled.bind(
            confirmationCheckBox.selectedProperty().not()
        );


        // Show/hide binding for discount/fee layout
        cancellationDiscountVBox.managedProperty().bind(cancellationDiscountVBox.visibleProperty());
        cancellationDiscountVBox.visibleProperty().bind(applyCancellationDiscount);

        cancellationFeeVBox.managedProperty().bind(cancellationFeeVBox.visibleProperty());
        cancellationFeeVBox.visibleProperty().bind(applyCancellationDiscount.not());

        // Invoice info fields
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

        // Invoice charges fields
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

        withoutVATCancellationTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceWithoutVATCancellationTotal.getValue()), invoiceWithoutVATCancellationTotal));
        cancellationTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceCancellationTotal.getValue()), invoiceCancellationTotal));
        cancellationFeeLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceWithoutVATTotal.getValue()), invoiceWithoutVATTotal));

        withoutVATTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceWithoutVATTotal.getValue()), invoiceWithoutVATTotal));
        VATTotalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceVATTotal.getValue()), invoiceVATTotal));
        totalLabel.textProperty().bind(Bindings.createStringBinding(() ->
                decimalFormatter.format(invoiceTotal.getValue()), invoiceTotal));

        // Set client
        clientNameLabel.setText(order.client.getValue().firstName.getValue() +
                    " " + order.client.getValue().lastName.getValue());
        clientEmailLabel.setText(order.client.getValue().email.getValue());

        // Set motorhome
        motorhomeLabel.setText(order.motorhome.getValue().brand.getValue() +
                    " - " + order.motorhome.getValue().model.getValue());
        motorhomeMileageStartLabel.setText(order.motorhome.getValue().mileage.getValue() + "km");

        // Set days
        long days = DAYS.between(order.startDate.getValue(), order.endDate.getValue());
        daysLabel.setText(days + "× " +
            decimalFormatter.format(order.motorhome.getValue().price.getValue().value.getValue()) + "kr");

        invoiceDaysTotal.setValue(days * order.motorhome.getValue().price.getValue().value.getValue());

        // Set season
        double modifier = order.seasonModifier.getValue();
        seasonModifierLabel.setText((modifier * 100) + "%");

        invoiceSeasonModifier.setValue(modifier);

        setCancellationCost();
    }

    @Override
    public void handleOKAction(ActionEvent event) {
        // 1. Update the order to mark it as cancelled
        //    If the original invoice has been paid
        // 2.       Schedule a refund

        boolean success = ValidationHandler.showError(errorLabel,
                    ValidationHandler.validateOrderDBOperation(Order.dbUpdate(order.id,
                            order.cancellationValue.getValue())));
        if (success) {
            Invoice invoice = Invoice.dbGetByOrderID(order.id);
            Payment payment = Payment.dbGetByInvoiceID(invoice.id);

            if (payment.date.getValue() != null) {
                Refund.dbInsert(new Refund(null, invoice, payment, null));
                CacheEngine.markForUpdate("refunds");
            }

            CacheEngine.markForUpdate("payments");
            CacheEngine.markForUpdate("cleaning");
            CacheEngine.markForUpdate("service");
            CacheEngine.markForUpdate("orders");
            super.handleOKAction(event);
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
        return TITLE;
    }

    /*
     *  Helpers
     */
    private void setCancellationCost() {
        // Calculate the cancellation cost and:
        // in case of a modifier, show the discount layout
        // in case of a fixed value, show the fee layout

        if (cancellationPeriods.size() > 0) {
            cancellationPeriods.sort((CancellationPeriod a, CancellationPeriod b) -> {
                // -1 represent infinite (move it to the end)
                if (a.start.getValue() == -1) {
                    return 1;
                } else if (b.start.getValue() == -1) {
                    return -1;
                }
                return a.start.getValue() - b.start.getValue();
            });

            double daysDiff = DAYS.between(LocalDate.now(), order.startDate.getValue());
            boolean found = false;
            for (int i = 0; i < cancellationPeriods.size() - 1; i++) {
                if (daysDiff <= cancellationPeriods.get(i).start.getValue()) {
                    found = true;
                    applyCancellationDiscount.setValue(true);

                    double modifier = cancellationPeriods.get(i).price.getValue().value.getValue();
                    cancellationLabel.setText((modifier * 100) + "%");

                    invoiceCancellationTotal.setValue(invoiceWithoutVATCancellationTotal
                            .doubleValue() * modifier * -1);
                    invoiceWithoutVATTotal.setValue(invoiceWithoutVATCancellationTotal
                            .doubleValue() + invoiceCancellationTotal.getValue());

                    order.cancellationValue.setValue(modifier);
                    break;
                }
            }

            if (!found) {
                double modifier = cancellationPeriods.get(cancellationPeriods.size() - 1)
                        .price.getValue().value.getValue();
                double minimum = cancellationPeriods.get(cancellationPeriods.size() - 1)
                        .minimumPrice.getValue().value.getValue();

                double amountAfterDiscount = invoiceWithoutVATCancellationTotal
                        .doubleValue() * modifier * -1;

                if (minimum > amountAfterDiscount) {
                    applyCancellationDiscount.setValue(false);

                    order.cancellationValue.setValue(minimum);
                    invoiceWithoutVATTotal.setValue(minimum);
                } else {
                    applyCancellationDiscount.setValue(true);

                    order.cancellationValue.setValue(modifier);
                    invoiceCancellationTotal.setValue(amountAfterDiscount);
                    invoiceWithoutVATTotal.setValue(invoiceWithoutVATCancellationTotal
                            .doubleValue() + invoiceCancellationTotal.getValue());
                }
            }

        }
    }

}
