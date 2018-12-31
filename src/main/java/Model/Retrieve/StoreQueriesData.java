package Model.Retrieve;

import javafx.util.Pair;

import java.io.*;
import java.util.*;

/**
 * we store the data on the queries
 */
public class StoreQueriesData {
    String path;// the path to save the doc in

    /**
     * we create a new file that we will save the data in
     * @param path the path of the doc
     */
    public StoreQueriesData(String path){
        this.path=path;
        File file = new File(path);
        if(file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * we add the data to the file
     * @param data the data to save
     */
    public void addToStorage(HashMap<String,Pair<QueryInfo, String[]>> data){
        File file = new File(path);
        try(FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            Pair<QueryInfo,String []> infoOnQuery;
            QueryInfo queryInfo;
            String [] docs;
            String toWrite;
            String [] keysInOrder = queryNumSorted(data);
            print(keysInOrder);
            for (int i=0;i<keysInOrder.length;i++) {
                toWrite="";
                infoOnQuery = data.get(keysInOrder[i]);
                queryInfo = infoOnQuery.getKey();
                docs = infoOnQuery.getValue();
                for(int j=0;j<docs.length;j++){
                    toWrite += ""+queryInfo.getNumOfQuery()+" "+0+" "+docs[j]+" "+1+" "+42.38+" mt"+"\n";

                }
                out.print(toWrite);
            }
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String [] queryNumSorted(HashMap<String,Pair<QueryInfo, String[]>> data)
    {
        int min;
        String minKey;
        List<String > list = new ArrayList<>(data.keySet());
        String [] keysInOrder = new String[list.size()];
        String key;
        int val;
        for(int i=0;i<keysInOrder.length;i++)
        {
            min=Integer.MAX_VALUE;
            minKey ="";
            for(int j=0;j<list.size();j++)
            {
                key = list.get(j);
                val = data.get(key).getKey().getNumOfQuery();
                if(min>val)
                {
                    min = val;
                    minKey = key;

                }
            }
            list.remove(minKey);
            keysInOrder[i] = minKey;
        }
        return keysInOrder;
    }
    private void print(String [] keysInOrder)
    {
        for (int i=0;i<keysInOrder.length;i++) {
            System.out.println(keysInOrder[i]);
        }
    }
}