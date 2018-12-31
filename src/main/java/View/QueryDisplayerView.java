package View;
import Controller.QueryDisplayController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class will represent a QueryDisplayer View (The scene where you can see the results of a single query
 */
public class QueryDisplayerView extends AbstractView {
    private String query;//The query
    private Pair<String, List<String>>[] relevanDocs;//The relevant docs to the query
    public Label query_text;//The main label
    public TableView tableView;//The table view
    public TableColumn docIdColumnTableView;///The document id column table view
    public TableColumn entitiesColumnTableView;//The entities column table view
    private QueryDisplayController controller;//The controller
    public Button goBackButton;//The go back Button
    private SavedViewData savedViewData;//The data that we want to save about the view

    /**
     * This function will set the information that we will display
     * @param query - The query
     * @param relevantDocs - The docs relevant to the query
     * @param savedViewData - The saved data
     */
    public void setQueryAndAnswers (String query, Pair<String, List<String>>[] relevantDocs,SavedViewData savedViewData)
    {
        this.savedViewData = savedViewData;
        this.query = query;
        this.relevanDocs = relevantDocs;
        this.query_text.setText("The relevant documents to the query: "+query);
        displayResults();
    }

    /**
     * This function will initialize the instance of this class
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.goBackButton.setStyle("-fx-background-color: #6495ED;");
        this.controller=new QueryDisplayController();
        controller.setView(this);
        docIdColumnTableView.setCellValueFactory(new PropertyValueFactory<QueryDisplayTableContent, String>("docID"));
        entitiesColumnTableView.setCellValueFactory(new PropertyValueFactory<QueryDisplayTableContent,String>("entities"));


    }

    /**
     * This function will change the view to the Search view
     */
    public void goBack()
    {
        viewChanger.goToSearch(savedViewData);
    }

    /**
     * This function will display the results of the query in the TableView
     */
    private void displayResults()
    {
        boolean flag = false;
        //For every key..
        ObservableList<QueryDisplayTableContent> items = FXCollections.observableArrayList();
        for(int i=0;i<this.relevanDocs.length;i++)
        {
            if(relevanDocs[i] !=null) {
                flag = true;
                items.add(new QueryDisplayTableContent(this.relevanDocs[i].getKey(), this.getEntities(this.relevanDocs[i].getValue())));
            }
        }
        if(!flag)
            items.add(new QueryDisplayTableContent("No Results Found",""));
        this.tableView.setItems(items);
    }

    /**
     * This function will transform the given list of entities to a string
     * @param entities - The given list of entities
     * @return - The list as a string
     */
    private String getEntities(List<String> entities)
    {
        String entitiesString = "";
        String entity;
        for(int i=0;i<entities.size();i++)
        {
            entity = entities.get(i);
            if(!entity.equals(""))
                entitiesString += entity+",";
        }
        if(entitiesString.equals(""))
            return "No Entities";
        return entitiesString.substring(0,entitiesString.length()-1);
    }

    /**
     * This class represents a query displayer table content
     */
    public class QueryDisplayTableContent
    {
        public String docID;//The Document id
        public String entities;//The entities of the document

        /**
         * The constructor of the class
         * @param docId - The document id
         * @param entities - The entities of the doc
         */
        public QueryDisplayTableContent(String docId,String entities)
        {
            this.docID = docId;
            this.entities = entities;
        }

        /**
         * This function will return the id of the doc
         * @return - The id of the doc
         */
        public String getDocID() {
            return docID;
        }

        /**
         * This function will return the entities of the doc
         * @return - The entities of the doc
         */
        public String getEntities() {
            return entities;
        }

        /**
         * This function will set the doc id
         * @param docID - The given doc id
         */
        public void setDocID(String docID) {
            this.docID = docID;
        }

        /**
         * This function will set the entities
         * @param entities - The given entities
         */
        public void setEntities(String entities) {
            this.entities = entities;
        }
    }


}



