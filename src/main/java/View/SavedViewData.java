package View;

import Model.Retrieve.QueryInfo;
import Model.Retrieve.Searcher;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class will contain the information that we want to save about the SearchView
 */
public class SavedViewData {
    private Searcher searcher;//The searcher
    private boolean isStemButtonDisabled;//True if the stem button is disables
    private boolean isStem;//True if we want to look at the stemmed posting files
    private boolean isSemantic;//True if we want to use semantics
    private String postingPath;//The posting file path
    private String quriesPath;// The queries file path
    private HashSet<String> quries;//The queries
    private String functionName;//The name of the function that updated the data
    public HashMap<String,Pair<QueryInfo,String []>> queriesAndResults;//The queries and their results

    /**
     * The constructor of the class
     * @param searcher - The searcher
     * @param isStemButtonDisabled - True if the stem button is disabled
     * @param isStem - True if we want to look at the stemmed posting files
     * @param isSemantic - True if we want to use semantics
     * @param postingPath - The posting file path
     * @param quriesPath - The queries file path
     * @param quries - The queries
     * @param functionName - The name of the function that updated the data
     * @param queriesAndResults - The queries and their results
     */
    public SavedViewData(Searcher searcher,boolean isStemButtonDisabled,boolean isStem,boolean isSemantic,String postingPath,String quriesPath,HashSet<String>quries,String functionName,HashMap<String,Pair<QueryInfo,String []>> queriesAndResults)
    {
        this.functionName = functionName;
        this.searcher = searcher;
        this.isStemButtonDisabled = isStemButtonDisabled;
        this.isStem = isStem;
        this.isSemantic = isSemantic;
        this.postingPath = postingPath;
        this.quriesPath = quriesPath;
        this.quries = quries;
        this.queriesAndResults = queriesAndResults;
    }

    /**
     * This function will return the searcher
     * @return - The searcher
     */
    public Searcher getSearcher() {
        return searcher;
    }

    /**
     * This function will return the posting file path
     * @return - The posting file path
     */
    public String getPostingPath() {
        return postingPath;
    }

    /**
     * This function will return the queries fle path
     * @return - The queries file path
     */
    public String getQuriesPath() {
        return quriesPath;
    }

    /**
     * This function will return the queries
     * @return - The queries
     */
    public HashSet<String> getQuries() {
        return quries;
    }

    /**
     * True if the stem button is disabled
     * @return - True if the stem button is disabled
     */
    public boolean isStemButtonDisabled()
    {
        return  this.isStemButtonDisabled;
    }

    /**
     * True if we want to use semantics
     * @return - True if we want to use semantics
     */
    public boolean isSemantic() {
        return isSemantic;
    }

    /**
     * True if we want to look at the stemmed posting files
     * @return - True if we want to look at the stemmed posting files
     */
    public boolean isStem() {
        return isStem;
    }

    /**
     * This function will return the name of the function that updated the data
     * @return - The name of the function that updated the data
     */
    public String getFunctionName() {
        return functionName;
    }
}
