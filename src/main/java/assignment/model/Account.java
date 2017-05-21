package assignment.model;


import javafx.beans.property.StringProperty;
import store.db.Storable;

import java.util.HashMap;

public class Account implements Storable {

    private StringProperty username;


    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("username", username.getValue());

        return values;
    }

    public static AccessType construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("username");

        return new AccessType(id, name);
    }

    /*
     *  DB helpers
     */
}
