package Model.Retrieve;

import Model.Index.DocInfo;

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
    private int numOfDocs;//Number of docs

    public Ranker(HashSet<TermInfo> queryTermInfo,double averageDocLength,int numOfDocs)
    {
        this.averageDocLength = averageDocLength;
        this.numOfDocs = numOfDocs;
        this.k=1.5;
        this.beta = 0.75;
        this.queryTermInfo = queryTermInfo;
        

    }

    public double Rank(DocInfo document)
    {
        // TODO: 12/23/2018 Add the semantics here somewhere
        //We will use the

        double bm25 = this.BM25(document);

        return bm25;
    }
    
    private double BM25(DocInfo document)
    {
        if(document.getDocName().equals("LA081289-0127"))
            System.out.println("s");
        double rank = 0;
        for(TermInfo termInfo: queryTermInfo)
        {
            // TODO: 12/23/2018  Need to update to the real value
            int df = termInfo.getDf();
            double idf = Math.log(this.numOfDocs*1.0/df)/Math.log(2);
            int tf =0;
            Object temp = termInfo.docIdTfMap.get(document.getDocNum());
            if(temp!=null)
            {
                tf= (int)temp;
            }
            int docLength = document.getLength();

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
