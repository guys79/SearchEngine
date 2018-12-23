package Model.Retrieve;

import Model.Index.DocumentReturnValue;
import Model.Index.Parser;

import java.util.*;

/**
 * This class represents a single simple query
 */
public class Query {
    private HashMap <String,Integer> termsAndTf;//A map of term as value and tf as key
    private String [] terms;//The terms in an array of strings



    /**
     * The constructor
     * This function will parse the query and process it
     * @param query - The given query as text
     * @param postingPath - The file path of the posting file
     * @param stem - True if we want to consider the stemmed files
     */
    public Query(String query,String postingPath,boolean stem) {


        Set<String> terms;
        //Parse the query
        Parser parserStemmed = new Parser(postingPath, "", stem);
        DocumentReturnValue parsedQuery = parserStemmed.motherOfAllFunctions(query);
        int index =0;
        if(stem)
        {
            //Parse the query
            Parser parserNotStemmed = new Parser(postingPath, "", false);
            DocumentReturnValue parsedQueryNotStemmed = parserNotStemmed.motherOfAllFunctions(query);
            this.terms = new String[parsedQuery.getDictionaryOfWords().size()+parsedQuery.getDictionaryOfUniqueTerms().size()+parsedQueryNotStemmed.getDictionaryOfWords().size()];
            terms = parsedQueryNotStemmed.getDictionaryOfWords().keySet();

            //For each semantic word!!!
            for (String key : terms) {
                this.terms[index]=key;
                index++;
                this.termsAndTf.put(key, parsedQuery.getDictionaryOfUniqueTerms().get(key));
            }

        }
        else
        {
            //Init the terms array
            this.terms = new String[parsedQuery.getDictionaryOfWords().size()+parsedQuery.getDictionaryOfUniqueTerms().size()];

        }





        //init the map
        this.termsAndTf = new HashMap<>();

        //Add the terms to the map and to the array
        terms = parsedQuery.getDictionaryOfUniqueTerms().keySet();
        for (String key : terms) {
            this.terms[index]=key;
            index++;
            this.termsAndTf.put(key, parsedQuery.getDictionaryOfUniqueTerms().get(key));
        }
        terms = parsedQuery.getDictionaryOfWords().keySet();
        for (String key : terms) {
            this.terms[index]=key;
            index++;
            this.termsAndTf.put(key, parsedQuery.getDictionaryOfWords().get(key));
        }



        //System.out.println(this.termsAndTf);

    }

    /**
     * This function will return the term in the place of 'index'
     * @param index - The given index
     * @return - The term in the place of index
     */
    public String getTerm(int index)
    {
        if(index>=0 && index<this.terms.length)
            return this.terms[index];
        return "";
    }

    /**
     * This function will return the number of occurrences of the ter in the text
     * @param term - Thd given term
     * @return  -The number of times that the term appeared in the query
     */
    public int getNumOfOccurrences(String term)
    {
        if(this.termsAndTf.containsKey(term))
            return this.termsAndTf.get(term);
        return 0;
    }

    /**
     * This function will return the size of the query
     * @return - The size of the query (The amount of unique terms)
     */
    public int size()
    {
        return this.terms.length;
    }

    /**
     * This function will return the query as a list of strings
     * @return - Query as a list of strings
     */
    public List<String> getQueryAsList()
    {
        List<String> newList = new ArrayList<>();
        for(int i=0;i<this.terms.length;i++)
        {
            newList.add(this.terms[i]);
        }
        return newList;
    }
}
