package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * This class will load a given dictionary from a file
 */
public class LoadDictionary implements Callable<HashMap<String,Integer>> {
    private HashMap<String,Integer> mainDictionary;
    private String filePath;

    /**
     * This is the constructor of the class
     * @param filePath - The path to the dictionary file
     */
    public LoadDictionary(String filePath)
    {
        this.filePath = filePath;
        this.mainDictionary = new HashMap<>();
    }

    /**
     * This function will add an entry to the dictionary
     * @param entry - The entry
     */
    private void addToDicionary(String entry)
    {
        int index = entry.indexOf('*');
        if(index==-1)
            return;
        String value = entry.substring(0,index);
        String key = entry.substring(index+1);
        this.mainDictionary.put(key,Integer.parseInt(value));

    }
    @Override
    public HashMap<String, Integer> call() throws Exception {
        // Reading the content of the file
        String term;
        File file = new File(this.filePath);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while((term= bufferedReader.readLine())!=null)
            {
                addToDicionary(term);
            }
            return this.mainDictionary;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
