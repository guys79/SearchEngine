package Controller;

import Model.Retrieve.GetCity;
import Model.Retrieve.GetQuery;
import Model.Retrieve.Searcher;
import View.SearchView;

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


    public void brows() {
        boolean isStem= view.getSemantic();
        String[] citys = view.getReleventCitys();
        String postingPath = view.getPostingPath();
        boolean isChecked = view.getStem();
        searcher = new Searcher(postingPath, isChecked, citys, isStem);
        String queryPath= view.querisPath;
        GetQuery q= new GetQuery(queryPath);
        String s= q.getNextQuery();
        /*while(s!=null){
            view.releventDoctoQuery.put(s,searcher.getMostRelevantDocNum(s));
            s=q.getNextQuery();
        }*/
    }

    public List<String> getNamesOfCitys(){
        Searcher s= new Searcher(view.getPostingPath());
        return s.getNamesOfCitys();
    }

    public void run() {
        String query= view.oneQuery.getText();
        boolean isStem= view.getSemantic();
        String[] citys = view.getReleventCitys();
        String postingPath = view.getPostingPath();
        boolean isChecked = view.getStem();
        searcher = new Searcher(postingPath, isChecked, citys, isStem);
        System.out.println(searcher+" searcher");
        String [] docs = searcher.getMostRelevantDocNum(query);
        System.out.println(docs[0]);
        view.releventDoctoQuery.put(query,docs);
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
}
