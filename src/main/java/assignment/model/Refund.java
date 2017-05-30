package assignment.model;


import assignment.util.Config;
import javafx.beans.property.*;
import store.db.Database;
import store.db.Storable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class Refund implements Storable {
    public static final String DB_TABLE_NAME = "refunds";
    public static final String[] DB_TABLE_COLUMNS = {"id", "invoice_id", "payment_id", "date"};
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DB_DATE_FORMAT);

    public String id;
    public ObjectProperty<Invoice> invoice;
    public ObjectProperty<Payment> payment;
    public ObjectProperty<LocalDate> date;

    public Refund() {
        id = null;
        invoice = new SimpleObjectProperty<>(null);
        payment = new SimpleObjectProperty<>(null);
        date = new SimpleObjectProperty<>(null);
    }

    public Refund(String id, Invoice invoice, Payment payment, LocalDate date) {
        this.id = id;
        this.invoice = new SimpleObjectProperty(invoice);
        this.payment = new SimpleObjectProperty(payment);
        this.date = new SimpleObjectProperty(date);
    }

    public double getTotal() {
        Properties invoiceProperties = Config.getConfig("invoice");
        Order order = invoice.getValue().order.getValue();
        if (order.cancellationValue.getValue() > 1) {
            // fixed cost
            double orderTotal = order.cancellationValue.getValue();

            return payment.getValue().getTotal() - orderTotal;
        }
        // cancellation modifier
        double total;
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
        // Add cancellation discount
        double cancellationModifier = order.cancellationValue.getValue();
        total += total * cancellationModifier * -1;

        // Add VAT
        double VATModifier = Double.valueOf(invoiceProperties.getProperty("INVOICE_VAT_MODIFIER"));
        total += total * VATModifier;

        return payment.getValue().getTotal() - total;
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("invoice_id", invoice.getValue().id);
        values.put("payment_id", payment.getValue().id);
        if (date.getValue() == null) {
            values.put("date", null);
        } else {
            values.put("date", formatter.format(date.getValue()));
        }

        return values;
    }

    public static Refund construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");

        Invoice invoice = Invoice.dbGet(valuesMap.get("invoice_id"));
        Payment payment = Payment.dbGet(valuesMap.get("payment_id"));

        LocalDate date = null;
        if (valuesMap.get("date") != null) {
            date = LocalDate.parse(valuesMap.get("date"), formatter);
        }

        return new Refund(id, invoice, payment, date);
    }

    /*
     *  DB helpers
     */
    public static Refund dbGet(String refundID) {
        if (refundID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", refundID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(refundID)) {
                return Refund.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Refund> dbGetAll() {
        List<Refund> result = new ArrayList<>();
        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Refund.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(Refund refund) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(refund.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int dbUpdate(String refundID, LocalDate date) {
        HashMap<String, String> entry = new HashMap<>();
        entry.put("date", (date == null) ? null : formatter.format(date));

        HashMap<String, String> whitelist = new HashMap<>();
        whitelist.put("id", refundID);

        try {
            return Database.getTable(DB_TABLE_NAME)
                    .update(entry, whitelist, new HashMap<>());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
