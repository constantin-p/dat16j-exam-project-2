package assignment.model;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import store.db.Database;
import store.db.Storable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Client implements Storable {
    public static final String DB_TABLE_NAME = "clients";

    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private String id;
    public StringProperty firstName;
    public StringProperty lastName;
    public StringProperty email;
    public ObjectProperty<LocalDate> dateOfBirth;

    public Client() {
        id = null;
        firstName = new SimpleStringProperty("");
        lastName = new SimpleStringProperty("");
        email = new SimpleStringProperty("");
        dateOfBirth = new SimpleObjectProperty<>(LocalDate.now());
    }

    public Client(String id, String firstName, String lastName, String email, LocalDate dateOfBirth) {
        this.id = id;
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.dateOfBirth = new SimpleObjectProperty<>(dateOfBirth);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("first_name", firstName.getValue());
        values.put("last_name", lastName.getValue());
        values.put("email", email.getValue());
        values.put("date_of_birth", DateTimeFormatter.ofPattern(DATE_FORMAT)
                .format(dateOfBirth.getValue()));

        return values;
    }

    public static Client construct(HashMap<String, String> valuesMap) {

        String id = valuesMap.get("id");
        String firstName = valuesMap.get("first_name");
        String lastName = valuesMap.get("last_name");
        String email = valuesMap.get("email");

        LocalDate dateOfBirth = LocalDate.parse(valuesMap.get("date_of_birth"),
                DateTimeFormatter.ofPattern(DATE_FORMAT));

        return new Client(id, firstName, lastName, email, dateOfBirth);
    }


    /*
     *  DB helpers
     */
    public static List<Client> dbGetAll() {
        List<Client> result = new ArrayList<>();
        try {
            List<HashMap<String, String>> returnList = Database.getTable(Client.DB_TABLE_NAME)
                    .getAll(Arrays.asList("id", "first_name", "last_name", "email", "date_of_birth"),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Client.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static Client dbGetByEmail(String email) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("email", email);

        try {
            HashMap<String, String> returnValues = Database.getTable(Client.DB_TABLE_NAME)
                    .get(Arrays.asList("id", "first_name", "last_name", "email", "date_of_birth"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("email") != null && returnValues.get("email").equals(email)) {
                return Client.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int dbInsert(Client client) {
        try {
            return Database.getTable(Client.DB_TABLE_NAME)
                    .insert(client.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
