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

public class Invoice implements Storable {
    public static final String DB_TABLE_NAME = "invoices";
    public static final String[] DB_TABLE_COLUMNS = {"id", "order_id", "date", "due_date"};
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DB_DATE_FORMAT);

    public String id;
    public ObjectProperty<Order> order;
    public ObjectProperty<LocalDate> date;
    public ObjectProperty<LocalDate> dueDate;

    public Invoice() {
        id = null;
        order = new SimpleObjectProperty<>(null);
        date = new SimpleObjectProperty<>(null);
        dueDate = new SimpleObjectProperty<>(null);
    }

    public Invoice(String id, Order order, LocalDate date,  LocalDate dueDate) {
        this.id = id;
        this.order = new SimpleObjectProperty(order);
        this.date = new SimpleObjectProperty(date);
        this.dueDate = new SimpleObjectProperty(dueDate);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("order_id", order.getValue().id);
        values.put("date", formatter.format(date.getValue()));
        values.put("due_date", formatter.format(dueDate.getValue()));

        return values;
    }

    public static Invoice construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");

        Order order = Order.dbGet(valuesMap.get("order_id"));
        LocalDate date = LocalDate.parse(valuesMap.get("date"), formatter);
        LocalDate dueDate = LocalDate.parse(valuesMap.get("due_date"), formatter);

        return new Invoice(id, order, date, dueDate);
    }

    /*
     *  DB helpers
     */
    public static Invoice dbGet(String invoiceID) {
        if (invoiceID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", invoiceID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(invoiceID)) {
                return Invoice.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Invoice> dbGetAll() {
        List<Invoice> result = new ArrayList<>();
        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Invoice.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(Invoice invoice) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(invoice.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
