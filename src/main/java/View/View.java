package View;


import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;

/**
 * This class is the class that is responsible on the view of the GUI
 * This class has access to the fxml file and declares as the controller in the fxml file
 */
public class View {
    public Button postingFilePath;//The button that will browse and get the path of the Posting file
    public Text postingFilePathText;//This text will present the path chosen to be the posting file's location
    public Button corpusFilePath;//The button that will browse and get the path of the corpus file
    public Text corpusFilePathText;//This text will present the path chosen to be the corpus location
    public Button startBtn;//This button is responsible for the start of the indexing
    public Button resetBtn;//This button is responsible to reset the indexing. Delete the posting files and the dictionary
    public CheckBox stemCheckBox;//If checked, we will stem the terms
    public ComboBox languagesComboBox;//The combo box that will display the languages

    /**
     * This function will start the indexing
     */
    public void Start()
    {

    }




}
