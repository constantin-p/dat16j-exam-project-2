package assignment.core.section;

public class AccountTypesController implements UISection {
    private static final String ACCESS_TYPE_NAME = "account_types";
    private static final String TEMPLATE_PATH = "templates/section/account_types.fxml";

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }
}
