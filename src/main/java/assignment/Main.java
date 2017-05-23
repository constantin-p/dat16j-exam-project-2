package assignment;


import assignment.core.RootController;
import assignment.core.auth.AuthManager;
import assignment.core.auth.LoginController;
import assignment.util.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import store.db.Database;

import java.io.IOException;

public class Main extends Application {
    public static final String COMPANY_NAME = "NORDIC";

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public Stage primaryStage;
    public AuthManager authManager = new AuthManager(() -> initRootLayout(),
            () -> initLoginLayout());

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // TODO: Catch unhandled errors and display the crash
        Config.loadConfig("store", "config/store_secret.properties");
        Database.configInstance(Config.getConfig("store"));

        initLoginLayout();
    }

    public static void main(String[] args) {
        launch(args);
    }


    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("templates/root.fxml"));

            RootController controller = new RootController(authManager, primaryStage);
            loader.setController(controller);

            Parent layout = loader.load();

            primaryStage.setScene(new Scene(layout, 600, 500));
            primaryStage.setTitle(COMPANY_NAME + " - Management System");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initLoginLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("templates/login.fxml"));

            LoginController controller = new LoginController(authManager);
            loader.setController(controller);

            Parent layout = loader.load();

            primaryStage.setScene(new Scene(layout, 300, 400));
            primaryStage.setTitle(COMPANY_NAME + " - Login");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
