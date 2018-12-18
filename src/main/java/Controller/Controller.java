package Controller;
import Model.Indexer;
import View.View;
import java.io.*;
import java.util.HashMap;


/**
 * This class is the controller of the GUi (part of the MVC design pattern)
 *
 */
public class Controller {

    private View view;//The view
    private Indexer indexer;//The model

    /**
     * The constructor of the class
     */
    public Controller() {
        indexer = null;
    }

    /**
     * This function will delete the posting files and will reset the dictionary
     */
    public void reset()
    {
        if(indexer!=null)
        {
        this.indexer.reset();
        }
    }

    /**
     * This function will set the view of the instance of the Controller class
     *
     * @param view - The given view
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * This function will start the indexing
     */
    public void start() {

        String corpusPath = view.getCorpusPath();
        String postingPath = view.getPostingPath();
        boolean isChecked = view.getStem();
        indexer = new Indexer(corpusPath, corpusPath, postingPath, isChecked);

        indexer.parseDocumentsThread();
    }

    /**
     * This function will return the number of documents scanned
     *
     * @return - The number of documents scanned
     */
    public int getNumOfDocuments() {
        return this.indexer.getNumberOfDocuments();
    }

    /**
     * This function will return the number of unique terms
     *
     * @return - The number of unique terms
     */
    public int getNumOfTerms() {
        return this.indexer.getDicSize();
    }

    /**
     * This function will get the collection of languages from the model and will send the collection to the View so it will display the languages
     */
    public void languageDisplay() {
        String languageFilePath = this.indexer.getLanguageFilePath();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(languageFilePath)));
            String language = "";
            while ((language = reader.readLine()) != null) {
                this.view.addLanguage(language);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This function will return the dictionary of the model
     *
     * @return - The dictionary of the model
     */
    public HashMap<String, int[]> getDictionary() {
        return this.indexer.getMainDictionary();


    }

    /**
     * This function will load the dictionary from a file
     */
    public void loadDictionary() {


        String postingPath = view.getPostingPath();
        String corpusPath = view.getCorpusPath();
        if (corpusPath.equals(""))
            corpusPath = postingPath;
        boolean isChecked = view.getStem();
        indexer = new Indexer(corpusPath, corpusPath, postingPath, isChecked);
        this.indexer.loadDictionary();


    }

}
