package assignment.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import store.db.Database;
import store.db.Storable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AccountType implements Storable {
    private static final String DB_TABLE_NAME = "accounttypes";
    private static final String DB_INTERSECTION_TABLE_NAME = "accounttype_accesstype";

    public String id;
    public StringProperty name;
    public ObservableList<AccessType> permissions = FXCollections.observableArrayList();

    public AccountType() {
        id = null;
        name = new SimpleStringProperty("");
    }

    public AccountType(String id, String name) {
        this.id = id;
        this.name = new SimpleStringProperty(name);

        if (this.id != null) {
            permissions.setAll(AccountType.dbGetAllAccessTypes(this.id));
        }
    }

    public void addPermission(AccessType accessType) {
        if (AccountType.dbInsertAccessType(id, accessType.id) == 1) {
            permissions.setAll(AccountType.dbGetAllAccessTypes(id));
        }
    }

    public boolean hasAccess(AccessType accessType) {
        for (AccessType accountAccessType: permissions) {
            if (accountAccessType.name.getValue().equals(accessType.name.getValue())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccess(String accessTypeName) {
        for (AccessType accountAccessType: permissions) {
            if (accountAccessType.name.getValue().equals(accessTypeName)) {
                return true;
            }
        }
        return false;
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

    public static AccountType construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");

        return new AccountType(id, name);
    }

    /*
     *  DB helpers
     */
    public static AccountType dbGet(String accountTypeID) {
        if (accountTypeID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", accountTypeID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList("id", "name"), searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(accountTypeID)) {
                return AccountType.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AccountType dbGetByName(String name) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("name", name);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList("id", "name"), searchQuery, new HashMap<>());

            if (returnValues.get("name") != null && returnValues.get("name").equals(name)) {
                return AccountType.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<AccountType> dbGetAll() {
        List<AccountType> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList("id", "name"), null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(AccountType.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static List<AccessType> dbGetAllAccessTypes(String accountTypeID) {
        if (accountTypeID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        List<AccessType> result = new ArrayList<>();

        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("accounttype_id", accountTypeID);

        try {
            List<HashMap<String, String>> returnList = Database
                    .getTable(DB_INTERSECTION_TABLE_NAME)
                    .getAll(Arrays.asList("accesstype_id"),
                            searchQuery, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(AccessType.dbGet(valuesMap.get("accesstype_id")));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(AccountType accountType) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(accountType.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int dbInsertAccessType(String accountTypeID, String accessTypeID) {
        if (accountTypeID == null || accessTypeID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> entry = new HashMap<>();
        entry.put("accounttype_id", accountTypeID);
        entry.put("accesstype_id", accessTypeID);

        try {
            return Database.getTable(DB_INTERSECTION_TABLE_NAME)
                .insert(entry);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
