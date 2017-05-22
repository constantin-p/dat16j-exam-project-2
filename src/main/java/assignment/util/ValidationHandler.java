package assignment.util;


import javafx.scene.control.Label;

public class ValidationHandler {

    // Error messages
    public static final String ERROR_DB_CONNECTION = "DB connection error";

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
