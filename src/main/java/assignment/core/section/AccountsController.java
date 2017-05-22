package assignment.core.section;

import assignment.core.RootController;

public class AccountsController implements UISection {
    public static final String ACCESS_TYPE_NAME = "accounts";
    private static final String TEMPLATE_PATH = "templates/section/accounts.fxml";

    private RootController rootController;

    public AccountsController(RootController rootController) {
        this.rootController = rootController;
    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }
}
