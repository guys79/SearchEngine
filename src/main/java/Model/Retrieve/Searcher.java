package Model.Retrieve;

import Model.Index.CityInfo;
import Model.Index.Indexer;
import Model.Index.LoadDictionary;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class will retrieve the most relevant documents given a collection of queries
 */
public class Searcher {

    private Indexer indexer;//The indexer
    private List<String> postingFileNames;//The names of the posting files
    private boolean stem;//True if we want to retrieve the information of the stemmed posting files, False - if we want to retrieve the information from the non stemmed files
    private ExecutorService executorService;//The threadpool
    private String postingFilesPath;//The path to the posting file
    private String [] relaventCities;//The array of relevant cities
    private Query query;//The query!
    private HashMap<String,int[]> mainMap;//The main map. The key is the term, and the value is an array in the size of 2
                                          //The first cell in the array is the df, and the second is cf
    private GetCity cityPostingInformation;//The class that we will use to get data on the cities
    private GetDoc documentPostingInformation;//The class that we will use to get data on the cities


    public Searcher(String postingFilesPath,boolean stem,String [] relaventCities,String query) {
        // TODO: 12/23/2018 Where get the futures?? 
        // TODO: 20/12/2018 Complete the constructor
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.stem = stem;
        this.postingFilesPath =postingFilesPath;

        //Getting the info from the three files (dictionary , doc, city)
        //Dictionary
        Future<HashMap<String,int []>> futureMap=executorService.submit(new LoadDictionary(this.postingFilesPath+"\\"+"dictionary"+"&"+this.stem+".txt"));
        //City
        this.cityPostingInformation = new GetCity(this.postingFilesPath+"\\"+"citys"+"&"+stem+".txt");
        Future<Boolean> futureCity = executorService.submit(this.cityPostingInformation);
        //Docs
        this.documentPostingInformation = new GetDoc(this.postingFilesPath+"\\"+"allDocs"+"&"+stem+".txt");
        Future<Boolean> futureDoc = executorService.submit(this.documentPostingInformation);
        
        
        //Initializing the data structures
        postingFileNames = new ArrayList<>();
        this.relaventCities = relaventCities;
        this.query = new Query(query,postingFilesPath,stem);
        File file = new File(postingFilesPath);


        //Getting the posting files names
        if(file.isDirectory()) {
            String stemS = ""+stem;
            String name="";
            int index =-1;
            File [] children =file.listFiles();
            for(int i=0;i<children.length;i++)
            {
                name = children[i].getName();
                index = name.lastIndexOf(".");
                if(name.substring(index-stemS.length(),index).equals(stemS))
                    this.postingFileNames.add(name);

            }

        }
        //Sorting the names so that we will be able to preform binary search
        this.postingFileNames.sort(String::compareToIgnoreCase);

    }
    private void printTermInfo(TermInfo termInfo)
    {
        System.out.println();
        HashMap<Integer,Integer> map = termInfo.getDocIdTfMap();
        for(Map.Entry<Integer,Integer> entry :map.entrySet())
        {
            System.out.println("The term - "+termInfo.getTerm()+" The Size " + map.size()+" The docId - "+ entry.getKey() +" The tf - "+entry.getValue());
        }
        System.out.println();
        System.out.println();
    }
    public void test()
    {

        // TODO: 12/23/2018  Check getRelevantData
        HashSet<TermInfo> termInfos = this.getRelevantData();
        for (TermInfo termInfo:termInfos)
        {
            printTermInfo(termInfo);
        }
        System.out.println("done");

    }
    /**
     * This function will return the name of the file(s) that contains the term
     *
     * @param term -The given term
     * @return - The list of files that contains the term
     */
    public List<String> getFileName(String term) {
        ArrayList<String> fileNames = new ArrayList<>();
        char note = (("" + term.charAt(0)).toLowerCase()).charAt(0);

        //Reduction.. Convert the term to 1+ ascii value
        if (!(note >= 'a' && note <= 'z')) {
            String termOther = "1";//The files that refer to others are the files that starts with 1
            for (int i = 0; i < term.length(); i++) {
                termOther = termOther + (int) (term.charAt(i));
            }
            term = termOther;
        }


        getFileNameRec(term, fileNames, 0, postingFileNames.size() - 1);

        return fileNames;

    }

