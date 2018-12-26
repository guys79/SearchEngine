package View;


import Controller.IndexController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class is the class that is responsible on the view of the GUI
 * This class has access to the fxml file and declares as the controller in the fxml file
 */
public class IndexView implements Initializable {
    public Button postingFilePath;//The button that will browse and get the path of the Posting file
    public Button corpusFilePath;//The button that will browse and get the path of the corpus file
    public Button startBtn;//This button is responsible for the start of the indexing
    public Button resetBtn;//This button is responsible to reset the indexing. Delete the posting files and the dictionary
    public Button loadDictionaryButton;//The button that is responsible to load the dictionary
    public CheckBox stemCheckBox;//If checked, we will stem the terms
    public ComboBox languagesComboBox;//The combo box that will display the languages
    public Text totalTimeText;//The text that will display the total amount of time it took to index all the corpus
    public Text documentQuantityText;//The text that will display the the number of documents
    public Text termQuantityText;//The text that will display the number of terms in the corpus
    public TableView dictionaryTableView;//The table view
    public TableColumn termTableColumn;//The column of the terms
    public TableColumn cfTableColumn;//The column of the cf
    private String corpusPath;//The path to the corpus
    private String postingPath;//The path to the posting
    private IndexController controller;//The controller


    /**
     * This function will start the indexing
     */
    public void start()
    {
        //Start time
        long start = System.nanoTime();
        //Disable the start button
        this.startBtn.setDisable(true);
        //Start indexing
        this.controller.start();
        //Stop time
        long elapsedTime = System.nanoTime() - start;

        //Writ results
        String totalTime =  (elapsedTime / 1000000000) / 60 + " minutes and " + (elapsedTime / 1000000000) % 60 + " seconds";
        String numOfDocument=""+controller.getNumOfDocuments();
        String numOfTerms=""+controller.getNumOfTerms();
        this.totalTimeText.setText(totalTime);
        this.documentQuantityText.setText(numOfDocument);
        this.termQuantityText.setText(numOfTerms);

        //Return the state to the start state
        this.corpusFilePath.setStyle("-fx-background-color: #000000;");
        this.postingFilePath.setStyle("-fx-background-color: #000000;");
        this.loadDictionaryButton.setStyle("-fx-background-color: #000000;");
        this.corpusPath = "";
        this.postingPath="";
        checkStart();
        checkLoad();
        this.languageDisplay();

    }

    /**
     * This function will reset the indexing.
     * This function will delete all of the files and will erase the dictionary
     */
    public void reset()
    {
        this.controller.reset();
    }

    /**
     * The initial of this class
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stemCheckBox.setSelected(true);
        this.startBtn.setDisable(true);
        this.corpusPath = "";
        this.postingPath ="";
        this.controller=new IndexController();
        controller.setView(this);
        termTableColumn.setCellValueFactory(new PropertyValueFactory<TableContent, String>("term"));
        cfTableColumn.setCellValueFactory(new PropertyValueFactory<TableContent,Integer>("cf"));
        loadDictionaryButton.setDisable(true);

    }

    /**
     * This function will choose a path to the corpus file
     */
    public void chooseCorpusPath()
    {
        String path;
        path =getAbstractPath("corpus path");
        if(path!=null)
        {
            this.corpusFilePath.setStyle("-fx-background-color: #3CB371;");
            this.corpusPath = path;
            checkStart();
        }
        else
        {
            this.corpusFilePath.setStyle("-fx-background-color: #B22222;");
        }


    }

    /**
     * This function will choose a path to the posting file
     */
    public void choosePostingPath()
    {
        String path;
        path =getAbstractPath("posting path");
        if(path!=null)
        {
            this.postingFilePath.setStyle("-fx-background-color: #3CB371;");
            this.postingPath=path;
            checkStart();
            checkLoad();
        }
        else
        {
            this.postingFilePath.setStyle("-fx-background-color: #B22222;");
        }

    }

    /**
     * This function will check if we can start the indexing
     */
    public void checkStart()
    {
        startBtn.setDisable(!(!this.corpusPath.equals("") && !this.postingPath.equals("")));
    }
    /**
     * This function will check if we can load the dictionary
     */
    public void checkLoad()
    {
        loadDictionaryButton.setDisable(this.postingPath.equals(""));
    }
    /**
     * This function will start a directory chooser with a given title
     * @param headline - The given headline
     * @return - The chosen path
     */
    public String getAbstractPath(String headline)
    {
        DirectoryChooser directoryChooser=new DirectoryChooser();
        directoryChooser.setTitle(headline);
        try {
            return directoryChooser.showDialog(null).getAbsolutePath();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * This function will display the data about the dictionary in the table
     */
    public void viewDictionary()
    {
        HashMap<String,int[]> dictionary = this.controller.getDictionary();
        //For every key..
        ObservableList<TableContent> items = FXCollections.observableArrayList();
        for(Map.Entry<String,int[]> entry:dictionary.entrySet())
        {
            items.add(new TableContent(entry.getKey(),entry.getValue()[1]));
        }
        this.dictionaryTableView.setItems(items);
        this.dictionaryTableView.getSortOrder().add(termTableColumn);
        //this.dictionaryTableView.getSortOrder().add(cfTableColumn);

    }

    /**
     * This function will load the dictionary from a file
     */
    public void loadDictionary()
    {
        //Change the button to red and disable the button
        this.loadDictionaryButton.setDisable(true);

        this.controller.loadDictionary();
        //Change the button to green and enable the button
        this.loadDictionaryButton.setStyle("-fx-background-color: #3CB371;");
        this.loadDictionaryButton.setDisable(false);
    }

    /**
     * This function will return if the checkBox is selected.
     * @return - True if the checkBox is selected, False otherwise
     */
    public boolean getStem()
    {
        return this.stemCheckBox.isSelected();
    }

    /**
     * This function will return the path of the corpus file
     * @return - The path of the corpus file
     */
    public String getCorpusPath() {
        return corpusPath;
    }

    /**
     * This function will return the path of the Posting files
     * @return - The path of the posting file
     */
    public String getPostingPath() {
        return postingPath;
    }

    /**
     * This function will display the language list
     */
    private void languageDisplay()
    {
        controller.languageDisplay();
    }

    /**
     * This function will add a language to the list of languages
     * @param language - The given language
     */
    public void addLanguage(String language)
    {
        this.languagesComboBox.getItems().add(language);
    }

    /**
     * Rhis class will represent a content of a TableView
     */
    public class TableContent
    {
        public String term;//The term
        public int cf;//The cf of the term

        /**
         * This is the constructor of the class
         * @param term - The given term
         * @param cf - The given cf
         */
        public TableContent(String term, int cf)
        {
            this.cf = cf;
            this.term = term;
        }

        /**
         * This function will return the term
         * @return - The term
         */
        public int getCf() {
            return cf;
        }

        /**
         * This function will return the cf of the term
         * @return - The cf of the term
         */
        public String getTerm() {
            return term;
        }

        /**
         * This function will set the cf
         * @param cf - The given cf
         */
        public void setCf(int cf) {
            this.cf = cf;
        }

        /**
         * This function will set the term
         * @param term - The given term
         */
        public void setTerm(String term) {
            this.term = term;
        }
    }
}
