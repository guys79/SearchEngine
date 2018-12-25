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
      /*  String postingPath = "C:\\Users\\guys79\\Desktop\\posting";
        Searcher searcher=new Searcher(postingPath,true,new String[0],false);

        String query = "between 9 to 10";
        String [] array = searcher.getMostRelevantDocNum(query);


        pr(array);*/
        // TODO: 25/12/2018 Fix bug in getTermsAndFiles, it dose not work on 8 or 9. should use in search the term + _ (maybe) .. or maybe in the names of the files




    }

    public static void pr(String []a)
    {
        for(int i=0;i<a.length;i++)
        {
            System.out.println(a[i]);
        }
    }
    public static void pr2(int []a)
    {
        for(int i=0;i<a.length;i++)
        {
            System.out.println(a[i]);
        }
    }
    public static void pr3(double []a)
    {
        for(int i=0;i<a.length;i++)
        {
            System.out.println(a[i]);
        }
        System.out.println();
    }
}

