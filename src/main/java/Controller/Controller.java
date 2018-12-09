package Controller;

import Model.Indexer;
import Model.Parser;
import View.View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is the controller of the GUi (part of the MVC design pattern)
 *
 */
public class Controller {

    private View view;//The view
    private Indexer indexer;//The model

    public Controller()
    {
        indexer=null;
    }

    public void reset()
    {
        this.indexer.reset();
    }
    public void setView(View view) {
        this.view = view;
    }


    public void start() {

        String corpusPath = view.getCorpusPath();
        String postingPath = view.getPostingPath();
        boolean isChecked = view.getStem();
        indexer = new Indexer(corpusPath, corpusPath, postingPath, isChecked);

        indexer.parseDocumentsThread();
    }

    public int getNumOfDocuments()
    {
        return this.indexer.getNumberOfDocuments();
    }
    public int getNumOfTerms()
    {
        return this.indexer.getDicSize();
    }
    public  void languageDisplay()
    {
        String languageFilePath = this.indexer.getLanguageFilePath();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(languageFilePath)));
            String language = "";
            while ((language = reader.readLine()) != null){
                this.view.addLanguage(language);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public HashMap<String ,int[]> getDictionary()
    {
         return this.indexer.getMainDictionary();


    }
    public void loadDictionary()
    {


            String postingPath = view.getPostingPath();
            String corpusPath = view.getCorpusPath();
            if(corpusPath.equals(""))
                corpusPath=postingPath;

            boolean isChecked = view.getStem();
            indexer = new Indexer(corpusPath, corpusPath, postingPath, isChecked);

        this.indexer.loadDictionary();


    }

}
