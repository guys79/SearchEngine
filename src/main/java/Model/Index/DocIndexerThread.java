package Model.Index;

/**
 * This class will index the document as runnable
 */
public class DocIndexerThread implements Runnable {
    private DocIndexer docIndexer;// The docIndexer

    /**
     * This function is the constructor of the class
     * @param docIndexer - The document indexer
     */
    public DocIndexerThread(DocIndexer docIndexer) {
        this.docIndexer = docIndexer;
    }

    @Override
    /**
     * This function will run the writeToTheFile function in the given docIndexer
     */
    public void run() {
        try {
            this.docIndexer.writeToTheFile();//Summoning the function that uploads
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This function will update the data about a document
     * @param docNum - The doc number
     * @param numOfuniqeTerms - The number of unique terms in the document
     * @param maxValues - the maximal frequency in the document
     * @param length - The length of the document
     */
    public void addDocData(int docNum, int numOfuniqeTerms, int maxValues,int length) {
        try {
            this.docIndexer.addToDic(docNum, numOfuniqeTerms, maxValues,length);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
