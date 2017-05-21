package assignment.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import store.db.Storable;

import java.util.HashMap;

public class Account implements Storable {

    private String id;
    private StringProperty username;

    public Account() {
        id = null;
        username = new SimpleStringProperty("");
    }

    public Account(String id, String username) {
        this.id = id;
        this.username = new SimpleStringProperty(username);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("username", username.getValue());

        return values;
    }

    public static Account construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String username = valuesMap.get("username");

        return new Account(id, username);
    }

    /*
     *  DB helpers
     */
}
