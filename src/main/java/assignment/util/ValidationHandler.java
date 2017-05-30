package assignment.util;


import assignment.model.*;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.YEARS;

public class ValidationHandler {

    // Error messages
    public static final String ERROR_DB = "DB error";
    public static final String ERROR_DB_CONNECTION = "DB connection error";

    public static final String ERROR_ACCOUNT_INVALID = "Invalid credentials";
    public static final String ERROR_ACCOUNT_USERNAME_REQUIRED = "Username required";
    public static final String ERROR_ACCOUNT_USERNAME_SHORT = "Username too short";
    public static final String ERROR_ACCOUNT_USERNAME_LONG = "Username too long";
    public static final String ERROR_ACCOUNT_USERNAME_INVALID = "Invalid username (non-alphanumeric)";
    public static final String ERROR_ACCOUNT_USERNAME_NONEXISTENT = "Username not registered";
    public static final String ERROR_ACCOUNT_USERNAME_DUPLICATE = "Username already registered";
    public static final String ERROR_ACCOUNT_ACCOUNT_TYPE_REQUIRED = "Account type required";
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

    public static final String ERROR_CLIENT_FIRSTNAME_REQUIRED = "First name required";
    public static final String ERROR_CLIENT_FIRSTNAME_SHORT = "First name too short";
    public static final String ERROR_CLIENT_FIRSTNAME_LONG = "First name too long";
    public static final String ERROR_CLIENT_FIRSTNAME_INVALID = "Invalid first name (non-alphanumeric)";
    public static final String ERROR_CLIENT_LASTNAME_REQUIRED = "Last name required";
    public static final String ERROR_CLIENT_LASTNAME_SHORT = "Last name too short";
    public static final String ERROR_CLIENT_LASTNAME_LONG = "Last name too long";
    public static final String ERROR_CLIENT_LASTNAME_INVALID = "Invalid last name (non-alphanumeric)";
    public static final String ERROR_CLIENT_EMAIL_REQUIRED = "Email required";
    public static final String ERROR_CLIENT_EMAIL_LONG = "Email too long";
    public static final String ERROR_CLIENT_EMAIL_DUPLICATE = "Email already registered";
    public static final String ERROR_CLIENT_EMAIL_INVALID = "Invalid email address";
    public static final String ERROR_CLIENT_ADDRESS_REQUIRED = "Address required";
    public static final String ERROR_CLIENT_ADDRESS_INVALID = "Invalid address (non-alphanumeric .,+_-)";
    public static final String ERROR_CLIENT_ADDRESS_LONG = "Address too long >65";

    public static final String ERROR_CLIENT_DOB_REQUIRED = "Date of birth required";
    public static final String ERROR_CLIENT_DOB_YOUNG = "Too young (<16 years)";

    public static final String ERROR_PRICE_NAME_REQUIRED = "Name required";
    public static final String ERROR_PRICE_NAME_SHORT = "Name too short";
    public static final String ERROR_PRICE_NAME_LONG = "Name too long";
    public static final String ERROR_PRICE_NAME_INVALID = "Invalid name (non-alphanumeric +_-)";
    public static final String ERROR_PRICE_NAME_DUPLICATE = "Name already registered";
    public static final String ERROR_PRICE_VALUE_REQUIRED = "Price value required";
    public static final String ERROR_PRICE_VALUE_INVALID = "Invalid price value (negative)";
    public static final String ERROR_PRICE_PRICE_TYPE_REQUIRED = "Price type required";

    public static final String ERROR_MOTORHOME_MODEL_REQUIRED = "Model required";
    public static final String ERROR_MOTORHOME_MODEL_SHORT = "Model too short";
    public static final String ERROR_MOTORHOME_MODEL_LONG = "Model too long";
    public static final String ERROR_MOTORHOME_MODEL_INVALID = "Invalid Model (non-alphanumeric)";

    public static final String ERROR_MOTORHOME_BRAND_REQUIRED = "Brand required";
    public static final String ERROR_MOTORHOME_BRAND_SHORT = "Brand too short";
    public static final String ERROR_MOTORHOME_BRAND_LONG = "Brand too long";
    public static final String ERROR_MOTORHOME_BRAND_INVALID = "Invalid Brand (non-alphanumeric)";
    public static final String ERROR_MOTORHOME_CAPACITY_LOW = "Capacity too low <1";
    public static final String ERROR_MOTORHOME_CAPACITY_BIG = "Capacity too big >10";
    public static final String ERROR_MOTORHOME_MILEAGE_INVALID = "Invalid mileage (negative)";
    public static final String ERROR_MOTORHOME_PRICE_REQUIRED = "Price required";

