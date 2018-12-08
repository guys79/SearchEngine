package Controller;

import Model.Indexer;
import View.View;

/**
 * This class is the controller of the GUi (part of the MVC design pattern)
 *
 */
public class Controller {

    private View view;//The view
    private Indexer indexer;//The model

    public Controller()
    {

    }

    public void setView(View view) {
        this.view = view;
    }


    public void start()
    {
        String corpusPath = view.getCorpusPath();
        String postingPath = view.getPostingPath();
        boolean isChecked = view.getStem();
        indexer = new Indexer(corpusPath,corpusPath,postingPath,isChecked);
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

}
