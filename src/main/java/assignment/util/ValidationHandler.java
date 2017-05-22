package assignment.util;


import javafx.scene.control.Label;

public class ValidationHandler {

    // Error messages
    public static final String ERROR_DB_CONNECTION = "DB connection error";

    public static final String ERROR_ACCOUNT_INVALID = "Invalid credentials";
    public static final String ERROR_ACCOUNT_USERNAME_REQUIRED = "Username required";
    public static final String ERROR_ACCOUNT_USERNAME_SHORT = "Username too short";
    public static final String ERROR_ACCOUNT_USERNAME_LONG = "Username too long";
    public static final String ERROR_ACCOUNT_USERNAME_INVALID = "Invalid username (non-alphanumeric)";
    public static final String ERROR_ACCOUNT_USERNAME_NONEXISTENT = "Username not registered";
    public static final String ERROR_ACCOUNT_USERNAME_DUPLICATE = "Username already registered";
    public static final String ERROR_ACCOUNT_PASSWORD_REQUIRED = "Password required";
    public static final String ERROR_ACCOUNT_PASSWORD_SHORT = "Password too short";
    public static final String ERROR_ACCOUNT_PASSWORD_LONG = "Password too long";
    public static final String ERROR_ACCOUNT_PASSWORD_INVALID = "Invalid password (non-alphanumeric)";
    public static final String ERROR_ACCOUNT_PASSWORD_DIFFERENT = "Passwords do not match";

    public static final String ERROR_ACCOUNT_TYPE_NAME_REQUIRED = "Name required";
    public static final String ERROR_ACCOUNT_TYPE_NAME_SHORT = "Name too short";
    public static final String ERROR_ACCOUNT_TYPE_NAME_LONG = "Name too long";
    public static final String ERROR_ACCOUNT_TYPE_NAME_INVALID = "Invalid name (non-alphanumeric)";
    public static final String ERROR_ACCOUNT_TYPE_NAME_DUPLICATE = "Name already registered";

    public static boolean showError(Label errorLabel, Response validation) {
        if (validation.success) {
            errorLabel.setVisible(false);
            return true;
        } else {
            errorLabel.setText(validation.msg);
            errorLabel.setVisible(true);
            return false;
        }
    }

    // Validation filters
    // ACCOUNT fields
    public static Response validateAccountUsername(String username) {
        if(username == null || username.isEmpty()) {
            return new Response(false, ERROR_ACCOUNT_USERNAME_REQUIRED);
        } else if (!username.matches("[a-zA-Z0-9]+")) {
            return new Response(false, ERROR_ACCOUNT_USERNAME_INVALID);
        } else if (username.length() <= 3) {
            return new Response(false, ERROR_ACCOUNT_USERNAME_SHORT);
        } else if (username.length() > 25) {
            return new Response(false, ERROR_ACCOUNT_USERNAME_LONG);
        }
        return new Response(true);
    }

    public static Response validateAccountPassword(String password, String repeatPassword) {
        if(password == null || password.isEmpty()) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_REQUIRED);
        } else if (!password.matches("[a-zA-Z0-9]+")) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_INVALID);
        } else if (password.length() <= 3) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_SHORT);
        } else if (password.length() > 25) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_LONG);
        } else if(!password.equals(repeatPassword)) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_DIFFERENT);
        }
        return new Response(true);
    }

    // ACCOUNT TYPE fields
    public static Response validateAccountTypeName(String name) {
        if(name == null || name.isEmpty()) {
            return new Response(false, ERROR_ACCOUNT_TYPE_NAME_REQUIRED);
        } else if (!name.matches("[a-zA-Z0-9 ]+")) {
            return new Response(false, ERROR_ACCOUNT_TYPE_NAME_INVALID);
        } else if (name.length() <= 3) {
            return new Response(false, ERROR_ACCOUNT_TYPE_NAME_SHORT);
        } else if (name.length() > 25) {
            return new Response(false, ERROR_ACCOUNT_TYPE_NAME_LONG);
        }
        return new Response(true);
    }

    public static Response validateAccountTypeDBOperation(int returnValue) {
        if(returnValue == 1) {
            return new Response(true);
        } else if (returnValue == -1) {
            return new Response(false, ERROR_ACCOUNT_TYPE_NAME_DUPLICATE);
        }

        return new Response(false, ValidationHandler.ERROR_DB_CONNECTION);
    }
}
