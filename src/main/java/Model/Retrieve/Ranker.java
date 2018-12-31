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

    /**
     * The constructor of the class
     * @param queryTermInfo - The information about the terms of the query
     * @param averageDocLength - The average document length
     * @param numOfDocs - The number of docs in the corpus
     */
    public Ranker(HashSet<TermInfo> queryTermInfo,double averageDocLength,int numOfDocs)
    {
        this.averageDocLength = averageDocLength;
        this.numOfDocs = numOfDocs;
        this.k=1.2;
        this.beta = 0.6;
        this.queryTermInfo = queryTermInfo;
        

    }

    /**
     * This function will give a single document a grade considering the given query terms information
     * @param document - The given document
     * @return - The grade that the document got
     */
    public double Rank(DocInfo document)
    {
        //We will use the BM25

        double bm25 = this.BM25(document);

        return bm25;
    }

    /**
     * This function will use the BM25 retrieval function to grade the document
     * @param document - The given document
     * @return - The grade that the BM25 returnes
     */
    private double BM25(DocInfo document)
    {
        double rank = 0;
        for(TermInfo termInfo: queryTermInfo)
        {
            int df = termInfo.getDf();
            double idf = Math.log(this.numOfDocs*1.0/df)/Math.log(20);

            //double idf = Math.log(this.numOfDocs*1.0/df)/Math.log(2);
            //Normalized
            //idf = idf/(idf+1);
            //idf = Math.pow(idf,1/1.5);
            int tf =0;
            Object temp = termInfo.docIdTfMap.get(document.getDocNum());
            if(temp!=null)
            {
                tf= (int)temp;
            }
            int docLength = document.getLength();
            double weight = termInfo.getWeight();
            double tfWeight = termInfo.getTfInQuery();
            if(tfWeight==0)
            {
                tfWeight = 1;
            }
            //double tfWeight = 1;
            //The BM25 formula
            rank+= idf*((tf*(this.k+1))/(tf+this.k*(1-this.beta+beta*(docLength/this.averageDocLength)))) * weight * tfWeight;
        }
        return rank;
    }

    /**
     * This function will get the weight of the term in the query
     * @param tf - The given tf
     * @return - The calculated weight
     */
    private double tfInQuery(int tf)
    {
        double sum = 0;
        for(int i=1;i<=tf;i++)
        {
            sum+=1.0/i;
        }
        return sum;

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
