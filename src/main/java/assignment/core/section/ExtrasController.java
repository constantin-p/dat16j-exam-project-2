package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Extras;
import assignment.model.Motorhome;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;


public class ExtrasController implements UISection {
    public static final String ACCESS_TYPE_NAME = "extras";
    private static final String TEMPLATE_PATH = "templates/section/extras.fxml";

    private RootController rootController;
    private ObservableList<Extras> extrasList = FXCollections.observableArrayList();

    @FXML
    private TableView<Extras> tableView;

    public ExtrasController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Extras, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        tableView.getColumns().addAll(nameColumn);
        tableView.setItems(extrasList);

        populateTableView();
    }


    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    @FXML
    public void handleAddAction(ActionEvent event) {
        Extras extras = rootController.modalDispatcher.showCreateExtrasModal(null);
        if (extras != null) {
            populateTableView();
        }
    }

    private void populateTableView() {

        List<Extras> extras = Extras.dbGetAll();
        extrasList.clear();
        extras.forEach(entry -> {
            extrasList.add(entry);
        });
    }
}
