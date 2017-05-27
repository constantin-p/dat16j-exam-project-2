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

public class Extra implements Storable {
    public static final String DB_TABLE_NAME = "extras";

    public String id;
    public StringProperty name;
    public ObjectProperty<Price> price;

    public Extra() {
        id = null;
        name = new SimpleStringProperty("");
        price = new SimpleObjectProperty<>(null);
    }

    public Extra(String id, String name, Price price) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleObjectProperty<>(price);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("name", name.getValue());
        values.put("price_id", price.getValue().id);

        return values;
    }

    public static Extra construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");

        Price price = Price.dbGet(valuesMap.get("price_id"));

        return new Extra(id, name, price);
    }

    /*
     *  DB helpers
     */
    public static Extra dbGet(String extraID) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", extraID);

        try {
            HashMap<String, String> returnValues = Database.getTable(Extra.DB_TABLE_NAME)
                    .get(Arrays.asList("id", "name", "price_id"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(extraID)) {
                return Extra.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Extra> dbGetAll() {
        List<Extra> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable(Extra.DB_TABLE_NAME)
                    .getAll(Arrays.asList("id", "name", "price_id"),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Extra.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(Extra extra) {
        try {
            return Database.getTable(Extra.DB_TABLE_NAME)
                    .insert(extra.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}