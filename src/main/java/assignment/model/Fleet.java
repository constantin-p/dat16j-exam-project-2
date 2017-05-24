package assignment.model;


import javafx.beans.property.*;
import store.db.Database;
import store.db.Storable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Fleet implements Storable {
    public static final String DB_TABLE_NAME = "fleet";

    public String id;
    public StringProperty name;
    public IntegerProperty capacity;

    public Fleet() {
        id = null;
        name = new SimpleStringProperty("");
        capacity = null;
    }

    public Fleet(String id, String name, int capacity) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.capacity = new SimpleIntegerProperty(capacity);
    }

    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("name", name.getValue());
        values.put("capacity", capacity.getValue().toString());

        return values;
    }

    public static Fleet construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");
        int capacity = Integer.parseInt(valuesMap.get("capacity"));

        return new Fleet(id, name, capacity);
    }

    public static List<Fleet> dbGetAll() {
        List<Fleet> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable("fleet")
                    .getAll(Arrays.asList("id", "name","capacity"),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Fleet.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static Fleet dbGet(String id) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", id);

        try {
            HashMap<String, String> returnValues = Database.getTable("players")
                    .get(Arrays.asList("id", "name","capacity"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(id)) {
                return Fleet.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Account dbGetByName(String name) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("name", name);

        try {
            HashMap<String, String> returnValues = Database.getTable(Account.DB_TABLE_NAME)
                    .get(Arrays.asList("id", "username", "capacity"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("name") != null && returnValues.get("name").equals(name)) {
                return Account.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Fleet dbGetByCapacity(String capacity) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("capacity", capacity);

        try {
            HashMap<String, String> returnValues = Database.getTable("fleet")
                    .get(Arrays.asList("id", "name", "capacity"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("capacity").equals(capacity)) {
                return Fleet.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int dbInsert(Fleet fleet) {
        try {
            return Database.getTable(Fleet.DB_TABLE_NAME)
                    .insert(fleet.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


}
