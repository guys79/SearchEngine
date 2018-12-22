package Model;
import sun.awt.Mutex;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * This class is the main indexer.
 * This class is responsible for the way we gather all data from the corpus and for the way we index all the data in the end
 */
public class Indexer {
    private ReadFile readFile; //The readfile
    private HashMap<String, int []> mainDictionary;//The main dictionary. key - Term, value - df
    private String postFilePath;//The oath to the posting files. in that path we will create all of the posting files
    private ExecutorService executorService;//The Threadpool
    private boolean stem;//True - if we will stem the terms, False - otherwise
    private String stopWordsPath;//The path to the stop words file.
    private Mutex addNewMutex;//Tye mutex that is responsible on the addition of a new term to the main dictionary
    private int docNum;//The document number
    private DocIndexer docIndexer;//The document indexer
    private PostingOfCities postingOfCities;//The city indexer
    private List<String> fileNames;//The list of new file names. This list will contain the names of the final posting files
    private LanguageIndexer languageIndexer;//The languageIndexer .This class will index the languages
    private AddDictionaryToFile addDictionaryToFile;//This class will add the dictionay into a file
    private List<String> namesOfNonTermPostingFiles;


    /**
     * This is the constructor of the class
     *
     * @param corpusPath    - The path to the corpus. that is where all the data is.
     * @param stopWordsPath - The path to the stop words file.
     * @param postFilepath  - The path to the location of the posting files to be
     * @param stem          - True - if we will stem the terms, False - otherwise
     */
    public Indexer(String corpusPath, String stopWordsPath, String postFilepath, boolean stem) {
        //Initializing all the variables
        this.fileNames = new ArrayList<>();
        this.docNum = 0;
        this.stopWordsPath = stopWordsPath;
        this.readFile = new ReadFile(corpusPath);
        this.mainDictionary = new HashMap<>();
        this.postFilePath = postFilepath;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.stem = stem;
        this.addNewMutex = new Mutex();
        this.docIndexer = new DocIndexer(postFilepath, this.readFile,stem);
        this.postingOfCities = new PostingOfCities(this.postFilePath,stem);
        this.languageIndexer = new LanguageIndexer(postFilePath,stem);
        this.namesOfNonTermPostingFiles = new ArrayList<>();

        //Creating the first temporary posting files
        initTempPosting();

        //Initialize the names of the non term posting file list
        this.namesOfNonTermPostingFiles.add("dictionary"+"_"+stem);
        this.namesOfNonTermPostingFiles.add("citys"+"_"+stem);
        this.namesOfNonTermPostingFiles.add("languages"+"_"+stem);
        this.namesOfNonTermPostingFiles.add("documents"+"_"+stem);

    }


    /**
     * This function will delete the posting files and will reset the main dictionary
     */
    public void reset()
    {

        List<String> postingFileNames = this.getListOfFileNames();
        List<String> nonPostingFileNames = this.getNamesOfNonTermPostingFiles();

        for(int i=0;i<postingFileNames.size();i++)
        {
            new File(postFilePath+"\\"+postingFileNames.get(i)+".txt").delete();
        }
        for(int i=0;i<nonPostingFileNames.size();i++)
        {
            new File(postFilePath+"\\"+nonPostingFileNames.get(i)+".txt").delete();
        }
        this.mainDictionary = new HashMap<>();
    }

    /**
     * This function will return the name of the language file
     * @return - The name of the language file
     */
    public String getLanguageFilePath()
    {
        return this.postFilePath+"\\"+"languages"+"&"+stem+".txt";
    }

    /**
     * This function will set the main dictionary
     * @param mainDictionary - The given main dictionary
     */
    public void setMainDictionary(HashMap<String,int []> mainDictionary)
    {
        this.mainDictionary = mainDictionary;
    }

    /**
     * This function will return the main Dictionary
     * @return - The main dictionary
     */
    public HashMap<String, int[]> getMainDictionary() {
        return mainDictionary;
    }

    /**
     * This function will shut the indexer down immediately
     * If this function is used, than the indexer will terminate all of the threads that are currently running
     */
    public void shutDownNow() {
        this.executorService.shutdownNow();
    }

    /**
     * This function will shut the indexer down.
     * If this function is used, than the indexer will not terminate all of the threads that are currently running, but he will not create more threads
     */
    public void shutDown() {
        if (!this.executorService.isShutdown())
            this.executorService.shutdown();
    }


    /**
     * This function will return the address of the city indexer (shallow copy)
     *
     * @return - The city indexer
     */
    public PostingOfCities getPostingOfCities() {
        return postingOfCities;
    }

    /**
     * This function will return the size of the dictionary
     *
     * @return - The size of the dictionary
     */
    public int getDicSize() {
        return this.mainDictionary.size();
    }

    /**
     * This function will return if we are stemming the terms
     *
     * @return - True if we are stemming the terms
     */
    public boolean getStem() {
        return this.stem;
    }

    /**
     * This function will return the path to the directory that the posting files are in (or will be in)
     *
     * @return - The path of the dictionary where the posting files are in (or will be in)
     */
    public String getFatherPath() {
        return this.postFilePath;
    }

