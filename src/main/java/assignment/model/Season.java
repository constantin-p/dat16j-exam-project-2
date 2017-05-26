package assignment.model;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import store.db.Database;
import store.db.Storable;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Season implements Storable {
    public static final String DB_TABLE_NAME = "seasons";
    public static final String[] DB_TABLE_COLUMNS = {"id", "name", "start", "price_id"};
    public static final String DB_START_DATE_FORMAT = "MM:dd";

    public String id;
    public StringProperty name;
    public ObjectProperty<MonthDay> start;
    public ObjectProperty<Price> price;

    public Season() {
        id = null;
        name = new SimpleStringProperty("");
        start = new SimpleObjectProperty<>(null);
        price = new SimpleObjectProperty<>(null);
    }

    public Season(String id, String name, MonthDay start, Price price) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.start = new SimpleObjectProperty<>(start);
        this.price = new SimpleObjectProperty<>(price);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("name", name.getValue());

        values.put("start", start.getValue().format(DateTimeFormatter.ofPattern(DB_START_DATE_FORMAT)));
        values.put("price_id", price.getValue().id);

        return values;
    }

    public static Season construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");

        MonthDay start = MonthDay.parse(valuesMap.get("start"),
                DateTimeFormatter.ofPattern(DB_START_DATE_FORMAT));

        Price price = Price.dbGet(valuesMap.get("price_id"));

        return new Season(id, name, start, price);
    }

    /*
     *  DB helpers
     */
    public static Season dbGet(String id) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", id);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(id)) {
                return Season.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Season> dbGetAll() {
        List<Season> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Season.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(Season season) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(season.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}