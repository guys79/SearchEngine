package Model.Retrieve;

import java.io.*;
import java.util.ArrayList;

public class GetQuery {
    ArrayList<String> arreyOfFile;

    public GetQuery(String path){
        File file = new File(path);
        final BufferedReader s;
        String av;
        arreyOfFile= new ArrayList<String>();
        try {
            s = new BufferedReader(new FileReader(file));
            while ((av = s.readLine()) != null) {
                if (av.contains("<title>")) {
                    arreyOfFile.add(av.substring(7,av.length()));

                }
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNextQuery(){
        if(this.arreyOfFile.size()==0)
            return null;
        String value =arreyOfFile.get(0);
        arreyOfFile.remove(0);
        return value;
    }
}
