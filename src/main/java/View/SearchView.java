package View;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchView implements Initializable{
    public Button postingFilePath;//The button that will browse and get the path of the Posting file
    public Button querisFilePath;//The button that will browse and get the path of the queries file
    public CheckBox stemCheckBox;//If checked, we will stem the terms
    public CheckBox semanticCheckBox;//If checked, we will stem the terms
    public ListView citys;
    public Button Brows;
    public Button RUN;
    public String postingPath;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
}
