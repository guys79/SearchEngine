package Controller;

import Model.Retrieve.Searcher;
import View.QueryDisplayerView;

public class QueryDisplayController {

    private QueryDisplayerView view;//The view associated with th controller
    private Searcher searcher;

    public void setView(QueryDisplayerView queryDisplayerView) {
        this.view = queryDisplayerView;
    }
    public void setSearcher(Searcher searcher)
    {
        this.searcher = searcher;
    }




}