    /**
     * This function is a recursive helper function
     * This function will return the name of the file(s) that contains the term
     * @param term - The given term
     * @param fileNames - The list that we will save the names of the files in (buffer)
     * @param start - The start index
     * @param end - The end index
     */
    private void getFileNameRec(String term, List<String> fileNames, int start, int end) {

        final int middle = (start + end) / 2;//Get the middle index
        String fullName = this.postingFileNames.get(middle);
        String name = fullName.substring(0,fullName.indexOf("_"));
        int comp = name.compareToIgnoreCase(term);
        //Problem
        if (middle == start && comp > 0) {
            return;
        }

        if (comp == 0) {
            String fullFileName = this.postingFileNames.get(middle);
            String fileName = fullFileName.substring(0,fullFileName.indexOf("_"));
            int index = middle;

            while (fileName.equalsIgnoreCase(term)) {
                fileNames.add(fullFileName);
                index--;
                if(index<0)
                    break;
                fullFileName = this.postingFileNames.get(index);
                fileName = fullFileName.substring(0,fullFileName.indexOf("_"));
            }
            if(index>=0) {
                fullFileName = this.postingFileNames.get(index);
                fileNames.add(fullFileName);
            }

            index = middle+1;
            while (index<this.postingFileNames.size()) {
                fullFileName = this.postingFileNames.get(index);
                fileName = fullFileName.substring(0,fullFileName.indexOf("_"));
                if(!fileName.equalsIgnoreCase(term))
                    break;
                fileNames.add(fullFileName);
                index++;
            }
            return;
        }

        String nextName = this.postingFileNames.get(middle + 1);
        nextName = nextName.substring(0,nextName.indexOf("_"));
        if (comp < 0 && (middle == end || nextName.compareToIgnoreCase(term) > 0)) {

            fileNames.add(fullName);
            return;
        }

        if (comp < 0)
            getFileNameRec(term, fileNames, middle + 1, end);
        else
            getFileNameRec(term, fileNames, start, middle);


    }


    /**
     * This function will return for a list of a given terms a map of file names and which terms they are containing
     * @param terms - The given terms
     * @return - A HashMap. The key is the name of the file, The value is a lis of all the terms that are in the document from the given terms list
     */
    private HashMap<String,List<String>> getTermsAndFiles(List<String> terms)
    {

        List<String>fileNames;
        HashMap<String,List<String>> fileNamesAndTerms = new HashMap<>();//This map contains file names as keys, and the terms that are supposed to be in the files
        String fileName = "";
        String term;
        for(int i=0;i<terms.size();i++)
        {
            term = terms.get(i);
            fileNames = this.getFileName(term);

            for(int j=0;j<fileNames.size();j++)
            {
                fileName = fileNames.get(j);
                if(!fileNamesAndTerms.containsKey(fileName))
                    fileNamesAndTerms.put(fileName,new ArrayList<>());
                fileNamesAndTerms.get(fileName).add(term);
            }
        }
        return fileNamesAndTerms;

    }

