package Model.Index;

import sun.awt.Mutex;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This Class is responsible to index the terms as a thread
 */
public class IndexerThread implements Runnable {
    private StringBuilder stringBuilder;//The stringBuilder that we will add the information to
    private String fileName;// The name of the file
    private String fatherPath;//The path of the father folder
    private boolean stem;//Do we need to stem
    private Mutex mutex;// A mutex


    /**
     * the constructor of the class
     * @param stem - If true, the class will stem the words. False otherwise
     * @param fatherPath - The path of the father folder
     * @param fileName - The name of the file
     * @param stringBuilder - The string builder will contain the string that we will write to the doc
     * @param mutex - The mutual mutex
     */
    public IndexerThread(boolean stem,String fatherPath,String fileName,StringBuilder stringBuilder,Mutex mutex)
    {
        this.mutex = mutex;
        this.stringBuilder = stringBuilder;
        this.stem =stem;
        this.fatherPath = fatherPath;
        this.fileName = fileName;

    }

    /**
     * This function append a new string to the string builder. This string represents one line in the temp posting files
     * @param docId - The document number
     * @param tf - The term frequency
     * @param term - The name of the term
     */
    public void addtoString(int docId,int tf,String term)
    {

        mutex.lock();
        this.stringBuilder.append(term);
        this.stringBuilder.append('_');
        this.stringBuilder.append(docId);
        this.stringBuilder.append('*');
        this.stringBuilder.append(tf);
        this.stringBuilder.append('\n');
        mutex.unlock();
    }
    @Override

    /**
     * This function will write all the data the it has to the file in the given path
     */
    public void run() {
        String type = ".txt";
        File tempPostFile = new File(fatherPath+"\\"+fileName+"_"+stem+type);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempPostFile.getAbsolutePath(),true));
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
}
