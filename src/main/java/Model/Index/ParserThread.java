package Model.Index;

import Model.Index.CityInfo;
import sun.awt.Mutex;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class will parse a single doc as a thread
 * The class will also update the city and doc indexers
 */
public class ParserThread implements Callable<ParserThreadReturnValue>
{
    private String text;//The text that need to be parsed
    private Parser parser;//The parser
    private Indexer indexer;//the indexer
    private int docId;//The doc number
    private ExecutorService executorService;//The threadpool
    private IndexerThread[] indexerThreads;//The Array of indexerThreads
    private boolean uploadToFile;//do we upload the terms into a dictionary now
    private StringBuilder [] stringBuilders;//The string builders for the different posting files
    private Mutex mutex;//A mutex
    private DocIndexerThread docIndexerThread;//The thread that parses a document
    private String city;//the name of the city
    private CityInfo[] cityInfo;//The list of informations on the cities

    /**
     * The constructor of the class
     * @param docId - the doc number
     * @param indexer - The indexer
     * @param text - The text that we are going to parse
     * @param parser - The parser
     * @param uploadToFile - True if we want to upload all the date we currently have to the disk
     * @param stringBuilders - The string builders for all the files
     * @param mutex - A mutual mutex
     * @param city - The city name, if exists
     * @param docIndexerThread - The doc indexer as a thred
     * @param cityInfo - The infromation about the cities
     */
    public ParserThread(int docId, Indexer indexer, String text, Parser parser, boolean uploadToFile, StringBuilder [] stringBuilders, Mutex mutex,String city,DocIndexerThread docIndexerThread,CityInfo[] cityInfo) {
        this.docIndexerThread = docIndexerThread;
        this.text = text;
        this.cityInfo = cityInfo;
        this.mutex = mutex;
        this.stringBuilders = stringBuilders;
        this.city = city;
        this.uploadToFile = uploadToFile;
        this.indexerThreads = new IndexerThread[27];
        //Init the indexer threads
        for(int i=0;i<this.indexerThreads.length-1;i++)
        {
            this.indexerThreads[i] = new IndexerThread(indexer.getStem(),indexer.getFatherPath(),""+(char)('a'+i),this.stringBuilders[i],this.mutex);
        }
        this.indexerThreads[this.indexerThreads.length-1] = new IndexerThread(indexer.getStem(),indexer.getFatherPath(),"other",stringBuilders[stringBuilders.length-1],this.mutex);
        this.parser = parser;
        this.indexer = indexer;
        this.docId = docId;
        //If needed, initialize the threadpool
        if(uploadToFile)
            this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);

    }

    @Override
    public ParserThreadReturnValue call() throws Exception {
        try {

            //Parse the doc
            DocumentReturnValue documentReturnValue = this.parser.motherOfAllFunctions(this.text);

            Set<String> keys = documentReturnValue.getDictionaryOfUniqueTerms().keySet();
            int tf;
            int sizeOfDoc = 0;
            char firstNote;
            //Assign the words to their correct file
            for (String key : keys) {
                if (key.length() > 0) {
                    sizeOfDoc++;
                    firstNote = ("" + key.charAt(0)).toLowerCase().charAt(0);
                    tf = documentReturnValue.getDictionaryOfUniqueTerms().get(key);
                    this.indexer.addDictionaries(key,tf);
                    if (firstNote >= 'a' && firstNote <= 'z')
                        this.indexerThreads[firstNote - 'a'].addtoString(docId, tf, key);
                    else
                        this.indexerThreads[this.indexerThreads.length - 1].addtoString(docId, tf, key);
                }

            }
            documentReturnValue.setDictionaryOfUniqueTerms(null);

            keys = documentReturnValue.getDictionaryOfWords().keySet();
            for (String key : keys) {
                if (key.length() > 0) {
                    sizeOfDoc++;
                    firstNote = ("" + key.charAt(0)).toLowerCase().charAt(0);
                    tf = documentReturnValue.getDictionaryOfWords().get(key);
                    this.indexer.addDictionaries(key,tf);
                    if (firstNote >= 'a' && firstNote <= 'z')
                        this.indexerThreads[firstNote - 'a'].addtoString(docId, tf, key);
                    else
                        this.indexerThreads[this.indexerThreads.length - 1].addtoString(docId, tf, key);
                }
            }
            documentReturnValue.setDictionaryOfWords(null);

            //Update the doc data
            this.docIndexerThread.addDocData(docId, sizeOfDoc, documentReturnValue.getMaxFrequency(),documentReturnValue.getDocLength());

            //Update the return value with the city data
            ParserThreadReturnValue parserThreadReturnValue = new ParserThreadReturnValue(new CityInfo(city,docId,documentReturnValue.getCityLocations()),null);

            //If needed to upload
            if (uploadToFile) {
                //The last one needs to be updated
                this.cityInfo[this.cityInfo.length-1] =new CityInfo(city,docId,documentReturnValue.getCityLocations());

                //Run all of the indexers
                for (int i = 0; i < this.indexerThreads.length; i++) {
                    this.executorService.submit(this.indexerThreads[i]);
                }
                this.executorService.submit(this.docIndexerThread);
                Future<Boolean> future = this.executorService.submit(new UpdateCitiesInfoThread(this.indexer.getPostingOfCities(),this.cityInfo));
                parserThreadReturnValue.future = future;
            }
            //Return the return value
            return parserThreadReturnValue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This function is responsible to shutdown the threadpool
     */
    public void shutDown()
    {
        this.executorService.shutdown();
    }


}
