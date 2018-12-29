package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class will handle the change between views
 */
public class ViewChanger {

    private static Stage primaryStage;//The primary stage

    /**
     * This constructor
     *
     * @param primaryStage - The primary stage
     */
    private ViewChanger(Stage primaryStage) {
    }

    /**
     * This function will set the primary stage n the ViewChanger
     * @param primaryStage -The given primaryStage
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * This function will receive the function's name and will change the primary stage accordingly
     * @param fileName - The name of the fxml file
     */
    private static void change(String fileName)
    {
        Parent root = null;
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource(fileName));
            primaryStage.setScene(new Scene(root, 900, 400));
            primaryStage.setResizable(false);
            primaryStage.setTitle("Search Engine Project");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This function will change the primary stage to be the stage of the index
     */
    public void goToIndex()
    {
        change("view.fxml");
    }
    /**
     * This function will change the primary stage to be the stage of the search (where you can retrieve docs)
     */
    public static void goToSearch()
    {
        change("search_view.fxml");
    }
    /**
     * This function will change the primary stage to be the stage of the query result display
     */
    public static void goToDisplayQueryResult()
    {
        change("desplay_docs.fxml");
    }



}