    public static final String ERROR_EXTRA_NAME_REQUIRED = "Name required";
    public static final String ERROR_EXTRA_NAME_SHORT = "Name too short";
    public static final String ERROR_EXTRA_NAME_LONG = "Name too long";
    public static final String ERROR_EXTRA_NAME_INVALID = "Invalid name (non-alphanumeric)";
    public static final String ERROR_EXTRA_NAME_DUPLICATE = "Name already registered";
    public static final String ERROR_EXTRA_PRICE_REQUIRED = "Price required";

    public static final String ERROR_ORDER_START_DATE_REQUIRED = "Start date required";
    public static final String ERROR_ORDER_START_DATE_PAST = "Start date not in future";
    public static final String ERROR_ORDER_END_DATE_REQUIRED = "End date required";
    public static final String ERROR_ORDER_END_DATE_PAST = "End date not in future";
    public static final String ERROR_ORDER_PICK_UP_REQUIRED = "Pick-up location required";
    public static final String ERROR_ORDER_PICK_UP_INVALID = "Invalid pick-up location (non-alphanumeric)";
    public static final String ERROR_ORDER_PICK_UP_LONG = "Pick-up location too long >65";
    public static final String ERROR_ORDER_PICK_UP_DISTANCE_LONG = "Pick-up distance too long >500";
    public static final String ERROR_ORDER_PICK_UP_DISTANCE_INVALID = "Invalid pick-up distance (negative)";
    public static final String ERROR_ORDER_DROP_OFF_REQUIRED = "Drop-off location required";
    public static final String ERROR_ORDER_DROP_OFF_INVALID = "Invalid drop-off location (non-alphanumeric)";
    public static final String ERROR_ORDER_DROP_OFF_LONG = "Drop-off location too long >65";
    public static final String ERROR_ORDER_DROP_OFF_DISTANCE_LONG = "Drop-off distance too long >500";
    public static final String ERROR_ORDER_DROP_OFF_DISTANCE_INVALID = "Invalid drop-off distance (negative)";
    public static final String ERROR_ORDER_CLIENT_REQUIRED = "Client required";
    public static final String ERROR_ORDER_MOTORHOME_REQUIRED = "Motorhome required";


    public static boolean validateControl(Control control, Label errorLabel, Response validation) {
        if (showError(errorLabel, validation)) {
            control.setStyle( "-fx-text-fill: #5a5a5a;");
            return true;
        } else {
            control.setStyle( "-fx-text-fill: #e53935;");
            return false;
        }
    }

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

    // TODO: Trim strings
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