    /**
     * This function creates all the temporary posting files
     */
    private void initTempPosting() {
        File father = new File(this.postFilePath);
        String type = ".txt";
        for (char c = 'a'; c <= 'z'; c++) {
            try {
                new File(father, c + "_" + stem + type).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            new File(father, "other" + "_" + stem + type).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will return the names of all of the non terms posting files.
     * Which means all of the files that are holding data about an entity other than term (city,document and so on)
     * @return A list of all the names of the non posting file
     */
    public List<String> getNamesOfNonTermPostingFiles() {
        return namesOfNonTermPostingFiles;
    }

    /**
     * This function is the function that starts indexing the data
     * This function goes through all of the files in the corpus, one file at a time.
     * For each file, all of his docs will be parsed and indexed
     */
    public void parseDocumentsThread() {

        List<String> fileDoc;//The list of document texts in a certain file
        fileDoc = this.readFile.getFile();

        int count = 1;
        Future<Boolean> futureOfLastFile = null;

        //This loop goes through every file in the corpus and will parse it and index it
        while (fileDoc != null) {
            System.out.println(count);
            count += 1;
            futureOfLastFile = this.parserFile(fileDoc);
            fileDoc = this.readFile.getFile();

        }

        //Indexing the last file
        if (futureOfLastFile != null)
            try {
                futureOfLastFile.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        //Indexing the city
        this.executorService.submit(this.postingOfCities);
        //Indexing the languages
        this.executorService.submit(this.languageIndexer);
        //Indexing the main dictionary
        this.addDictionaryToFile = new AddDictionaryToFile(this.postFilePath,this.mainDictionary,stem);
        this.executorService.submit(this.addDictionaryToFile);
        //Organizing the indexed file into the wanted state
        sortAndSplit();
    }

    /**
     * This function will be summoned when all of the temporary files are filled with all of the data
     * This function will take that data, sort it, and will write it again in an organized way
     */
    private void sortAndSplit() {
        //The number of docs that we want
        //int numOfDocs = 1000;
        //The number of terms per document
        int numOfTermsPerDoc = 60000;
        Future<List<String>>[] listFuture = new Future[27];
        int lastIndexThatGotIndexed = -1;
        System.out.println("Now The split and merge!");
        //Foreach file, summon a thread that will read the file and assign it into new file in an orginized way
        for (int i = 0; i < 26; i++) {

            //Submit the thread to the thread pool
            listFuture[i] = this.executorService.submit(new SortAndSplitThread(this.postFilePath, numOfTermsPerDoc, "" + (char) ('a' + i), this.stem));


            try {
                this.fileNames.addAll(listFuture[i].get());
                System.out.println("split and sort "+(i+1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        //Submitting the last file into the ThreadPool
        listFuture[listFuture.length - 1] = this.executorService.submit(new SortAndSplitThread(this.postFilePath, numOfTermsPerDoc, "other", this.stem));
        //Waiting for the last threads to finish


            try {
                this.fileNames.addAll(listFuture[listFuture.length - 1].get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        System.out.println("split and sort "+27);
        //Shutdown the ThreadPool
        this.executorService.shutdown();

        //delete "other"
        File file =new File(this.postFilePath+"\\"+"other_"+stem+".txt");
        file.delete();

    }

    /**
     * This function will load The dictionary from a file and will update this instance's main dictionary
     */
    public void loadDictionary()
    {
        ExecutorService executorService =Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);
        Future<HashMap<String,int []>> future=executorService.submit(new LoadDictionary(this.postFilePath+"\\"+"dictionary"+"&"+this.stem+".txt"));
        try {
            this.setMainDictionary(future.get());
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        this.setMainDictionary(null);
    }

    /**
     * This function will return the list of the names of the final posting file
     * @return - The list of names of the final posting file
     */
    public List<String> getListOfFileNames ()
    {
        return this.fileNames;
    }
    /**
     * This function will parse and index a single file
     * @param file - The single file as an array of all the documents in that file
     * @return - The Future of the last thread that handles the last doc
     */
    private Future<Boolean> parserFile(List<String> file) {
        //Creating the DocIndexer thread
        DocIndexerThread docIndexerThread = new DocIndexerThread(this.docIndexer);

        //For every doc
        Future<ParserThreadReturnValue>[] futures = new Future[file.size()];
        ParserThread[] parserThreads = new ParserThread[file.size()];
        CityInfo[] cityInfo = new CityInfo[file.size()];

        //For every posting file
        StringBuilder[] stringBuilders = new StringBuilder[27];
        for (int i = 0; i < stringBuilders.length; i++) {
            stringBuilders[i] = new StringBuilder();
        }

        //Creating the mutual mutex
        Mutex mutex = new Mutex();

        //The text of the document
        String doc = "";

        //The city name of the doc (if exist)
        String city = "";

        //The language in the doc (if exist)
        String language="";

        //For every doc
        for (int i = 0; i < parserThreads.length-1; i++) {
            doc = file.get(i);//get the doc
            city = this.getCityName(doc);//get the city name
            language = this.getLanguage(doc);//get the language
            this.languageIndexer.addLanguage(language);//Add the language into the language indexer
            this.docNum++;//Get the doc number
            //Create the thread that will parse the document as well as index it and his information
            parserThreads[i] = new ParserThread(docNum, this, this.findSub("TEXT", doc), new Parser(this.stopWordsPath, city, stem), false, stringBuilders, mutex, city, docIndexerThread, cityInfo);
            futures[i] = this.executorService.submit(parserThreads[i]);


        }

        //Adding the information about the cities
        for (int i = 0; i < futures.length - 1; i++) {
            try {
                cityInfo[i] = futures[i].get().cityInfo;

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        this.docNum++;
        doc = file.get(file.size() - 1);
        city = this.getCityName(doc);//get the city name
        language = getLanguage(doc);//get the language
        this.languageIndexer.addLanguage(language);//Add the language into the languageIndexer
        parserThreads[parserThreads.length - 1] = new ParserThread(docNum, this, this.findSub("TEXT", doc), new Parser(this.stopWordsPath, city, stem), true, stringBuilders, mutex, city, docIndexerThread, cityInfo);
        futures[futures.length - 1] = this.executorService.submit(parserThreads[parserThreads.length - 1]);
        Future<Boolean> futureToReturn = null;
        ParserThreadReturnValue parserThreadReturnValue= null;
        try {
            parserThreadReturnValue= futures[futures.length - 1].get();
            futureToReturn = parserThreadReturnValue.future;
            cityInfo[cityInfo.length-1] = parserThreadReturnValue.cityInfo;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        parserThreads[parserThreads.length - 1].shutDown();

        return futureToReturn;
    }

    /**
     * This function extracts the string between the given tag's type
     * @param tag - The type of the tag, TEXT, F p=10 and so on
     * @param str - The String from which we need the substring from
     * @return - The wanted substring between the two tags
     */
    private String findSub(String tag, String str) {
        String start = "<" + tag + ">";
        String end = "</" + tag + ">";
        int index1 = str.indexOf(start);
        int index2 = str.indexOf(end);
        if (index1 == -1 || index2 == -1)
            return "";
        return str.substring(index1 + start.length(), index2);


    }

    /**
     * This function will return the number of indexed documents
     * @return - The number of indexed document
     */
    public int getNumberOfDocuments()
    {
        return this.docNum;
    }

    /**
     * This function updates the dictionary with a term appearance
     * @param term - The given term
     * @param tf - The term frequency
     */
    public void addDictionaries(String term,int tf) {

        int[] temp;
        this.addNewMutex.lock();

        if (this.mainDictionary.containsKey(term)) {
            temp = this.mainDictionary.get(term);
            temp[0]= temp[0]+1;
            temp[1]= temp[1]+tf;
            this.mainDictionary.put(term, temp);

            this.addNewMutex.unlock();
            return;
        }
        String lower = term.toLowerCase();
        String upper = term.toUpperCase();
        if (term.charAt(0) >= 'A' && term.charAt(0) <= 'Z') {
            if (this.mainDictionary.containsKey(lower)) {
                temp = this.mainDictionary.get(lower);
                temp[0]= temp[0]+1;
                temp[1]= temp[1]+tf;
                this.mainDictionary.put(lower, temp);
                this.addNewMutex.unlock();
                return;
            }

        }
        if (term.charAt(0) >= 'a' && term.charAt(0) <= 'z') {
            if (this.mainDictionary.containsKey(upper)) {
                temp = this.mainDictionary.get(upper);
                temp[0]= temp[0]+1;
                temp[1]= temp[1]+tf;
                this.mainDictionary.put(lower, temp);
                this.mainDictionary.remove(upper);

                this.addNewMutex.unlock();
                return;
            }
        }
        temp = new int[2];
        temp[0]=1;
        temp[1]=tf;
        this.mainDictionary.put(term, temp);
        this.addNewMutex.unlock();

    }

    /**
     * This function will return the name of the city (if exists) in the string
     * @param str - The given string
     * @return - The name of the city (if exists)
     */
    private String getCityName(String str) {

        String start = "<F P=104>";
        String end = "</F>";
        int index1 = str.indexOf(start);


        if (index1 == -1)
            return "";
        str = str.substring(index1 + start.length());
        int index2 = str.indexOf(end);
        str = str.substring(0, index2);
        String[] strings = str.split(" ");
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].length() > 0 && !strings[i].equals(" "))
                return strings[i].toUpperCase();
        }
        return "";
    }
    /**
     * This function will return the name of a language (if exists) in the string
     * @param str - The given string
     * @return - The name of the city (if exists)
     */
    private String getLanguage(String str) {

        String start = "<F P=105>";
        String end = "</F>";
        int index1 = str.indexOf(start);


        if (index1 == -1)
            return "";
        str = str.substring(index1 + start.length());
        int index2 = str.indexOf(end);
        str = str.substring(0, index2);
        String[] strings = str.split(" ");
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].length() > 0 && !strings[i].equals(" "))
                return strings[i].toUpperCase();
        }
        return "";
    }


}

