package Model.Retrieve;

import Model.Index.CityInfo;
import Model.Index.DocInfo;
import Model.Index.Indexer;
import Model.Index.LoadDictionary;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import javax.swing.text.html.ListView;
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

    private List<String> postingFileNames;//The names of the posting files
    private boolean stem;//True if we want to retrieve the information of the stemmed posting files, False - if we want to retrieve the information from the non stemmed files
    private ExecutorService executorService;//The threadpool
    private String postingFilesPath;//The path to the posting file
    private String [] relaventCities;//The array of relevant cities
    private HashMap<String,int[]> mainMap;//The main map. The key is the term, and the value is an array in the size of 2
                                          //The first cell in the array is the df, and the second is cf
    private GetCity cityPostingInformation;//The class that we will use to get data on the cities
    private GetDoc documentPostingInformation;//The class that we will use to get data on the cities
    private EntityRetrieval entityRetrieval;//The entity retriever
    private boolean semantic;//True if we want to use semantics
    private int numOfDoc;//The number of documents
    private double averageDocLength;//The average document length

    /**
     * A constructor of the class
     * @param postingFilesPath - The posting file path
     * @param isStemmed - True if we want to retrieve data from the stemmed posting files
     */
    public Searcher(String postingFilesPath,boolean isStemmed){
        this.postingFilesPath =postingFilesPath;
        this.stem = isStemmed;
        this.semantic = false;
    }

    /**
     * This function will return the array of relevant cities
     * @return - The list of relevant cities
     */
    public String[] getRelaventCities() {
        return relaventCities;
    }

    /**
     * A constructor of the class
     * @param postingFilesPath - The posting file path
     * @param stem - True if we want to retrieve data from the stemmed posting files
     * @param relaventCities - The cities that are that has to be associated
     * @param semantic - true if we want to use semantics
     */
    public Searcher(String postingFilesPath, boolean stem, String [] relaventCities, boolean semantic) {

        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.stem = stem;
        this.postingFilesPath =postingFilesPath;
        this.semantic = semantic;
        this.numOfDoc = -1;
        this.averageDocLength = -1;
        Future<HashMap<String,int []>> futureMap;
        Future<Boolean> futureCity;
        Future<Boolean> futureDoc;
        Future<Boolean> futureEntity;
        //Getting the info from the three files (dictionary , doc, city)
        //Dictionary
        futureMap=executorService.submit(new LoadDictionary(this.postingFilesPath+"\\"+"dictionary"+"&"+this.stem+".txt"));
        //City
        this.cityPostingInformation = new GetCity(this.postingFilesPath+"\\"+"citys"+"&"+stem+".txt");
        futureCity = executorService.submit(this.cityPostingInformation);
        //Docs
        this.documentPostingInformation = new GetDoc(this.postingFilesPath+"\\"+"allDocs"+"&"+stem+".txt");
        futureDoc = executorService.submit(this.documentPostingInformation);
        //Entities
        this.entityRetrieval = new EntityRetrieval(this.postingFilesPath+"\\"+"entities"+"&"+stem+".txt");
        futureEntity = executorService.submit(this.entityRetrieval);

        
        
        //Initializing the data structures
        postingFileNames = new ArrayList<>();
        this.relaventCities = relaventCities;
        File file = new File(postingFilesPath);


        //Getting the posting files names
        if(file.isDirectory()) {
            String stemS = "_"+stem;
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


        try {
            System.out.println("getting city");
            futureCity.get();
            System.out.println("getting Docs");
            futureDoc.get();
            System.out.println("getting Entities");
            futureEntity.get();
            System.out.println("getting dictionary");
            this.mainMap =futureMap.get();
            shutDown();
        } catch (InterruptedException e) {
            shutDown();
            e.printStackTrace();
        } catch (ExecutionException e) {
            shutDown();
            e.printStackTrace();
        }


    }

    /**
     * This function will shut down the searcher retrieval threadpool
     */
    public void shutDown()
    {
        this.executorService.shutdown();
    }

    /**
     * This function will return the name of the file(s) that contains the term
     *
     * @param term -The given term
     * @return - The list of files that contains the term
     */
    public List<String> getFileName(String term) {
        ArrayList<String> fileNames = new ArrayList<>();
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

        int comp = compareIrgular(name,term);
        //Problem
        if (middle == start && comp > 0) {
            return;
        }

        if (comp==0) {
            String fullFileName = fullName;
            String fileName = name;
            int index = middle;

            while (comp == 0) {
                fileNames.add(fullFileName);
                index--;
                if(index<0)
                    break;
                fullFileName = this.postingFileNames.get(index);
                fileName = fullFileName.substring(0,fullFileName.indexOf("_"));
                comp = compareIrgular(fileName,term);
            }
            if(index>=0) {
                fullFileName = this.postingFileNames.get(index);
                fileNames.add(fullFileName);
            }

            index = middle+1;
            while (index<this.postingFileNames.size()) {
                fullFileName = this.postingFileNames.get(index);
                fileName = fullFileName.substring(0,fullFileName.indexOf("_"));
                comp = compareIrgular(fileName,term);
                if(comp!=0)
                    break;
                fileNames.add(fullFileName);
                index++;
            }
            return;
        }

        String nextName = this.postingFileNames.get(middle + 1);
        nextName = nextName.substring(0,nextName.indexOf("_"));
        if (comp < 0 && (middle == end || compareIrgular(nextName,term)>0)) {

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
     * This function will compare a term to a file name
     * @param fileName - The file name
     * @param term - the term
     * @return - positive number - if the file name is "bigger" than the term
     *         - negative number - if the file name is "smaller" than the term
     *         - zero - if the file name is equal
     */
    public int compareIrgular(String fileName,String term)
    {

        if(fileName.equals("1^^_"))
            return 1;
        char note =(""+term.charAt(0)).toLowerCase().charAt(0);
        if(note>='a' && note<='z')
            return fileName.compareToIgnoreCase(term);
        if(fileName.charAt(0)!='1')
            return 1;
        String [] split = fileName.split("\\^");
        String fileNameRestore = "";
        for(int i=1;i<split.length;i++)
        {
            fileNameRestore+= (char)(Integer.parseInt(split[i]));

        }
        fileNameRestore+="_";
        term+="_";
        return fileNameRestore.compareToIgnoreCase(term);
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
        System.out.println(fileNamesAndTerms);

        Set<String> keys = fileNamesAndTerms.keySet();
        Future<HashSet<TermInfo>> [] futures = new Future[keys.size()];
        int i = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        //For each file, get the data about the terms
        for(String key:keys)
        {
            //public RetrieveTermInfo(String fileName, HashSet<String> terms,String postingFilePath)
            futures[i] = executorService.submit(new RetrieveTermInfo(key,new HashSet(fileNamesAndTerms.get(key)),this.postingFilesPath));
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
            executorService.shutdown();
        }
        return new HashSet<>(tempDic.values());

    }

    /**
     * This function will return the relevant information about a query
     * @param query - The given query
     * @return - The information about the query
     */
    private HashSet<TermInfo> getRelevantData(Query query)
    {
        //Getting the terms of the query as a list
        List<String> queryTerms = query.getQueryAsList();
        System.out.println(queryTerms);
        //Check the terms data
        HashSet<TermInfo> termInfos = this.getTheInformationAboutTheTerms(queryTerms);
        //If there are cities as filter
        if(this.relaventCities.length!=0) {

            List<String> citiesToCheck = new ArrayList<>();
            //Go through all of the cities, if the city is a term in the query, don't add it (saves additional checks)
            //Stemming the cities if needed
            if(stem) {
                String city;
                SnowballStemmer stemmer = new porterStemmer();
                for (int i = 0; i < this.relaventCities.length; i++) {

                    city = this.relaventCities[i].toLowerCase();
                    stemmer.setCurrent(city);
                    if(stemmer.stem())
                        city = stemmer.getCurrent();
                    citiesToCheck.add(city);

                }

            }
            else
            {
                citiesToCheck = Arrays.asList(this.relaventCities);
            }



            Iterator firstMap = termInfos.iterator();
            //Check the cities as if they were terms
            HashSet<TermInfo> citiesInfo = this.getTheInformationAboutTheTerms(citiesToCheck);


            //Adding the docs that are has the city in the title
            HashSet<Integer> docsInTitle = new HashSet<>();
            for(int i=0;i<this.relaventCities.length;i++)
            {
                docsInTitle.addAll(this.cityPostingInformation.getDetailsOnCitys(this.relaventCities[i]));
            }
            this.sizesOfRelevantDocsCity(citiesInfo,docsInTitle);
            //The filter
            while (firstMap.hasNext()) {
                TermInfo current = (TermInfo) firstMap.next();
                HashMap<Integer, Integer> tempMap = current.getDocIdTfMap();
                Iterator secondMap = tempMap.entrySet().iterator();
                while (secondMap.hasNext()) {
                    Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) secondMap.next();
                    int key = entry.getKey();
                    if (!checkIfInTermInfo(key, citiesInfo,docsInTitle))
                        secondMap.remove();
                }
            }
        }
        else
        {
            sizesOfRelevantDocsNoCity();
        }
        return termInfos;
    }

    /**
     * This function will check if the nformation about the term is from a valid document (according to the city filter)
     * @param docNum - The number of doc
     * @param termInfos - The docs that the city appears in them
     * @param docsWithTitle - The docs that the cities are int he title
     * @return - True if the document is a valid information source
     */
    private boolean checkIfInTermInfo(int docNum,HashSet<TermInfo> termInfos,HashSet<Integer> docsWithTitle)
    {
        if(docsWithTitle.contains(docNum))
            return true;
        for(TermInfo termInfo : termInfos)
        {
            if(termInfo.getDocIdTfMap().containsKey(docNum))
                return true;
        }
        return false;
    }

    /**
     * This function will update the number of scanned docs and their average length if there are cities in the city filter
     * @param termInfos - The docs that the city appears in them
     * @param docsWithTitle - The docs that the cities are int he title
     */
    private void sizesOfRelevantDocsCity(HashSet<TermInfo> termInfos,HashSet<Integer> docsWithTitle)
    {

        HashSet<Integer> everyThing= new HashSet<>(docsWithTitle);
        double sum =0;
        for(TermInfo termInfo:termInfos)
        {
            everyThing.addAll(termInfo.docIdTfMap.keySet());

        }
        for(Integer docNum: everyThing)
        {
            sum+=this.documentPostingInformation.getDetailsOnDocs(docNum).getLength();
        }
        this.numOfDoc = everyThing.size();
        this.averageDocLength = sum/this.numOfDoc;



    }

    /**
     * This function will update the number of scanned docs and their average length if there are cities in the city filter
     */
    private void sizesOfRelevantDocsNoCity()
    {

       this.numOfDoc = this.documentPostingInformation.getNumOfDocs();
       this.averageDocLength = this.documentPostingInformation.getAverageLength();



    }

    /**
     * This function will return the most relevant docs to the given
     * @param queryText -The query as a text
     * @return - The array of relevant doc id's
     */
    public int [] getMostRelevantDocNum(String queryText)
    {

        Query query;
        //The relevant data
        if(!this.semantic)
            query = new Query(queryText,this.postingFilesPath,this.stem);
        else
            query =new SemanticQuery(queryText,postingFilesPath,stem);

        HashSet<TermInfo> queryData =this.getRelevantData(query);


        //Updating the df and tf in the query

        int [] info;
        for(TermInfo termInfo:queryData)
        {
            info = this.mainMap.get(termInfo.getTerm().toLowerCase());
            if(info==null)
                info = this.mainMap.get(termInfo.getTerm().toUpperCase());
            termInfo.setDf(info[0]);

            termInfo.setTfInQuery(query.getNumOfOccurrences(termInfo.getTerm()));
        }

        if(this.semantic)
        {
            SemanticQuery semanticQuery = (SemanticQuery) query;
            for(TermInfo termInfo:queryData)
            {
                termInfo.setWeight(semanticQuery.getWeight(termInfo.getTerm()));
            }

        }


        //Getting the relevant docNums
        HashSet<Integer>docNumHash = new HashSet<>();
        for(TermInfo termInfo:queryData)
        {
            docNumHash.addAll(termInfo.docIdTfMap.keySet());
        }


        //Getting the docs info

        Ranker ranker = new Ranker(queryData,this.averageDocLength,numOfDoc);
        double minValue = Double.MIN_VALUE;
        double score;
        final int NUM_OF_DOCS_TO_RETURN = 50;
        double [] scores = new double[NUM_OF_DOCS_TO_RETURN];
        int [] docsToReturn = new int[NUM_OF_DOCS_TO_RETURN];

        for(int i=0;i<NUM_OF_DOCS_TO_RETURN;i++)
        {
            scores[i] = Double.MIN_VALUE;
            docsToReturn[i] = -1;
        }




        for(int docNum:docNumHash)
        {
            score = ranker.Rank(this.documentPostingInformation.getDetailsOnDocs(docNum));
            if(minValue<score) {
                minValue = update(scores, docsToReturn, docNum, score, minValue);
            }

        }



        sortByScore(scores,docsToReturn);
       // pr(docsToReturn);
        //pr2(scores);
        //String [] docNames = new String[NUM_OF_DOCS_TO_RETURN];
        //pr(docsToReturn);
        //pr2(scores);
        //boolean flag = true;
        return docsToReturn;
        //return docNames;
    }

    /**
     * This function will update the array of relevant docs with the given doc if needed
     * @param scores - The array of scores
     * @param id - The array of doc id's
     * @param doc - The doc id
     * @param score - The score of the doc
     * @param minValue - The minimum value in the array of scores
     * @return - The new minimum value
     */
    private double update(double [] scores, int [] id,int doc,double score,double minValue)
    {
        int index = -1;

        for(int i=0;i<scores.length;i++)
        {
            if(scores[i] == minValue)
            {
                index = i;
                break;
            }
        }

        scores[index] = score;
        id[index] = doc;

        double min = Double.MAX_VALUE;

        for(int i=0;i<scores.length;i++)
        {
            if(scores[i]<min)
                min =scores[i];
        }
        return min;

    }

    /**
     * This function will sort the id's array in accordance to the given scores array
     * @param scores - The list of scores
     * @param id - The list of id's
     */
    private void sortByScore(double [] scores, int [] id) {
        double[] scoresCopy = new double[scores.length];
        int[] idCopy = new int[id.length];
        for (int i = 0; i < scores.length; i++) {
            scoresCopy[i] = scores[i];
            idCopy[i] = id[i];
        }

        double maxVal=Double.MIN_VALUE;
        int max=-1;
        int indexMax = 0;

        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < scores.length; j++) {
                if(maxVal<scoresCopy[j])
                {
                    maxVal = scoresCopy[j];
                    max = idCopy[j];
                    indexMax = j;
                }
            }
            scores[i] = maxVal;
            id[i] = max;

            scoresCopy[indexMax] = Double.MIN_VALUE;
            maxVal=Double.MIN_VALUE;
            max =-1;
        }

    }

    /**
     * This function will return the names of the cities that we have indexed
     * @return - A list of the cities that we have indexed
     */
    public List<String> getNamesOfCitys(){
        GetCity getCity = new GetCity(this.postingFilesPath+"\\"+"citys"+"&"+stem+".txt");
        List<String> myTrue= getCity.getNamesOfCitys();
        return  myTrue;
    }

    /**
     * This function will return whether we search in the stemmed posting files or not
     * @return - True if we stemmed
     */
    public boolean getStem()
    {
        return this.stem;
    }

    /**
     * This function will return whether we used semantics in order to find the most relevant document
     * @return - True if we used semantics
     */
    public boolean getSemantic()
    {
        return this.semantic;
    }

    /**
     * This fnction will return the posting file path used
     * @return - The posting file path
     */
    public String getPostingFile()
    {
        return this.postingFilesPath;
    }

    /**
     * This function will return the entities of the document
     * @param docId - The doc id
     * @return - A list of the entities
     */
    public List<String> getEntities(int docId)
    {
        return this.entityRetrieval.getEntities(docId);
    }

    /**
     * This function will return the name of the doc with te given id
     * @param docId - The given doc id
     * @return - The doc name
     */
    public String getDocName(int docId)
    {
        return this.documentPostingInformation.getDetailsOnDocs(docId).getDocName();
    }

}

