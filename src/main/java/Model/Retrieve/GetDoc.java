package Model.Retrieve;


import Model.Index.DocInfo;

import java.io.*;
import java.util.HashSet;
import java.util.concurrent.Callable;

/**
 * this class should give us the data on a specific document(the data that we saved in the text document)
 */
public class GetDoc implements Callable<Boolean> {
    private String contentOfDocs;//The content of the documents
    private String path;//The path to the soc file

    /**
     * The constructor
     * @param path - The path to the doc file
     */
    public GetDoc(String path){
        this.path = path;
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
                contentOfDocs=av;
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
     *
     * @param docNum- the number of the doc that we want to the data on
     * @return- the data on the doc (a DocInfo )
     */
    public DocInfo getDetailsOnCitys(int docNum){
        String numOfDoc = ""+docNum;
        DocInfo docInfo = null;
        int locOfDoc=contentOfDocs.indexOf("#"+numOfDoc);
        String dataOnDoc= contentOfDocs.substring(locOfDoc+1,contentOfDocs.length());
        dataOnDoc= dataOnDoc.substring(0,dataOnDoc.indexOf("#"));
        String [] info=dataOnDoc.split(";");
        for(int i=1;i<info.length;i++){
            docInfo=new DocInfo(Integer.parseInt(numOfDoc),info[1],Integer.parseInt(info[2]),Integer.parseInt(info[3]),info[4],Integer.parseInt(info[5]));
        }
        return docInfo;

    }

    /**
     * This function will read the content of the document file
     * @return - True if the process succeeded
     */
    @Override
    public Boolean call() throws Exception {
        return this.readContent();
    }
}
