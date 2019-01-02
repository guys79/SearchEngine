package Controller;
import Model.Index.Indexer;
import View.IndexView;
import java.io.*;
import java.util.HashMap;


/**
 * This class is the controller of the GUi (part of the MVC design pattern)
 *
 */
public class IndexController {

    private IndexView view;//The view
    private Indexer indexer;//The model

    /**
     * The constructor of the class
     */
    public IndexController() {
        indexer = null;
    }


    /**
     * This function will delete the posting files and will reset the main dictionary
     * @return True if the process was successfuk
     */
    public boolean reset()
    {
        String postFilePath = view.getPostingPath();
        boolean stem = view.getStem();
        File file = new File(postFilePath);
        File [] children =file.listFiles();
        boolean flag= true;
        String end = ""+stem+".txt";
        for(int i=0;i<children.length;i++)
        {
            if(children[i].getName().substring(children[i].getName().length()-end.length()).equals(end))
                flag = flag && children[i].delete();
        }
        File check = new File(postFilePath+"\\"+"dictionary"+"&"+!stem+".txt");
        if(!check.exists())
        {
            File stopWords = new File(postFilePath+"\\stop_words.txt");
            flag = flag && stopWords.delete();
        }
        if(indexer!=null)
        {
            indexer.reset();
        }
        return flag;
    }
    /**
     * This function will set the view of the instance of the IndexController class
     *
     * @param view - The given view
     */
    public void setView(IndexView view) {
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
     * This function will get the collection of languages from the model and will send the collection to the IndexView so it will display the languages
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
