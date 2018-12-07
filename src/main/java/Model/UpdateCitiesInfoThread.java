package Model;

import java.util.concurrent.Callable;

public class UpdateCitiesInfoThread implements Callable<Boolean>{
    private PostingOfCities postingOfCities;
    private CityInfo [] cityInfo;
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
