package View;

import Controller.SearchController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    public Button querisFilePath;//The button that will browse and get the path of the queries file
    @FXML
    public CheckBox stemCheckBox;//If checked, we will stem the terms
    @FXML
    public CheckBox semanticCheckBox;//If checked, we will stem the terms
    @FXML
    public ListView citys;
    @FXML
    public Button Brows;
    @FXML
    public Button RUN;
    @FXML
    public Button loadCities_btn;
    @FXML
    public Button reset;
    @FXML
    public TextField oneQuery;
    @FXML
    public ListView qeuriesToChoose;
    @FXML
    Button goToIndex;


    public SearchController controller;
    public HashSet<String> releventDoctoQuery;
    public String postingPath;
    public String querisPath;
    public boolean didLoadCities;
    private SavedViewData savedViewData;
    private String summoningFunctionName;



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        this.summoningFunctionName = "";
        this.savedViewData = null;
        this.RUN.setDisable(true);
        this.Brows.setDisable(true);
        this.stemCheckBox.setDisable(true);
        this.releventDoctoQuery = new HashSet<>();
        disableLoadCities();
        postingPath= "";
        querisPath= "";
        this.controller=new SearchController();
        controller.setView(this);
        this.postingFilePath.setStyle("-fx-background-color: #000000;");
        this.querisFilePath.setStyle("-fx-background-color: #000000;");
        checkInitStemming();
        this.qeuriesToChoose.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            String query = (String)newSelection;
                viewChanger.goToDisplayQueryResult(query,this.controller.run(query),savedViewData);

            });

    }

    public void goToIndex()
    {
        this.viewChanger.goToIndex();
    }
    public void resetSettings()
    {
        this.summoningFunctionName = "";
        this.savedViewData = null;
        this.RUN.setDisable(true);
        this.Brows.setDisable(true);
        this.stemCheckBox.setDisable(true);
        this.releventDoctoQuery = new HashSet<>();
        disableLoadCities();
        postingPath= "";
        querisPath= "";
        this.postingFilePath.setStyle("-fx-background-color: #000000;");
        this.querisFilePath.setStyle("-fx-background-color: #000000;");
        this.semanticCheckBox.setSelected(false);
        this.postingFilePath.setDisable(false);
        this.querisFilePath.setDisable(false);
        this.semanticCheckBox.setDisable(false);
        this.qeuriesToChoose.getItems().clear();
        this.controller=new SearchController();
        controller.setView(this);
        citys.getItems().clear();
        checkInitStemming();
    }
    private void disableLoadCities()
    {
        this.loadCities_btn.setDisable(true);
        this.didLoadCities = false;
    }
    private void checkInitStemming()
    {
        this.stemCheckBox.setSelected(true);
    }
    public void setOneQuery(){
        checkRun();
    }

    private void saveCurrentData()
    {
        this.savedViewData=new SavedViewData(this.controller.getSearcher(),this.stemCheckBox.isDisabled(),this.stemCheckBox.isSelected(),semanticCheckBox.isSelected(),this.postingPath,this.querisPath,this.releventDoctoQuery,summoningFunctionName);
    }
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
        this.querisFilePath.setStyle("-fx-background-color: #3CB371;");
        this.summoningFunctionName = savedViewData.getFunctionName();



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

    public void disableAll()
    {
        this.postingFilePath.setDisable(true);
        this.querisFilePath.setDisable(true);
        this.stemCheckBox.setDisable(true);
        this.semanticCheckBox.setDisable(true);
        this.Brows.setDisable(true);
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

             boolean validTrue = this.controller.checkForMustHavePostingFiles(true) && this.controller.checkForPostingFiles(true);
             boolean validFalse = this.controller.checkForPostingFiles(false) && this.controller.checkForMustHavePostingFiles(false) ;
        /*    System.out.println("validTrue");
            System.out.println("Must have - "+ this.controller.checkForMustHavePostingFiles(true));
            System.out.println("Posting - "+ this.controller.checkForPostingFiles(true));
            System.out.println("validFalse");
            System.out.println("Must have - "+ this.controller.checkForMustHavePostingFiles(false));
            System.out.println("Posting - "+ this.controller.checkForPostingFiles(false));*/
             if(validFalse || validTrue)
             {
                 this.loadCities_btn.setDisable(false);
                 this.postingFilePath.setStyle("-fx-background-color: #3CB371;");
                 if(validFalse && validTrue)
                 {
                     this.stemCheckBox.setDisable(false);
                 }
                 else if(validFalse)
                 {
                     this.stemCheckBox.setDisable(true);
                     this.stemCheckBox.setSelected(false);
                 }
                 else
                 {
                     this.stemCheckBox.setDisable(true);
                     this.stemCheckBox.setSelected(true);
                 }
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


    public void loadCitiesButtonPress() {


        ObservableList<String> list = FXCollections.observableArrayList();
        citys.setItems(list);
        List<String> namesOfCitys = controller.getNamesOfCitys(this.getStem());
        namesOfCitys.sort(String::compareToIgnoreCase);
        System.out.println("this is" + namesOfCitys);
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
            this.querisFilePath.setStyle("-fx-background-color: #3CB371;");
            this.querisPath = path;
            checkStart();
        } else {
            this.querisFilePath.setStyle("-fx-background-color: #B22222;");
        }
    }

        /**
         * This function will check if we can start the indexing
         */
    public void checkStart()
    {
        Brows.setDisable(!(!this.querisPath.equals("") && !this.postingPath.equals("") && didLoadCities));
    }

    private void checkRun(){
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

    public String[] getReleventCitys(){
        ObservableList<String> selectedItems =  citys.getSelectionModel().getSelectedItems();
        String [] itemsToReturn= new String[selectedItems.size()];
        int i=0;
        for(String s : selectedItems){
            itemsToReturn[i]=s;
            System.out.println(s);
            i++;
        }
        return itemsToReturn;
    }

    public String getPostingPath(){
        return postingPath;
    }

    public String getQueriesPath() {
        return querisPath;
    }

    /**
     * This function will start the indexing
     */
    public void Brows()
    {
        controller.brows();
        displayQueries();
        this.summoningFunctionName = "brows";
        saveCurrentData();
        disableAll();
    }

    private void displayQueries() {
        ObservableList<String> list = FXCollections.observableArrayList();
        qeuriesToChoose.setItems(list);
        for (String query :this.releventDoctoQuery) {
            list.add(query);
        }
        qeuriesToChoose.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void run(){
        String query = this.oneQuery.getText();
        controller.run(query);
        displayQueries();
        this.summoningFunctionName = "run";
        saveCurrentData();
        //disableAll();
        ///this.RUN.setDisable(false);
        viewChanger.goToDisplayQueryResult(query,this.controller.run(query),savedViewData);
    }

}
