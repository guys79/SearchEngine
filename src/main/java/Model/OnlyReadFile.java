package Model;

import sun.awt.Mutex;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class is responsible to read the data from the corpus
 * In addition, this class will contain some data on the document
 */
public class OnlyReadFile {
    private HashMap<Integer, Integer> max_values_dict;
    private int indexInDirectory;  // The index of the namesInDirectory list
    private ArrayList<String> namesInDirectory;  // The paths of all the files in the corpus
    private ArrayList<Integer> namesOfDocs;
    private ArrayList<String> docsInFile; // In every cell in there is a number and a list. The number is the docId and the list is the content of the file
    private int doc_id_generator;  //The counter of the document, the counter is the docId value
    private HashMap<Integer,String> dictionary_doc_name_id;  // A dictionary, The key is the name of the doc, and the value is the dicId
    private Mutex mutexDoc; //this mutex will protect dictionary of id's and numbers (dictionary_doc_name_id)
    private Mutex mutexCity; //this mutex will protect dictionary of the numbers and cities (citys)

    /**
     * this is the constructor.
     * it should initialaize the parameters and create a file that will save our data
     * @param path- the path of the corpus
     */
    public OnlyReadFile(String path) {
        //initilaize everything
        indexInDirectory = 0;
        doc_id_generator = 0;
        namesInDirectory = new ArrayList<String>();
        namesOfDocs = new ArrayList<Integer>();
        docsInFile = new ArrayList<String>();
        max_values_dict = new HashMap<Integer, Integer>();
        dictionary_doc_name_id = new HashMap<Integer, String>();
        this.mutexCity = new Mutex();
        this.mutexDoc = new Mutex();
        //we initilize all the pathes
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        File[] directoryListing1;
        File dir1;
        for (int i = 0; i < directoryListing.length; i++) {
            dir1 = new File(directoryListing[i].toString());
            directoryListing1 = dir1.listFiles();
            if(directoryListing1==null)
                continue;
            for (int j = 0; j < directoryListing1.length; j++) {
                namesInDirectory.add(directoryListing1[j].toString());
            }
        }

    }

    /**
     * we find the name of the doc and save it
     * @param content-  the lines of the file
     * @param firstLine- the number of the line of the doc where the doc starts
     * @return- the name of the file
     */
    private String findTheName(ArrayList<String> content, int firstLine){
        boolean found1 = true;
        int i = firstLine;
        while (found1) {
            if (content.get(i).contains("<DOCNO>")) {
                found1 = false;
            }
            ;
            i++;
        }
        String start= "<DOCNO>";
        i=i-1;
        String end= "</DOCNO>";
        String myString=content.get(i);
        String nameOfFile=myString.substring(myString.indexOf(start)+start.length()+1,myString.indexOf(end)-1);
        return  nameOfFile;
    }

    /**
     *  we store the files in an array
     * @param start- the number of the line of the doc where the doc starts
     * @param end- the number of the line of the doc where the doc ends
     * @param content- the lines of the file
     */
    private void storeFiles(int start, int end, ArrayList<String> content){
        String newArrey="";
        for (int i=start;i<end+1;i++) {
            newArrey= newArrey+"\n"+content.get(i);
        }
        // we save the name of the file.
        String name = findTheName(content,start);
        dictionary_doc_name_id.put( doc_id_generator,name);
        doc_id_generator = doc_id_generator+1;
        docsInFile.add(newArrey);
    }

    /**
     * this function will give you the next file until there are no more files
     * @return- the next file in the corpus
     */
    public ArrayList<String> getFile() {
        if (indexInDirectory == namesInDirectory.size()) {
            return null;
        }
        //we initilize the thing that will read the files
        docsInFile = new ArrayList<String>();
        String path = namesInDirectory.get(indexInDirectory);
        File file = new File(path);
        final BufferedReader s;
        String av;
        ArrayList<String> arreyOfFile = new ArrayList<String>();
        try {
            s = new BufferedReader(new FileReader(file));
            while ((av = s.readLine()) != null) {
                arreyOfFile.add(av);
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // we get the data of city and name between the tags.
        String doc = "<DOC>";
        String docClose = "</DOC>";
        for (int i = 0; i < arreyOfFile.size(); i++) {
            if (arreyOfFile.get(i).contains(doc)) {
                int num = i;
                while (false == (arreyOfFile.get(num).contains(docClose))) {
                    num = num + 1;
                }
                storeFiles(i, num, arreyOfFile);
                i=num;
            }

        }
        indexInDirectory++;
        return docsInFile;
    }

    /**
     * @param id- the id of the doc
     * @return- the name of the doc
     */
    public String getNamesOfDocs( int id) {
        String value = "";
        this.mutexDoc.lock();
        if(dictionary_doc_name_id.containsKey(id)){
            value =  dictionary_doc_name_id.remove(id);
            this.mutexDoc.unlock();
            return value;
        }
        this.mutexDoc.unlock();
        return "";
    }
}
