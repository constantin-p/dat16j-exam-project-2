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

public class RepairJob implements Storable {
    public static final String DB_TABLE_NAME = "repairjobs";
    public static final String[] DB_TABLE_COLUMNS = {"id", "motorhome_id",
            "date", "done", "details"};
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DB_DATE_FORMAT);

    public String id;
    public ObjectProperty<Motorhome> motorhome;
    public ObjectProperty<LocalDate> date;
    public StringProperty details;
    public BooleanProperty done;

    public RepairJob() {
        id = null;
        motorhome = new SimpleObjectProperty<>(null);
        date = new SimpleObjectProperty<>(null);
        done = new SimpleBooleanProperty(false);
        details = new SimpleStringProperty(null);
    }

    public RepairJob(String id, Motorhome motorhome, LocalDate date, boolean done, String details) {
        this.id = id;
        this.motorhome = new SimpleObjectProperty(motorhome);
        this.date = new SimpleObjectProperty(date);
        this.done = new SimpleBooleanProperty(done);
        this.details = new SimpleStringProperty(details);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("motorhome_id", motorhome.getValue().id);
        values.put("date", formatter.format(date.getValue()));
        values.put("done", done.getValue() ? "1" : "0");
        values.put("details", details.getValue());

        return values;
    }

    public static RepairJob construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");

        Motorhome motorhome = Motorhome.dbGet(valuesMap.get("motorhome_id"));

        LocalDate date = LocalDate.parse(valuesMap.get("date"), formatter);
        boolean done = valuesMap.get("done").equals("1");
        String details = valuesMap.get("details");

        return new RepairJob(id, motorhome, date, done, details);
    }

    /*
     *  DB helpers
     */
    public static RepairJob dbGet(String repairJobID) {
        if (repairJobID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", repairJobID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(repairJobID)) {
                return RepairJob.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<RepairJob> dbGetAll() {
        List<RepairJob> result = new ArrayList<>();
        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                System.out.println("   REPAIR" + valuesMap);
                result.add(RepairJob.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(RepairJob repairJob) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(repairJob.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int dbUpdate(String repairJobID, boolean done) {
        HashMap<String, String> entry = new HashMap<>();
        entry.put("done", done ? "1" : "0");

        HashMap<String, String> whitelist = new HashMap<>();
        whitelist.put("id", repairJobID);

        try {
            return Database.getTable(DB_TABLE_NAME)
                    .update(entry, whitelist, new HashMap<>());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
