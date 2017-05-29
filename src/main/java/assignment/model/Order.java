package assignment.model;


import assignment.util.Config;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import store.db.Database;
import store.db.Storable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Order implements Storable {
    public static final String DB_TABLE_NAME = "orders";
    private static final String DB_INTERSECTION_TABLE_NAME = "order_extra";
    public static final String[] DB_TABLE_COLUMNS = {"id", "start_date", "end_date",
            "pick_up", "pick_up_distance", "drop_off", "drop_off_distance",
            "client_id", "motorhome_id", "motorhome_price_value",
            "motorhome_mileage_start", "motorhome_mileage_end",
            "season_price_modifier", "cancellation_price_value"};
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DB_DATE_FORMAT);

    public String id;
    public ObjectProperty<LocalDate> startDate;
    public ObjectProperty<LocalDate> endDate;
    public StringProperty pickUp;
    public IntegerProperty pickUpDistance;
    public StringProperty dropOff;
    public IntegerProperty dropOffDistance;

    public ObjectProperty<Client> client;

    public ObjectProperty<Motorhome> motorhome;
    public DoubleProperty motorhomeValue;
    public IntegerProperty motorhomeMileageStart;
    public IntegerProperty motorhomeMileageEnd;

    public DoubleProperty seasonModifier;

    public boolean isCancelled;
    public DoubleProperty cancellationValue; // null: order not cancelled, <=1: modifier, >1: fixed value

    public ObservableList<Map.Entry<Extra, Double>> extras = FXCollections.observableArrayList();

    public Order() {
        id = null;
        startDate = new SimpleObjectProperty<>(LocalDate.now().plusDays(1));
        endDate = new SimpleObjectProperty<>(startDate.getValue().plusDays(1));
        pickUp = new SimpleStringProperty("");
        pickUpDistance = new SimpleIntegerProperty(0);
        dropOff = new SimpleStringProperty("");
        dropOffDistance = new SimpleIntegerProperty(0);
        client = new SimpleObjectProperty<>(null);
        motorhome = new SimpleObjectProperty<>(null);
        motorhomeValue = new SimpleDoubleProperty(0.00);
        motorhomeMileageStart = new SimpleIntegerProperty(0);
        motorhomeMileageEnd = new SimpleIntegerProperty(0);
        seasonModifier = new SimpleDoubleProperty(0.00);

        isCancelled = false;
        cancellationValue = new SimpleDoubleProperty(0.00);
    }

    public Order(String id, LocalDate startDate, LocalDate endDate,
                 String pickUp, Integer pickUpDistance, String dropOff, Integer dropOffDistance,
                 Client client, Motorhome motorhome, Double motorhomeValue,
                 int motorhomeMileageStart, int motorhomeMileageEnd,
                 Double seasonModifier, boolean isCanceled, Double cancellationValue) {
        this.id = id;
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.pickUp = new SimpleStringProperty(pickUp);
        this.pickUpDistance = new SimpleIntegerProperty(pickUpDistance);
        this.dropOff = new SimpleStringProperty(dropOff);
        this.dropOffDistance = new SimpleIntegerProperty(dropOffDistance);
        this.client = new SimpleObjectProperty<>(client);
        this.motorhome = new SimpleObjectProperty<>(motorhome);
        this.motorhomeValue = new SimpleDoubleProperty(motorhomeValue);
        this.motorhomeMileageStart = new SimpleIntegerProperty(motorhomeMileageStart);
        this.motorhomeMileageEnd = new SimpleIntegerProperty(motorhomeMileageEnd);
        this.seasonModifier = new SimpleDoubleProperty(seasonModifier);

        this.isCancelled = isCanceled;
        this.cancellationValue = new SimpleDoubleProperty(cancellationValue);

        if (this.id != null) {
            extras.setAll(Order.dbGetAllExtras(this.id));
        }
    }

    public void schedulePayment() {
        Invoice invoice = generateInvoice();

        Payment payment = new Payment(null, invoice, null);
        Payment.dbInsert(payment);
    }

    public void scheduleCleaningJob() {
        CleaningJob cleaningJob = new CleaningJob(null, this,
                endDate.getValue().plusDays(1), false);
        CleaningJob.dbInsert(cleaningJob);
    }

    public void scheduleServiceJob() {
        ServiceJob serviceJob = new ServiceJob(null, this,
                endDate.getValue().plusDays(1), false);
        ServiceJob.dbInsert(serviceJob);
    }

    public Invoice generateInvoice() {
        int paymentPeriod = Integer.valueOf(Config.getConfig("invoice")
                .getProperty("INVOICE_PAYMENT_PERIOD"));
        Invoice invoice = new Invoice(null, this,
                LocalDate.now(), LocalDate.now().plusDays(paymentPeriod));

        if (Invoice.dbInsert(invoice) == 1) {
            return Invoice.dbGetByOrderID(id);
        }
        return null;
    }

    public boolean hasInvoice() {
        return Invoice.dbGetByOrderID(id).id != null;
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<>();

        values.put("start_date", startDate.getValue().format(formatter));
        values.put("end_date", endDate.getValue().format(formatter));
        values.put("pick_up", pickUp.getValue());
        values.put("pick_up_distance", pickUpDistance.getValue().toString());
        values.put("drop_off", dropOff.getValue());
        values.put("drop_off_distance", dropOffDistance.getValue().toString());
        values.put("client_id", client.getValue().id);
        values.put("motorhome_id", motorhome.getValue().id);
        values.put("motorhome_price_value", motorhomeValue.getValue().toString());
        values.put("motorhome_mileage_start", motorhomeMileageStart.getValue().toString());
        values.put("motorhome_mileage_end", motorhomeMileageEnd.getValue().toString());
        values.put("season_price_modifier", seasonModifier.getValue().toString());

        if (isCancelled) {
            values.put("cancellation_price_value", cancellationValue.getValue().toString());
        } else {
            values.put("cancellation_price_value", null);
        }

        return values;
    }

    public static Order construct(HashMap<String, String> valuesMap) {
        String id = valuesMap.get("id");

        LocalDate startDate = LocalDate.parse(valuesMap.get("start_date"), formatter);
        LocalDate endDate = LocalDate.parse(valuesMap.get("end_date"), formatter);

        String pickUp = valuesMap.get("pick_up");
        int pickUpDistance = Integer.valueOf(valuesMap.get("pick_up_distance"));
        String dropOff = valuesMap.get("drop_off");
        int dropOffDistance = Integer.valueOf(valuesMap.get("drop_off_distance"));

        Client client = Client.dbGet(valuesMap.get("client_id"));
        Motorhome motorhome = Motorhome.dbGet(valuesMap.get("motorhome_id"));
        double motorhomeValue = Double.valueOf(valuesMap.get("motorhome_price_value"));
        int motorhomeMileageStart = Integer.valueOf(valuesMap.get("motorhome_mileage_start"));
        int motorhomeMileageEnd = Integer.valueOf(valuesMap.get("motorhome_mileage_end"));
        double seasonModifier = Double.valueOf(valuesMap.get("season_price_modifier"));

        String cancellationValueString = valuesMap.get("cancellation_price_value");
        boolean isCancelled = false;
        double canceledPriceValue = 0.00;
        if (cancellationValueString != null) {
            isCancelled = true;
            canceledPriceValue = Double.valueOf(cancellationValueString);
        }

        return new Order(id, startDate, endDate, pickUp, pickUpDistance, dropOff, dropOffDistance,
                client, motorhome, motorhomeValue, motorhomeMileageStart, motorhomeMileageEnd, seasonModifier, isCancelled, canceledPriceValue);
    }

    /*
     *  DB helpers
     */
    public static Order dbGet(String orderID) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("id", orderID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null && returnValues.get("id").equals(orderID)) {
                return Order.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Order dbGetByDateMotorhomeClient(LocalDate startDate, LocalDate endDate,
               String motorhomeID, String clientID) {
        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("start_date", startDate.format(formatter));
        searchQuery.put("end_date", endDate.format(formatter));
        searchQuery.put("motorhome_id", motorhomeID);
        searchQuery.put("client_id", clientID);

        try {
            HashMap<String, String> returnValues = Database.getTable(DB_TABLE_NAME)
                    .get(Arrays.asList(DB_TABLE_COLUMNS),
                            searchQuery, new HashMap<>());

            if (returnValues.get("id") != null) {
                return Order.construct(returnValues);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Order> dbGetAll() {
        List<Order> result = new ArrayList<>();

        try {
            List<HashMap<String, String>> returnList = Database.getTable(DB_TABLE_NAME)
                    .getAll(Arrays.asList(DB_TABLE_COLUMNS),
                            null, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(Order.construct(valuesMap));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static List<Map.Entry<Extra, Double>> dbGetAllExtras(String orderID) {
        if (orderID == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        List<Map.Entry<Extra, Double>> result = new ArrayList<>();

        HashMap<String, String> searchQuery = new HashMap<>();
        searchQuery.put("order_id", orderID);

        try {
            List<HashMap<String, String>> returnList = Database
                    .getTable(DB_INTERSECTION_TABLE_NAME)
                    .getAll(Arrays.asList("extra_id", "extra_price_value"),
                            searchQuery, null);

            returnList.forEach((HashMap<String, String> valuesMap) -> {
                result.add(new AbstractMap.SimpleEntry<>(
                    Extra.dbGet(valuesMap.get("extra_id")),
                    Double.valueOf(valuesMap.get("extra_price_value")
                )));
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static int dbInsert(Order order) {
        try {
            return Database.getTable(DB_TABLE_NAME)
                    .insert(order.deconstruct());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int dbInsertExtra(String orderID, Extra extra) {
        if (orderID == null || extra.id == null) {
            throw new IllegalArgumentException("Invalid ID given as argument! [null]");
        }
        HashMap<String, String> entry = new HashMap<>();
        entry.put("order_id", orderID);
        entry.put("extra_id", extra.id);
        entry.put("extra_price_value", extra.price.getValue().value.getValue().toString());

        try {
            return Database.getTable(DB_INTERSECTION_TABLE_NAME)
                    .insert(entry);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int dbUpdate(String orderID, Double cancellationPriceValue) {
        HashMap<String, String> entry = new HashMap<>();
        entry.put("cancellation_price_value", (cancellationPriceValue == null)
                ? null
                : cancellationPriceValue.toString());

        HashMap<String, String> whitelist = new HashMap<>();
        whitelist.put("id", orderID);

        try {
            return Database.getTable(DB_TABLE_NAME)
                    .update(entry, whitelist, new HashMap<>());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}