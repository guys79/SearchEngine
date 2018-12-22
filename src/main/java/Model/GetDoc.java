package Model;

import java.io.*;
import java.util.HashSet;

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

    public HashSet<DocInfo> getDetailsOnCitys(String numOfDoc){
        HashSet<DocInfo> infoToReturn= new HashSet<DocInfo>();
        int locOfDoc=contentOfDocs.indexOf("#"+numOfDoc);
        String dataOnDoc= contentOfDocs.substring(locOfDoc+1,contentOfDocs.length());
        dataOnDoc= dataOnDoc.substring(0,dataOnDoc.indexOf("#"));
        String [] info=dataOnDoc.split(";");
        for(int i=1;i<info.length;i++){
            infoToReturn.add(new DocInfo(Integer.parseInt(numOfDoc),info[1],Integer.parseInt(info[2]),Integer.parseInt(info[3]),info[4],Integer.parseInt(info[5])));
        }
        return infoToReturn;
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
