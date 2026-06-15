package Main;

import Util.AppNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new AppNavigator(primaryStage).navigateToLogin();
    }
}
