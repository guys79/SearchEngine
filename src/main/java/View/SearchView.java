package View;

import Controller.SearchController;
import Model.Retrieve.QueryInfo;
import Model.Retrieve.StoreQueriesData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

public class SearchView extends AbstractView{
    @FXML
    public Button postingFilePath;//The button that will browse and get the path of the Posting file
    @FXML
    public Button browse;//The button that will browse and get the path of the queries file
    @FXML
    public CheckBox stemCheckBox;//If checked, we will stem the terms
    @FXML
    public CheckBox semanticCheckBox;//If checked, we will stem the terms
    @FXML
    public ListView citys;//The ListView of cities
    @FXML
    public Button save;//The Browse button
    @FXML
    public Button RUN;//The run button
    @FXML
    public Button loadCities_btn;//The load cities button
    @FXML
    public Button reset;//The reset button
    @FXML
    public TextField oneQuery;//The TextField
    @FXML
    public ListView qeuriesToChoose;//The listView of queries
    @FXML
    public Button goToIndex;//The Go Index button

    public SearchController controller;//The controller
    public HashSet<String> releventDoctoQuery;//The queries
    public String postingPath;//The posting file path
    public HashMap<String,Pair<QueryInfo,String []>> queriesAndResults;//The queries and their results
    public String querisPath;//The queries file path
    public boolean didLoadCities;//True if we have already loaded the cities
    private SavedViewData savedViewData;//The data that we save on the view
    private String summoningFunctionName;//The name of the function that saved the data last
    private String resultsName;

    /**
     * This function will initialize the instance of this class
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        resultsName="results.txt";
        this.summoningFunctionName = "";
        this.queriesAndResults = new HashMap<>();
        this.savedViewData = null;
        this.RUN.setDisable(true);
        this.browse.setDisable(true);
        this.stemCheckBox.setDisable(true);
        this.releventDoctoQuery = new HashSet<>();
        disableLoadCities();
        postingPath= "";
        querisPath= "";
        this.controller=new SearchController();
        controller.setView(this);
        this.postingFilePath.setStyle("-fx-background-color: #000000;");
        this.browse.setStyle("-fx-background-color: #000000;");
        checkInitStemming();
        this.qeuriesToChoose.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            String query = (String)newSelection;
                viewChanger.goToDisplayQueryResult(query,this.controller.run(query),savedViewData);

            });

    }

    /**
     * This function will redirect the view to the indexer view
     */
    public void goToIndex()
    {
        this.viewChanger.goToIndex();
    }

    /**
     * This function will reset the settings of the searcher
     */
    public void resetSettings()
    {

        this.summoningFunctionName = "";
        this.savedViewData = null;
        this.RUN.setDisable(true);
        this.browse.setDisable(true);
        this.stemCheckBox.setDisable(true);
        this.releventDoctoQuery = new HashSet<>();
        disableLoadCities();
        postingPath= "";
        querisPath= "";
        this.postingFilePath.setStyle("-fx-background-color: #000000;");
        this.browse.setStyle("-fx-background-color: #000000;");
        this.semanticCheckBox.setSelected(false);
        this.postingFilePath.setDisable(false);
        this.semanticCheckBox.setDisable(false);
        this.qeuriesToChoose.getItems().clear();
        this.controller=new SearchController();
        controller.setView(this);
        citys.getItems().clear();
        checkInitStemming();
    }

    /**
     * This function will disable the Load cities button
     */
    private void disableLoadCities()
    {
        this.loadCities_btn.setDisable(true);
        this.didLoadCities = false;
    }

    /**
     * This function will set the stemming check box
     * @return - True if the posting file is valid
     */
    private boolean checkInitStemming() {

        if(this.postingPath!=null && !this.postingPath.equals("")) {
            boolean validTrue = this.controller.checkForMustHavePostingFiles(true) && this.controller.checkForPostingFiles(true);
            boolean validFalse = this.controller.checkForPostingFiles(false) && this.controller.checkForMustHavePostingFiles(false);
            if (validFalse || validTrue) {
                this.loadCities_btn.setDisable(false);
                this.postingFilePath.setStyle("-fx-background-color: #3CB371;");
                if (validFalse && validTrue) {
                    this.stemCheckBox.setDisable(false);
                    this.stemCheckBox.setSelected(true);
                } else if (validFalse) {
                    this.stemCheckBox.setDisable(true);
                    this.stemCheckBox.setSelected(false);
                } else {
                    this.stemCheckBox.setDisable(true);
                    this.stemCheckBox.setSelected(true);
                }
                return true;

            }
        }
        return false;
    }


    /**
     * This function will save the current data of the view
     */
    private void saveCurrentData()
    {
        this.savedViewData=new SavedViewData(this.controller.getSearcher(),this.stemCheckBox.isDisabled(),this.stemCheckBox.isSelected(),semanticCheckBox.isSelected(),this.postingPath,this.querisPath,this.releventDoctoQuery,summoningFunctionName,this.queriesAndResults);
    }


