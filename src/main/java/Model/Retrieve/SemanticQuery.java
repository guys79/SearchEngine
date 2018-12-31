package Model.Retrieve;

import Model.Index.DocumentReturnValue;
import Model.Index.Parser;
import Model.Index.StopWordsHolder;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;
import sun.awt.Mutex;

import java.util.*;
import java.util.concurrent.*;

public class SemanticQuery extends Query {


    private HashMap<String,Double>termsAndScores;
    private Mutex mutex;
    /**
     * The constructor
     * This function will parse the query and process it
     *
     * @param query       - The given query as text
     * @param postingPath - The file path of the posting file
     * @param stem        - True if we want to consider the stemmed files
     */
    public SemanticQuery(String query, String postingPath, boolean stem) {
        super(query, postingPath, stem);
        final int NUM_OF_WORDS_PER_TERM=7;
        this.termsAndScores = new HashMap<>();
        this.mutex = new Mutex();
        HashSet<String> termsToCheckSemantic;
        if(stem)
        {
            //Parse the query
            Parser parserNotStemmed = new Parser(postingPath, "", false);
            DocumentReturnValue parsedQueryNotStemmed = parserNotStemmed.motherOfAllFunctions(query);
            termsToCheckSemantic = new HashSet<>(parsedQueryNotStemmed.getDictionaryOfWords().keySet());
            for(String term:parsedQueryNotStemmed.getDictionaryOfUniqueTerms().keySet())
            {
                this.termsAndScores.put(term,1.0);
            }


        }
        else
        {
            termsToCheckSemantic = new HashSet<>();
            char note;
            for(int i=0;i<this.terms.length;i++)
            {
                note = (""+this.terms[i].charAt(0)).toLowerCase().charAt(0);
                if(note>='a' && note<='z')
                    termsToCheckSemantic.add(this.terms[i]);
                else
                    this.termsAndScores.put(this.terms[i],1.0);
            }
        }


        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        Future<Boolean> [] futures = new Future[termsToCheckSemantic.size()];
        int i=0;
        for(String key:termsToCheckSemantic)
        {
            futures[i] = executorService.submit(new AddSemanticToQuery(this,key,NUM_OF_WORDS_PER_TERM,postingPath+"\\stop_words.txt"));
            i++;
        }

        for(int j=0;j<futures.length;j++)
        {
            try {
                futures[j].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        if(stem)
        {
            HashMap<String,Double>stemmedTermsAndScores = new HashMap<>();
            SnowballStemmer stemmer = new porterStemmer();
            String stemmed;
            Iterator iterator = this.termsAndScores.entrySet().iterator();
            String key;
            Map.Entry<String,Double> entry;
            while (iterator.hasNext())
            {
                entry = (Map.Entry<String,Double>)iterator.next();
                key = entry.getKey();
                stemmer.setCurrent(key.toLowerCase());
                if (stemmer.stem())
                    stemmed = stemmer.getCurrent();
                else
                    stemmed = key;
                stemmedTermsAndScores.put(stemmed,entry.getValue());
            }

            this.termsAndScores = stemmedTermsAndScores;

        }


        //System.out.println(this.termsAndScores.size()+" in total");

    }
    public void addTermsAndScores(String original,HashMap<String,Double> sTermsAndScores)
    {
        this.mutex.lock();
       // System.out.println(sTermsAndScores.size()+1);
        this.termsAndScores.put(original,1.0);
        //If there are no semantic words
        if(sTermsAndScores.size()==0) {

            return;
        }


        double divideBy = 1.1;
        double max = Double.MIN_VALUE;
        double score;
        for(String key:sTermsAndScores.keySet())
        {
            score = sTermsAndScores.get(key);
            if(max<score)
            {
                max =score;
            }
        }


        double scoreToAdd;
        String key;
        for(String keys:sTermsAndScores.keySet())
        {
            key = keys.toLowerCase();
            scoreToAdd = sTermsAndScores.get(key);
            scoreToAdd =(scoreToAdd/max)/divideBy;
            if(this.termsAndScores.containsKey(key))
            {

                score = this.termsAndScores.get(key);
                if(score>=scoreToAdd) {
                    continue;
                }
            }
            this.termsAndScores.put(key,scoreToAdd);
        }
        this.mutex.unlock();


    }

    @Override
    public List<String> getQueryAsList() {
        return new ArrayList<>(this.termsAndScores.keySet());
    }

    public double getWeight(String term)
    {
        return this.termsAndScores.get(term.toLowerCase());
    }
}
