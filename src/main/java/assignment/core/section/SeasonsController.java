package assignment.core.section;

import assignment.core.RootController;
import assignment.model.Extra;
import assignment.model.Season;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class SeasonsController implements UISection {
    private static final String ACCESS_TYPE_NAME = "seasons";
    private static final String TEMPLATE_PATH = "templates/section/seasons.fxml";

    private RootController rootController;
    private ObservableList<Season> seasonList = FXCollections.observableArrayList();

    @FXML
    private TableView<Season> tableView;

    public SeasonsController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML
    public void initialize() {
        TableColumn<Season, String> nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        TableColumn<Season, String> priceColumn = new TableColumn("Price");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().price.getValue().value.getValue() +
                        " / " + cellData.getValue().price.getValue().type.getValue().name.getValue())
        );
        tableView.getColumns().addAll(nameColumn, priceColumn);
        tableView.setItems(seasonList);

        populateTableView();
    }

    public static String getAccessTypeName() {
        return ACCESS_TYPE_NAME;
    }

    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    /*
     *  Helpers
     */
    private void populateTableView() {

        List<Season> seasons = Season.dbGetAll();
        seasonList.clear();
        seasons.forEach(entry -> {
            seasonList.add(entry);
        });
    }
}
