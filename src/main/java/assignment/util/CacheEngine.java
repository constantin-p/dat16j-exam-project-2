package assignment.util;



import assignment.model.Payment;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import store.db.TableHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.SECONDS;

public class CacheEngine {
    private static final Logger LOGGER = Logger.getLogger(TableHandler.class.getName());

    private static int lifespan = 10; // seconds, DEFAULT

    private static HashMap<String, Object> stateMap = new HashMap<>();
    private static HashMap<String, Instant> accessMap = new HashMap<>(); // store timestamps
    private static HashMap<String, BooleanProperty> dirtyMap = new HashMap<>();

    public interface DataGetter<U> {
        U get();
    };

    public interface DataSetter<U> {
        void set(U u);
    };

    public static void configInstance(Properties configuration) {
        lifespan = Integer.valueOf(configuration.getProperty("DATA_DECAY_LIFESPAN"));
    }


    public static <T> void get(String key, DBOperation<T> operation, boolean forceUpdate) {
        boolean stateMapContainsKey = stateMap.containsKey(key);
        boolean lifespanOver = accessMap.containsKey(key) &&
                SECONDS.between(accessMap.get(key), Instant.now()) > lifespan;

        if (!stateMapContainsKey || stateMapContainsKey && lifespanOver || forceUpdate) {
        //            Task<T> task = new Task<T>() {
        //                @Override
        //                public T call() {
        //                    return operation.dataGetter.get();
        //                }
        //            };

        //            task.setOnFailed(e -> {
        //                Throwable ex = task.getException();
        //                LOGGER.log(Level.SEVERE, ex.toString(), ex);
        //            });

        //            task.setOnSucceeded(e -> {
        //                T result = task.getValue();
        //                operation.dataSetter.set(result);
        //
        //                // Save the data to the stateMap
        //                stateMap.put(key, result);
        //                accessMap.put(key, Instant.now());
        //
        //                if (!stateMapContainsKey) {
        //                    dirtyMap.put(key, new SimpleBooleanProperty(false));
        //                    dirtyMap.get(key).addListener((observable, oldValue, newValue) -> {
        //                        if (newValue) {
        //                            get(key, operation, true);
        //                        }
        //                    });
        //                } else if (dirtyMap.containsKey(key) && dirtyMap.get(key).getValue()) {
        //                    dirtyMap.get(key).setValue(false);
        //                }
        //            });

        //            new Thread(task).start();


            T result = operation.dataGetter.get();
            operation.dataSetter.set(result);

            // Save the data to the stateMap
            stateMap.put(key, result);
            accessMap.put(key, Instant.now());

            if (!stateMapContainsKey) {
                dirtyMap.put(key, new SimpleBooleanProperty(false));
                dirtyMap.get(key).addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        get(key, operation, true);
                    }
                });
            } else if (dirtyMap.containsKey(key) && dirtyMap.get(key).getValue()) {
                dirtyMap.get(key).setValue(false);
            }
        } else {
            operation.dataSetter.set((T) stateMap.get(key));
        }
    }

    public static <T> void get(String key, DBOperation<T> operation) {
        get(key, operation, false);
    }

    public static void markForUpdate(String key) {
        if (stateMap.containsKey(key)) {
            dirtyMap.get(key).setValue(true);
        }
    }
}
