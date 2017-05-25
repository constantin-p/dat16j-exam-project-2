package assignment.core.section;

import assignment.core.RootController;
import javafx.fxml.FXML;

public class CleaningController implements UISection {
    private static final String ACCESS_TYPE_NAME = "cleaning";
    private static final String TEMPLATE_PATH = "templates/section/cleaning.fxml";

    private RootController rootController;


    public CleaningController(RootController rootController) {
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
