package assignment.core.auth;


import assignment.model.Account;
import assignment.util.Authentication;
import assignment.util.Response;
import assignment.util.ValidationHandler;
import store.db.Database;

import java.util.HashMap;

public class AuthManager {

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
            return new Response(false, ValidationHandler.ERROR_DB);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, ValidationHandler.ERROR_DB);
        }
    }
}
