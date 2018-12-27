package Model.Index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * This class will get save the dictionary in a file
 */
public class AddDictionaryToFile implements Callable<Boolean> {
    private HashMap<String,int[]> mainMap;//The main dictionary
    private String filePath;//The path to the posting file
    private String fileName;//The name of the file

    /**
     * This is the constructor of the class
     * @param filePath - The given path in which we want to save the dictionary in
     * @param memory - The main dictionary
     * @param stem - True if we stemmed the terms. False- otherwise
     */
    public AddDictionaryToFile(String filePath,HashMap<String,int []> memory,boolean stem)
    {
        this.mainMap = memory;
        this.filePath = filePath;
        this.fileName = "dictionary"+"&"+stem+".txt";
        //this.fileName = "dictionary"+"&"+stem+".csv";
    }

    /**
     * This function will convert the data in the map to a StringBuilder
     * @param stringBuilder - The given StringBuilder
     */
    private void convertToStringBuilder(StringBuilder stringBuilder)
    {
        int []temp;
        for(Map.Entry<String,int[]> entry:this.mainMap.entrySet())
        {
            temp = entry.getValue();
            stringBuilder.append(temp[0]+"^"+temp[1]+"*"+entry.getKey()+"\n");

        }
    }

    /**
     * This function will add the dictionary to a file in a thread
     * @return - True if the process went successfully. False - otherWise
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {
        File fileTowWiteTo = new File(this.filePath+"\\"+this.fileName);
        try {
            fileTowWiteTo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        BufferedWriter bufferedWriter = null;
        StringBuilder stringBuilder = new StringBuilder();
        this.convertToStringBuilder(stringBuilder);
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileTowWiteTo.getAbsolutePath(), true));
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.close();
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }
}
