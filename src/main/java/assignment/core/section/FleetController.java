package assignment.core.section;

import assignment.core.RootController;
import javafx.fxml.FXML;

public class FleetController implements UISection {
    public static final String ACCESS_TYPE_NAME = "fleet";
    private static final String TEMPLATE_PATH = "templates/section/fleet.fxml";

    private RootController rootController;


    public FleetController(RootController rootController) {
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
