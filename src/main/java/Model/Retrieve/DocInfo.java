package Model.Retrieve;

/**
 * this class contains the information on a doc (the information that we saved about the document in the indexing)
 */
public class DocInfo {
    private int docNum;//the document number
    private String docName;//the name of the document
    private int numOfuniqeTerms;//the number of unique terms in the document
    private int maxValues;//the frequency of the term with the maximum frequency in the document
    private int length;//the length of the document
    private String cityName;//if there is a city between the tags we return it

    /**
     *
     * @param docNum the document number
     * @param docName the name of the document
     * @param numOfuniqeTerms the number of unique terms in the document
     * @param maxValues the frequency of the term with the maximum frequency in the document
     * @param cityName the length of the document
     * @param length if there is a city between the tags we return it
     */
    public DocInfo(int docNum,String docName,int numOfuniqeTerms, int maxValues,String cityName,int length){
        this.cityName= cityName;
        this.docName=docName;
        this.docNum=docNum;
        this.numOfuniqeTerms= numOfuniqeTerms;
        this.maxValues= maxValues;
        this.length= length;
    }

    /**
     *
     * @return docNum
     */
    public int getDocNum(){
        return this.docNum;
    }
    /**
     *
     * @return docName
     */
    public String getDocName(){
        return this.docName;
    }
    /**
     *
     * @return numOfuniqeTerms
     */
    public int getNumOfuniqeTerms(){
        return this.numOfuniqeTerms;
    }
    /**
     *
     * @return maxValues
     */
    public int getMaxValues() {
        return maxValues;
    }
    /**
     *
     * @return length
     */
    public int getLength() {
        return length;
    }
    /**
     *
     * @return cityName
     */
    public String getCityName(){
        return cityName;
    }
}