package Model.Retrieve;

import Model.Index.StopWordsHolder;
import jdk.nashorn.internal.parser.JSONParser;
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


public class AddSemanticToQuery implements Callable<Boolean> {

    private String term;
    private SemanticQuery semanticQuery;
    private int maxWords;
    private StopWordsHolder stopWordsHolder;
    public AddSemanticToQuery(SemanticQuery semanticQuery,String term,int maxWords,String stopWordsPath)
    {
        this.term = term;
        this.semanticQuery = semanticQuery;
        this.maxWords = maxWords;
        this.stopWordsHolder = new StopWordsHolder(stopWordsPath);

    }

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
            this.semanticQuery.addTermsAndScores(this.term,mapToAdd);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

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
                topTerms.put(name, jsonObject.getDouble("score"));
                count++;
            }
        }

       // System.out.println(this.term +" "+topTerms);
        return topTerms;

    }
    @Override
    public Boolean call() throws Exception {
        return getAndUploadData();
    }
}
