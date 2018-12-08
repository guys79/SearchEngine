package View;


import Controller.Controller;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the class that is responsible on the view of the GUI
 * This class has access to the fxml file and declares as the controller in the fxml file
 */
public class View implements Initializable {
    public Button postingFilePath;//The button that will browse and get the path of the Posting file
    public Button corpusFilePath;//The button that will browse and get the path of the corpus file
    public Button startBtn;//This button is responsible for the start of the indexing
    public Button resetBtn;//This button is responsible to reset the indexing. Delete the posting files and the dictionary
    public CheckBox stemCheckBox;//If checked, we will stem the terms
    public ComboBox languagesComboBox;//The combo box that will display the languages
    public Text totalTimeText;//The text that will display the total amount of time it took to index all the corpus
    public Text documentQuantityText;//The text that will display the the number of documents
    public Text termQuantityText;//The text that will display the number of terms in the corpus
    private String corpusPath;
    private String postingPath;
    private Controller controller;

    /**
     * This function will start the indexing
     */
    public void start()
    {
        long start = System.nanoTime();
        this.startBtn.setDisable(true);
        this.controller.start();
        long elapsedTime = System.nanoTime() - start;
        String totalTime =  (elapsedTime / 1000000000) / 60 + " minutes and " + (elapsedTime / 1000000000) % 60 + " seconds";
        String numOfDocument=""+controller.getNumOfDocuments();
        String numOfTerms=""+controller.getNumOfTerms();
        this.totalTimeText.setText(totalTime);
        this.documentQuantityText.setText(numOfDocument);
        this.termQuantityText.setText(numOfTerms);
        startBtn.setDisable(false);

    }

    /**
     * This function will reset the indexing.
     * This function will delete all of the files and will erase the dictionary
     */
    public void reset()
    {

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
        this.controller=new Controller();
        controller.setView(this);

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

    }

    /**
     * This function will load the dictionary from a file
     */
    public void loadDictionary()
    {

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
}
