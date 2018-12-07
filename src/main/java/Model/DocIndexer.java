package Model;

import sun.awt.Mutex;
import java.io.*;
import java.util.HashMap;
import java.util.Set;

/**
 * This class is responsible to index the information about the documents
 */
public class DocIndexer{
    private HashMap<Integer,String> tempData;//This map saves the data of a chunk of docs
    private ReadFile readFile;//The readFile
    private String postingPath;//The location of the posting files
    private Mutex mutex;//A mutex

    /**
     * this is the constructor.
     * it should initialize the parameters and create a file that will save our data
     * @param readFile- the read file
     * @param location - The location of the file the posting file that we will create
     */
    public DocIndexer(String location,ReadFile readFile){
        this.mutex = new Mutex();
        tempData= new HashMap<Integer,String>();
        this.readFile = readFile;
        String name = "postingListOfFile";
        try {
            PrintWriter postingListOfFile = new PrintWriter(location+"\\"+name+".txt", "UTF-8");
            postingListOfFile.close();
            File file = new File(location+"\\"+name+".txt");
            postingPath = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will return the Readfile instance of this class
     * @return - The read file
     */
    public ReadFile getReadFile() {
        return readFile;
    }

    /**
     * this function shuld write the data to the file
     */
    public void writeToThefile(){
        this.mutex.lock();
        File file = new File(postingPath);
        try(FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {

            String str;
            Set<Integer> keys = tempData.keySet();

            for(Integer key : keys)
            {
                str= tempData.get(key);
                out.print(str);
            }
            tempData = new HashMap<>();

            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.mutex.unlock();
    }
    /**
     * this function should add the infomation to a local dictionary and afterwards to the file
     * @param docNum- the number of the doc
     * @param numOfuniqeTerms- the number of uniqe terms
     * @param maxValues- the max number of ocurences of the max term in the file
     * @param length - the length of the doc
     */
    public void addToDic(int docNum, int numOfuniqeTerms, int maxValues,int length) {
        this.mutex.lock();
        if(docNum!=-1) {
            String str = readFile.getNamesOfDocs(docNum) + ";" + numOfuniqeTerms + ";" + maxValues + ";" + readFile.getNameOfCity(docNum)+";"+docNum+";"+length+"#";
            tempData.put(docNum,str);
        }
        this.mutex.unlock();
    }


}
