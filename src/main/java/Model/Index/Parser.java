package Model.Index;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;
import java.util.*;

/**
 * This class will receive a string and will parse it.
 * the returned value is DocumentreturnValue type
 */
public class Parser{

    private StopWordsHolder stopWordsHolder;//The stopWordsHolder
    private HashMap<String, Integer> dictionaryOfWords;//the dictionary of regular words
    private HashMap<String, Integer> dictionaryOfUniqueTerms;//the dictionary of the unique words
    private HashMap<String, Integer> monthDictionary;//The dictionary of the months (may - 5 and so on)
    private HashMap<String, Double> weightDictionary;//The dictionary of the weights (kg -1, ton - 1000  and so on)
    private HashSet<String> numberDiscriber;//The number describer HashSet (thousands, trillion and so on)
    private int maxFreq;//The maximum frequency in the doc
    private SnowballStemmer stemmer;//The Stemmer
    private HashSet<Integer> cityLocations;//The locations of the city in the file
    private String cityName;//The name of the city
    private boolean toStem;//True if we need to stem, False otherwise

    /**
     * An empty constructor
     */
    public Parser()
    {

    }

    /**
     * This is the constructor of the class
     * @param pathToStopWordsFile - The path to the stop words file
     * @param cityName - The name of the city
     * @param toStem - TTrue if we need to stem, False otherwise
     */
    public Parser(String pathToStopWordsFile,String cityName,boolean toStem) {
        this.stopWordsHolder = new StopWordsHolder(pathToStopWordsFile+"\\stop_words.txt");
        this.toStem = toStem;
        this.dictionaryOfWords = new HashMap<>();
        this.dictionaryOfUniqueTerms = new HashMap<>();
        this.maxFreq = -1;
        this.initMonths();
        this.initNumberDescriber();
        this.initWeight();
        stemmer = new porterStemmer();
        this.cityLocations = new HashSet<>();
        if(cityName == null)
            this.cityName = "";
        else
        {
            this.cityName = cityName;

        }

    }

    /**
     * This function will initialize the weight dictionary
     */
    private void initWeight()
    {
        this.weightDictionary = new HashMap<>();
        this.weightDictionary.put("gram",0.001);
        this.weightDictionary.put("g",0.001);
        this.weightDictionary.put("grams",0.001);
        this.weightDictionary.put("decagram",0.01);
        this.weightDictionary.put("dag",0.01);
        this.weightDictionary.put("decagrams",0.01);
        this.weightDictionary.put("hectogram",0.1);
        this.weightDictionary.put("hg",0.1);
        this.weightDictionary.put("hectograms",0.1);
        this.weightDictionary.put("kilogram",1.0);
        this.weightDictionary.put("kg",1.0);
        this.weightDictionary.put("kilograms",1.0);
        this.weightDictionary.put("tonne",1000.0);
        this.weightDictionary.put("tonnes",1000.0);
        this.weightDictionary.put("ton",1000.0);
        this.weightDictionary.put("tons",1000.0);
        this.weightDictionary.put("decigram",0.0001);
        this.weightDictionary.put("decigrams",0.0001);
        this.weightDictionary.put("dg",0.0001);
        this.weightDictionary.put("centigram",0.00001);
        this.weightDictionary.put("centigrams",0.00001);
        this.weightDictionary.put("cg",0.00001);
        this.weightDictionary.put("milligram",0.000001);
        this.weightDictionary.put("mg",0.000001);
        this.weightDictionary.put("milligrams",0.000001);



    }

    /**
     * This function will initialize the HashSet of the number describer
     */
    private void initNumberDescriber()
    {
        this.numberDiscriber = new HashSet<>();
        this.numberDiscriber.add("thousand");
        this.numberDiscriber.add("thousands");
        this.numberDiscriber.add("million");
        this.numberDiscriber.add("millions");
        this.numberDiscriber.add("billion");
        this.numberDiscriber.add("billions");
        this.numberDiscriber.add("bn");
        this.numberDiscriber.add("trillion");
        this.numberDiscriber.add("trillions");
        this.numberDiscriber.add("quadrillion");
        this.numberDiscriber.add("quadrillions");
    }

    /**
     * This function will initialize the dictionary of the months
     */
    private void initMonths() {
        this.monthDictionary = new HashMap<>();
        this.monthDictionary.put("jan", 1);
        this.monthDictionary.put("feb", 2);
        this.monthDictionary.put("mar", 3);
        this.monthDictionary.put("apr", 4);
        this.monthDictionary.put("may", 5);
        this.monthDictionary.put("jun", 6);
        this.monthDictionary.put("jul", 7);
        this.monthDictionary.put("aug", 8);
        this.monthDictionary.put("sep", 9);
        this.monthDictionary.put("oct", 10);
        this.monthDictionary.put("nov", 11);
        this.monthDictionary.put("dec", 12);
        this.monthDictionary.put("january", 1);
        this.monthDictionary.put("february", 2);
        this.monthDictionary.put("march", 3);
        this.monthDictionary.put("april", 4);
        this.monthDictionary.put("june", 6);
        this.monthDictionary.put("july", 7);
        this.monthDictionary.put("august", 8);
        this.monthDictionary.put("september", 9);
        this.monthDictionary.put("october", 10);
        this.monthDictionary.put("november", 11);
        this.monthDictionary.put("december", 12);
    }

