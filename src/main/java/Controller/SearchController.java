package Controller;

import Model.Retrieve.GetQuery;
import Model.Retrieve.Query;
import Model.Retrieve.QueryInfo;
import Model.Retrieve.Searcher;
import View.SearchView;
import javafx.util.Pair;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * This class is the controller of the Search scene
 */
public class SearchController {
    private SearchView view;//The view
    private Searcher searcher;//The searcher

    /**
     * This constructor
     */
    public SearchController(){
        searcher=null;
    }

    /**
     * This function will set the view of the class
     * @param searchView - The given view
     */
    public void setView(SearchView searchView) {
        this.view= searchView;
    }

    /**
     * This function will check if the Searcher needs to be initialized again, If so it will initialize it
     * @param postingFIlesPath - The posting file path
     * @param stem - True if we want to check the stemmed posting files
     * @param semantics - True if we want to use semantics
     * @param cities - The list of cities filter
     */
    public void initializeSeacherIfNeeded(String postingFIlesPath, boolean stem, boolean semantics,String [] cities)
    {
        if(checkIfNeedToInitialize(stem,semantics,postingFIlesPath,cities))
        {
            this.searcher = new Searcher(postingFIlesPath,stem,cities,semantics);
        }
    }

    /**
     * This function will get the queries and their results will add them to the view
     */
    public void brows() {
        boolean isStem= view.getStem();
        String[] citys = view.getReleventCitys();
        String postingPath = view.getPostingPath();
        boolean isSemantic = view.getSemantic();
        initializeSeacherIfNeeded(postingPath,isStem,isSemantic,citys);

        String queryPath= view.querisPath;
        view.releventDoctoQuery = new HashSet<>();
        GetQuery q= new GetQuery(queryPath);
        QueryInfo queryInfo = q.getNextQuery();
        String s;
        while(queryInfo!=null){
            s = queryInfo.getMyQuery();
            view.releventDoctoQuery.add(s);
            int [] docId = searcher.getMostRelevantDocNum(s);
            String[] docNames = new String[docId.length];
            for(int i=0;i<docNames.length;i++)
            {
                if(docId[i]==-1)
                    docNames[i] = null;
                else
                    docNames[i] = this.searcher.getDocName(docId[i]);

            }
            view.queriesAndResults.put(queryInfo.getMyQuery(),new Pair<>(queryInfo,docNames));
            queryInfo=q.getNextQuery();
        }
    }

    /**
     * This function will return the searcher
     * @return - The searcher
     */
    public Searcher getSearcher() {
        return searcher;
    }

    /**
     * This function will set the searcher
     * @param searcher - The given searcher
     */
    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    /**
     * This function will return the name of the cities
     * @param isStemmed -True if we want to look at the stemmed posting files
     * @return - The list of cities that we have indexed
     */
    public List<String> getNamesOfCitys(boolean isStemmed){
        Searcher s= new Searcher(view.getPostingPath(),isStemmed);
        return s.getNamesOfCitys();
    }

    /**
     * This function will run a single query and will retrieve the relevant docs
     * @param query - The given query
     * @return - The docNames and their entities
     */
    public Pair<String,List<String>> [] run(String query) {
        boolean isStem= view.getStem();
        String[] citys = view.getReleventCitys();
        String postingPath = view.getPostingPath();
        boolean isSemantic = view.getSemantic();
        initializeSeacherIfNeeded(postingPath,isStem,isSemantic,citys);
        int [] docId = searcher.getMostRelevantDocNum(query);
        Pair<String,List<String>>[] docsInfo = new Pair[docId.length];
        String [] docNames = new String[docId.length];
        String name;
        for(int i=0;i<docsInfo.length;i++)
        {
            if(docId[i]==-1)
                docsInfo[i] = null;
            else {
                name = this.searcher.getDocName(docId[i]);
                docsInfo[i] = new Pair<String, List<String>>(name, this.searcher.getEntities(docId[i]));
                docNames[i] = name;
            }

        }
        Random random = new Random();
        view.queriesAndResults.put(query,new Pair<>(new QueryInfo(random.nextInt(900)+100,query),docNames));
        return docsInfo;
    }

    /**
     * This function will check if the necessary files are in the posting directory
     * @param stem - True if we want to look at the stemmed posting files
     * @return - Return True if all of the necessary files are in the posting directory
     */
    public boolean checkForMustHavePostingFiles(boolean stem)
    {
        String postingPath = this.view.getPostingPath();
        File postingFile = new File(postingPath);

        File [] children = postingFile.listFiles();

        HashSet<String>keyWords = new HashSet<>();
        keyWords.add("stop_words.txt");
        keyWords.add("citys&"+stem+".txt");
        keyWords.add("allDocs&"+stem+".txt");
        keyWords.add("entities&"+stem+".txt");
        keyWords.add("dictionary&"+stem+".txt");

        for(int i=0;i<children.length;i++)
        {

            if(keyWords.contains(children[i].getName()))
            {
                keyWords.remove(children[i].getName());
                if(keyWords.size()==0)
                    return true;
            }
        }
        return false;
    }

    /**
     * Ths function will check that there is at least 1 posting file that we can retrieve info from
     * @param stem - True if we want to look at the stemmed posting files
     * @return - True if there is at least 1 posting file that we can retrieve info from
     */
    public boolean checkForPostingFiles(boolean stem)
    {

        String postingPath = this.view.getPostingPath();
        File postingFile = new File(postingPath);

        File [] children = postingFile.listFiles();
        String substringToSearchFor = "_"+stem+".txt";
        String name;
        String subName;
        for(int i=0;i<children.length;i++){
            name = children[i].getName();
            if(name.length()<substringToSearchFor.length())
                continue;
            subName = name.substring(name.length()-substringToSearchFor.length());
            if(subName.equals(substringToSearchFor))
            {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param stem - True if we want to look at the stemmed posting files
     * @param semantic - True if we want to use semantics in our search
     * @param postingPath - The location of the posting files
     * @return - True if we need to iitialize the searcher
     */
    private boolean checkIfNeedToInitialize(boolean stem, boolean semantic, String postingPath,String []cities)
    {
        if(this.searcher == null)
            return true;
        String [] citiesSearcher = this.searcher.getRelaventCities();
        HashSet<String> citiesAsHash = new HashSet<>();
        for(int i=0;i<citiesSearcher.length;i++)
        {
            citiesAsHash.add(citiesSearcher[i]);
        }
        for(int i=0;i<cities.length;i++)
        {
            if(citiesAsHash.contains(cities[i]))
                citiesAsHash.remove(cities[i]);
        }
        if(citiesAsHash.size()!=0)
            return true;
        return  (!(this.searcher.getStem()== stem && this.searcher.getSemantic() == semantic && this.searcher.getPostingFile().equals(postingPath)));
    }
}
