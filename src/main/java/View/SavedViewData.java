package View;

import Model.Retrieve.Searcher;

import java.util.HashSet;
import java.util.List;

public class SavedViewData {
    private Searcher searcher;
    private boolean isStemButtonDisabled;
    private boolean isStem;
    private boolean isSemantic;
    private String postingPath;
    private String quriesPath;
    private HashSet<String> quries;
    private String functionName;

    public SavedViewData(Searcher searcher,boolean isStemButtonDisabled,boolean isStem,boolean isSemantic,String postingPath,String quriesPath,HashSet<String>quries,String functionName)
    {
        this.functionName = functionName;
        this.searcher = searcher;
        this.isStemButtonDisabled = isStemButtonDisabled;
        this.isStem = isStem;
        this.isSemantic = isSemantic;
        this.postingPath = postingPath;
        this.quriesPath = quriesPath;
        this.quries = quries;
    }

    public Searcher getSearcher() {
        return searcher;
    }

    public String getPostingPath() {
        return postingPath;
    }

    public String getQuriesPath() {
        return quriesPath;
    }

    public HashSet<String> getQuries() {
        return quries;
    }
    public boolean isStemButtonDisabled()
    {
        return  this.isStemButtonDisabled;
    }

    public boolean isSemantic() {
        return isSemantic;
    }

    public boolean isStem() {
        return isStem;
    }

    public String getFunctionName() {
        return functionName;
    }
}
