package assignment.model;


import javafx.beans.property.*;
import store.db.Database;
import store.db.Storable;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CancellationPeriod implements Storable {
    public static final String DB_TABLE_NAME = "cancellation_periods";
    public static final String[] DB_TABLE_COLUMNS = {"id", "name", "start", "price_id", "minimum_price_id"};

    public String id;
    public StringProperty name;
    public IntegerProperty start;
    public ObjectProperty<Price> price;
    public ObjectProperty<Price> minimumPrice;

    public CancellationPeriod() {
        id = null;
        name = new SimpleStringProperty("");
        start = new SimpleIntegerProperty(0);
        price = new SimpleObjectProperty<>(null);
        minimumPrice = new SimpleObjectProperty<>(null);
    }

    public CancellationPeriod(String id, String name, int start, Price price, Price minimumPrice) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.start = new SimpleIntegerProperty(start);
        this.price = new SimpleObjectProperty<>(price);
        this.minimumPrice = new SimpleObjectProperty<>(minimumPrice);
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("name", name.getValue());

        if (start.getValue() == -1) {
            values.put("start", null);
        } else {
            values.put("start", start.getValue().toString());
        }

        values.put("price_id", price.getValue().id);
        if (minimumPrice.getValue() == null) {
            values.put("minimum_price_id", null);
        } else {
            values.put("minimum_price_id", minimumPrice.getValue().id);
        }

        return values;
    }

    public static CancellationPeriod construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");
        String name = valuesMap.get("name");

        int start = -1;
        if (valuesMap.get("start") != null) {
            start = Integer.valueOf(valuesMap.get("start"));
        }

        Price price = Price.dbGet(valuesMap.get("price_id"));
        Price minimumPrice = null;
        if (valuesMap.get("minimum_price_id") != null) {
            minimumPrice = Price.dbGet(valuesMap.get("minimum_price_id"));
        }

        return new CancellationPeriod(id, name, start, price, minimumPrice);
    }

    /*
     *  DB helpers
     */
    public static CancellationPeriod dbGet(String cancellationPeriodID) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", cancellationPeriodID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(cancellationPeriodID)) {
                return CancellationPeriod.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<CancellationPeriod> dbGetAll() {
        List<CancellationPeriod> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(CancellationPeriod.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(CancellationPeriod CancellationPeriod) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(CancellationPeriod.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}