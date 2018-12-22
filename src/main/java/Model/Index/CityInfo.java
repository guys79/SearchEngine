package Model.Index;

import java.util.HashSet;

/**
 * This class contains the information thar we want to save on a city
 */
public class CityInfo {
    private String city;// Name of the city
    private int doc;//doc number in which the city appears
    private HashSet<Integer> cityLoc;// The locations of the city in the document

    /**
     * The constructor of the class
     * @param city - The city name
     * @param doc - The doc number in which the city appears
     * @param cityLoc - The locations of the city name in the document
     */
    public CityInfo(String city, int doc,HashSet<Integer> cityLoc)
    {
        this.city = city;
        this.cityLoc = cityLoc;
        this.doc = doc;
    }

    /**
     * This function will return the docId
     * @return - The docId
     */
    public int getDoc() {
        return doc;
    }

    /**
     * This function will return the name of the city
     * @return - the city name
     */
    public String getCity() {
        return city;
    }


    /**
     * This function will return the city's locations
     * @return - The hashSet of locations
     */
    public HashSet<Integer> getCityLoc() {
        return cityLoc;
    }
}
