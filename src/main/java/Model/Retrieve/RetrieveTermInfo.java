package Model.Retrieve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class will retrieve the information about the terms given their file location and their posting file path
 */
public class RetrieveTermInfo implements Callable<HashSet<TermInfo>> {

    private String postingFilePath;//The posting directory path
    private String  fileName;//The name of the file
    private HashSet<String> terms;//The list of terms
    private HashSet<TermInfo> termInfo;//The list of term information that we will return

    /**
     * This is the constructor of the class
     * @param fileName - The name of the file
     * @param terms - The given list of files
     * @param postingFilePath - The path of the posting files (all of the files in the given dictionary)
     */
    public RetrieveTermInfo(String fileName, HashSet<String> terms,String postingFilePath)
    {
        this.postingFilePath = postingFilePath;
        this.termInfo = new HashSet<>();
        this.fileName = fileName;
        this.terms = terms;
    }

    /**
     * This function will return all of the data about the terms that the file contains
     * @return - The data on the given terms
     */
    public HashSet<TermInfo> retrieveInfo()
    {
        String filePath = this.postingFilePath+"\\"+this.fileName;
        File file = new File(filePath);
        BufferedReader bufferedReader = null;
        int index=0;
        String term="";
        TermInfo current = null;
        String line;
        boolean firstTime = true;
        String termLine;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            //While we didn't finish the file and the we didn't go through all of the terms
            while((line= bufferedReader.readLine())!=null)
            {
                if(line.indexOf('_')==-1)
                {
                    continue;
                }
                termLine = line.substring(0,line.indexOf('_'));

                if(!term.equalsIgnoreCase(termLine)) {
                    //If there are no more terms
                    if(terms.size()==0)
                        break;
                    //If this is the first time encountering that term (and he is from the hash of terms)
                    if (this.terms.contains(termLine)) {
                        term = termLine;
                        this.terms.remove(term);
                        current = new TermInfo(term);
                        this.termInfo.add(current);
                        parseAndAddToMap(line,current);
                    }
                }
                else
                {
                    parseAndAddToMap(line,current);
                }

            }
            bufferedReader.close();
            return this.termInfo;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This function will add another piece of information about a term from this posting file
     * @param line - The line in the posting file
     * @param termInfo - The termInfo that we want to add the information to
     */
    private void parseAndAddToMap(String line,TermInfo termInfo)
    {
        int index1 = line.indexOf('_');
        int index2 = line.indexOf('*');
        String docId = line.substring(index1+1,index2);
        String tf = line.substring(index2+1);
        int docIdInt = Integer.parseInt(docId);
        int tfInt = Integer.parseInt(tf);
        termInfo.addInfo(docIdInt,tfInt);
    }


    /**
     * This function will return all of the data about the terms that the file contains
     * @return
     * @throws Exception
     */
    @Override
    public HashSet<TermInfo> call() throws Exception {
        return this.retrieveInfo();
    }
}
