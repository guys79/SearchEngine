package Model.Retrieve;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * this class should give us the data on a specific City(Only the data that is relevant to the retrieval algorithm)
 */
public class GetCity implements Callable<Boolean> {
    private HashMap<String,HashSet<Integer>> cityInfo;//The information about the cities
    private String path;//The path to the file

    /**
     * The constructor of the class
     * @param path - The path to the city file
     */
    public GetCity(String path){
        this.path = path;
        this.cityInfo = new HashMap<>();
    }

    /**
     * This function will add the data on the city to the map
     * @param dataOnCity - The given city data
     */
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
        this.cityInfo.put(cityName.toLowerCase(),docNums);

    }

    /**
     * This function will read the content of the cities form the file
     * @return - True if that process succeeded
     */
    public List<String> getNamesOfCitys(){
        List<String> namesOfCitys= new ArrayList<String>();
        File file = new File(path);
        final BufferedReader s;
        String av;
        try {
            s = new BufferedReader(new FileReader(file));
            while ((av = s.readLine()) != null) {
                namesOfCitys.add(av.substring(0,av.indexOf('@')));
            }
            s.close();
            return namesOfCitys;
        } catch (FileNotFoundException e) {
          //  e.printStackTrace();
        } catch (IOException e) {
           // e.printStackTrace();
        }
        return null;
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

        return this.cityInfo.get(nameOfCity.toLowerCase());
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
