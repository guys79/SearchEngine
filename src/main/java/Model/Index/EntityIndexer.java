package Model.Index;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class EntityIndexer implements Callable<Boolean> {

    private String path;//The path to the entity file
    private HashMap<Integer,String []> entities;//The map of entities and doc numbers
    private HashMap<String, int []> mainDictionary;//The main dictionary

    /**
     * The constructor
     * @param path - The path to the entity file
     * @param entities - The map of entities and doc numbers
     * @param mainDictionary - The main dictionary
     */
    public EntityIndexer(String path,HashMap<Integer,String []> entities,HashMap<String, int []> mainDictionary)
    {
        this.path =path;
        this.entities = entities;
        this.mainDictionary = mainDictionary;
    }

    /**
     * This function will upload all the data to the file
     * @return - True if the process is successful
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {

        StringBuilder stringBuilder = new StringBuilder();
        int maxSize;
        String [] entityDoc;
        for(int docNum:this.entities.keySet())
        {
            maxSize = 5;
            entityDoc = this.entities.get(docNum);
            stringBuilder.append(docNum);
            for(int i=0;maxSize>0&& i<entityDoc.length;i++)
            {
                //If entity
                if(entityDoc[i].equals(entityDoc[i].toUpperCase())&& this.mainDictionary.containsKey(entityDoc[i]))
                {
                    maxSize--;
                    stringBuilder.append("_"+entityDoc[i]);
                }
            }
            stringBuilder.append("\n");
        }
        return upload(stringBuilder);
    }

    /**
     * This function will upload the information in the StringBuilder to the file
     * @param stringBuilder - The given StringBuilder
     * @return - True if the process is successful
     */
    private boolean upload(StringBuilder stringBuilder)
    {
        File fileTowWiteTo = new File(this.path);
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
