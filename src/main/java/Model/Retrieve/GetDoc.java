package Model.Retrieve;


import Model.Index.DocInfo;

import java.io.*;
import java.util.HashSet;

/**
 * this class should give us the data on a specific document(the data that we saved in the text document)
 */
public class GetDoc {
    private String contentOfDocs;

    public GetDoc(String path){
        File file = new File(path);
        final BufferedReader s;
        String av;
        try {
            s = new BufferedReader(new FileReader(file));
            while ((av = s.readLine()) != null) {
                contentOfDocs=av;
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param numOfDoc- the number of the doc that we want to the data on
     * @return- the data on the doc (a DocInfo )
     */
    public DocInfo getDetailsOnCitys(String numOfDoc){
        DocInfo docInfo = null;
        int locOfDoc=contentOfDocs.indexOf("#"+numOfDoc);
        String dataOnDoc= contentOfDocs.substring(locOfDoc+1,contentOfDocs.length());
        dataOnDoc= dataOnDoc.substring(0,dataOnDoc.indexOf("#"));
        String [] info=dataOnDoc.split(";");
        for(int i=1;i<info.length;i++){
            docInfo=new DocInfo(Integer.parseInt(numOfDoc),info[1],Integer.parseInt(info[2]),Integer.parseInt(info[3]),info[4],Integer.parseInt(info[5]));
        }
        return docInfo;
        /*
        while(dataOnCity.indexOf('?')>0) {
            dataOnCity = dataOnCity.substring(dataOnCity.indexOf('?') + 1, dataOnCity.length());
            int idx = dataOnCity.indexOf('?');
            if (idx > dataOnCity.indexOf(';')) {
                idx = dataOnCity.indexOf(';');
            }
            int numOfDoc = Integer.parseInt(dataOnCity.substring(0, idx));
            dataOnCity= dataOnCity.substring(idx,dataOnCity.length());
            if(dataOnCity.indexOf('?')!=-1) {
                while (dataOnCity.indexOf(';') != -1 && dataOnCity.indexOf(';') < dataOnCity.indexOf('?')) {
                }
            }
        }
        */
    }
}