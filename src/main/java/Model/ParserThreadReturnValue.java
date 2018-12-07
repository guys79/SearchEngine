package Model;

import java.util.concurrent.Future;

/**
 * this class will contain the data that the parserThread will return
 */
public class ParserThreadReturnValue {
    CityInfo cityInfo; //the info about the city
    Future<Boolean>future;//The future of the city

    /**
     * The constructor of the class
     * @param cityInfo - the given city info
     * @param future - The future of the thread that runs the information about that city
     */
    public ParserThreadReturnValue(CityInfo cityInfo, Future<Boolean>future)
    {
        this.cityInfo = cityInfo;
        this.future = future;
    }
}
