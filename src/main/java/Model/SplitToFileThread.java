package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class will write given data to a file in a thread
 */
public class SplitToFileThread implements Runnable {
    private String filePath;//The path of the file
    private StringBuilder stringBuilder;//the string builder - the data

    /**
     * This 
     * @param filePath
     * @param stringBuilder
     */
    public SplitToFileThread(String filePath,StringBuilder stringBuilder)
    {
        this.stringBuilder = stringBuilder;
        this.filePath = filePath;
    }
    @Override
    public void run() {

        File fileTowWiteTo = new File(this.filePath);
        try {
            fileTowWiteTo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileTowWiteTo.getAbsolutePath(), true));
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
