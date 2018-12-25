package Model.Index;

import sun.awt.Mutex;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * This class is the city indexer.
 * This class will update the data on the cities and will put it in posting files
 */
public class PostingOfCities implements Callable<Boolean>{
    private String postingPath;// the path of the file that we save data in
    private String urlSTR;
    private int counter;
    private ArrayList<String> nameOfCitys;// a list of the names of the citys
    private HashMap<String, Integer> placesInDoc;// the dictionary. you give it a doc and it gives you the place of the char in the file that starts the data of a doc
    private HashMap<String, String[]> DetailsOnCitys_web;// the details on the citys
    private HashMap<String,HashMap<Integer,HashSet<Integer>>> DetailsOnCitys_doc;// the details from the docs
    private Mutex mutex;

    /**
     * this is the constructor.
     * it should initialaize the parameters and create a file that will save our data
     * @param location- The location of the posting file that we will create
     * @param stem - True if we stemmed the terms. False- otherwise
     */
    public PostingOfCities(String location,boolean stem) {
        this.mutex = new Mutex();
        //initilaizes the parameters
        placesInDoc = new HashMap<String, Integer>();
        counter = 0;
        DetailsOnCitys_web = new HashMap<>();
        DetailsOnCitys_doc = new HashMap<>();
        nameOfCitys = new ArrayList<>();
        urlSTR = "http://getcitydetails.geobytes.com/GetCityDetails?fqcn=";
        //creating the file that we will write to
        try {
            PrintWriter postingListOfFile = new PrintWriter(location+"\\"+"citys"+"&"+stem+".txt", "UTF-8");
            postingListOfFile.close();
            File file = new File(location+"\\"+"citys"+"&"+stem+".txt");
            postingPath = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * we write the data that we saved in local to the file.
     * @return - True is uploaded successfully
     */
    public boolean uploadToFile() {
        File file = new File(postingPath);
        //initialize the thing that will write to the file
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String str;
            int size = nameOfCitys.size();
            String cityName;
            String detailsWeb;
            String detailsDoc = "";
            String toWrite;
            StringBuilder stringBuilder=new StringBuilder();
            // we write to file all the data. new city is delimitered by #, new doc with the places dell by - and all the athors by ;
            for (int i = 0; i < size; i++) {
                detailsDoc="";
                cityName = nameOfCitys.get(i);
                detailsWeb = cityName+ "@";
                //System.out.printf(cityName);
                //data from the web
                if(this.DetailsOnCitys_web.containsKey(cityName)) {
                    detailsWeb = detailsWeb  + DetailsOnCitys_web.get(cityName)[0] + "@" + DetailsOnCitys_web.get(cityName)[1] + "@" + DetailsOnCitys_web.get(cityName)[2];
                    DetailsOnCitys_web.remove(cityName);
                }


                // data of the docs
                Set<Integer> keysIn;
                Set<Integer> keysInIn;
                keysIn = this.DetailsOnCitys_doc.get(cityName).keySet();
                //Foreach doc that city appeared in
                for(int docNumber:keysIn)
                {
                    detailsDoc = detailsDoc+"&"+docNumber;
                    keysInIn = this.DetailsOnCitys_doc.get(cityName).get(docNumber);
                    //For each location of the city in the file
                    for(int loc:keysInIn)
                    {
                        detailsDoc = detailsDoc+";"+loc;
                    }
                }

                // we write the doc data to the file
                DetailsOnCitys_doc.remove(cityName);
                toWrite = detailsWeb + detailsDoc;
                stringBuilder.append(toWrite+"\n");
                toWrite = detailsWeb + detailsDoc;
                stringBuilder.append(toWrite);
                // we update the dictionary of where is the data
                placesInDoc.put(cityName, counter);
                counter = counter + toWrite.length();
            }
            out.print(stringBuilder);
            out.close();
            return true;
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            return false;
        }
    }

    /**
     * we take the data from the city
     * @param info- the string that that shout contain data on the city
     * @param arrOfWeb- array that we write the data to
     * @return- the array after we wrote the data to him
     */
    public String[] PutInfo(String info, String[] arrOfWeb) {
        arrOfWeb[0] = "nv";
        arrOfWeb[1] = "nv";
        arrOfWeb[2] = "nv";
        String sub;
        //we get the data on the contry
        if (info.contains("geobytescountry")) {
            int place = info.indexOf("geobytescountry") + 18;
            sub = info.substring(place, info.length());
            int geresh = sub.indexOf('"');
            if (geresh > 0) {
                arrOfWeb[0] = sub.substring(0, geresh);
            }

        }
        //we get the data on the population
        if (info.contains("geobytespopulation")) {
            int place = info.indexOf("geobytespopulation") + 21;
            sub = info.substring(place, info.length());
            int geresh = sub.indexOf('"');
            if (geresh > 0) {
                Parser parser = new Parser();
                String toSave = parser.convertNumberToWantedState(sub.substring(0, geresh));
                arrOfWeb[1] = toSave;
            }
        }
        //we get the data on the currency code
        if (info.contains("geobytescurrencycode")) {
            int place = info.indexOf("geobytescurrencycode") + 23;
            sub = info.substring(place, info.length());
            int geresh = sub.indexOf('"');
            if (geresh > 0) {
                arrOfWeb[2] = sub.substring(0, geresh);
            }
        }
        return arrOfWeb;
    }

    /**\
     * this function will save the data on the city.
     * the data of the positions in the term and the data that we got from the internet
     * @param city- the city to be saved
     * @param doc- the doc that the city is in
     * @param sity- this is the set of positions in the doc
     * @return- the name of the city
     */
    public String addCity(String city, int doc,HashSet sity){
        this.mutex.lock();
        //we get the list of arrays that we need to append the arrey to
        HashMap<Integer,HashSet<Integer>> cityDoc;
        if (!DetailsOnCitys_doc.containsKey(city)) {
            //we add to the dict
            nameOfCitys.add(city);
            cityDoc = new HashMap<>();
            DetailsOnCitys_doc.put(city, cityDoc);


        } else {
            cityDoc = DetailsOnCitys_doc.get(city);
        }
        // we add the array to list of arreys
        HashSet<Integer> tempArr1 = sity;
        cityDoc.put(doc,sity);
        //we get the data from the internet
        if (!DetailsOnCitys_web.containsKey(city)) {
            URL url = null;
            try {
                url = new URL(urlSTR + city);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            URLConnection conection = null;

            try {
                conection = url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream inputstream = null;

            try {
                inputstream = conection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<String> arr = new ArrayList<String>();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
            //we take all the data from the internet

            String str = null;
            try {
                str = br.readLine();

                while (str != null) {
                    arr.add(str);
                    str = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            String[] arrOfWeb = new String[3];
            String[] tempArr = new String[3];
            arrOfWeb[0] = "nv";
            arrOfWeb[1] = "nv";
            arrOfWeb[2] = "nv";
            for (int i = 0; i < arr.size(); i++) {
                tempArr = PutInfo(arr.get(i), arrOfWeb);
                if (arrOfWeb[0].equals("nv")) {
                    arrOfWeb[0] = tempArr[0];
                }
                if (arrOfWeb[1].equals("nv")) {
                    arrOfWeb[1] = tempArr[1];
                }
                if (arrOfWeb[2].equals("nv")) {
                    arrOfWeb[2] = tempArr[2];
                }
            }
            DetailsOnCitys_web.put(city, arrOfWeb);
        }
        this.mutex.unlock();
        return city;
    }


    @Override
    public Boolean call() throws Exception {
        return this.uploadToFile();
    }
}