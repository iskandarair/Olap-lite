/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.Queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.ext.com.google.common.collect.HashBiMap;
import org.json.JSONObject;
import researchproject.Sparql.SparqlService;
import researchproject.Virtuoso.VirtuosoClient;
import researchproject.mapping.GlobalSchemaParser;
import researchproject.mapping.JsonObjectParser;
import researchproject.mapping.TripleCombinator;
import researchproject.models.Triple;

/**
 *
 * @author Iskandar
 */
public class LocalQueryBuilder {
    public final static String _mappingUri =  "http://localhost:8890/DAV-mapping";// "http://localhost:8890/DAV-mapping"; // "http://localhost:8890/Mapping";//
    public final static String _globalSchemaUri = "http://localhost:8890/DAV-fixed";
    public final static String _datasetUri = "http://localhost:8890/Data";
    
    public static String build(String globalQuery)
    {
       List<String> subqueries = GlobalSchemaParser.getSchema(globalQuery);
       List<String> variablesQueries = GlobalSchemaParser.getVariablesQuery(globalQuery);
       Map<String,String> variables = new HashMap<>();
       for(int j=0; subqueries.size() > j; j++)
       {
           //obtaining variables from Triples to store and later replace them (global with local)
            String variablesQuery = variablesQueries.get(j);
            //List<String> variables2 = SparqlService.getVariables(variablesQuery);
            //variables.putAll(GlobalSchemaParser.GetVariables(variables2));
            JSONObject subQueryVariables = VirtuosoClient.sendRequest(variablesQuery, _globalSchemaUri);
            variables.putAll(JsonObjectParser.getVariables(subQueryVariables));
            
            //obtaining triples from localSchema
            String subQuery =subqueries.get(j);
            List<Triple> contstructedTriples =  SparqlService.getCnsctFromGlobalMapping(subQuery);
            //JSONObject jObject =   VirtuosoClient.sendRequest(subQuery, _globalSchemaUri);
            //List<Triple> contstructedTriples = JsonObjectParser.getTriples(jObject);
            
            // checking all combinations of triples like (1st,3th,5th triples)(1st,2nd,4th,5th triples)
            // to find local Schema triples match
            String[][] combinations =  TripleCombinator.getAllCombinations(contstructedTriples.size());
            Set<String> localSchemas = new HashSet<>();
            localSchemas.addAll(getLocalSchemaMatches(combinations,contstructedTriples));

            List<Triple> result = getTriplesFromLsmt(localSchemas);
            
            String globalTriples =  GlobalSchemaParser.getGlobalTriples(subQuery);            
            String localTriples = TripleCombinator.buildlocalizedRequest(result);
            //replacing existing global schema with local one 
            globalQuery = globalQuery.replace(globalTriples," "+localTriples+" ");
       }
       //replace variables in SELECT params 
       for (Map.Entry<String, String> entry : variables.entrySet())
       {
           globalQuery = globalQuery.replaceAll("\\?"+entry.getKey()+"(?![A-Za-z]+)",entry.getValue());
       }
       System.out.println(globalQuery);
       //JSONObject object = VirtuosoClient.sendRequest(globalQuery, "http://localhost:8890/DAV");
       return globalQuery;// localizedSubQueries;
    }
    
    // gettings subject predicate object from Local Schema Matched Triples 
    private static List<Triple> getTriplesFromLsmt( Set<String> localSchemas)
    {
            List<Triple> result = new ArrayList<>();
            for(String schema : localSchemas) {
                 String lSchema = TripleCombinator.BuildLSchemaRequest(schema);
                 //JSONObject tripleObject = VirtuosoClient.sendRequest(lSchema,_mappingUri);
                 //List<Triple> triples =  JsonObjectParser.getTriples(tripleObject);
                 List<String> pairs = SparqlService.getFromGlobalMapping(lSchema);
                 List<Triple> triples = GlobalSchemaParser.getTriples(pairs);   
                 result.addAll(triples);
            } 
            return result;
    }
    // Getting lsmt by sending localized queries
    private static List<String> getLocalSchemaMatches(String[][] combinations,List<Triple> contstructedTriples)
    {
            List<String> lSchema =  new ArrayList<>();
            for(int i = combinations.length-1; i >= 0; i--)
            {
                List<Triple> triples = TripleCombinator.getTriples(combinations[i],contstructedTriples);
                String query = TripleCombinator.localizedQuery(triples);
                //JSONObject matchedJObject = VirtuosoClient.sendRequest(query,_mappingUri); //
                //lSchema.addAll(JsonObjectParser.getResults(matchedJObject, "lsmt"));
                List<String> pairs = SparqlService.getFromGlobalMapping(query);
                List<String> lsmts = GlobalSchemaParser.getLsmt(pairs);    
                lSchema.addAll(lsmts);
                //localSchemas.addAll(lSchema);///if(lSchema != null) removed since if it empty nothing is added
                System.out.println("=======" + i +"=======");
                //return lSchema;
            }
            return lSchema;
    }
}
