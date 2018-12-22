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
        //launch(args);
        String postingPath = "C:\\Users\\guy schlesinger\\Downloads\\corpus\\posting";
        Searcher searcher=new Searcher(postingPath,true);
        HashSet<String> terms = new HashSet<>();
        terms.add("40");
        terms.add("42");
        terms.add("42%");

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

        // TODO: 22/12/2018 Check why the size of 40 is ne more than the real size 
        RetrieveTermInfo retrieveTermInfo =new RetrieveTermInfo("15248_95495254494851_true.txt",terms,postingPath);
        HashSet<TermInfo> termInfos;
        try {
            //Future<HashSet<TermInfo>> future = executorService.submit(retrieveTermInfo);
            //termInfos = future.get();
            termInfos = retrieveTermInfo.retrieveInfo();
            for(TermInfo termInfo:termInfos)
            {
                printTermInfo(termInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void printTermInfo(TermInfo termInfo)
    {
        System.out.println();
        HashMap<Integer,Integer> map = termInfo.getDocIdTfMap();
        for(Map.Entry<Integer,Integer> entry :map.entrySet())
        {
            System.out.println("The term - "+termInfo.getTerm()+" The Size " + map.size()+" The docId - "+ entry.getKey() +" The tf - "+entry.getValue());
        }
        System.out.println();
        System.out.println();
    }


}