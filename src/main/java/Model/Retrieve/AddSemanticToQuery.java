package Model.Retrieve;

import Model.Index.StopWordsHolder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * This class will add the stemmed words to the query
 */
public class AddSemanticToQuery implements Callable<Boolean> {

    private String term;//The term of which we want to gt info on
    private SemanticQuery semanticQuery;//The query
    private int maxWords;//The number of words that we want to retrieve
    private StopWordsHolder stopWordsHolder;//The stopWords holder

    /**
     * The constructor
     * @param semanticQuery - The term of which we want to gt info on
     * @param term - The query
     * @param maxWords - The number of semantic words that we want to get
     * @param stopWordsPath - The path to the stopWords file
     */
    public AddSemanticToQuery(SemanticQuery semanticQuery,String term,int maxWords,String stopWordsPath)
    {
        this.term = term;
        this.semanticQuery = semanticQuery;
        this.maxWords = maxWords;
        this.stopWordsHolder = new StopWordsHolder(stopWordsPath);

    }

    /**
     * This function will get the information about the semantic words and will add it to the query
     * @return - True if the process was successful
     */
    private boolean getAndUploadData()
    {

        String urlSemantic="https://api.datamuse.com/words?ml="+term;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(urlSemantic);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            HashMap<String,Double> mapToAdd=null;
            while((line= bufferedReader.readLine())!=null)
            {
                mapToAdd = getTopTerms(line);
            }
            inputStreamReader.close();
            bufferedReader.close();
            this.semanticQuery.addTermsAndScores(this.term.toLowerCase(),mapToAdd);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This function will return the wanted information from the top matched words
     * @param input - The
     * @return - The information from the API
     */
    private HashMap<String,Double> getTopTerms(String input)
    {

        HashMap<String,Double> topTerms = new HashMap<>();
        JSONArray jsonArray = new JSONArray(input);
        JSONObject jsonObject;
        String name;
        int count = 0;
        for(int i=0;count<maxWords && i<jsonArray.length();i++)
        {

            jsonObject = jsonArray.getJSONObject(i);
            name = jsonObject.getString("word").split(" ")[0];
            if(!this.stopWordsHolder.isStopWord(name)&&!topTerms.containsKey(name))
            {
                //  System.out.println(name +" "+count);
                topTerms.put(name.toLowerCase(), jsonObject.getDouble("score"));
                count++;
            }
        }

        // System.out.println(this.term +" "+topTerms);
        return topTerms;

    }

    /**
     * This function will add the data about the semantic to the query as a Callable
     * @return
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {
        return getAndUploadData();
    }
}