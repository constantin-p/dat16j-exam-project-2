package assignment;


import assignment.core.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public Stage primaryStage;
    public String companyName = "NORDIC";

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // TODO: Catch unhandled errors and display the crash
        initRootLayout();
    }

    public static void main(String[] args) {
        launch(args);
    }


    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("templates/root.fxml"));

            RootController controller = new RootController(primaryStage);
            loader.setController(controller);

            Parent layout = loader.load();

            primaryStage.setScene(new Scene(layout, 600, 500));
            primaryStage.setTitle(companyName + " - Management System");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
