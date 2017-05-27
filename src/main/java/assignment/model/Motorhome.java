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
    public static final String[] DB_TABLE_COLUMNS = {"id", "brand", "model", "capacity", "mileage", "price_id"};

    public String id;
    public StringProperty brand;
    public StringProperty model;
    public IntegerProperty capacity;
    public IntegerProperty mileage;
    public ObjectProperty<Price> price;

    public Motorhome() {
        id = null;
        brand = new SimpleStringProperty("");
        model = new SimpleStringProperty("");
        capacity = new SimpleIntegerProperty(1);
        mileage = new SimpleIntegerProperty(0);
        price = new SimpleObjectProperty<>(null);
    }

    public Motorhome(String id, String brand, String model, int capacity, int mileage, Price price) {
        this.id = id;
        this.brand = new SimpleStringProperty(brand);
        this.model = new SimpleStringProperty(model);
        this.capacity = new SimpleIntegerProperty(capacity);
        this.mileage = new SimpleIntegerProperty(mileage);
        this.price = new SimpleObjectProperty<>(price);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("brand", brand.getValue());
        values.put("model", model.getValue());
        values.put("capacity", capacity.getValue().toString());
        values.put("mileage", mileage.getValue().toString());
        values.put("price_id", price.getValue().id);

        return values;
    }

    public static Motorhome construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String brand = valuesMap.get("brand");
        String model = valuesMap.get("model");
        int capacity = Integer.valueOf(valuesMap.get("capacity"));
        int mileage = Integer.valueOf(valuesMap.get("mileage"));

        Price price = Price.dbGet(valuesMap.get("price_id"));

        return new Motorhome(id, brand, model, capacity, mileage, price);
    }

    /*
     *  DB helpers
     */
    public static Motorhome dbGet(String motorhomeID) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", motorhomeID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(motorhomeID)) {
                return Motorhome.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Motorhome> dbGetAll() {
        List<Motorhome> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
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

    public static int dbInsert(Motorhome motorhome) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(motorhome.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
