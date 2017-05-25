package assignment.model;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import store.db.Database;
import store.db.Storable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Price implements Storable {
    public static final String DB_TABLE_NAME = "prices";

    private String id;
    public StringProperty name;
    public StringProperty value;
    public ObjectProperty<PriceType> type;

    public Price() {
        id = null;
        name = new SimpleStringProperty("");
        value = new SimpleStringProperty("");
        type = new SimpleObjectProperty<>(null);
    }

    public Price(String id, String name, String value, PriceType type) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleStringProperty(value);
        this.type = new SimpleObjectProperty(type);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("name", name.getValue());
        values.put("value", value.getValue());
        values.put("pricetype_id", type.getValue().id);

        return values;
    }

    public static Price construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");
        String value = valuesMap.get("value");

        PriceType type = PriceType.dbGet(valuesMap.get("pricetype_id"));

        return new Price(id, name, value, type);
    }

    /*
     *  DB helpers
     */
    public static List<Price> dbGetAll() {
        List<Price> result = new ArrayList<>();
        try {
            List<HashMap<String, String>> returnList = Database.getTable(Price.DB_TABLE_NAME)
                    .getAll(Arrays.asList("id", "name", "value", "pricetype_id"),
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

    public static int dbInsert(Price price) {
        try {
            return Database.getTable(Price.DB_TABLE_NAME)
                    .insert(price.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
