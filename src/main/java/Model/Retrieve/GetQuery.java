package Model.Retrieve;

import java.io.*;
import java.util.ArrayList;

/**
 * we extract data(the query and the id of the query) for every query and return this data
 */
public class GetQuery {
    ArrayList<QueryInfo> arreyOfFile;//a list that will contain all the queries

    /**
     * we extract all the queries from the document
     * @param path the path to the file of queries
     */
    public GetQuery(String path){
        File file = new File(path);
        final BufferedReader s;
        String av;
        arreyOfFile= new ArrayList<QueryInfo>();
        String title = "<title>";
        String name = "<num>";
        try {
            s = new BufferedReader(new FileReader(file));
            while ((av = s.readLine()) != null) {
                if (av.contains("<top>")) {
                    QueryInfo queryInfo=new QueryInfo();
                    av= s.readLine();
                    while (!av.equals("</top>")) {

                        if(av.length()>=title.length() && av.substring(0,title.length()).equals(title)) {

                            queryInfo.setMyQuery(av.substring(title.length(), av.length()));
                        }
                        else if(av.length()>=name.length() && av.substring(0,name.length()).equals(name))
                        {
                            String num="";
                            for(int i=0;i<av.length();i++)
                            {
                                if(Character.isDigit(av.charAt(i)))
                                    num=num+av.charAt(i);
                            }
                            queryInfo.setNumOfQuery(Integer.parseInt(num));

                        }

                        av= s.readLine();
                    }
                    arreyOfFile.add(queryInfo);
                }
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will return the next query in the queries document
     * @return the next query in the queries document
     */
    public QueryInfo getNextQuery(){
        if(this.arreyOfFile.size()==0)
            return null;
        QueryInfo value =arreyOfFile.get(0);
        arreyOfFile.remove(0);
        return value;
    }
}