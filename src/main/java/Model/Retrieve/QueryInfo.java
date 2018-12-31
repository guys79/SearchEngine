package Model.Retrieve;

/**
 * this class should contain the information on a query
 */
public class QueryInfo {
    private int numOfQuery;// the number of the query
    private String myQuery;// the query itself

    /**
     * The constructor
     */
    public QueryInfo(){

    }

    /**
     * The constructor
     * @param numOfQuery the number of the query
     * @param myQuery the query itself
     */
    public QueryInfo(int numOfQuery, String myQuery){
        this.myQuery= myQuery;
        this.numOfQuery= numOfQuery;
    }

    /**
     * This function will return the query
     * @return myQuery - The query
     */
    public String getMyQuery() {
        return myQuery;
    }

    /**
     * This function will return the id of the query
     * @return numOfQuery - The id of the query
     */
    public int getNumOfQuery() {
        return numOfQuery;
    }

    /**
     * This function will set the query
     * @param myQuery - The query
     */
    public void setMyQuery(String myQuery) {
        this.myQuery = myQuery;
    }

    /**
     * This function will set the query id
     * @param numOfQuery - The query id
     */
    public void setNumOfQuery(int numOfQuery) {
        this.numOfQuery = numOfQuery;
    }
}