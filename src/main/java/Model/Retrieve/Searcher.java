package Model.Retrieve;

import Model.Index.Indexer;

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


    public Searcher(String postingFilesPath,boolean stem) {
        // TODO: 20/12/2018 Complete the constructor
        postingFileNames = new ArrayList<>();
        File file = new File(postingFilesPath);
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.stem = stem;
        this.postingFilesPath =postingFilesPath;
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
        HashSet<TermInfo> termInfos = this.getTheInformationAbouttheTemrs();
        for (TermInfo termInfo:termInfos)
        {
            printTermInfo(termInfo);
        }

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

    //private HashMap<String,List<String>> gettermsAndFiles(List<String> terms)

    /**
     * This function will return for a list of a given terms a map of file names and which terms they are containing
     * @param terms - The given terms
     * @return - A HashMap. The key is the name of the file, The value is a lis of all the terms that are in the document from the given terms list
     */
    public HashMap<String,List<String>> getTermsAndFiles(List<String> terms)
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

    private HashSet<TermInfo> getTheInformationAbouttheTemrs()
    {
        List<String> terms  = new ArrayList<>();// TODO: 12/22/2018 The query (Replace with the actual query)
        terms.add("$$$$$$$$");
        terms.add("$$$$$$$");
        terms.add("$$$$");
        terms.add("$$");
        terms.add("oligom");

        HashMap<String,List<String>> fileNamesAndTerms = this.getTermsAndFiles(terms);
        Set<String> keys = fileNamesAndTerms.keySet();
        Future<HashSet<TermInfo>> [] futures = new Future[keys.size()];

        int i = 0;
        for(String key:keys)
        {
            //public RetrieveTermInfo(String fileName, HashSet<String> terms,String postingFilePath)
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
                for(TermInfo key:temp)
                {
                    term = key.getTerm();
                    //If the term is already in the map
                    if(tempDic.containsKey(term))
                    {
                        TermInfo exist = tempDic.get(term);
                        Set<Integer> setOfKeys;
                        //If the key's dictionary is bigger than the existing's dictionary
                        if(key.getDocIdTfMap().size()>exist.getDocIdTfMap().size())
                        {
                            setOfKeys = exist.getDocIdTfMap().keySet();
                            for(int docId:setOfKeys)
                            {
                                key.addInfo(docId,exist.getDocIdTfMap().get(docId));
                            }
                            //Update the term info
                            tempDic.put(term,key);
                        }
                        else
                        {
                            setOfKeys = key.getDocIdTfMap().keySet();
                            for(int docId:setOfKeys)
                            {
                                exist.addInfo(docId,key.getDocIdTfMap().get(docId));
                            }
                            //We don't need to update the map
                        }
                    }
                    else
                    {
                        tempDic.put(term,key);
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


}