package Model.Retrieve;


import Model.Index.DocInfo;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * this class should give us the data on a specific document(the data that we saved in the text document)
 */
public class GetDoc implements Callable<Boolean> {
    private HashMap<Integer,DocInfo> DocInfo;//The information about the documents
    private String path;//The path to the soc file
    private int numOdDocs;
    private double averageLength;

    /**
     * The constructor
     * @param path - The path to the doc file
     */
    public GetDoc(String path){
        this.path = path;
        this.DocInfo = new HashMap<>();
        this.numOdDocs = 0;
        this.averageLength =0;
    }

    /**
     * This function will receive a line from the file and will parse it and add it to the dictionary
     * @param line - The given line
     */
    private void addToMap(String line)
    {
        String [] info = line.split(";");
        int docNum = Integer.parseInt(info[0]);
        String docName = info[1];
        int numberOfUniqueTerms = Integer.parseInt(info[2]);
        int maxFreq = Integer.parseInt(info[3]);
        String cityName = info[4];
        int length = Integer.parseInt(info[5]);

        this.averageLength = this.averageLength*numOdDocs +length;
        this.numOdDocs++;
        this.averageLength = this.averageLength/numOdDocs;

        this.DocInfo.put(docNum,new DocInfo(docNum,docName,numberOfUniqueTerms,maxFreq,cityName,length));




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
     *
     * @param docNum- the number of the doc that we want to the data on
     * @return- the data on the doc (a DocInfo )
     */
    public DocInfo getDetailsOnDocs(int docNum){

            return this.DocInfo.get(docNum);




    }

    public double getAverageLength() {
        return averageLength;
    }

    public int getNumOdDocs() {
        return numOdDocs;
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