    /**
     * This function will receive data and will config the view with that data
     * @param savedViewData - The given data
     */
    public void configure(SavedViewData savedViewData)
    {
        this.controller.setSearcher(savedViewData.getSearcher());
        this.stemCheckBox.setSelected(savedViewData.isStem());
        this.stemCheckBox.setDisable(savedViewData.isStemButtonDisabled());
        this.semanticCheckBox.setSelected(savedViewData.isSemantic());
        this.postingPath = savedViewData.getPostingPath();
        this.postingFilePath.setStyle("-fx-background-color: #3CB371;");
        this.releventDoctoQuery = savedViewData.getQuries();
        this.querisPath = savedViewData.getQuriesPath();
        this.browse.setStyle("-fx-background-color: #3CB371;");
        this.summoningFunctionName = savedViewData.getFunctionName();
        this.queriesAndResults = savedViewData.queriesAndResults;


        loadCitiesButtonPress();

        checkStart();
        checkRun();
        checkInitStemming();
        displayQueries();
        this.savedViewData = savedViewData;
        disableAll();
        if(this.summoningFunctionName.equals("run"))
        {
            this.RUN.setDisable(false);
        }


    }

    /**
     * This function will disable most of the nodes in the view
     */
    public void disableAll()
    {
        this.postingFilePath.setDisable(true);
        this.browse.setDisable(true);
        this.stemCheckBox.setDisable(true);
        this.semanticCheckBox.setDisable(true);
        this.browse.setDisable(true);
        this.RUN.setDisable(true);

    }


    /**
     * This function will choose a path to the posting file
     */
    public void choosePostingPath()
    {
        String path;
        path =getDirectoryAbstractPath("posting path");
        if(path!=null)
        {
            this.postingPath = path;
            if(checkInitStemming())
            {
               checkStart();
               checkRun();
             }
             else
             {
                 this.disableLoadCities();
                 this.postingFilePath.setStyle("-fx-background-color: #B22222;");
             }

        }
        else
        {
            this.disableLoadCities();
            this.postingFilePath.setStyle("-fx-background-color: #B22222;");
        }

    }

    /**
     * This function will load the cities to the listView
     */
    public void loadCitiesButtonPress() {


        ObservableList<String> list = FXCollections.observableArrayList();
        citys.setItems(list);
        List<String> namesOfCitys = controller.getNamesOfCitys(this.getStem());
        namesOfCitys.sort(String::compareToIgnoreCase);
        for (int i = 0; i < namesOfCitys.size(); i++) {
            list.add(namesOfCitys.get(i));
        }
        citys.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.didLoadCities = true;
        checkStart();
        checkRun();
    }



    /**
     * This function will choose a path to the corpus file
     */
    public void chooseQueryPath() {
        String path;
        path = getAbstractFilePath("queries path");
        if (path != null) {
            this.browse.setStyle("-fx-background-color: #3CB371;");
            this.querisPath = path;
            Brows();
            disableAll();
        } else {
            this.browse.setStyle("-fx-background-color: #B22222;");
        }


    }

        /**
         * This function will check if we can start the indexing
         */
    public void checkStart()
    {
        browse.setDisable(!(!this.postingPath.equals("") && didLoadCities));
    }

    public void checkRun(){
        RUN.setDisable(!(!this.postingPath.equals("")&& didLoadCities));
    }

    /**
     * This function will start a directory chooser with a given title
     * @param headline - The given headline
     * @return - The chosen path
     */
    public String getDirectoryAbstractPath(String headline)
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
     * This function will start a file chooser with a given title
     * @param headline - The given headline
     * @return - The chosen path
     */
    public String getAbstractFilePath(String headline)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(headline);
        try {
            return fileChooser.showOpenDialog(null).getAbsolutePath();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            return null;
        }
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
     * This function will return if the checkBox is selected.
     * @return - True if the checkBox is selected, False otherwise
     */
    public boolean getSemantic()
    {
        return this.semanticCheckBox.isSelected();
    }

    /**
     * This function will return the relevant cities
     * @return - The relevant cities (filter)
     */
    public String[] getReleventCitys(){
        ObservableList<String> selectedItems =  citys.getSelectionModel().getSelectedItems();
        String [] itemsToReturn= new String[selectedItems.size()];
        int i=0;
        for(String s : selectedItems){
            itemsToReturn[i]=s;
            i++;
        }
        return itemsToReturn;
    }

    /**
     * This function will return the posting file path
     * @return - The posting file path
     */
    public String getPostingPath(){
        return postingPath;
    }


    /**
     * This function will get and display the queries
     */
    public void Brows()
    {
        controller.brows();
        displayQueries();
        this.summoningFunctionName = "brows";
        saveCurrentData();
        disableAll();
    }

    /**
     * This function will display the queries
     */
    private void displayQueries() {
        ObservableList<String> list = FXCollections.observableArrayList();
        qeuriesToChoose.setItems(list);
        for (String query :this.releventDoctoQuery) {
            list.add(query);
        }
        qeuriesToChoose.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * This function will run the Search Engine on the query
     */
    public void run(){
        String query = this.oneQuery.getText();
        controller.run(query);
        displayQueries();
        this.summoningFunctionName = "run";
        saveCurrentData();
        viewChanger.goToDisplayQueryResult(query,this.controller.run(query),savedViewData);
    }

    /**
     * This function will save the results in a file
     */
    public void saveInFile()
    {
        String path = getDirectoryAbstractPath("Choose results");
        if(path!=null)
        {
            path = path+"\\"+this.resultsName;
            StoreQueriesData storeQueriesData = new StoreQueriesData(path);
            storeQueriesData.addToStorage(this.queriesAndResults);
            this.queriesAndResults = new HashMap<>();
        }

    }

}
