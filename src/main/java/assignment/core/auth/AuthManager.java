package assignment.core.auth;


import assignment.model.Account;
import assignment.util.Authentication;
import assignment.util.Response;
import assignment.util.ValidationHandler;
import store.db.Database;
import store.db.TableHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthManager {
    private static final Logger LOGGER = Logger.getLogger(TableHandler.class.getName());

    public Account currentUser = null;
    public Runnable initRootLayoutRequest;
    public Runnable initLoginLayoutRequest;

    public AuthManager(Runnable initRootLayoutRequest, Runnable initLoginLayoutRequest) {
        this.initRootLayoutRequest = initRootLayoutRequest;
        this.initLoginLayoutRequest = initLoginLayoutRequest;
    }

    public Response login(String username, String password) {
        try {
            HashMap<String, String> searchQuery = new HashMap<>();
            searchQuery.put("username", username);

            HashMap<String, String> returnValues = Database.getTable(Account.DB_TABLE_NAME)
                    .get(Arrays.asList("username", "hash", "accounttype_id"),
                            searchQuery, new HashMap<>());

            if (returnValues.get("hash") == null) {
                return new Response(false, ValidationHandler.ERROR_ACCOUNT_USERNAME_NONEXISTENT);
            } if (Authentication.validate(password, returnValues.get("hash"))) {
                // Set the current user
                currentUser = Account.construct(returnValues);
                return new Response(true);
            } else {
                return new Response(false, ValidationHandler.ERROR_ACCOUNT_INVALID);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            return new Response(false, ValidationHandler.ERROR_DB);
        }
    }

    public void signOut() {
        currentUser = null;
        initLoginLayoutRequest.run();
    }

    public static Response register(Account account, String password) {
        try {
            HashMap<String, String> entry = account.deconstruct();
            entry.put("hash", Authentication.hash(password));

            int returnValue = Database.getTable(Account.DB_TABLE_NAME)
                    .insert(entry);

            if (returnValue == 1) {
                return new Response(true);
            } else if (returnValue == -1) {
                return new Response(false, ValidationHandler.ERROR_ACCOUNT_USERNAME_DUPLICATE);
            }

            // Invalid response
            LOGGER.log(Level.SEVERE, "Invalid DB return value", returnValue);
            return new Response(false, ValidationHandler.ERROR_DB);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            return new Response(false, ValidationHandler.ERROR_DB);
        }
    }
}
