package Model.Retrieve;

import java.util.HashMap;

/**
 * This class represents information about the terms
 */
public class TermInfo {
    private String term;//The term
    public HashMap<Integer,Integer> docIdTfMap;//This map is the map of docId as the key and the tf as value
    private int df;//Teh document frequency

    /**
     * The constructor
     * @param term - The given term
     * @param docId - The docId
     * @param tf - The term frequency of the term in the doc
     */
    public  TermInfo(String term, int docId, int tf)
    {
        this.term = term;
        this.docIdTfMap = new HashMap<>();
        this.docIdTfMap.put(docId,tf);
        this.df=0;
    }

    /**
     * The constructor
     * @param term - The given tern
     */
    public TermInfo(String term)
    {
        this.term = term;
        this.docIdTfMap = new HashMap<>();
        this.df=0;
    }

    /**
     * This function will add an information about the term
     * @param docId - The docNumber
     * @param tf - The term frequency of the term in the doc
     */
    public void addInfo(int docId,int tf)
    {
        this.docIdTfMap.put(docId,tf);
    }

    /**
     * This function will return the name of the term
     * @return - The name of the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * This function will return a map containing docId as keys and tf as values
     * @return - A map containing docId as keys and tf as values
     */
    public HashMap<Integer, Integer> getDocIdTfMap() {
        return docIdTfMap;
    }

    /**
     * This function wil return the size of the TermInfo
     * @return - This function will return the size of the TermInfo (number of pieces of information)
     */
    public int size()
    {
        return this.getDocIdTfMap().size();
    }

    /**
     * This function will return the document frequency of the term
     * @return - The document frequency of the term
     */
    public int getDf() {
        return df;
    }

    /**
     * This function will save the document frequency of the term
     * @param df - the document frequency of the term
     */
    public void setDf(int df) {
        this.df = df;
    }
}
