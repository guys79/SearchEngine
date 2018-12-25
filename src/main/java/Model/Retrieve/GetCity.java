package Model.Retrieve;


import Model.Index.CityInfo;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

/**
 * this class should give us the data on a specific City(Only the data that is relevant to the retrieval algorithm)
 */
public class GetCity implements Callable<Boolean> {
    private HashMap<String,HashSet<Integer>> cityInfo;//The information about the cities
    private String path;//The path to the file

    /**
     * The constructor of the class
     * @param path - Te path to the city file
     */
    public GetCity(String path){
        this.path = path;
        this.cityInfo = new HashMap<>();
    }


    private void addToMap(String dataOnCity)
    {
        String [] info=dataOnCity.split("&");
        String cityName = info[0].substring(0,dataOnCity.indexOf('@'));
        String doc;
        HashSet<Integer> docNums = new HashSet<>();
        int index;
        for(int i=1;i<info.length;i++)
        {
            index =info[i].indexOf(';');
            if(index!=-1)
                doc = info[i].substring(0,index);
            else
            {
                doc = info[i];
            }
            docNums.add(Integer.parseInt(doc));
        }
        this.cityInfo.put(cityName,docNums);

    }
    /**
     * This function will read the content of the cities form the file
     * @return - True if that process succeeded
     */
    private boolean readContent(){

        File file = new File(path);
        final BufferedReader s;
        String av;
        try {
            s = new BufferedReader(new FileReader(file));
            while ((av = s.readLine()) != null) {
                addToMap(av);
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
     * @param nameOfCity- the name of the city that we want to get the data on
     * @return- a list of CityInfo while each CityInfo contains data on a doc that the city appeared in
     */
    public HashSet<Integer> getDetailsOnCitys(String nameOfCity){
        /*HashSet<CityInfo> infoToReturn= new HashSet<>();
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
        return infoToReturn;*/
        return this.cityInfo.get(nameOfCity);
    }

    /**
     * This function will read the content of the cities form the file as a callable
     * @return - True if that process succeeded
     */
    @Override
    public Boolean call() throws Exception {
        return this.readContent();
    }
}
