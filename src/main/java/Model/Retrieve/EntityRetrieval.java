package Model.Retrieve;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class will retrieve the information about the entities from the entities file as a callable
 */
public class EntityRetrieval implements Callable<Boolean> {

    private String path;//The path to the entities file
    private HashMap<Integer,List<String>> entities; // The map that we will save the information in

    /**
     * The constructor
     * @param path - The path to the entities file
     */
    public EntityRetrieval(String path)
    {
        this.path = path;
        this.entities = new HashMap<>();
    }


    /**
     * This function will load the content of the file
     * @return - True if the process was successful
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {
        return readContent();
    }

    /**
     * This function will receive a line from the file and will parse it and add it to the dictionary
     * @param line - The given line
     */
    private void addToMap(String line)
    {
        String [] info = line.split("_");
        List<String> entityList = new ArrayList<>();
        int docNum = Integer.parseInt(info[0]);
        for(int i=1;i<info.length;i++)
        {
            entityList.add(info[i]);
        }
        this.entities.put(docNum,entityList);
    }

    /**
     * This function will read the content of the document file
     * @return - True if the process succeeded
     */
    public boolean readContent()
    {
        File file = new File(path);
        final BufferedReader s;
        String av;
        try {
            s = new BufferedReader(new FileReader(file));
            while ((av = s.readLine()) != null) {
                this.addToMap(av);
            }
            s.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This funcrion will return the entities of the document
     * @param docNum - The document number
     * @return - The list of entities
     */
    public List<String> getEntities(int docNum)
    {
        return this.entities.get(docNum);
    }


}
