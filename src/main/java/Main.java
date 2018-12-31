import Model.Retrieve.Searcher;
import View.ViewChanger;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
       ViewChanger viewChanger = new ViewChanger();
        viewChanger.setPrimaryStage(primaryStage);
        viewChanger.goToSearch();
        //viewChanger.goToIndex();
        //String posting = "C:\\Users\\guys79\\Desktop\\posting";
        //public Searcher(String postingFilesPath, boolean stem, String [] relaventCities, boolean semantic) {
        //Searcher searcher = new Searcher(posting,true,new String[0],false);
        //int [] id = searcher.getMostRelevantDocNum("between 9 t0 10");
        //System.out.println(searcher.getDocName(id[0]));
        //double num = Math.pow(10,255);
        //int comp = compareIrgular("1^49^48^48","100 M Dollars");
        //System.out.println(comp);


      //pr(id);
    }


    public static void main(String[] args) {
        launch(args);
    }
    public static void pr(int []a)
    {
        for(int i=0;i<a.length;i++)
        {
            System.out.println(a[i]);
        }
    }

    public static int compareIrgular(String fileName,String term)
    {

        if(fileName.equals("1^^_"))
            return 1;
        char note =(""+term.charAt(0)).toLowerCase().charAt(0);
        if(note>='a' && note<='z')
            return fileName.compareToIgnoreCase(term);
        String [] split = fileName.split("\\^");
        System.out.println(split[0]);
        String fileNameRestore = "";
        for(int i=1;i<split.length;i++)
        {
            System.out.println(split[i]);
            fileNameRestore+= (char)(Integer.parseInt(split[i]));

        }
        System.out.println(fileNameRestore);
        return fileNameRestore.compareToIgnoreCase(term);
    }
}