    /**
     * This function will receive a number term and will parse it
     * For example 134 thousand ~ 134K and so on.
     * @param term - The given term
     * @return - The normalized state of the term
     */
    public String convertNumberToWantedState(String term) {
        try {
            String numToReturn = this.parseNumber(term);
            if (numToReturn.indexOf("/") != -1)
                return numToReturn;
            double num = Double.parseDouble(numToReturn);
            long limit = 1000;
            if (num < limit) {
                return this.curveAroundTheEdges(num);
            }
            limit = limit * 1000;
            if (num < limit) {
                return this.curveAroundTheEdges(1000 * num / limit) + "K";
            }
            limit = limit * 1000;
            if (num < limit) {
                return this.curveAroundTheEdges(1000 * num / limit) + "M";
            }
            limit = limit * 1000;
            return this.curveAroundTheEdges(1000 * num / limit) + "B";

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * This function will scan a word into the dictionary.
     * If needed, the function will parse the stem the word
     * If the word is in small letters, it will get a priority regarding capital letters
     * This means that if the word appears more than once in the doc, in both capital and small letters
     * The fnction will save it as a term with small letters
     * @param word - The given word
     */
    private void wordScan(String word) {
        boolean flag = word.length() > 0 && word.charAt(0) >= 'A' && word.charAt(0) <= 'Z';

        String stemmed;
        //If we want to stem
        if(toStem) {
            stemmer.setCurrent(word.toLowerCase());
            if (stemmer.stem())
                stemmed = stemmer.getCurrent();
            else
                stemmed = word;

            word = stemmed;
        }
        String upperWord = word.toUpperCase();
        String lowerWord = word.toLowerCase();
        int numberOfApp = 0;
        if (flag) {
            if (this.dictionaryOfWords.containsKey(upperWord)) {
                this.dictionaryOfWords.put(upperWord, this.dictionaryOfWords.get(upperWord) + 1);
            } else {
                if (this.dictionaryOfWords.containsKey(lowerWord)) {
                    this.dictionaryOfWords.put(lowerWord, this.dictionaryOfWords.get(lowerWord) + 1);
                } else {
                    this.dictionaryOfWords.put(upperWord, numberOfApp + 1);
                }

            }

        } else {
            if (word.length() > 0 && word.charAt(0) >= 'a' && word.charAt(0) <= 'z') {

                if (this.dictionaryOfWords.containsKey(lowerWord)) {
                    numberOfApp = this.dictionaryOfWords.get(lowerWord);
                } else {
                    if (this.dictionaryOfWords.containsKey(upperWord)) {
                        numberOfApp = this.dictionaryOfWords.get(upperWord);
                        this.dictionaryOfWords.remove(upperWord);
                    }


                }
                this.dictionaryOfWords.put(lowerWord, numberOfApp + 1);


            } else {
                if (this.dictionaryOfWords.containsKey(word)) {
                    numberOfApp = this.dictionaryOfWords.get(lowerWord);
                }
                this.dictionaryOfWords.put(lowerWord, numberOfApp + 1);
            }


        }


    }

    /**
     * This function will scan a list of words
     * @param arrayOfWords - The array of words
     */
    private void scanListOfWords(String[] arrayOfWords) {
        for (int i = 0; i < arrayOfWords.length; i++) {
            this.wordScan(arrayOfWords[i]);
        }

    }


    /**
     *This function will receive a number, will round it and will lose extra '0'
     * @param number - The given number
     * @return - The number without the unnecessary content
     */
    private String curveAroundTheEdges(double number) {
        number = Math.round(number * 100.0) / 100.0;
        String stringToReturn = "" + number;
        int index = stringToReturn.indexOf(".");
        if (index == -1)
            return stringToReturn;
        if (stringToReturn.charAt(stringToReturn.length() - 1) == '0')
            return stringToReturn.substring(0, stringToReturn.length() - 2);

        return stringToReturn;
    }

    /**
     * This function will return if the string represents a double or not
     * @param number - The number
     * @return - True if the String is a double
     */
    private boolean isDouble(String number) {
        try {
            Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * This function will determine the kind of the number
     * For example, 10000 will return 'R', 100 thousand will return K and so on..
     * @param term - The given number term
     * @return - The term
     */
    private char numberKind(String term) {
        if (this.isDouble(term)) {
            return 'R';
        }

        int length = term.length();
        char lowerFirstNote = ("" + term.charAt(length - 1)).toLowerCase().charAt(0);
        if (lowerFirstNote == 'k')
            return 'K';
        if (lowerFirstNote == 'm')
            return 'M';
        if (lowerFirstNote == 'b')
            return 'B';
        if (lowerFirstNote == 't')
            return 'T';
        if (lowerFirstNote == 'q')
            return 'Q';

        String lowerTerm = term.toLowerCase();

        if (length > 2 && (lowerTerm.substring(length - 2).equals("bn") || lowerTerm.substring(length).equals(" bn"))) {
            return 'B';
        }
        if (length > 7) {
            String last7 = lowerTerm.substring(length - 7);
            if (last7.equals("million"))
                return 'M';
            if (last7.equals("billion"))
                return 'B';
            if (length > 8) {
                String last8 = lowerTerm.substring(length - 8);
                if (last8.equals("millions"))
                    return 'M';
                if (last8.equals("billions")) {
                    return 'B';
                }
                if (last8.equals("trillion")) {
                    return 'T';
                }
                if (last8.equals("thousand")) {
                    return 'K';
                }
                if (length > 9) {
                    String last9 = lowerTerm.substring(length - 9);
                    if (last9.equals("trillions"))
                        return 'T';
                    if (last9.equals("thousands"))
                        return 'K';
                    if (length > 11) {
                        String last11 = lowerTerm.substring(length - 11);
                        if (last11.equals("quadrillion"))
                            return 'Q';
                        if (length > 12) {
                            String last12 = lowerTerm.substring(length - 12);
                            if (last12.equals("quadrillions"))
                                return 'Q';

                        }

                    }

                }


            }


        }
        return 'R';


    }

    /**
     * This function will return the number in it's "natual" state.
     * For example, 100K will turn to 100000 ad so on
     * @param number - The term number
     * @param kind - The kind of number
     * @return - The number in it's natual state
     */
    private String irregularCaseHandler(String number, char kind) {
        if (kind == 'R')
            return this.numberWithoutExtraContent(number);
        int multi = 1;
        if (kind == 'M')
            multi = 2;
        if (kind == 'B')
            multi = 3;
        if (kind == 'T')
            multi = 4;
        if (kind == 'Q')
            multi = 5;
        return "" + Math.pow(1000, multi) * Double.parseDouble(this.numberWithoutExtraContent(number));
    }

    /**
     * This function will return the number from the term number
     * For example, 100K will return 100
     * @param number - The given number term
     * @return - the number out of the number term
     */
    private String numberWithoutExtraContent(String number) {
        boolean flag = number.charAt(0) =='-';
        if(flag)
            number = number.substring(1);
        int index = number.indexOf('/');
        int countE = 1;
        int i = 0;
        String prefix = "";
        if (index != -1) {
            prefix = number.substring(0, index);
            number = prefix + number.substring(index + 1);
            i = index;

        }
        String newNumber = "";
        int length = number.length();
        if (length == 0)
            return "";
        char note = number.charAt(0);
        while (i < length - 1 && ((note >= '0' && note <= '9') || note == '.' ||(countE!=0 && note=='E'))) {
            if(note == 'E')
                countE--;
            newNumber += note;
            note = number.charAt(i + 1);
            i++;
        }
        if (i == length - 1 && ((note >= '0' && note <= '9') || note == '.')) {
            newNumber += note;
        }
        if (index != -1) {
            newNumber = prefix + newNumber;
            newNumber = newNumber.substring(0, index) + "/" + newNumber.substring(index);
            return newNumber;
        }
        if(flag)
            newNumber = "-"+newNumber;
        return "" + Double.parseDouble(newNumber);
    }

    /**
     * This function will get a number term and will parse ir
     * @param term - The given term
     * @return - The parsed number in it's "natual" state, 100K will turn to 100000
     */
    private String parseNumber(String term) {
        term = term.replaceAll(",","");
        char kind = this.numberKind(term);
        return this.irregularCaseHandler(term, kind);
    }

    /**
     * This function will return the maximal frequency observed so far in a certain hashmap
     * @param hashMap - The hashMap
     */
    private void maxFrequency(HashMap<String, Integer> hashMap) {
        Set<String> setOfKeys = hashMap.keySet();
        int value = -1;
        for (String key : setOfKeys) {
            value = hashMap.get(key);
            if (this.maxFreq < value) {
                this.maxFreq = value;
            }
        }


    }

    /**
     * This function will add aterm in to a dictionary and will update the necessary data
     * @param key -The given term
     */
    private void addToTermDic(String key) {
        if (this.dictionaryOfUniqueTerms.containsKey(key)) {
            this.dictionaryOfUniqueTerms.put(key, this.dictionaryOfUniqueTerms.get(key) + 1);
            return;

        }
        this.dictionaryOfUniqueTerms.put(key, 1);
    }

    /**
     * This function will parse and return the percentage term
     * @param percentTerm - The percentage term
     * @return -The oarsed ercentage term
     */
    private String percentageNumberParsing(String percentTerm) {
        int length = percentTerm.length();
        String numToReturn = "";
        if (percentTerm.charAt(length - 1) == '%') {
            if (percentTerm.charAt(length - 2) == ' ')
                return this.convertNumberToWantedState(percentTerm.substring(0, length - 2)) + "%";
            return this.convertNumberToWantedState(percentTerm.substring(0, length - 1)) + "%";
        }
        if (percentTerm.substring(length - 8).toLowerCase().equals(" percent"))
            return this.convertNumberToWantedState(percentTerm.substring(0, length - 8)) + "%";
        if (percentTerm.substring(length - 11).toLowerCase().equals(" percentage"))
            return this.convertNumberToWantedState(percentTerm.substring(0, length - 11)) + "%";
        return "";

    }

    /**
     * This function will parse and return the price term
     * @param priceTerm - The given price term
     * @return - The parsed price term
     */
    private String priceNumberParsing(String priceTerm) {
        int length = priceTerm.length();
        String termToDollars = "Dollars";
        String num = "";
        if (length == 0)
            return "";
        if (priceTerm.charAt(0) == '$') {
            num = priceTerm.substring(1);
        } else {
            num = priceTerm.toLowerCase();
            if (length > 13 && priceTerm.substring(length - 12).equals("u.s. dollars"))
                num = num.substring(0, length - 13);
            else {
                if (length > 12 && priceTerm.substring(length - 11).equals("u.s. dollar"))
                    num = num.substring(0, length - 12);
                else {
                    if (length > 8 && priceTerm.substring(length - 7).equals("dollars"))
                        num = num.substring(0, length - 8);
                    else {
                        num = num.substring(0, length - 7);
                    }
                }
            }

        }
        num = this.parseNumber(num);
        if (num.indexOf("/") != -1) {
            return num + " " + termToDollars;
        }
        double num_in_double = Double.parseDouble(num);
        if (num_in_double >= 1000000) {
            return this.curveAroundTheEdges(num_in_double / 1000000) + " M " + termToDollars;
        }
        return this.curveAroundTheEdges(num_in_double) + " " + termToDollars;
    }

    /**
     * This function willr ecieve a string that represents a month and will return the number that the month represent's
     * For example, june will return 6
     * @param nameOfMonth - The term of the month
     * @return - the number that the month represents
     */
    private int monthToNum(String nameOfMonth) {
        nameOfMonth = nameOfMonth.toLowerCase();
        if (this.monthDictionary.containsKey(nameOfMonth))
            return this.monthDictionary.get(nameOfMonth);
        return -1;
    }

    /**
     * This function will check if thestring represents an integer
     * @param number - The Sstring
     * @return - True if the string represents an integer
     */
    private boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * This function will parse return the range ter
     * @param rangeTerm - The given range term
     * @return - The array of parsed terms
     */
    private String[] rangeTermParser(String rangeTerm) {
        int numberOfHyphens = this.countCharInString(rangeTerm, '-');
        String[] arrayToReturn;
        int length = rangeTerm.length();
        if (numberOfHyphens == 2) {
            arrayToReturn = new String[1];
            arrayToReturn[0] = rangeTerm;
            return arrayToReturn;
        }
        String firstHalf = "";
        String secondHalf = "";
        int index = -1;
        if (numberOfHyphens == 0) {
            String endOfEx = rangeTerm.substring(8).toLowerCase();
            length = endOfEx.length();
            index = endOfEx.indexOf(" and");
            if (index == -1) {
                index = endOfEx.indexOf(" to");
                firstHalf = endOfEx.substring(0, index);
                secondHalf = endOfEx.substring(endOfEx.indexOf(" to") + 4);
            } else {
                firstHalf = endOfEx.substring(0, index);
                secondHalf = endOfEx.substring(endOfEx.indexOf(" and") + 5);
            }

        } else {
            if (numberOfHyphens == 1) {
                index = rangeTerm.indexOf('-');
                firstHalf = rangeTerm.substring(0, index);
                secondHalf = rangeTerm.substring(index + 1);
            }
        }
        String tempFirst = this.convertNumberToWantedState(firstHalf);
        String tempSecond = this.convertNumberToWantedState(secondHalf);
        if (!tempFirst.equals("") && !tempSecond.equals("")) {
            arrayToReturn = new String[3];
            arrayToReturn[0] = tempFirst;
            arrayToReturn[1] = tempSecond;
            arrayToReturn[2] = tempFirst + "-" + tempSecond;
            return arrayToReturn;
        }
        if (tempFirst.equals("") && tempSecond.equals("")) {
            arrayToReturn = new String[1];
            arrayToReturn[0] = firstHalf + "-" + secondHalf;
            return arrayToReturn;
        }
        arrayToReturn = new String[2];
        if (tempFirst.equals("")) {
            arrayToReturn[0] = firstHalf + "-" + tempSecond;
            arrayToReturn[1] = tempSecond;
            return arrayToReturn;
        }
        arrayToReturn[0] = tempFirst + "-" + secondHalf;
        arrayToReturn[1] = tempFirst;
        return arrayToReturn;
    }

    /**
     * This function will return the number of tikmes that a character appeared in a string
     * @param str - The given string
     * @param chr - The given character
     * @return - The number of times that the character appeared in the string
     */
    private int countCharInString(String str, char chr) {
        return str.length() - str.replaceAll("" + chr, "").length();
    }

    /**
     * This function will parse and return the givem full-date term
     * @param term - The given full-date term
     * @return - The array of parsed terms
     */
    private String[] fullDate(String term) {
        String[] newTerm;
        String[] stringsToReturn;
        String temp = "";
        if (term.indexOf("/") != -1) {
            newTerm = term.split("/");
            if (!this.isInteger(newTerm[0]) || !this.isInteger(newTerm[1]) || !this.isInteger(newTerm[2])) {
                return null;
            }
            int term0 = Integer.parseInt(newTerm[0]);
            int term1 = Integer.parseInt(newTerm[1]);
            int term2 = Integer.parseInt(newTerm[2]);
            if (term1 > 12) {
                temp = newTerm[1];
                newTerm[1] = newTerm[0];
                newTerm[0] = temp;
            }
            if (newTerm[1].length() == 1) {
                newTerm[1] = "0" + newTerm[1];
            }
            if (newTerm[2].length() > 2) {
                stringsToReturn = new String[3];
                stringsToReturn[2] = this.convertNumberToWantedState(newTerm[2]);
                if (newTerm[2].length() == 3) {
                    newTerm[2] = newTerm[2].substring(1);
                } else {
                    newTerm[2] = newTerm[2].substring(2);
                }

            } else {
                stringsToReturn = new String[2];
            }
            stringsToReturn[0] = newTerm[0] + "-" + newTerm[1] + "-" + newTerm[2];
            stringsToReturn[1] = newTerm[0] + "-" + newTerm[1];
            return stringsToReturn;



        }
        String val = term;
        term = term.toLowerCase();
        term = term.replaceAll("th", "");
        term = term.replaceAll(",", "");
        newTerm = term.split(" ");
        if (newTerm.length < 3)
            return null;
        if (this.isInteger(newTerm[1])) {
            temp = newTerm[1];
            newTerm[1] = newTerm[0];
            newTerm[0] = temp;
        }
        String day = this.date(newTerm[0] + " " + newTerm[1]);
        if (day == null)
            return null;
        String year = this.date(newTerm[1] + " " + newTerm[2]);
        day = day.substring(3);
        String[] yearArray = year.split("-");
        String month = yearArray[1];
        year = yearArray[0];
        if (year.length() > 2) {
            stringsToReturn = new String[3];
            stringsToReturn[2] = this.convertNumberToWantedState(year);
            if (year.length() == 3) {
                year = year.substring(1);
            } else {
                year = year.substring(2);
            }

        } else {
            stringsToReturn = new String[2];
        }
        stringsToReturn[0] = day + "-" + month + "-" + year;
        stringsToReturn[1] = day + "-" + month;
        return stringsToReturn;


    }

    /**
     * This function will parse a date term (8 june)
     * @param term - The given term
     * @return - The parsed term
     */
    private String date(String term) {
        term = term.toLowerCase();
        String[] newTerm = term.split(" ");
        String day = "";
        String month = "";
        if (this.isInteger(newTerm[0])) {
            month = "" + this.monthToNum(newTerm[1]);
            if (month.equals("-1"))
                return null;
            day = newTerm[0];
        } else {
            month = "" + this.monthToNum(newTerm[0]);
            if (month.equals("-1"))
                return null;
            day = newTerm[1];

        }
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (day.length() < 2) {
            day = "0" + day;
        }
        if (Integer.parseInt(day) < 32)
            return month + "-" + day;
        return day + "-" + month;

    }

    /**
     * This function checks if a string represents a number.
     * For example, 1,000 will return True. 10,00,0 will return false
     * @param term - The given term
     * @return - True if the term is a number
     */
    private boolean isNumber(String term) {
        if(!this.isDouble(term.replaceAll(",","")))
            return false;
        int index = term.indexOf('.');
        if (index == term.length() - 1)
            return false;
        String temp = term;
        if (index != -1) {
            temp = temp.substring(index);
        }
        List<Integer> indices = this.allLocationsOfSubstringinString(temp,",");
        int length = indices.size();
        if (length>0)
        {
            if (indices.get(length-1) != temp.length() -4)
                return false;
            if (indices.get(0)<1 || indices.get(0)>3)
                return false;
            for(int i=0; i<length-1;i++)
            {
                if(indices.get(i+1)-indices.get(i)!=4)
                    return false;
            }
        }

        return true;

    }

    /**
     * This function will return all the locations of a substyring in a string
     * @param str - The string
     * @param substr - the substring
     * @return - A list of locations of the substring in the string
     */
    private List<Integer> allLocationsOfSubstringinString(String str,String substr)
    {
        List<Integer> pos= new ArrayList<>();
        int index = str.indexOf(substr);
        int length= 0;
        while (index!=-1)
        {
            pos.add(index+length);
            str=str.substring(index+1);
            length=length+index+1;
            index = str.indexOf(substr);
        }
        return pos;
    }

    /**
     * This function will combine an array and a dictionary
     * The function will add all the terms in the list into the dictionary
     * @param stringList - The given array
     */
    private void combineListAndDictionary(String [] stringList)
    {
        for(int i=0;i<stringList.length;i++)
        {

            this.addToTermDic(stringList[i]);
        }
    }

    /**
     * This function will return if the number is a number term
     * For example True - 100K, 100000M 100. False - 123 Thousand
     * @param term
     * @return
     */
    private boolean isNumberTerm(String term)
    {
        int length = term.length();
        if (length == 0)
            return false;
        term = term.toLowerCase();
        if(this.isNumber(term))
            return true;
        char note = term.charAt(length-1);
        if (note=='k' ||note=='m' ||note=='b' ||note=='t' ||note=='q')
            return this.isNumber(term.substring(0,length-1));
        if(length<2)
            return false;
        if(term.substring(length-2).equals("bn"))
            return this.isNumber(term.substring(0,length-2));
        return this.isFraction(term);
    }

    /**
     * This function will return if a string represents a fraction
     * @param term - The String
     * @return - True if this string represents a fraction
     */
    private boolean isFraction(String term)
    {
        int index = term.indexOf('/');
        if(index == -1)
            return false;
        return this.isDouble(term.substring(0,index)) && this.isDouble(term.substring(index+1));
    }

    /**
     * This function will return true for the numbers that end with 'th'.
     * True - 65th 1th and so on
     * @param number - The string
     * @return - True if the string is an integer that ends with 'th'
     */
    private boolean isIntegerThatEndsWithTh(String number)
    {
        int length =number.length();
        if(length<=2)
            return false;
        return number.toLowerCase().substring(length-2).equals("th") && this.isInteger(number.substring(0,length-2));
    }

    /**
     * This function will return True if the term is a number describer
     * @param term - The term
     * @return - true if the term is a a number describer (million, trllions and so on)
     */
    private boolean isNumberDescriber(String term)
    {
        return this.numberDiscriber.contains(term.toLowerCase());
    }

    private boolean isDate(String term)
    {
        int numOfSlashes = this.countCharInString(term,'/');
        if(numOfSlashes!=2)
            return false;
        int index = term.indexOf('/');
        String first = term.substring(0,index);
        int index2 = term.lastIndexOf('/');
        String second = term.substring(index+1,index2);
        String third = term.substring(index2+1);

        if (!(this.isInteger(first)&& this.isInteger(second) && this.isInteger(third)))
            return false;
        int firstInt = Integer.parseInt(first);
        int secondInt = Integer.parseInt(second);
        if(!(firstInt >= 1 && secondInt >= 1))
        {
            return false;
        }
        return (firstInt<=31 && secondInt<=12) || (secondInt<=31 && secondInt<=12);

    }

    /**
     * This function will find the location of the first letter in a string.
     * We assume that the string has letters only in the end of it
     * For example, "1234 kg" will return 5
     * @param weight - The weight term
     * @return - The location of the letter in a string
     */
    private int findTheLetter(String weight)
    {

        weight = weight.toLowerCase();
        int length = weight.length();
        char tav = ' ';

        for (int i=length-1;i>=0;i--)
        {
            tav = weight.charAt(i);
            if (!(tav>='a' && tav<='z'))
                return length - i-1;
        }
        return 0;

    }

    /**
     * This function will return the number we need to multiply a number to get to Kg
     * tone - 0.001, gram - 1000
     * @param term - The given measurement unit.
     * @return - the number we need to multiply a number to get to Kg
     */
    private double howMuchToMultForKg(String term)
    {
        term = term.toLowerCase();
        Object o = this.weightDictionary.get(term);
        if(o!=null)
            return (double)o;
        return 0;
    }

    /**
     * Tihs function will return if a term is a weight measurement
     * @param term - The given term
     * @return - True if the term is a weight measurement
     */
    private boolean isWeightMeasurement(String term)
    {
        term = term.toLowerCase();
        return this.weightDictionary.containsKey(term);
    }

    /**
     * This function will parse weight term
     * @param weightTerm - The weight term
     * @return - The parsed weight term
     */
    private String convertToKg(String weightTerm)
    {

        weightTerm = this.convertToDigitNum(weightTerm);
        int length = weightTerm.length();
        int place =this.findTheLetter(weightTerm); // number of letters
        String number = weightTerm.substring(0,length-place);
        String measureUnit = weightTerm.substring(length-place);
        if (number.length() == 0)
        {
            return measureUnit;
        }
        double multiBy = this.howMuchToMultForKg(measureUnit);
        double numberAsDouble = Double.parseDouble(number);
        numberAsDouble = multiBy*numberAsDouble;
        return this.convertNumberToWantedState(""+convertNumberToWantedState(""+numberAsDouble))+"kg";




    }

    /**
     * This function will take terms like 100K Kg and will return 100000Kg
     * the function will parse the number and will return the number and the measurement unit without spaces
     * @param term - the weight term
     * @return - The weight term
     */
    private String convertToDigitNum(String term)
    {
        String [] array = term.split(" ");
        if(array.length==3 && this.isFraction(array[1]))
        {
            return this.fractionToNumber(array[0],array[1])+array[2];
        }
        if(array.length==3)
        {
            return this.parseNumber(array[0] + " " + array[1]) + array[2];
        }
        if(array.length==2)
        {
            return this.parseNumber(array[0]) + array[1];
        }
        return term;

    }

    /**
     * This function will take a fraction like 1 6/7 and will return it like 1.67
     * @param integer - The integer of the fraction
     * @param fraction - The actual fraction
     * @return - The calculated fraction
     */
    private String fractionToNumber(String integer, String fraction)
    {
        int index = fraction.indexOf("/");
        return this.curveAroundTheEdges(Double.parseDouble(integer) + Double.parseDouble(fraction.substring(0,index))/Double.parseDouble(fraction.substring(index+1)));
    }

    /**
     * This function will teake a text and will pasre every token in the text.
     * @param text - The given text
     * @return - Model.Index.DocumentReturnValue object containing the parsed text and it's characteristics
     */
    public DocumentReturnValue motherOfAllFunctions(String text)
    {
        this.cityLocations = new HashSet<>();
        this.dictionaryOfUniqueTerms = new HashMap<>();
        this.dictionaryOfWords = new HashMap<>();
        this.maxFreq = -1;
        int index =-1;
        int counter = -1;
        String currentTerm = "";
        String nextTerm ="";
        boolean moreWords = index!=-1;
        int countSave = -1;
        String lower = "";
        //text = text after punctuations removal
        text =text.replaceAll("\\.\\.+|--+", ". ");
        text = text.replaceAll("[\\.,][ \n\t\"]|[\\|\"+&^:\t*!\\\\@#=`~;)(?><}{_\\[\\]]", " ");
        text = text.replaceAll("n't|'(s|t|mon|d|ll|m|ve|re)", "");
        text = text.replaceAll("'","");
        String [] tokenArray = text.split("\n|\\s+");

        int lengthOfTokens = tokenArray.length;
        int count = 0;// Number of tokens scanned
        int length =-1;
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        int hyphenIndex = -1;
        String temp="";
        int numTemp=0;
        String day = "";
        int numberOfHyphens = -1;
        String [] words;
        while(true)
        {
            if(count == lengthOfTokens)
                break;
            currentTerm = tokenArray[count];

            count++;
            length = currentTerm.length();
            if(length ==0 || (length==1 && !(currentTerm.charAt(0)>='0' && currentTerm.charAt(0)<='9'))) {
                if (count == lengthOfTokens)
                    break;
                continue;
            }
            if(currentTerm.charAt(currentTerm.length()-1)=='.') {
                currentTerm = currentTerm.substring(0, currentTerm.length() - 1);
                if (currentTerm.length() == 0) {
                    if (count == lengthOfTokens)
                        break;
                    continue;
                }
            }


            if(currentTerm.charAt(0)=='.' ) {
                currentTerm = currentTerm.substring(1);
                if (currentTerm.length() == 0) {
                    if (count == lengthOfTokens)
                        break;
                    continue;
                }
            }
            if(currentTerm.length()==0 || (currentTerm.length()==1 && !(currentTerm.charAt(0)>='0' && currentTerm.charAt(0)<='9')))
            {
                if (count == lengthOfTokens)
                    break;
                continue;
            }

            if(this.stopWordsHolder.isStopWord(currentTerm))
                continue;
            counter++;
            if(cityName.toLowerCase().equals(currentTerm.toLowerCase()))
                this.cityLocations.add(counter);
            if(this.isIntegerThatEndsWithTh(currentTerm))
                currentTerm = currentTerm.substring(0,length-2);
            if(this.isNumberTerm(currentTerm))
            {
                if(count == lengthOfTokens) {
                    this.addToTermDic(this.convertNumberToWantedState(currentTerm));
                    break;
                }
                if(this.isFraction(currentTerm))
                {
                    count--;
                    currentTerm = "0";
                }


                nextTerm = tokenArray[count];
                count++;

                if(this.isInteger(currentTerm))
                {
                    if(Math.abs(Integer.parseInt(currentTerm))<1000)
                    {
                        if(nextTerm.length()!=0 && (this.isFraction(nextTerm) || (nextTerm.charAt(nextTerm.length()-1) =='%' && this.isFraction(nextTerm.substring(0,nextTerm.length()-1)))))
                        {
                            flag = nextTerm.charAt(nextTerm.length()-1) == '%';
                            currentTerm =currentTerm+" "+nextTerm;

                            if(count == lengthOfTokens)
                            {
                                if(flag)
                                    this.addToTermDic(this.percentageNumberParsing(currentTerm));
                                else
                                    this.addToTermDic(this.convertNumberToWantedState(currentTerm));
                                break;
                            }

                            nextTerm = tokenArray[count];
                            count++;

                            lower = nextTerm.toLowerCase();

                            if(lower.equals("dollars")||lower.equals("dollar"))
                            {
                                currentTerm =currentTerm+" "+nextTerm;
                                this.addToTermDic(this.priceNumberParsing(currentTerm));
                                if(count == lengthOfTokens)
                                    break;
                                continue;
                            }
                            if(lower.equals("percent")||lower.equals("percentage") || lower.equals("%"))
                            {
                                currentTerm = currentTerm+" "+nextTerm;
                                this.addToTermDic(this.percentageNumberParsing(currentTerm));
                                if(count == lengthOfTokens)
                                    break;
                                continue;
                            }
                            if(this.isWeightMeasurement(lower))
                            {
                                currentTerm = currentTerm+" " +nextTerm;
                                this.addToTermDic(this.convertToKg(currentTerm));
                                if(count == lengthOfTokens)
                                    break;
                                continue;
                            }

                            count--;
                            this.addToTermDic(this.convertNumberToWantedState(currentTerm));
                            continue;
                        }

                        if(Integer.parseInt(currentTerm)<=31 &&Integer.parseInt(currentTerm)>=1)
                        {
                            if(this.monthToNum(nextTerm)!=-1)
                            {
                                currentTerm = currentTerm+" " +nextTerm;
                                if(count==lengthOfTokens)
                                {
                                    this.addToTermDic(this.date(currentTerm));
                                    break;
                                }
                                nextTerm = tokenArray[count];
                                count++;

                                if(this.isInteger(nextTerm) && Integer.parseInt(nextTerm)<=2500)
                                {
                                    currentTerm = currentTerm +" " + nextTerm;
                                    this.combineListAndDictionary(this.fullDate(currentTerm));
                                    if(count == lengthOfTokens)
                                        break;
                                    continue;
                                }
                                count--;
                                this.addToTermDic(this.date(currentTerm));
                                continue;
                            }
                        }
                    }



                }
                lower = nextTerm.toLowerCase();
                flag = false;
                temp =currentTerm;
                if(this.isNumberDescriber(lower))
                {
                    currentTerm =currentTerm+" "+nextTerm;
                    if(count!=lengthOfTokens) {
                        nextTerm = tokenArray[count];
                        lower = nextTerm.toLowerCase();
                        count++;
                        flag = true;
                    }

                }
                if(lower.equals("dollars")||lower.equals("dollar"))
                {
                    currentTerm =currentTerm+" "+nextTerm;
                    this.addToTermDic(this.priceNumberParsing(currentTerm));
                    if(count == lengthOfTokens)
                        break;
                    continue;
                }
                if(lower.equals("percent")||lower.equals("percentage") || lower.equals("%"))
                {
                    currentTerm = currentTerm+" "+nextTerm;
                    this.addToTermDic(this.percentageNumberParsing(currentTerm));
                    if(count == lengthOfTokens)
                        break;
                    continue;
                }
                if(this.isWeightMeasurement(lower))
                {
                    currentTerm = currentTerm+" " +nextTerm;
                    this.addToTermDic(this.convertToKg(currentTerm));
                    if(count == lengthOfTokens)
                        break;
                    continue;
                }
                if(flag)
                {
                    count--;
                    nextTerm = tokenArray[count];
                    lower = nextTerm.toLowerCase();
                }
                currentTerm = temp;
                hyphenIndex = nextTerm.indexOf('-');
                flag = hyphenIndex!=-1 && this.isNumberDescriber(nextTerm.substring(0,hyphenIndex));
                flag1 =this.isNumberDescriber(nextTerm);
                if(flag1 || flag)
                {
                    currentTerm = currentTerm+" "+nextTerm;
                }

                if(flag)
                {
                    if(this.isDouble(nextTerm.substring(hyphenIndex+1)))
                    {
                        if(count == lengthOfTokens)
                        {
                            this.combineListAndDictionary(this.rangeTermParser(currentTerm));
                            break;
                        }

                        nextTerm = tokenArray[count];
                        count++;

                        if(this.isNumberDescriber(nextTerm)|| this.isFraction(nextTerm))
                        {
                            currentTerm = currentTerm+" " + nextTerm;
                            this.combineListAndDictionary(this.rangeTermParser(currentTerm));
                            if(count == lengthOfTokens)
                                break;
                            continue;

                        }
                        this.combineListAndDictionary(this.rangeTermParser(currentTerm));
                        count--;
                        continue;

                    }
                    else
                    {
                        this.combineListAndDictionary(this.rangeTermParser(currentTerm));
                        if(count == lengthOfTokens)
                            break;
                        continue;
                    }


                }
                else
                {
                    if(count == lengthOfTokens)
                    {
                        this.addToTermDic(this.convertNumberToWantedState(currentTerm));
                        break;
                    }
                    if (flag1) {
                        nextTerm = tokenArray[count];
                        count++;
                    }

                    lower = nextTerm.toLowerCase();
                    if(lower.equals("dollars")||lower.equals("dollar"))
                    {
                        currentTerm =currentTerm+" "+nextTerm;
                        this.addToTermDic(this.priceNumberParsing(currentTerm));
                        if(count == lengthOfTokens)
                            break;
                        continue;
                    }
                    if(lower.equals("percent")||lower.equals("percentage") || lower.equals("%"))
                    {
                        currentTerm = currentTerm+" "+nextTerm;
                        this.addToTermDic(this.percentageNumberParsing(currentTerm));
                        if(count == lengthOfTokens)
                            break;
                        continue;
                    }
                    if(this.isWeightMeasurement(lower))
                    {
                        currentTerm = currentTerm+" " +nextTerm;
                        this.addToTermDic(this.convertToKg(currentTerm));
                        if(count == lengthOfTokens)
                            break;
                        continue;
                    }

                    if(lower.replace(".","").equals("us"))
                    {

                        if(count == lengthOfTokens)
                        {
                            this.addToTermDic(this.convertNumberToWantedState(currentTerm));
                            this.wordScan(nextTerm);
                            break;
                        }
                        nextTerm = tokenArray[count];
                        count++;
                        lower = nextTerm.toLowerCase();
                        if(lower.equals("dollars")||lower.equals("dollar"))
                        {
                            currentTerm =currentTerm+" u.s. dollars";
                            this.addToTermDic(this.priceNumberParsing(currentTerm));
                            if(count == lengthOfTokens)
                                break;
                            continue;
                        }
                        this.addToTermDic(this.convertNumberToWantedState(currentTerm));
                        count--;
                        continue;
                    }
                    count--;
                    this.addToTermDic(this.convertNumberToWantedState(currentTerm));
                    continue;









                }





            }

            if(currentTerm.charAt(currentTerm.length()-1) == '%'&& isInteger(currentTerm.substring(0,currentTerm.length()-1))) {
                this.addToTermDic(currentTerm);
                if(count == lengthOfTokens)
                    break;
                continue;
            }
            if(currentTerm.charAt(0)=='$')
            {
                if(this.isNumberTerm(currentTerm.substring(1)))
                {
                    currentTerm = currentTerm.substring(1);
                    //Ends with k,m,b,t,q
                    if(!this.isDouble(currentTerm))
                    {
                        this.addToTermDic(this.priceNumberParsing("$"+currentTerm));
                        if(count == lengthOfTokens)
                            break;
                        continue;
                    }

                    if(count == lengthOfTokens)
                    {
                        this.addToTermDic(this.priceNumberParsing("$"+currentTerm));
                        break;
                    }

                    nextTerm = tokenArray[count];
                    count++;

                    if(this.isNumberDescriber(nextTerm))
                    {
                        currentTerm = currentTerm + " " +nextTerm;
                    }

                    this.addToTermDic(this.priceNumberParsing("$"+currentTerm));
                    if(count == lengthOfTokens)
                        break;
                    continue;

                }
            }

            if(currentTerm.toLowerCase().equals("between"))
            {
                numTemp = 0;
                if(count == lengthOfTokens)
                {
                    break;
                }

                nextTerm = tokenArray[count];
                count++;
                temp = currentTerm;

                if(!this.isNumberTerm(nextTerm) || (count == lengthOfTokens))
                {
                    if(count == lengthOfTokens)
                    {
                        count--;
                    }
                    continue;
                }

                temp = temp +" "+ nextTerm;
                nextTerm = tokenArray[count];
                count++;


                if(this.isNumberDescriber(nextTerm))
                {
                    temp = temp +" "+ nextTerm;
                    nextTerm = tokenArray[count];
                    count++;
                    numTemp++;

                }

                if((!(nextTerm.toLowerCase().equals("and") || nextTerm.toLowerCase().equals("to")))|| count == lengthOfTokens)
                {
                    count = count-2-numTemp;
                    continue;
                }

                temp = temp +" "+ nextTerm;
                nextTerm = tokenArray[count];
                count++;


                if(! this.isNumberTerm(nextTerm))
                {
                    count = count-3;
                    continue;
                }
                temp = temp +" "+ nextTerm;
                if(count!=lengthOfTokens)
                {

                    nextTerm = tokenArray[count];
                    count++;

                    if(this.isNumberDescriber(nextTerm))
                    {
                        temp = temp +" "+ nextTerm;
                    }
                    else
                    {
                        count--;
                    }

                }
                currentTerm = temp;
                this.combineListAndDictionary(this.rangeTermParser(currentTerm));
                if(count == lengthOfTokens)
                    break;
                continue;




            }

            if(this.monthToNum(currentTerm)!=-1)
            {
                if(count==lengthOfTokens)
                {
                    if(!currentTerm.toLowerCase().equals("may"))
                        this.wordScan(currentTerm);
                    break;
                }
                nextTerm = tokenArray[count];
                count++;

                flag1 = this.isInteger(nextTerm);
                flag2 = this.isIntegerThatEndsWithTh(nextTerm);
                flag3 = nextTerm.length()!=0 && (this.isIntegerThatEndsWithTh(nextTerm.substring(0,nextTerm.length()-1))) && nextTerm.charAt(nextTerm.length()-1)==',';
                flag4 = nextTerm.length()!=0 && this.isInteger(nextTerm.substring(0,nextTerm.length()-1)) && nextTerm.charAt(nextTerm.length()-1)==',';
                if(flag1||flag2||flag3||flag4){
                    day = nextTerm;
                    flag = true;

                    if(flag2)
                    {
                        day = nextTerm.substring(0,nextTerm.length()-2);
                    }
                    if(flag3)
                    {
                        day = nextTerm.substring(0,nextTerm.length()-3);
                    }
                    if(flag4)
                    {
                        day = nextTerm.substring(0,nextTerm.length()-1);
                    }
                    flag = Integer.parseInt(day)<=31 && Integer.parseInt(day)>=1;
                    currentTerm = currentTerm + " " + day;
                    if(!flag || count == lengthOfTokens)
                    {
                        this.addToTermDic(this.date(currentTerm));
                        if(count == lengthOfTokens)
                            break;
                        continue;
                    }

                    nextTerm = tokenArray[count];
                    count++;

                    if(!this.isInteger(nextTerm))
                    {
                        count--;
                        this.addToTermDic(this.date(currentTerm));
                        continue;
                    }

                    currentTerm = currentTerm + " " + nextTerm;
                    this.combineListAndDictionary(this.fullDate(currentTerm));
                    if(count == lengthOfTokens)
                        break;
                    continue;

                }


            }

            if(this.isDate(currentTerm))
            {
                this.combineListAndDictionary(this.fullDate(currentTerm));
                if(count == lengthOfTokens)
                    break;
                continue;
            }
            if(this.isWordNumber(currentTerm))
            {
                if(count == lengthOfTokens)
                {
                    this.combineListAndDictionary(this.rangeTermParser(currentTerm));
                    break;
                }
                nextTerm = tokenArray[count];
                count++;

                if(this.isNumberDescriber(nextTerm) || this.isFraction(nextTerm))
                {
                    currentTerm = currentTerm + " " + nextTerm;
                    this.combineListAndDictionary(this.rangeTermParser(currentTerm));
                    if(count == lengthOfTokens)
                        break;
                    continue;
                }
                count--;

            }
            if(this.isWordWord(currentTerm)||this.isWordWordWord(currentTerm)) {
                this.combineListAndDictionary(this.rangeTermParser(currentTerm));
                if(count == lengthOfTokens)
                    break;
                continue;
            }
            //If word
            if(currentTerm.toLowerCase().equals("may")) {
                counter --;
                continue;
            }
            words = currentTerm.split("-|/");
            counter = counter-1+words.length;
            this.scanListOfWords(words);
            if(count == lengthOfTokens)
                break;
        }
        this.maxFrequency(this.dictionaryOfUniqueTerms);
        this.maxFrequency(this.dictionaryOfWords);
        int maxf = this.maxFreq;
        return new DocumentReturnValue(this.dictionaryOfWords,this.dictionaryOfUniqueTerms,maxf,this.cityLocations,counter);
    }

    /**
     * This function will return true if the term is a word-number term
     * A number term is a term that starts with a word, has one hyphen and then a number
     * Examples, ham-34
     * @param term - The given term
     * @return - true if the term is a number term
     */
    private boolean isWordNumber(String term)
    {
        int numberOfHyphens = this.countCharInString(term,'-');
        if(numberOfHyphens!=1)
        {
            return false;
        }
        String second = term.substring(term.indexOf('-')+1);
        if(second.length()==0)
            return false;
        return this.isNumberTerm(second);
    }

    /**
     * This function will return true if the term is a word-word term
     * A number term is a term that starts with a word, has one hyphen and then a word
     * For example, ham-pizza
     * @param term - The given term
     * @return - True if the term is a word-word term
     */
    private boolean isWordWord(String term)
    {
        int numberOfHyphens = this.countCharInString(term,'-');
        if(numberOfHyphens!=1)
        {
            return false;
        }
        String second = term.substring(term.indexOf('-')+1);
        return second.length()!=0;
    }

    /**
     * This function will return true if the term is a word-word-word term
     * A number term is a term that starts with a word, has one hyphen and then a word and than another hyphen and then another word
     * For example, ham-pizza-building
     * @param term - The given term
     * @return - True if the term is a word-word term
     */
    private boolean isWordWordWord(String term)
    {
        int numberOfHyphens = this.countCharInString(term,'-');
        if(numberOfHyphens!=2)
        {
            return false;
        }
        String second = term.substring(term.indexOf('-')+1);
        if(second.length()==1)
            return false;
        String third = term.substring(term.lastIndexOf('-')+1);
        return third.length()!=0;
    }


}


