package Model;

import sun.awt.Mutex;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Indexer {
    private ReadFile readFile;
    private HashMap<String,Integer> mainDictionary;
    private TreeMap<String,String> currentMemory;
    private String postFilePath;
    private int numOfPostingFile;
    private ExecutorService executorService;
    private boolean stem;
    private String stopWordsPath;
    private Mutex addNewMutex;
    private int docNum;
    private DocIndexer docIndexer;
    private PostingOfCities postingOfCities;
    private List<String> fileNames;



    public Indexer(String corpusPath,String stopWordsPath,String postFilepath,boolean stem) {
        this.fileNames = new ArrayList<>();
        this.docNum = 0;
        this.stopWordsPath = stopWordsPath;
        this.readFile = new ReadFile(corpusPath);
        this.mainDictionary = new HashMap<>();
        this.currentMemory = new TreeMap<>();
        this.postFilePath = postFilepath;
        this.numOfPostingFile = 0;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.stem = stem;
        this.addNewMutex = new Mutex();
        this.docIndexer = new DocIndexer(postFilepath,this.readFile);
        this.postingOfCities = new PostingOfCities(this.postFilePath);
        initTempPosting();

    }
    public void shutDown()
    {
        this.executorService.shutdownNow();
    }
    public void printDicKeysandDf()
    {
        Set<String>strings =this.mainDictionary.keySet();

        for(String k:strings)
        {
            System.out.println("the key - "+k+" ### The df -"+this.mainDictionary.get(k));
        }
    }

    public DocIndexer getDocIndexer() {
        return docIndexer;
    }

    public PostingOfCities getPostingOfCities() {
        return postingOfCities;
    }

    public int getDicSize()
    {
        return this.mainDictionary.size();
    }
    public boolean getStem()
    {
        return this.stem;
    }
    public String getFatherPath()
    {
        return this.postFilePath;
    }
    private void initTempPosting()
    {
        File father = new File(this.postFilePath);
        String type = ".txt";
        for(char c ='a'; c<='z';c++)
        {
            try {
                new File(father,c+"_"+stem+type).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            new File(father,"other"+"_"+stem+type).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseDocumentsThread()
    {
        List<String> fileDoc;
       fileDoc = this.readFile.getFile();
       int count=1;
        ParserThreadReturnValue parserThreadreturnValue;
        Future<Boolean> futureOfLastFile=null;
       while(fileDoc!=null) {
           System.out.println(count);
           count+=1;
           //parserFile(fileDoc);
           futureOfLastFile= this.parserFile(fileDoc);
           fileDoc = this.readFile.getFile();
       }
       if(futureOfLastFile!=null)
           try {
               futureOfLastFile.get();
           } catch (InterruptedException e) {
               e.printStackTrace();
           } catch (ExecutionException e) {
               e.printStackTrace();
           }
        this.executorService.submit(this.postingOfCities);
       int size = this.mainDictionary.size();
        sortAndSplit(size);
    }
    private void sortAndSplit(int dicSize)
    {
        int numOfDocs = 1000;
        int numOfTermsPerDoc = dicSize/numOfDocs;
        Future<List<String>>[] listFuture = new Future[27];
        for(int i=0;i<26;i++) {
            listFuture[i] = this.executorService.submit(new SortAndSplitThread(this.postFilePath, numOfTermsPerDoc, "" + (char) ('a' + i), this.stem));


            try {
                this.fileNames.addAll(listFuture[i].get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        listFuture[listFuture.length-1] = this.executorService.submit(new SortAndSplitThread(this.postFilePath,numOfTermsPerDoc,"other",this.stem));
        this.executorService.shutdown();

    }
    public Future<Boolean> parserFile(List<String> file)
    {
        DocIndexerThread docIndexerThread =new DocIndexerThread(this.docIndexer);
        //For every file
        Future<ParserThreadReturnValue> [] futures= new Future[file.size()];
        ParserThread [] parserThreads = new ParserThread[file.size()];
        StringBuilder [] stringBuilders = new StringBuilder[27];
        CityInfo[] cityInfo = new CityInfo[file.size()];
        for(int i=0;i<stringBuilders.length;i++)
        {
            stringBuilders[i] = new StringBuilder();
        }
        Mutex mutex = new Mutex();
        String doc= "";
        String city ="";
        for(int i =0; i<parserThreads.length;i++)
        {
            doc = file.get(i);
            city = this.firstWordWithoutSpaces(doc);
            this.docNum++;
            parserThreads[i] = new ParserThread(docNum,this,this.findSub("TEXT",doc),new Parser(this.stopWordsPath,city,stem),false,stringBuilders,mutex,city,docIndexerThread,cityInfo);
            futures[i] = this.executorService.submit(parserThreads[i]);


        }

        for(int i =0; i<futures.length-1;i++)
        {
            //System.out.println("h");
            try {
                cityInfo[i] = futures[i].get().cityInfo;

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        this.docNum++;
        doc = file.get(file.size()-1);
        city = this.firstWordWithoutSpaces(doc);
        parserThreads[parserThreads.length-1] = new ParserThread(docNum,this,this.findSub("TEXT",doc),new Parser(this.stopWordsPath,city,stem),true,stringBuilders,mutex,city,docIndexerThread,cityInfo);
        futures[futures.length-1] = this.executorService.submit(parserThreads[parserThreads.length-1]);
        Future<Boolean> futureToReturn =null;
        try {
            futureToReturn = futures[futures.length-1].get().future;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        parserThreads[parserThreads.length-1].shutDown();

        return futureToReturn;
    }

    private String findSub(String tag, String str)
    {
        String start = "<" + tag + ">";
        String end = "</" + tag + ">";
        int index1=str.indexOf(start);
        int index2=str.indexOf(end);
        if(index1==-1 || index2 ==-1)
            return "";
        return str.substring(index1 + start.length(), index2);


    }
    private String addToExistingList(String list,int tf,int docid)
    {
        list = list +","+tf+","+docid;
        return list;
    }
    private void addWordsToDictionaryTempUnique(String term,int tf, int docid)
    {
        String newList="";
        if(this.currentMemory.containsKey(term))
        {
            newList = this.currentMemory.get(term);
        }
        this.currentMemory.put(term,this.addToExistingList(newList,tf,docid));
    }

    private void addRegularToTempDic(String term,int tf,int docId)
    {
        String newList= "";
        if(this.currentMemory.containsKey(term))
        {
            newList = this.currentMemory.get(term);
            if(newList==null)
                newList ="";
            this.currentMemory.put(term,this.addToExistingList(newList,tf,docId));
            return;
        }
        String lower = term.toLowerCase();
        String upper = term.toUpperCase();
        if(this.mainDictionary.containsKey(lower))
        {
            newList = this.currentMemory.get(lower);
            if(newList==null) {
                if(this.currentMemory.containsKey(upper))
                {
                    newList = this.currentMemory.get(upper);
                    this.currentMemory.remove(upper);
                }
                else
                {
                    newList = "";
                }
            }
            this.currentMemory.put(lower,this.addToExistingList(newList,tf,docId));
            return;
        }
        newList = this.currentMemory.get(upper);
        if(newList==null)
            newList ="";
        this.currentMemory.put(upper,this.addToExistingList(newList,tf,docId));
        return;






    }

    public void addDictionaries(String term,int tf,int docId,boolean regular)
    {

        int temp;
        this.addNewMutex.lock();

        //mutex_term.lock
        if(this.mainDictionary.containsKey(term))
        {
            temp=this.mainDictionary.get(term) +1;
            this.mainDictionary.put(term,temp);

            this.addNewMutex.unlock();
            return;
        }
        String lower = term.toLowerCase();
        String upper = term.toUpperCase();
        if(term.charAt(0)>='A' && term.charAt(0)<='Z')
        {
            if(this.mainDictionary.containsKey(lower))
            {
                temp=this.mainDictionary.get(lower) +1;
                this.mainDictionary.put(lower,temp);
                this.addNewMutex.unlock();
                return;
            }

        }
        if(term.charAt(0)>='a' && term.charAt(0)<='z')
        {
            if(this.mainDictionary.containsKey(upper))
            {
                temp = this.mainDictionary.get(upper)+1;
                this.mainDictionary.put(lower,temp);
                this.mainDictionary.remove(upper);

                this.addNewMutex.unlock();
                return;
            }
        }

        this.mainDictionary.put(term,1);
        this.addNewMutex.unlock();

    }
    public String firstWordWithoutSpaces(String str)
    {

        String start = "<F P=104>";
        String end = "</F>";
        int index1=str.indexOf(start);


        if(index1==-1)
            return "";
        str = str.substring(index1 + start.length());
        int index2=str.indexOf(end);
        //System.out.println(""+index1+" "+index2);
        //System.out.println(str);
        str = str.substring(0, index2);
       // System.out.println("1 " +str );
        String[] strings = str.split(" ");
        for(int i=0; i<strings.length;i++)
        {
            if(strings[i].length()>0 && !strings[i].equals(" "))
                return strings[i].toUpperCase();
        }
        return "";
    }
    public void test()
    {
        /*addRegularWordsToMainDictionary("GUY");
        this.printMainDic();
        addRegularWordsToMainDictionary("guy");
        this.printMainDic();
        addRegularWordsToMainDictionary("GUY");
        this.printMainDic();
        addRegularWordsToMainDictionary("guy");
        this.printMainDic();
        addRegularWordsToMainDictionary("ADI");
        this.printMainDic();
        addRegularWordsToMainDictionary("ADI");
        this.printMainDic();
        addRegularWordsToMainDictionary("adi");
        this.printMainDic();
        addRegularWordsToMainDictionary("guy2");
        this.printMainDic();
        addRegularWordsToMainDictionary("1-2-3");
        this.printMainDic();
        addRegularWordsToMainDictionary("1-2-3");
        this.printMainDic();*/

        this.addDictionaries("GUY",2,31,true);
        this.printMainDic();
        this.printTempDic();
        this.addDictionaries("GUY2",12,13,true);
        this.printMainDic();
        this.printTempDic();
        this.addDictionaries("guy",42,23,true);
        this.printMainDic();
        this.printTempDic();
        this.addDictionaries("1/2",62,93,false);
        this.printMainDic();
        this.printTempDic();
        this.addDictionaries("1/2",26,39,false);
        this.printMainDic();
        this.printTempDic();
        this.addDictionaries("1.23K",27,63,false);
        this.printMainDic();
        this.printTempDic();
        this.addDictionaries("GUY",27,63,true);
        this.printMainDic();
        this.printTempDic();
        this.addDictionaries("guy",227,633,true);
        this.printMainDic();
        this.printTempDic();


    }

    private void printMainDic()
    {
        Set<String>keys = this.mainDictionary.keySet();
        for (String key:keys) {
            System.out.print("term - "+key+" df - "+this.mainDictionary.get(key));
        }
        System.out.println();
    }
    private void printTempDic()
    {
        Set<String>keys = this.currentMemory.keySet();
        for (String key:keys) {

            System.out.print("term - "+key+" list - " + this.currentMemory.get(key)+" * ");
        }
        System.out.println();
    }
    private void writeCurrentDictionaryToFile(HashMap<String,String> dictionary)
    {
        File father = new File(this.postFilePath);
        this.numOfPostingFile++;
        File temp_posting_file = new File(father,""+stem+"_"+this.numOfPostingFile);
        try {
            temp_posting_file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Set<String> keys = dictionary.keySet();
        StringBuilder stringBuilder = new StringBuilder();
        for(String key:keys)
        {
            stringBuilder.append(key+'\n');
        }
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(temp_posting_file.getAbsolutePath(),true));
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


}

