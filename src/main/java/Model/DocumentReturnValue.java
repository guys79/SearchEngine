package Model;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents the data that we retrieved from the parser about a single document
 */
public class DocumentReturnValue {
    private HashMap<String, Integer> dictionaryOfWords;// The nap of the "regular" words (not unique terms)
    private HashMap<String, Integer> dictionaryOfUniqueTerms;// The map of unique terms
    private HashSet<Integer> cityLocations;//The locations of a city in the doc
    private int maxFrequency;//The maximal frequency in this document
    private int docLength;// The length of the document

    /**
     * The constructor of thhis class
     * @param dictionaryOfWords - The dictionary of regular words that we have found while parsing
     * @param dictionaryOfUniqueTerms - The dictionary of unique words that we have found while parsing
     * @param maxF - The maximal frequency in the document
     * @param cityLocations - The locations of the city in the document
     * @param docLength - the documment's length
     */
    public DocumentReturnValue(HashMap<String,Integer> dictionaryOfWords,HashMap<String,Integer> dictionaryOfUniqueTerms,int maxF,HashSet<Integer>cityLocations,int docLength)
    {
        this.docLength = docLength;
        this.dictionaryOfUniqueTerms = dictionaryOfUniqueTerms;
        this.dictionaryOfWords = dictionaryOfWords;
        this.maxFrequency = maxF;
        this.cityLocations = cityLocations;
    }

    /**
     * This function will return the length of the doc
     * @return - The length of the doc
     */
    public int getDocLength() {
        return docLength;
    }

    /**
     * This function will return the maximal frequency of a term in the document
     * @return - The maximal frequency
     */
    public int getMaxFrequency() {
        return maxFrequency;
    }

    /**
     * This locations of the city in the document
     * @return - The HashMap of the locations of the city in the file
     */
    public HashSet<Integer> getCityLocations() {
        return cityLocations;
    }

    /**
     * This function will return the map of regular words
     * @return - The map of regular wordss
     */
    public HashMap<String, Integer> getDictionaryOfUniqueTerms() {
        return dictionaryOfUniqueTerms;
    }

    /**
     * This function will return the dictionary of unique terms
     * @return - The dictionary of unique terms
     */
    public HashMap<String, Integer> getDictionaryOfWords() {
        return dictionaryOfWords;
    }

    /**
     * This function will set the value of the dictionary of unique words
     * @param dictionaryOfUniqueTerms - The given dictionary
     */
    public void setDictionaryOfUniqueTerms(HashMap<String, Integer> dictionaryOfUniqueTerms) {
        this.dictionaryOfUniqueTerms = dictionaryOfUniqueTerms;
    }

    /**
     * This function will set the value of the dictionary of regular words
     * @param dictionaryOfWords - The dictionary
     */
    public void setDictionaryOfWords(HashMap<String, Integer> dictionaryOfWords) {
        this.dictionaryOfWords = dictionaryOfWords;
    }
}
