package assignment.model;


import javafx.beans.property.*;
import store.db.Database;
import store.db.Storable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Motorhome implements Storable {
    public static final String DB_TABLE_NAME = "motorhomes";

    public String id;
    public StringProperty brand;
    public StringProperty model;
    public IntegerProperty capacity;

    public Motorhome() {
        id = null;
        brand = new SimpleStringProperty("");
        model = new SimpleStringProperty("");
        capacity = new SimpleIntegerProperty(1);
    }

    public Motorhome(String id, String brand, String model, int capacity) {
        this.id = id;
        this.brand = new SimpleStringProperty(brand);
        this.model = new SimpleStringProperty(model);
        this.capacity = new SimpleIntegerProperty(capacity);
    }

    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("brand", brand.getValue());
        values.put("model", model.getValue());
        values.put("capacity", capacity.getValue().toString());

        return values;
    }

    public static Motorhome construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String brand = valuesMap.get("brand");
        String model = valuesMap.get("model");
        int capacity = Integer.parseInt(valuesMap.get("capacity"));

        return new Motorhome(id, brand, model, capacity);
    }

    public static List<Motorhome> dbGetAll() {
        List<Motorhome> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable(Motorhome.DB_TABLE_NAME)
                    .getAll(Arrays.asList("id","brand", "model", "capacity"),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Motorhome.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static Motorhome dbGet(String id) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", id);

        try {
            HashMap<String, String> returnValues = Database.getTable(Motorhome.DB_TABLE_NAME)
                    .get(Arrays.asList("id", "brand", "model", "capacity"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(id)) {
                return Motorhome.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int dbInsert(Motorhome motorhome) {
        try {
            return Database.getTable(Motorhome.DB_TABLE_NAME)
                    .insert(motorhome.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


}
