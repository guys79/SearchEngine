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
    private HashMap<String, Integer> mainDictionary;//The main dictionary. key - Term, value - df
    private String postFilePath;//The oath to the posting files. in that path we will create all of the posting files
    private ExecutorService executorService;//The Threadpool
    private boolean stem;//True - if we will stem the terms, False - otherwise
    private String stopWordsPath;//The path to the stop words file.
    private Mutex addNewMutex;//Tye mutex that is responsible on the addition of a new term to the main dictionary
    private int docNum;//The document number
    private DocIndexer docIndexer;//The document indexer
    private PostingOfCities postingOfCities;//The city indexer
    private List<String> fileNames;//The list of new file names. This list will contain the names of the final posting files


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
        this.docIndexer = new DocIndexer(postFilepath, this.readFile);
        this.postingOfCities = new PostingOfCities(this.postFilePath);

        //Creating the first temporary posting files
        initTempPosting();

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
     * @return
     */
    public PostingOfCities getPostingOfCities() {
        return postingOfCities;
    }

    /**
     * This function will return the size of the dictionary
     *
     * @return
     */
    public int getDicSize() {
        return this.mainDictionary.size();
    }

    /**
     * This function will return True if we are stemming the terms
     *
     * @return
     */
    public boolean getStem() {
        return this.stem;
    }

    /**
     * This function will return the path to the directory that the posting files are in (or will be in)
     *
     * @return
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
        int size = this.mainDictionary.size();
        //Indexing the main dictionary

        //Orginizing the indexed file into the wanted state
        sortAndSplit(size);
        //this.executorService.shutdown();
    }

    /**
     * This function will be summoned when all of the temporary files are filled with all of the data
     * This function will take that data, sort it, and will write it again in an organized way
     * @param dicSize - The size of the main dictionary
     */
    private void sortAndSplit(int dicSize) {
        //The number of docs that we want
        int numOfDocs = 1000;
        //The number of terms per document
        int numOfTermsPerDoc = dicSize / numOfDocs;
        Future<List<String>>[] listFuture = new Future[27];
        int lastIndexThatGotIndexed = -1;
        System.out.println("Now The split and merge!");
        //Foreach file, summon a thread that will read the file and assign it into new file in an orginized way
        for (int i = 0; i < 26; i++) {

            //Submit the thread to the thread pool
            listFuture[i] = this.executorService.submit(new SortAndSplitThread(this.postFilePath, numOfTermsPerDoc, "" + (char) ('a' + i), this.stem));


            try {
                if (i % 5 == 4)
                {
                    lastIndexThatGotIndexed=i;
                    //Stop indexing every 5 files so that the heap won;t overload
                    for(int j=i-4;j<=i;j++)
                    {
                        System.out.println(j +" out of 27");
                        this.fileNames.addAll(listFuture[j].get());
                    }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //Submitting the last file into the ThreadPool
        listFuture[listFuture.length - 1] = this.executorService.submit(new SortAndSplitThread(this.postFilePath, numOfTermsPerDoc, "other", this.stem));
        //Waiting for the last threads to finish
        for(int i=lastIndexThatGotIndexed+1;i<listFuture.length;i++) {
            System.out.println(i +" out of 27");
            try {
                this.fileNames.addAll(listFuture[i].get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        //Shutdown the ThreadPool
        this.executorService.shutdown();

    }

    /**
     * This function will parse and index a single file
     * @param file - The single file as an array of all the documents in that file
     * @return - The Future of the last thread that handles the last doc
     */
    public Future<Boolean> parserFile(List<String> file) {
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

        //For every doc
        for (int i = 0; i < parserThreads.length; i++) {
            doc = file.get(i);//get the doc
            city = this.getCityName(doc);//get the city name
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
        city = this.getCityName(doc);
        parserThreads[parserThreads.length - 1] = new ParserThread(docNum, this, this.findSub("TEXT", doc), new Parser(this.stopWordsPath, city, stem), true, stringBuilders, mutex, city, docIndexerThread, cityInfo);
        futures[futures.length - 1] = this.executorService.submit(parserThreads[parserThreads.length - 1]);
        Future<Boolean> futureToReturn = null;
        try {
            futureToReturn = futures[futures.length - 1].get().future;
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
     * This function updates the dictionary with a term appearance
     * @param term - The given term
     */
    public void addDictionaries(String term) {

        int temp;
        this.addNewMutex.lock();

        if (this.mainDictionary.containsKey(term)) {
            temp = this.mainDictionary.get(term) + 1;
            this.mainDictionary.put(term, temp);

            this.addNewMutex.unlock();
            return;
        }
        String lower = term.toLowerCase();
        String upper = term.toUpperCase();
        if (term.charAt(0) >= 'A' && term.charAt(0) <= 'Z') {
            if (this.mainDictionary.containsKey(lower)) {
                temp = this.mainDictionary.get(lower) + 1;
                this.mainDictionary.put(lower, temp);
                this.addNewMutex.unlock();
                return;
            }

        }
        if (term.charAt(0) >= 'a' && term.charAt(0) <= 'z') {
            if (this.mainDictionary.containsKey(upper)) {
                temp = this.mainDictionary.get(upper) + 1;
                this.mainDictionary.put(lower, temp);
                this.mainDictionary.remove(upper);

                this.addNewMutex.unlock();
                return;
            }
        }

        this.mainDictionary.put(term, 1);
        this.addNewMutex.unlock();

    }

    /**
     * This function will return the name of the city (if exists) in the string
     * @param str - The given string
     * @return - The name of the city (if exists)
     */
    public String getCityName(String str) {

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


}

