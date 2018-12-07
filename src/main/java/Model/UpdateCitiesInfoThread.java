package Model;

import java.util.concurrent.Callable;

/**
 * This class will update the data in the city indexer
 * This class will do it as a thread
 */
public class UpdateCitiesInfoThread implements Callable<Boolean>{
    private PostingOfCities postingOfCities;// The indexer of the cities
    private CityInfo [] cityInfo;//The information about the cities

    /**
     * This is the constructor of the class
     * @param postingOfCities - The given cities indexer
     * @param cityInfo - The given cities information
     */
    public UpdateCitiesInfoThread(PostingOfCities postingOfCities,CityInfo[] cityInfo)
    {
        this.postingOfCities = postingOfCities;
        this.cityInfo = cityInfo;
    }
    @Override
    public Boolean call() {
        try {
            for(int i=0; i<this.cityInfo.length;i++)
            {
                //For every valid city, update the city indexer
                if(this.cityInfo[i].getCity().length()>1) {
                    this.postingOfCities.addCity(this.cityInfo[i].getCity(), this.cityInfo[i].getDoc(), this.cityInfo[i].getCityLoc());
                }
            }
            return true;
        }
        catch (Exception e)
        {

            e.printStackTrace();
            return false;
        }

    }
}
