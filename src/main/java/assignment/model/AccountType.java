package assignment.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import store.db.Storable;

import java.util.HashMap;

public class AccountType implements Storable {

    private String id;
    private StringProperty name;

    public AccountType() {
        id = null;
        name = new SimpleStringProperty("");
    }

    public AccountType(String id, String name) {
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

    public static AccessType construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");

        return new AccessType(id, name);
    }

    /*
     *  DB helpers
     */
}
