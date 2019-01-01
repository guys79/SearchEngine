package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.List;

/**
 * This class will handle the change between views
 */
public class ViewChanger {

    private Stage primaryStage;//The primary stage
    private FXMLLoader fxmlLoader;

    /**
     * This constructor
     *
     */
    public ViewChanger() {
    }

    /**
     * This function will set the primary stage n the View.ViewChanger
     * @param givenPrimaryStage -The given primaryStage
     */
    public void setPrimaryStage(Stage givenPrimaryStage) {
        primaryStage = givenPrimaryStage;
    }


    /**
     * This function will receive the function's name and will change the primary stage accordingly
     * @param fileName - The name of the fxml file
     */
    private void change(String fileName)
    {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("/"+fileName));
            Parent root= (Parent) fxmlLoader.load();
            AbstractView abstractView = fxmlLoader.getController();
            abstractView.setViewChanger(this);
            primaryStage.setScene(new Scene(root, 900, 461));
            primaryStage.setResizable(false);
            primaryStage.setTitle("Search Engine Project");
            primaryStage.show();

        } catch (Exception e) {
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
    public void goToSearch()
    {
        change("search_view.fxml");
    }
    /**
     * This function will change the primary stage to be the stage of the search (where you can retrieve docs)
     * @param savedViewData - The data we saved on SearcherView
     */
    public void goToSearch(SavedViewData savedViewData)
    {
        change("search_view.fxml");
        SearchView searchView = fxmlLoader.getController();
        searchView.configure(savedViewData);
    }

    /**
     * This function will change the primary stage to be the stage of the query result display
     * @param query - The query
     * @param docs - The relevant docs
     * @param savedViewData - The data that we saved on the SearchView
     */
    public void goToDisplayQueryResult(String query, Pair<String, List<String>>[] docs,SavedViewData savedViewData)
    {
        change("desplay_docs.fxml");
        QueryDisplayerView queryDisplayerView = fxmlLoader.getController();
        queryDisplayerView.setQueryAndAnswers(query,docs,savedViewData);

    }



}
