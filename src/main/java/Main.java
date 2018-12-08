import Model.Indexer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("view.fxml"));
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Search Engine Project");
        primaryStage.show();
        long start = System.nanoTime();
        String pathToCorpus = "C:\\Users\\guy schlesinger\\Downloads\\corpus\\d";
        //String pathToCorpus = "C:\\Users\\guy schlesinger\\Downloads\\corpus\\corpus";
        String pathToStopWords = "C:\\Users\\guy schlesinger\\Downloads\\stop_words.txt";
        String des = "C:\\Users\\guy schlesinger\\Downloads\\corpus\\neee";
        Indexer indexer = new Indexer(pathToCorpus, pathToStopWords, des, true);
        try {

           /* indexer.parseDocumentsThread();
            long elapsedTime = System.nanoTime() - start;
            System.out.println("the size of the dictionary " + indexer.getDicSize());
            System.out.println(indexer.getListOfFileNames());
            System.out.println(elapsedTime / 1000000000 + " second ~ " + (elapsedTime / 1000000000) / 60 + " minutes and " + (elapsedTime / 1000000000) % 60 + " seconds");*/
            HashMap<String,Integer> stringIntegerHashMap=indexer.loadDictionary();
            System.out.println(stringIntegerHashMap.size());


        } catch (Exception e) {
            e.printStackTrace();
            indexer.shutDownNow();
        }

    }


    public static void main(String[] args) {
        launch(args);
    }


}