    public static Response validateAccountPassword(String password) {
        if(password == null || password.isEmpty()) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_REQUIRED);
        } else if (!password.matches("[a-zA-Z0-9]+")) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_INVALID);
        } else if (password.length() <= 3) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_SHORT);
        } else if (password.length() > 25) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_LONG);
        }
        return new Response(true);
    }

    public static Response validateAccountRepeatPassword(String password, String repeatPassword) {
        if(!password.equals(repeatPassword)) {
            return new Response(false, ERROR_ACCOUNT_PASSWORD_DIFFERENT);
        }
        return new Response(true);
    }

    public static Response validateAccountAccountType(AccountType accountType) {
        if(accountType == null || accountType.id == null) {
            return new Response(false, ERROR_ACCOUNT_ACCOUNT_TYPE_REQUIRED);
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

    // PRICE fields
    public static Response validatePriceName(String name) {
        if(name == null || name.isEmpty()) {
            return new Response(false, ERROR_PRICE_NAME_REQUIRED);
        } else if (!name.matches("[a-zA-Z0-9 +_-]+")) {
            return new Response(false, ERROR_PRICE_NAME_INVALID);
        } else if (name.length() <= 3) {
            return new Response(false, ERROR_PRICE_NAME_SHORT);
        } else if (name.length() > 25) {
            return new Response(false, ERROR_PRICE_NAME_LONG);
        }
        return new Response(true);
    }

    public static Response validatePriceValue(double value) {
        if(value == 0) {
            return new Response(false, ERROR_PRICE_VALUE_REQUIRED);
        } else if(value < 0) {
            return new Response(false, ERROR_PRICE_VALUE_INVALID);
        }
        return new Response(true);
    }

    public static Response validatePricePriceType(PriceType priceType) {
        if(priceType == null || priceType.id == null) {
            return new Response(false, ERROR_PRICE_PRICE_TYPE_REQUIRED);
        }
        return new Response(true);
    }

    public static Response validatePriceDBOperation(int returnValue) {
        if(returnValue == 1) {
            return new Response(true);
        } else if (returnValue == -1) {
            return new Response(false, ERROR_PRICE_NAME_DUPLICATE);
        }
        return new Response(false, ValidationHandler.ERROR_DB_CONNECTION);
    }

    // CLIENT fields
    public static Response validateClientFirstName(String firstName) {
        if(firstName == null || firstName.isEmpty()) {
            return new Response(false, ERROR_CLIENT_FIRSTNAME_REQUIRED);
        } else if (!firstName.chars().allMatch(Character::isLetter)) {
            return new Response(false, ERROR_CLIENT_FIRSTNAME_INVALID);
        } else if (firstName.length() <= 1) {
            return new Response(false, ERROR_CLIENT_FIRSTNAME_SHORT);
        } else if (firstName.length() > 25) {
            return new Response(false, ERROR_CLIENT_FIRSTNAME_LONG);
        }
        return new Response(true);
    }

    public static Response validateClientLastName(String lastName) {
        if(lastName == null || lastName.isEmpty()) {
            return new Response(false, ERROR_CLIENT_LASTNAME_REQUIRED);
        } else if (!lastName.chars().allMatch(Character::isLetter)) {
            return new Response(false, ERROR_CLIENT_LASTNAME_INVALID);
        } else if (lastName.length() <= 1) {
            return new Response(false, ERROR_CLIENT_LASTNAME_SHORT);
        } else if (lastName.length() > 25) {
            return new Response(false, ERROR_CLIENT_LASTNAME_LONG);
        }
        return new Response(true);
    }

    public static Response validateClientEmail(String email) {
        if(email == null || email.isEmpty()) {
            return new Response(false, ERROR_CLIENT_EMAIL_REQUIRED);
        } else if (!email.matches("^(.+)@(.+)$")) {
            return new Response(false, ERROR_CLIENT_EMAIL_INVALID);
        } else if (email.length() > 25) {
            return new Response(false, ERROR_CLIENT_EMAIL_LONG);
        }
        return new Response(true);
    }

    public static Response validateClientAddress(String location) {
        if(location == null || location.isEmpty()) {
            return new Response(false, ERROR_CLIENT_ADDRESS_REQUIRED);
        } else if (!location.matches("[a-zA-Z0-9 .,+_-]+")) {
            return new Response(false, ERROR_CLIENT_ADDRESS_INVALID);
        } else if (location.length() > 65) {
            return new Response(false, ERROR_CLIENT_ADDRESS_LONG);
        }
        return new Response(true);
    }

    public static Response validateClientDateOfBirth(LocalDate date) {
        if(date == null) {
            return new Response(false, ERROR_CLIENT_DOB_REQUIRED);
        } else if (YEARS.between(date, LocalDate.now()) < 16) {
            return new Response(false, ERROR_CLIENT_DOB_YOUNG);
        }
        return new Response(true);
    }

    public static Response validateClientDBOperation(int returnValue) {
        if(returnValue == 1) {
            return new Response(true);
        } else if (returnValue == -1) {
            return new Response(false, ERROR_CLIENT_EMAIL_DUPLICATE);
        }

        return new Response(false, ValidationHandler.ERROR_DB_CONNECTION);
    }

    // MOTORHOME fields
    public static Response validateMotorhomeBrand(String brand) {
        if(brand == null || brand.isEmpty()) {
            return new Response(false, ERROR_MOTORHOME_BRAND_REQUIRED);
        } else if (!brand.matches("[a-zA-Z0-9 ]+")) {
            return new Response(false, ERROR_MOTORHOME_BRAND_INVALID);
        } else if (brand.length() <= 3) {
            return new Response(false, ERROR_MOTORHOME_BRAND_SHORT);
        } else if (brand.length() > 25) {
            return new Response(false, ERROR_MOTORHOME_BRAND_LONG);
        }
        return new Response(true);
    }

    public static Response validateMotorhomeModel(String model) {
        if(model == null || model.isEmpty()) {
            return new Response(false, ERROR_MOTORHOME_MODEL_REQUIRED);
        } else if (!model.matches("[a-zA-Z0-9 ]+")) {
            return new Response(false, ERROR_MOTORHOME_MODEL_INVALID);
        } else if (model.length() <= 3) {
            return new Response(false, ERROR_MOTORHOME_MODEL_SHORT);
        } else if (model.length() > 25) {
            return new Response(false, ERROR_MOTORHOME_MODEL_LONG);
        }
        return new Response(true);
    }

    public static Response validateMotorhomeCapacity(int capacity) {
        if(capacity < 1) {
            return new Response(false, ERROR_MOTORHOME_CAPACITY_LOW);
        } else if (capacity > 10) {
            return new Response(false, ERROR_MOTORHOME_CAPACITY_BIG);
        }
        return new Response(true);
    }

    public static Response validateMotorhomeMileage(int mileage) {
        if(mileage < 0) {
            return new Response(false, ERROR_MOTORHOME_MILEAGE_INVALID);
        }
        return new Response(true);
    }

    public static Response validateMotorhomePrice(Price price) {
        if(price == null || price.id == null) {
            return new Response(false, ERROR_MOTORHOME_PRICE_REQUIRED);
        }
        return new Response(true);
    }

    public static Response validateMotorhomeDBOperation(int returnValue) {
        if(returnValue == 1) {
            return new Response(true);
        } else if (returnValue == -1) {
            return new Response(false, ERROR_ACCOUNT_TYPE_NAME_DUPLICATE);
        }
        return new Response(false, ValidationHandler.ERROR_DB_CONNECTION);
    }

    // EXTRA fields
    public static Response validateExtraName(String name) {
        if(name == null || name.isEmpty()) {
            return new Response(false, ERROR_EXTRA_NAME_REQUIRED);
        } else if (!name.matches("[a-zA-Z0-9 ]+")) {
            return new Response(false, ERROR_EXTRA_NAME_INVALID);
        } else if (name.length() <= 3) {
            return new Response(false, ERROR_EXTRA_NAME_SHORT);
        } else if (name.length() > 25) {
            return new Response(false, ERROR_EXTRA_NAME_LONG);
        }
        return new Response(true);
    }

    public static Response validateExtraPrice(Price price) {
        if(price == null || price.id == null) {
            return new Response(false, ERROR_EXTRA_PRICE_REQUIRED);
        }
        return new Response(true);
    }

    public static Response validateExtraDBOperation(int returnValue) {
        if(returnValue == 1) {
            return new Response(true);
        } else if (returnValue == -1) {
            return new Response(false, ERROR_EXTRA_NAME_DUPLICATE);
        }

        return new Response(false, ValidationHandler.ERROR_DB_CONNECTION);
    }

    // ORDER fields
    public static Response validateOrderStartDate(LocalDate startDate) {
        if(startDate == null) {
            return new Response(false, ERROR_ORDER_START_DATE_REQUIRED);
        } else if (startDate.isBefore(LocalDate.now())) {
            return new Response(false, ERROR_ORDER_START_DATE_PAST);
        }
        return new Response(true);
    }

    public static Response validateOrderEndDate(LocalDate endDate) {
        if (endDate == null) {
            return new Response(false, ERROR_ORDER_END_DATE_REQUIRED);
        } else if (endDate.isBefore(LocalDate.now().plusDays(1))) {
            return new Response(false, ERROR_ORDER_END_DATE_PAST);
        }
        return new Response(true);
    }

    public static Response validateOrderPickUp(String location) {
        if(location == null || location.isEmpty()) {
            return new Response(false, ERROR_ORDER_PICK_UP_REQUIRED);
        } else if (!location.matches("[a-zA-Z0-9 ]+")) {
            return new Response(false, ERROR_ORDER_PICK_UP_INVALID);
        } else if (location.length() > 65) {
            return new Response(false, ERROR_ORDER_PICK_UP_LONG);
        }
        return new Response(true);
    }

    public static Response validateOrderPickUpDistance(int distance) {
        if (distance < 0) {
            return new Response(false, ERROR_ORDER_PICK_UP_DISTANCE_INVALID);
        } else if (distance > 500) {
            return new Response(false, ERROR_ORDER_PICK_UP_DISTANCE_LONG);
        }
        return new Response(true);
    }

    public static Response validateOrderDropOff(String location) {
        if(location == null || location.isEmpty()) {
            return new Response(false, ERROR_ORDER_DROP_OFF_REQUIRED);
        } else if (!location.matches("[a-zA-Z0-9 ]+")) {
            return new Response(false, ERROR_ORDER_DROP_OFF_INVALID);
        } else if (location.length() > 65) {
            return new Response(false, ERROR_ORDER_DROP_OFF_LONG);
        }
        return new Response(true);
    }

    public static Response validateOrderDropOffDistance(int distance) {
        if (distance < 0) {
            return new Response(false, ERROR_ORDER_DROP_OFF_DISTANCE_INVALID);
        } else if (distance > 500) {
            return new Response(false, ERROR_ORDER_DROP_OFF_DISTANCE_LONG);
        }
        return new Response(true);
    }

    public static Response validateOrderClient(Client client) {
        if(client == null || client.id == null) {
            return new Response(false, ERROR_ORDER_CLIENT_REQUIRED);
        }
        return new Response(true);
    }

    public static Response validateOrderMotorhome(Motorhome motorhome) {
        if(motorhome == null || motorhome.id == null) {
            return new Response(false, ERROR_ORDER_MOTORHOME_REQUIRED);
        }
        return new Response(true);
    }

    public static Response validateOrderDBOperation(int returnValue) {
        if(returnValue == 1) {
            return new Response(true);
        }
        return new Response(false, ValidationHandler.ERROR_DB_CONNECTION);
    }
}
