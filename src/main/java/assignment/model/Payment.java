package assignment.model;


import javafx.beans.property.*;
import store.db.Database;
import store.db.Storable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Payment implements Storable {
    public static final String DB_TABLE_NAME = "payments";
    public static final String[] DB_TABLE_COLUMNS = {"id", "invoice_id", "date"};
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DB_DATE_FORMAT);

    public String id;
    public ObjectProperty<Invoice> invoice;
    public ObjectProperty<LocalDate> date;

    public Payment() {
        id = null;
        invoice = new SimpleObjectProperty<>(null);
        date = new SimpleObjectProperty<>(null);
    }

    public Payment(String id, Invoice invoice, LocalDate date) {
        this.id = id;
        this.invoice = new SimpleObjectProperty(invoice);
        this.date = new SimpleObjectProperty(date);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("invoice_id", invoice.getValue().id);
        values.put("date", formatter.format(date.getValue()));

        return values;
    }

    public static Payment construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");

        Invoice invoice = Invoice.dbGet(valuesMap.get("invoice_id"));
        LocalDate date = LocalDate.parse(valuesMap.get("date"), formatter);

        return new Payment(id, invoice, date);
    }

    /*
     *  DB helpers
     */
    public static Payment dbGet(String paymentID) {
        if (paymentID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", paymentID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(paymentID)) {
                return Payment.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Payment> dbGetAll() {
        List<Payment> result = new ArrayList<>();
        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Payment.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(Payment payment) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(payment.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
