package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Account;
import assignment.model.Extra;
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
    private ObservableList<Extra> extraList = FXCollections.observableArrayList();

    @FXML
    private TableView<Extra> tableView;

    public ExtrasController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Extra, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        TableColumn<Extra, String> priceColumn = new TableColumn("Price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().price.getValue().value);
        tableView.getColumns().addAll(nameColumn, priceColumn);
        tableView.setItems(extraList);

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
        Extra extra = rootController.modalDispatcher.showCreateExtrasModal(null);
        if (extra != null) {
            populateTableView();
        }
    }

    private void populateTableView() {

        List<Extra> extras = Extra.dbGetAll();
        extraList.clear();
        extras.forEach(entry -> {
            extraList.add(entry);
        });
    }
}