    /**
     * This function will get all the information about all the terms that are in the given list
     * @param terms - The given terms that we want to check
     * @return - The information about all of the terms i
     */
    private HashSet<TermInfo> getTheInformationAboutTheTerms(List<String>terms)
    {

        //Getting the names of the files that will contain the terms
        HashMap<String,List<String>> fileNamesAndTerms = this.getTermsAndFiles(terms);
        Set<String> keys = fileNamesAndTerms.keySet();
        Future<HashSet<TermInfo>> [] futures = new Future[keys.size()];

        int i = 0;
        //For each file, get the data about the terms
        for(String key:keys)
        {
            futures[i] = this.executorService.submit(new RetrieveTermInfo(key,new HashSet(fileNamesAndTerms.get(key)),this.postingFilesPath));
            i++;
        }

        HashMap<String,TermInfo> tempDic = new HashMap<>();
        HashSet<TermInfo> temp;
        String term;
        for(int j = 0; j<futures.length;j++)
        {
            try {
                temp = futures[j].get();
                if(temp!=null) {
                    for (TermInfo key : temp) {
                        term = key.getTerm();
                        //If the term is already in the map
                        if (tempDic.containsKey(term)) {
                            TermInfo exist = tempDic.get(term);
                            Set<Integer> setOfKeys;
                            //If the key's dictionary is bigger than the existing's dictionary
                            if (key.getDocIdTfMap().size() > exist.getDocIdTfMap().size()) {
                                setOfKeys = exist.getDocIdTfMap().keySet();
                                for (int docId : setOfKeys) {
                                    key.addInfo(docId, exist.getDocIdTfMap().get(docId));
                                }
                                //Update the term info
                                tempDic.put(term, key);
                            } else {
                                setOfKeys = key.getDocIdTfMap().keySet();
                                for (int docId : setOfKeys) {
                                    exist.addInfo(docId, key.getDocIdTfMap().get(docId));
                                }
                                //We don't need to update the map
                            }
                        } else {
                            tempDic.put(term, key);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new HashSet<>(tempDic.values());

    }

    private HashSet<TermInfo> getRelevantData()
    {
        //Getting the terms of the query as a list
        List<String> queryTerms = this.query.getQueryAsList();

        List<String> citiesToCheck = new ArrayList<>();
        //Go through all of the cities, if the city is a term in the query, don't add it (saves additional checks)
        for(int i=0;i<this.relaventCities.length;i++)
        {
            if(this.query.getNumOfOccurrences(this.relaventCities[i].toLowerCase())==0 && this.query.getNumOfOccurrences(this.relaventCities[i].toUpperCase())==0)
                citiesToCheck.add(this.relaventCities[i]);
        }


        //Check the terms data
        HashSet<TermInfo> termInfos = this.getTheInformationAboutTheTerms(queryTerms);
        Iterator firstMap = termInfos.iterator();

        if(this.relaventCities.length!=0) {
            //Check the cities as if they were terms
            HashSet<TermInfo> citiesInfo = this.getTheInformationAboutTheTerms(citiesToCheck);
            // TODO: 12/23/2018 Check for doc with the city in the title
            while (firstMap.hasNext()) {
                TermInfo current = (TermInfo) firstMap.next();
                HashMap<Integer, Integer> tempMap = current.getDocIdTfMap();
                Iterator secodMap = tempMap.entrySet().iterator();
                while (secodMap.hasNext()) {
                    Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) secodMap.next();
                    int key = entry.getKey();
                    if (!checkIfInTermInfo(key, citiesInfo))
                        secodMap.remove();
                }
            }
        }
        return termInfos;
    }
    private boolean checkIfInTermInfo(int docNum,HashSet<TermInfo> termInfos)
    {
        for(TermInfo termInfo : termInfos)
        {
            if(termInfo.getDocIdTfMap().containsKey(docNum))
                return true;
        }
        return false;
    }


    public List<Integer> getMostRelevantDocNum()
    {
        HashSet<TermInfo> queryData =this.getRelevantData();

        HashSet<Integer> numOfDocs = new HashSet<>();

        //Adding all the relevant docNum (Maybe unnecessary - saves that info twice)
        for(TermInfo queryTermInfo:queryData)
        {
            numOfDocs.addAll(queryTermInfo.docIdTfMap.keySet());
        }


        return null;
    }


}

