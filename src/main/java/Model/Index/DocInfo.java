package Model.Index;

/**
 * This class represents the information that we want to save on a document
 */
public class DocInfo {
    private int docNum;//The doc number
    private String docName;//The document name
    private int numOfUniqeTerms;//Number of unique terms
    private int maxValues;//The Maximun frequency
    private int length;//The length of the doc
    private String cityName;//The name of the city

    /**
     * The constructor
     * @param docNum - The document number
     * @param docName - The document name
     * @param numOfuniqeTerms -The number of unique terms
     * @param maxValues - The maximum frequency
     * @param cityName - The name of the city
     * @param length - The length of the doc
     */
    public DocInfo(int docNum,String docName,int numOfuniqeTerms, int maxValues,String cityName,int length){
        this.cityName= cityName;
        this.docName=docName;
        this.docNum=docNum;
        this.numOfUniqeTerms= numOfuniqeTerms;
        this.maxValues= maxValues;
        this.length= length;
    }

    /**
     * This function will return the document number
     * @return - The document number
     */
    public int getDocNum(){
        return this.docNum;
    }

    /**
     * This function will return teh document name
     * @return - The document name
     */
    public String getDocName(){
        return this.docName;
    }

    /**
     * This function will get the number of unique terms
     * @return - The number of unique terms
     */
    public int getNumOfUniqeTerms(){
        return this.numOfUniqeTerms;
    }

    /**
     * This function will return the maximum frequency
     * @return - The maximum frequency
     */
    public int getMaxValues() {
        return maxValues;
    }

    /**
     * This function will return the length of the document
     * @return - The length of the document
     */
    public int getLength() {
        return length;
    }

    /**
     * This function will return the nam of the city
     * @return - The name of the city
     */
    public String getCityName(){
        return cityName;
    }
}
