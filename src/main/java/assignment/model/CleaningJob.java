package assignment.model;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import store.db.Database;
import store.db.Storable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CleaningJob implements Storable {
    public static final String DB_TABLE_NAME = "cleaningjobs";
    public static final String[] DB_TABLE_COLUMNS = {"id", "order_id", "date", "done"};
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DB_DATE_FORMAT);

    public String id;
    public ObjectProperty<Order> order;
    public ObjectProperty<LocalDate> date;
    public BooleanProperty done;

    public CleaningJob() {
        id = null;
        order = new SimpleObjectProperty<>(null);
        date = new SimpleObjectProperty<>(null);
        done = new SimpleBooleanProperty(false);
    }

    public CleaningJob(String id, Order order, LocalDate date, boolean done) {
        this.id = id;
        this.order = new SimpleObjectProperty(order);
        this.date = new SimpleObjectProperty(date);
        this.done = new SimpleBooleanProperty(done);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("order_id", order.getValue().id);
        values.put("date", formatter.format(date.getValue()));
        values.put("done", done.getValue() ? "1" : "0");

        return values;
    }

    public static CleaningJob construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");

        Order order = Order.dbGet(valuesMap.get("order_id"));

        LocalDate date = LocalDate.parse(valuesMap.get("date"), formatter);
        boolean done = valuesMap.get("done").equals("1");

        return new CleaningJob(id, order, date, done);
    }

    /*
     *  DB helpers
     */
    public static CleaningJob dbGet(String cleaningJobID) {
        if (cleaningJobID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", cleaningJobID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(cleaningJobID)) {
                return CleaningJob.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<CleaningJob> dbGetAll() {
        List<CleaningJob> result = new ArrayList<>();
        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(CleaningJob.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(CleaningJob cleaningJob) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(cleaningJob.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int dbUpdate(String cleaningJobID, boolean done) {
        HashMap<String, String> entry = new HashMap<>();
        entry.put("done", done ? "1" : "0");

        HashMap<String, String> whitelist = new HashMap<>();
        whitelist.put("id", cleaningJobID);

        try {
            return Database.getTable(DB_TABLE_NAME)
                    .update(entry, whitelist, new HashMap<>());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
