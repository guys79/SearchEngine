package View;
import Controller.QueryDisplayController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QueryDisplayerView extends AbstractView {
    private String query;//The query
    private Pair<String, List<String>>[] relevanDocs;//The relevant docs to the query
    public Label query_text;//The main label
    public TableView tableView;//The table view
    public TableColumn docIdColumnTableView;///The document id column table view
    public TableColumn entitiesColumnTableView;//The entities column table view
    private QueryDisplayController controller;//The controller
    public Button goBackButton;//The go back Button
    private SavedViewData savedViewData;


    public void setQueryAndAnswers (String query, Pair<String, List<String>>[] relevantDocs,SavedViewData savedViewData)
    {
        this.savedViewData = savedViewData;
        this.query = query;
        this.relevanDocs = relevantDocs;
        this.query_text.setText("The relevant documents to the query: "+query);
        displayResults();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.goBackButton.setStyle("-fx-background-color: #B22222;");
        this.controller=new QueryDisplayController();
        controller.setView(this);
        docIdColumnTableView.setCellValueFactory(new PropertyValueFactory<QueryDisplayTableContent, String>("docID"));
        entitiesColumnTableView.setCellValueFactory(new PropertyValueFactory<QueryDisplayTableContent,String>("entities"));


    }
    public void goBack()
    {
        viewChanger.goToSearch(savedViewData);
    }
    private void displayResults()
    {
        //For every key..
        ObservableList<QueryDisplayTableContent> items = FXCollections.observableArrayList();
        for(int i=0;i<this.relevanDocs.length;i++)
        {
            System.out.println(this.relevanDocs[i].getKey());
            items.add(new QueryDisplayTableContent(this.relevanDocs[i].getKey(),this.getEntities(this.relevanDocs[i].getValue())));
        }
        this.tableView.setItems(items);
    }
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

        public QueryDisplayTableContent(String docId,String entities)
        {
            this.docID = docId;
            this.entities = entities;
        }

        public String getDocID() {
            return docID;
        }

        public String getEntities() {
            return entities;
        }

        public void setDocID(String docID) {
            this.docID = docID;
        }

        public void setEntities(String entities) {
            this.entities = entities;
        }
    }
}



