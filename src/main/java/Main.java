import Model.Retrieve.Query;
import Model.Retrieve.RetrieveTermInfo;
import Model.Retrieve.Searcher;
import Model.Retrieve.TermInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("view.fxml"));
        primaryStage.setScene(new Scene(root, 900, 400));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Search Engine Project");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
        /*String postingPath = "C:\\Users\\guys7\\Desktop\\posting";
        Searcher searcher=new Searcher(postingPath,true,new String[0],"man open door");
        searcher.test();
        Query query = new Query("This guy came to israel on 10-04",postingPath,true);*/
    }



}

