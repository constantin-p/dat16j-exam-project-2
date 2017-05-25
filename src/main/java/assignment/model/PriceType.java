package assignment.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import store.db.Database;
import store.db.Storable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PriceType implements Storable {
    private static final String DB_TABLE_NAME = "pricetypes";

    public String id;
    public StringProperty name;

    public PriceType() {
        id = null;
        name = new SimpleStringProperty("");
    }

    public PriceType(String id, String name) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("name", name.getValue());

        return values;
    }

    public static PriceType construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");

        return new PriceType(id, name);
    }

    /*
     *  DB helpers
     */
    public static PriceType dbGet(String priceTypeID) {
        if (priceTypeID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", priceTypeID);

        try {
            HashMap<String, String> returnValues = Database.getTable(PriceType.DB_TABLE_NAME)
                    .get(Arrays.asList("id", "name"), searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(priceTypeID)) {
                return PriceType.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<PriceType> dbGetAll() {
        List<PriceType> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable(PriceType.DB_TABLE_NAME)
                    .getAll(Arrays.asList("id", "name"), null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(PriceType.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }
}
