package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class will write given data to multiple files (almost even in number of fields)
 */
public class SplitToFileThread implements Runnable {
    private String filePath;//The path of the file
    private StringBuilder stringBuilder;//the string builder - the data

    /**
     * This Is the constructor of the class
     * @param filePath - The path of the file we want to write to
     * @param stringBuilder - The given string builder that holds the information that we want to write to the file
     */
    public SplitToFileThread(String filePath,StringBuilder stringBuilder)
    {
        this.stringBuilder = stringBuilder;
        this.filePath = filePath;
    }

    @Override
    /**
     * This function will run this class as a thread and write the given data to the file
     */
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
