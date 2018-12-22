package Model.Retrieve;

import java.util.HashMap;

/**
 * This class represents information about the terms
 */
public class TermInfo {
    String term;//The term
    HashMap<Integer,Integer> docIdTfMap;//This map is the map of docId as the key and the tf as value

    /**
     * The constructor
     * @param term - The given term
     * @param docId - The docId
     * @param tf - The term frequency of the term in the doc
     */
    public  TermInfo(String term, int docId,int tf)
    {
        this.term = term;
        this.docIdTfMap = new HashMap<>();
        this.docIdTfMap.put(docId,tf);
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
}
