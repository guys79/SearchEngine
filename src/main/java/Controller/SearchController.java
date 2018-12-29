package Controller;

import Model.Retrieve.GetCity;
import Model.Retrieve.GetQuery;
import Model.Retrieve.Searcher;
import View.SearchView;

import java.util.Dictionary;
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

    /**
     * This function will start the indexing
     */
    public void brows() {
        boolean isStem= view.getSemantic();
        String[] citys = view.getReleventCitys();
        String postingPath = view.getPostingPath();
        boolean isChecked = view.getStem();
        searcher = new Searcher(postingPath, isChecked, citys, isStem);
        String queryPath= view.querisPath;
        GetQuery q= new GetQuery(queryPath);
        String s= q.getNextQuery();
        while(s!=null){
            view.releventDoctoQuery.put(s,searcher.getMostRelevantDocNum(s));
            s=q.getNextQuery();
        }
    }

    public List<String> getNamesOfCitys(){
        Searcher s= new Searcher(view.getPostingPath());
        return s.getNamesOfCitys();
    }

    public void Run() {
        String query= view.oneQuery.getText();
        boolean isStem= view.getSemantic();
        String[] citys = view.getReleventCitys();
        String postingPath = view.getPostingPath();
        boolean isChecked = view.getStem();
        searcher = new Searcher(postingPath, isChecked, citys, isStem);
        view.releventDoctoQuery.put(query,searcher.getMostRelevantDocNum(query));
    }
}