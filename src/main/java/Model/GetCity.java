package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class GetCity {
    private String contentOfCitys;

    public GetCity(String path){
        File file = new File(path);
        final BufferedReader s;
        String av;
        try {
            s = new BufferedReader(new FileReader(file));
            while ((av = s.readLine()) != null) {
                contentOfCitys=av;
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashSet<CityInfo> getDetailsOnCitys(String nameOfCity){
        HashSet<CityInfo> infoToReturn= new HashSet<CityInfo>();
        int locOfCity=contentOfCitys.indexOf(nameOfCity);
        String dataOnCity= contentOfCitys.substring(locOfCity,contentOfCitys.length());
        dataOnCity= dataOnCity.substring(0,dataOnCity.indexOf("#"));
        String [] info=dataOnCity.split("&");
        for(int i=1;i<info.length;i++){
            if(!info[i].contains(";")){
                infoToReturn.add(new CityInfo(nameOfCity,Integer.parseInt(info[i]),null));
            }
            else{
                HashSet<Integer> loc= new HashSet<Integer>();
                String[] locs= info[i].split(";");
                int doc=Integer.parseInt(locs[0]);
                for(int j=1;j<locs.length;j++){
                    loc.add(Integer.parseInt(locs[j]));
                }
                infoToReturn.add(new CityInfo(nameOfCity,doc,loc));
            }
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
