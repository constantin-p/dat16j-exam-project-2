package assignment.model;


import javafx.beans.property.*;
import store.db.Database;
import store.db.Storable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Extras implements Storable {
    public static final String DB_TABLE_NAME = "extras";

    public String id;
    public StringProperty name;


    public Extras() {
        id = null;
        name = new SimpleStringProperty("");

    }

    public Extras(String id, String name) {
        this.id = id;
        this.name = new SimpleStringProperty(name);

    }

    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("name", name.getValue());


        return values;
    }

    public static Extras construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");


        return new Extras(id, name);
    }

    public static List<Extras> dbGetAll() {
        List<Extras> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable(Extras.DB_TABLE_NAME)
                    .getAll(Arrays.asList("id","name"),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Extras.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static Extras dbGet(String id) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", id);

        try {
            HashMap<String, String> returnValues = Database.getTable(Extras.DB_TABLE_NAME)
                    .get(Arrays.asList("id","name"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(id)) {
                return Extras.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int dbInsert(Extras extras) {
        try {
            return Database.getTable(Extras.DB_TABLE_NAME)
                    .insert(extras.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


}