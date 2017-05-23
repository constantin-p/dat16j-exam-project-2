package assignment.core.section;

import assignment.core.RootController;
import javafx.fxml.FXML;

public class RefundsController implements UISection {
    public static final String ACCESS_TYPE_NAME = "refunds";
    private static final String TEMPLATE_PATH = "templates/section/refunds.fxml";

    private RootController rootController;


    public RefundsController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {

    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }
}
