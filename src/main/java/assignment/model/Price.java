package assignment.model;


import javafx.beans.property.*;
import store.db.Database;
import store.db.Storable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Price implements Storable {
    public static final String DB_TABLE_NAME = "prices";
    public static final String[] DB_TABLE_COLUMNS = {"id", "name", "value", "pricetype_id"};

    public String id;
    public StringProperty name;
    public DoubleProperty value;
    public ObjectProperty<PriceType> type;

    public Price() {
        id = null;
        name = new SimpleStringProperty("");
        value = new SimpleDoubleProperty(0.00);
        type = new SimpleObjectProperty<>(null);
    }

    public Price(String id, String name, double value, PriceType type) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleDoubleProperty(value);
        this.type = new SimpleObjectProperty(type);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("name", name.getValue());
        values.put("value", value.getValue().toString());
        values.put("pricetype_id", type.getValue().id);

        return values;
    }

    public static Price construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");
        double value = Double.valueOf(valuesMap.get("value"));

        PriceType type = PriceType.dbGet(valuesMap.get("pricetype_id"));

        return new Price(id, name, value, type);
    }

    /*
     *  DB helpers
     */
    public static Price dbGet(String priceID) {
        if (priceID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", priceID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(priceID)) {
                return Price.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Price dbGetByName(String name) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("name", name);

        try {
            HashMap<String, String> returnValues = Database.getTable(Price.DB_TABLE_NAME)
                    .get(Arrays.asList("id", "name", "value", "pricetype_id"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("name") != null && returnValues.get("name").equals(name)) {
                return Price.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Price> dbGetAll() {
        List<Price> result = new ArrayList<>();
        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Price.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(Price price) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(price.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
