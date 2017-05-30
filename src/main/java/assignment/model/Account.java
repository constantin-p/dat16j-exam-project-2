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

public class Account implements Storable {
    public static final String DB_TABLE_NAME = "accounts";

    private String id;
    public StringProperty username;
    public ObjectProperty<AccountType> type;

    public Account() {
        id = null;
        username = new SimpleStringProperty("");
        type = new SimpleObjectProperty<>(null);
    }

    public Account(String id, String username, AccountType type) {
        this.id = id;
        this.username = new SimpleStringProperty(username);
        this.type = new SimpleObjectProperty(type);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("username", username.getValue());
        values.put("accounttype_id", type.getValue().id);

        return values;
    }

    public static Account construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String username = valuesMap.get("username");

        AccountType type = AccountType.dbGet(valuesMap.get("accounttype_id"));

        return new Account(id, username, type);
    }

    public boolean hasAccess(AccessType accessType) {
        return type.getValue().hasAccess(accessType);
    }

    public boolean hasAccess(String accessTypeName) {
        return type.getValue().hasAccess(accessTypeName);
    }

    /*
     *  DB helpers
     */
    public static Account dbGetByUsername(String username) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("username", username);

        try {
            HashMap<String, String> returnValues = Database.getTable(Account.DB_TABLE_NAME)
                    .get(Arrays.asList("id", "username", "accounttype_id"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("username") != null && returnValues.get("username").equals(username)) {
                return Account.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Account> dbGetAll() {
        List<Account> result = new ArrayList<>();
        try {
            List<HashMap<String, String>> returnList = Database.getTable(Account.DB_TABLE_NAME)
                    .getAll(Arrays.asList("id", "username", "accounttype_id"),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Account.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }
}
