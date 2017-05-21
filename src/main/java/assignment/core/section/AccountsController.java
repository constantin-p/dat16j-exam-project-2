package assignment.core.section;

public class AccountsController implements UISection {
    public static final String ACCESS_TYPE_NAME = "accounts";
    private static final String TEMPLATE_PATH = "templates/section/accounts.fxml";

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }
}
