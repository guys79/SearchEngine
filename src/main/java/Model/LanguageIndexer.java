package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.Callable;

/**
 * This class is responsible to index all the languages in the corpus
 * This class will upload the data as a thread
 */
public class LanguageIndexer implements Callable<Boolean> {
    private HashSet<String> languages;//The HashSet of languages
    private String fatherPath;
    private String fileName;

    /**
     * The constructor of the class
     * @param fatherPath - The path to save the file
     * @param stem - True if we stemmed the terms. False- otherwise
     */
    public LanguageIndexer(String fatherPath,boolean stem)
    {
        this.languages = new HashSet<>();
        this.fatherPath = fatherPath;
        this.fileName = "languages"+"&"+stem+".txt";
    }

    /**
     * This function will add a language tot he language indexer
     * @param language - The given language
     */
    public void addLanguage(String language)
    {
        this.languages.add(language);
    }

    /**
     * Rhis function will take the data we gathered and will add it into a given StringBuilder
     * @param stringBuilder - The given StringBuilder
     */
    private void convertDataToStringBuilder(StringBuilder stringBuilder)
    {
        for(String key:this.languages)
        {
            if (key!=null && !key.equals(""))
                stringBuilder.append(key+"\n");
        }
    }

    /**
     * This function will upload all of the language data we have gathered so far to a file
     * @return - True- if the process was successful, False- otherwise
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {
        File fileTowWiteTo = new File(this.fatherPath+"\\"+this.fileName);
        try {
            fileTowWiteTo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        StringBuilder stringBuilder=new StringBuilder();
        //Adding the data to the stringBuilder
        convertDataToStringBuilder(stringBuilder);
        this.languages = null;
        BufferedWriter bufferedWriter = null;
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
