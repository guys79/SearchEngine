package Model.Index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class will save the stop words in a list so we can access the list fast
 */
public class StopWordsHolder {
    private HashSet<String> listOfStopWords;// The set of stop words
    private String pathToStopWordsFile;// the path to the stop words file

    /**
     * This is the constructor of the class, it will init the set of stop words using the file of the stop words
     * @param pathToStopWordsFile - The path to the file of stop words
     */
    public StopWordsHolder(String pathToStopWordsFile)
    {
        //Initializing the set of stop words
        this.listOfStopWords = new HashSet<>();
        this.pathToStopWordsFile = pathToStopWordsFile;

        // Reading the content of the file
        String content ="";
        File file = new File(pathToStopWordsFile);
        FileReader reader = null;
        try{
            reader = new FileReader(file);
            char [] filleBuffer = new char[(int)file.length()];
            reader.read(filleBuffer);
            reader.close();
            content = new String(filleBuffer);
            //Initializing the set of words
            this.initSet(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will initialize the stop words set
     * @param content - The content of the stop words file
     */
    private void initSet(String content)
    {
        String [] content_array = content.split("\n|\r|between");
        for(int i=0;i<content_array.length-1;i++){
            this.listOfStopWords.add(content_array[i]);
        }
        // If the last stop word end with an '\n' them remove it
        if (content_array[content_array.length-1].charAt(content_array[content_array.length-1].length()-1) == '\n')
            content_array[content_array.length-1] = content_array[content_array.length-1].substring(0,content_array[content_array.length-1].length()-1);
        this.listOfStopWords.add(content_array[content_array.length-1]);

    }

    /**
     * This function will return true if the word is a stop word
     * @param word - The given word
     * @return - true if the word is part of the stop words set
     */
    public boolean isStopWord(String word){
        return this.listOfStopWords.contains(word.toLowerCase());
    }

}
