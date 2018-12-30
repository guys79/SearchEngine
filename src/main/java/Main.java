import View.ViewChanger;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewChanger viewChanger = new ViewChanger();
        viewChanger.setPrimaryStage(primaryStage);
        viewChanger.goToSearch();
    }


    public static void main(String[] args) {
        launch(args);
    }
}