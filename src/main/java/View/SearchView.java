package View;

import Controller.SearchController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

public class SearchView implements Initializable{
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
    public TextField oneQuery;

    @FXML
    public ListView qeuriesToChoose;

    public SearchController controller;
    public HashMap<String,String[]> releventDoctoQuery;
    public String postingPath;
    public String querisPath;



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        this.RUN.setDisable(true);
        this.Brows.setDisable(true);
        this.releventDoctoQuery = new HashMap<>();
        postingPath= "";
        querisPath= "";
        this.controller=new SearchController();
        controller.setView(this);
        checkInitStemming();
    }

    private void checkInitStemming()
    {
        this.stemCheckBox.setSelected(true);
    }
    public void setOneQuery(){
        checkRun();
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
            ObservableList<String> list = FXCollections.observableArrayList();
            citys.setItems(list);
            List<String> namesOfCitys=controller.getNamesOfCitys();
            System.out.println("this is"+namesOfCitys);
            for(int i=0; i<namesOfCitys.size();i++){
                list.add(namesOfCitys.get(i));
            }
            citys.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            checkStart();
            checkRun();
        }
        else
        {
            this.postingFilePath.setStyle("-fx-background-color: #B22222;");
        }

    }

    /**
     * This function will choose a path to the corpus file
     */
    public void chooseQueryPath() {
        String path;
        path = getAbstractPath("queries path");
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
        Brows.setDisable(!(!this.querisPath.equals("") && !this.postingPath.equals("")));
    }

    private void checkRun(){
        RUN.setDisable(!this.postingPath.equals("")&&this.oneQuery.getText().equals(""));
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
            i++;
        }
        return itemsToReturn;
    }

    public String getPostingPath(){
        return postingPath;
    }

    public String getQuerisPath() {
        return querisPath;
    }

    /**
     * This function will start the indexing
     */
    public void Brows()
    {
        controller.brows();
        displayQueries();
    }

    private void displayQueries() {
        System.out.println("jggighigojhghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        ObservableList<String> list = FXCollections.observableArrayList();
        qeuriesToChoose.setItems(list);
        for (String query :this.releventDoctoQuery.keySet()) {
            list.add(query);
        }
        qeuriesToChoose.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void Run(){
        controller.Run();
        displayQueries();
    }

}
