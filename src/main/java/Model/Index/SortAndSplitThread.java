package Model.Index;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class will sort the terms in a certain file and will split it into sub files with approximately the same number of terms
 */
public class SortAndSplitThread implements Callable<List<String>>{

    private String filePath;//the path of the posting files
    TreeMap<String,String> terms;//The map of terms
    private String fileName;//The name of the file that we will draw the data from
    int numOfTermsPerFile;//the number of terms that we will save in a posting file
    ArrayList<String> namesOfFiles;//The name of the new files
    ExecutorService executorService;//The threadpool
    private boolean stem;//true if we need to stem

    /**
     * This constructor of the class
     * @param filePath - The folser that the file is in
     * @param numberOfTermsPerFile - The number of terms that we will put in each document
     * @param fileName - The name of the file
     * @param stem - True if we stemmed the terms, False otherwise
     */
    public SortAndSplitThread(String filePath,int numberOfTermsPerFile,String fileName,boolean stem)
    {
        this.namesOfFiles = new ArrayList<>();
        this.filePath = filePath;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);
        this.terms = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.stem = stem;
        this.fileName = fileName;
        this.numOfTermsPerFile = numberOfTermsPerFile;
    }


    /**
     * This function will split the information gathered into small files.
     * each file will be sorted
     * The name of the file is the name of the biggest term (in terms of lexicographic order) that is not in the file
     */
    private void splitDocs() {
        File file = new File(this.filePath+"\\"+fileName+"_"+stem+".txt");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            writer.print("");
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        StringBuilder stringBuilder=new StringBuilder();

        String fileName = this.fileName;
        if(fileName.equals("other"))
            fileName="1";
        int counter = 0;
        this.namesOfFiles.add(fileName+ "_" + stem );
        for(Map.Entry<String,String>key:this.terms.entrySet())
        {
            counter++;
            stringBuilder.append(key.getKey()+this.terms.get(key.getKey())+"\n");
            if(counter == this.numOfTermsPerFile)
            {
                this.executorService.submit(new SplitToFileThread(this.filePath+"\\"+fileName+"_"+stem+".txt",stringBuilder));
                if(this.fileName.equals("other"))
                {
                    fileName = "1";//The files that refer to others are the files that starts with 1
                    boolean flag= true;
                    for(int i=0;i<key.getKey().length();i++)
                    {
                        if(key.getKey().charAt(i)=='_') {
                            fileName = fileName + '_';
                            flag = !flag;
                        }
                        if(flag)
                            fileName = fileName +"^"+ (int)(key.getKey().charAt(i));
                        else
                            fileName = fileName +(int)(key.getKey().charAt(i));
                    }

                }
                else
                {
                    fileName = key.getKey();
                }
                this.namesOfFiles.add(fileName+ "_" + stem );
                stringBuilder = new StringBuilder();
                counter = 0;
            }

        }
        if(counter!=0) {
            this.executorService.submit(new SplitToFileThread(this.filePath+ "\\" + fileName + "_" + stem + ".txt", stringBuilder));
        }
        else
        {
            this.namesOfFiles.remove(fileName+ "_" + stem );
        }
        this.executorService.shutdown();

    }

    /**
     * Ths function will gather the data from the file into the map
     * @param terms - Thw term that we want to assign tot eh files
     */
    private void assignToMaps(String terms) {
        String term;
        int indexOfStar;

        indexOfStar = terms.indexOf('*');
        if(indexOfStar == -1) {
            return;
        }
        term = terms.substring(0, indexOfStar);
        this.terms.put(term,terms.substring(indexOfStar));

    }

    /**
     * This function will write the data of the file to the assigned file
     * @return - The list of new file names
     */
    @Override
    public List<String> call(){

        // Reading the content of the file
        String term;
        File file = new File(this.filePath+"\\"+this.fileName+"_"+stem+".txt");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while((term= bufferedReader.readLine())!=null)
            {
                assignToMaps(term);
            }
            bufferedReader.close();
            splitDocs();
            return this.namesOfFiles;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
