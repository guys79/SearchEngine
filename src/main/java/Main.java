import Model.Index.DocumentReturnValue;
import Model.Index.Parser;
import Model.Retrieve.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("view.fxml"));
        primaryStage.setScene(new Scene(root, 900, 400));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Search Engine Project");
        primaryStage.show();
    }


    public static void main(String[] args) {
        //launch(args);
        //while (true) {
            test();
        //}
        //test2();
        //test3();

        System.out.println("done");



    }
    public static void test()
    {
        String postingPath = "C:\\Users\\guys79\\Desktop\\postingNew";
        boolean semantic = true;
        boolean stem =true;
        String [] cities =new String[2];
//        cities[0]="paris";
        cities[0]="dakar";
        cities[1]="HEFEI";

        Searcher searcher=new Searcher(postingPath,stem,cities,semantic);
        String queryPath ="C:\\Users\\guys79\\Desktop\\queries.txt";
        GetQuery getQuery = new GetQuery(queryPath);
        //String query = getQuery.getNextQuery();
        String query = "guy Chunnel impact";
        String [] array;
       while(query!=null) {

            array = searcher.getMostRelevantDocNum(query);
            System.out.println("most relevant to \"" + query + "\" is " + array[0]);
            pr(array);
            //query = getQuery.getNextQuery();
           query =null;
        }
        searcher.shutDown();

    }
    public static void test2()
    {
        String postingPath = "C:\\Users\\guys79\\Desktop\\postingNew";
        String query="guy the man";
        SemanticQuery semanticQuery = new SemanticQuery(query,postingPath,true);
        //AddSemanticToQuery addSemanticToQuery2 = new AddSemanticToQuery(new SemanticQuery("ss",postingPath,false),"ocean");
    }



    public static void test3()
    {
        String path = "C:\\Users\\guys79\\Desktop\\corpus";
        String pathToCreate1 = "C:\\Users\\guys79\\Desktop\\corpus\\Everything1.txt";
        String pathToCreate2= "C:\\Users\\guys79\\Desktop\\corpus\\Everything2.txt";
        String pathToCreate3= "C:\\Users\\guys79\\Desktop\\corpus\\Everything3.txt";
        String pathToCreate4= "C:\\Users\\guys79\\Desktop\\corpus\\Everything4.txt";
        String pathToCreate5= "C:\\Users\\guys79\\Desktop\\corpus\\Everything5.txt";
        String pathToCreate6= "C:\\Users\\guys79\\Desktop\\corpus\\Everything6.txt";
        File fileNew1 = new File(pathToCreate1);
        File fileNew2 = new File(pathToCreate2);
        File fileNew3 = new File(pathToCreate3);
        File fileNew4 = new File(pathToCreate4);
        File fileNew5 = new File(pathToCreate5);
        File fileNew6 = new File(pathToCreate6);
        try {
            fileNew1.createNewFile();
            fileNew2.createNewFile();
            fileNew3.createNewFile();
            fileNew4.createNewFile();
            fileNew5.createNewFile();
            fileNew6.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        File[] directoryListing1;
        File dir1;
        BufferedWriter bufferedWriter1 =null;
        BufferedWriter bufferedWriter2 =null;
        BufferedWriter bufferedWriter3 =null;
        BufferedWriter bufferedWriter4 =null;
        BufferedWriter bufferedWriter5 =null;
        BufferedWriter bufferedWriter6 =null;
        int co=0;
        try {
            bufferedWriter1 = new BufferedWriter(new FileWriter(fileNew1));
            bufferedWriter2 = new BufferedWriter(new FileWriter(fileNew2));
            bufferedWriter3 = new BufferedWriter(new FileWriter(fileNew3));
            bufferedWriter4 = new BufferedWriter(new FileWriter(fileNew4));
            bufferedWriter5 = new BufferedWriter(new FileWriter(fileNew5));
            bufferedWriter6 = new BufferedWriter(new FileWriter(fileNew6));
            for (int i = 0; i < directoryListing.length; i++) {
                dir1 = new File(directoryListing[i].toString());
                directoryListing1 = dir1.listFiles();
                if(directoryListing1==null)
                    continue;
                for (int j = 0; j < directoryListing1.length; j++) {
                    co++;
                    System.out.println(co);
                    BufferedReader bufferedReader =new BufferedReader(new FileReader(directoryListing1[j]));
                    String line;
                    while ((line=bufferedReader.readLine())!=null)
                    {
                        if(co<300)
                            bufferedWriter1.write(line);
                        else if(co<600)
                            bufferedWriter2.write(line);
                        else if(co<900)
                            bufferedWriter3.write(line);
                        else if(co<1200)
                            bufferedWriter4.write(line);
                        else if(co<1500)
                            bufferedWriter5.write(line);
                        else
                            bufferedWriter6.write(line);
                    }
                    bufferedReader.close();
                }
            }
            bufferedWriter1.close();
            bufferedWriter2.close();
            bufferedWriter3.close();
            bufferedWriter4.close();
            bufferedWriter5.close();
            bufferedWriter6.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

