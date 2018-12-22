package Model;

public class DocInfo {
    private int docNum;
    private String docName;
    private int numOfuniqeTerms;
    private int maxValues;
    private int length;
    private String cityName;

    public DocInfo(int docNum,String docName,int numOfuniqeTerms, int maxValues,String cityName,int length){
        this.cityName= cityName;
        this.docName=docName;
        this.docNum=docNum;
        this.numOfuniqeTerms= numOfuniqeTerms;
        this.maxValues= maxValues;
        this.length= length;
    }

    public int getDocNum(){
        return this.docNum;
    }

    public String getDocName(){
        return this.docName;
    }

    public int getNumOfuniqeTerms(){
        return this.numOfuniqeTerms;
    }

    public int getMaxValues() {
        return maxValues;
    }

    public int getLength() {
        return length;
    }

    public String getCityName(){
        return cityName;
    }
}
