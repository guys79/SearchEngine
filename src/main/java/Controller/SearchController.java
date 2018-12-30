package Controller;

import Model.Retrieve.GetCity;
import Model.Retrieve.GetQuery;
import Model.Retrieve.Searcher;
import View.SearchView;
import View.ViewChanger;
import javafx.util.Pair;

import java.io.File;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;

public class SearchController {
    private SearchView view;//The view
    private Searcher searcher;

    public SearchController(){
        searcher=null;
    }

    public void setView(SearchView searchView) {
        this.view= searchView;
    }

    public void initializeSeacherIfNeeded(String postingFIlesPath, boolean stem, boolean semantics,String [] cities)
    {
        if(checkIfNeedToInitialize(stem,semantics,postingFIlesPath))
        {
            this.searcher = new Searcher(postingFIlesPath,stem,cities,semantics);
        }
    }
    public void brows() {
        boolean isStem= view.getStem();
        String[] citys = view.getReleventCitys();
        String postingPath = view.getPostingPath();
        boolean isSemantic = view.getSemantic();
        initializeSeacherIfNeeded(postingPath,isStem,isSemantic,citys);
        String queryPath= view.querisPath;
        view.releventDoctoQuery = new HashSet<>();
        GetQuery q= new GetQuery(queryPath);
        String s= q.getNextQuery();
        while(s!=null){
            view.releventDoctoQuery.add(s);
            s=q.getNextQuery();
        }
    }


    public Searcher getSearcher() {
        return searcher;
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public List<String> getNamesOfCitys(boolean isStemmed){
        Searcher s= new Searcher(view.getPostingPath(),isStemmed);
        return s.getNamesOfCitys();
    }

    public Pair<String,List<String>> [] run(String query) {
        boolean isStem= view.getStem();
        String[] citys = view.getReleventCitys();
        String postingPath = view.getPostingPath();
        boolean isSemantic = view.getSemantic();
        initializeSeacherIfNeeded(postingPath,isStem,isSemantic,citys);
        int [] docId = searcher.getMostRelevantDocNum(query);
        Pair<String,List<String>>[] docNames = new Pair[docId.length];
        for(int i=0;i<docNames.length;i++)
        {
            if(docId[i]==-1)
                docNames[i] = null;
            else
                docNames[i] = new Pair<String,List<String>>(this.searcher.getDocName(docId[i]),this.searcher.getEntities(docId[i]));

        }
        return docNames;
    }
    private void print(String [] array)
    {
        for(int i=0;i<array.length;i++)
        {
            System.out.println(array[i]);
        }
    }
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
    private boolean checkIfNeedToInitialize(boolean stem, boolean semantic, String postingPath)
    {
        return this.searcher == null || (!(this.searcher.getStem()== stem && this.searcher.getSemantic() == semantic && this.searcher.getPostingFile().equals(postingPath)));
    }
}
