package Model.Retrieve;

import Model.Index.DocumentReturnValue;
import Model.Index.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a single simple query
 */
public class Query {
    private HashMap <String,Integer> termsAndTf;

    public Query(String query,String postingPath,boolean stem) {
        this.termsAndTf = new HashMap<>();
        Parser parser = new Parser(postingPath, "", stem);
        DocumentReturnValue parsedQuery = parser.motherOfAllFunctions(query);
        Set<String> terms = parsedQuery.getDictionaryOfUniqueTerms().keySet();
        for (String key : terms) {
            this.termsAndTf.put(key, parsedQuery.getDictionaryOfUniqueTerms().get(key));
        }
        terms = parsedQuery.getDictionaryOfWords().keySet();
        for (String key : terms) {
            this.termsAndTf.put(key, parsedQuery.getDictionaryOfWords().get(key));
        }
        System.out.println(this.termsAndTf);

    }
// TODO: 12/22/2018 implement get(int index)
// TODO: 12/22/2018 implement getAsStrings() This will return the query as strings
}
