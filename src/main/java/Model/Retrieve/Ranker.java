package Model.Retrieve;

import java.util.HashSet;

/**
 * This class will represent a Ranker.
 * A Ranker will rank a document with the given query info
 */
public class Ranker {

    private double k;//The k int the BM25 equation
    private double beta;//The beta in the BM25  equation
    private double averageDocLength;//The average document length
    private HashSet<TermInfo> queryTermInfo;//The hashSet of termInfo of the query

    public Ranker(HashSet<TermInfo> queryTermInfo,double averageDocLength)
    {
        this.averageDocLength = averageDocLength;
        this.k=1.5;
        this.beta = 0.75;
        this.queryTermInfo = queryTermInfo;
        

    }

    public double Rank(String document)// TODO: 12/23/2018 The document will not be represented as a string but as an object
    {
        // TODO: 12/23/2018 Add the semantics here somewhere
        //We will use the

        double bm25 = this.BM25(document);

        return 0;
    }
    
    private double BM25(String document)// TODO: 12/23/2018 The document will not be represented as a string but as an object
    {
        int rank = 0;
        for(TermInfo termInfo: queryTermInfo)
        {
            // TODO: 12/23/2018  Need to update to the real value
            double idf = 0;
            int tf = 0;
            int docLength = 0;

            //The BM25 formula
            rank+= idf*((tf*(this.k+1))/(tf+this.k*(1-this.beta+beta*(docLength/this.averageDocLength))));
        }
        return rank;
    }

    /**
     * This function will return the beta that is a part of the BM25 equation
     * @return - The beta
     */
    public double getBeta() {
        return beta;
    }

    /**
     * This function will return the K that is a part of the BM25 equation
     * @return - The beta
     */
    public double getK() {
        return k;
    }

    /**
     * This function will set the beta that is a part of the BM25 equation
     * @param beta - The given beta
     */
    public void setBeta(double beta) {
        this.beta = beta;
    }

    /**
     * This function will set the K that is a part of the BM25 equation
     * @param k - The K
     */
    public void setK(double k) {
        this.k = k;
    }
}
