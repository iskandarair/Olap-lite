/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;
import researchproject.Parser.BsbmQuery;
import researchproject.Parser.EntailmentParser;
import researchproject.Parser.Helper;
import researchproject.Parser.OuterClassParser;
import researchproject.Parser.QueryParser;
import researchproject.Queries.GQueries;
import researchproject.Queries.LocalQueryBuilder;
import researchproject.SqlService.SqlRepository;
import researchproject.SqlService.SqlService;
import researchproject.Virtuoso.VirtuosoClient;
import researchproject.mapping.JsonObjectParser;

/**
 *
 * @author HP
 */
public class ResearchProject {

    private static String _query = "prefix qb: <http://purl.org/linked-data/cube#> prefix qb4o: <http://purl.org/olap#> prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix skos: <http://www.w3.org/2004/02/skos/core#> prefix xsd: <http://www.w3.org/2001/XMLSchema#> prefix bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> prefix bsbm-inst: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/> prefix gs: <http://example.org/globalschema#> prefix ex: <http://example.org/federation#> CONSTRUCT{ ?offer rdf:type qb:Observation . ?offer qb:DataSet gs:OfferDataset . ?offer ex:product ?product . ?offer ex:vendor ?vendor . ?vendor rdf:type bsbm:Vendor . ?vendor skos:broader ?city . ?city skos:broader ?country . } WHERE { ?offer rdf:type qb:Observation . ?offer qb:DataSet gs:OfferDataset . ?offer ex:product ?product . ?offer ex:vendor ?vendor . ?vendor rdf:type bsbm:Vendor . ?vendor skos:broader ?city . ?city skos:broader ?country . }";
    private static String _named_uri = "http://localhost:8890/DAV-mapping";
    private static int[] queryNo = new int[]{0,1,2,3,4,5,6,7};
    
    public static void main(String[] args) {
       
       String globalQuery = GQueries.getQuery(1);
       System.out.println("globalQuery --> " + globalQuery);
       String localizedQuery = LocalQueryBuilder.build(globalQuery);
       System.out.println("localizedQuery --> " + localizedQuery);
       String entailedQuery = Helper.RemoveDoubleSpaces(EntailmentParser.GetBasicQuery(localizedQuery));
       
       SqlRepository.Initialize();
       exectureSubQuery(entailedQuery, 6);
      /*
      //int i =7;
      for(int i =0; i <queryNo.length; i++)
      {
          int queryNumber = 7;//queryNo[i];           
           String queryString = Helper.RemoveDoubleSpaces(BsbmQuery.getQuery(i));
           String entailedQuery = EntailmentParser.GetBasicQuery(queryString);
           
           System.out.println("=======    Query #" + (i+1) + "   ========");
           exectureSubQuery(entailedQuery, (i+1));
           System.out.println("=======    FINISHED   ========");
      }
      */
     }
    
    public static void exectureSubQuery(String queryString, int index)
    {
        System.out.println(queryString);
        System.out.println("=========");
        Map<String, List<String>> tableDetails =new HashMap<String, List<String>>();
        List<String> resultSet = QueryParser.GetInnerQuery(queryString);
        int tableIndex = 1;
        for(String query : resultSet)
        {
            //System.out.println(query);
            System.out.println("=== " + query);
            try {                 
                String table = "Table_Query" + index + "_table" + tableIndex;
                System.out.println("query -->"+ query);
                JSONObject jObject = VirtuosoClient.sendRequest(query, "http://localhost:8890/Data-set");
                List<String> columns = SqlService.createTable(jObject, table);
                String sql = JsonObjectParser.populateTable(table, jObject);
                SqlRepository.runStatement(sql);
                tableDetails.put(table, columns);
            }
            catch (Exception e)
            {
                System.out.println("corrupted query occured: " + e);
            }
            tableIndex += 1;
        }
        String outerSqlstat = OuterClassParser.buildOuterQuery(queryString, tableDetails, index); 
        System.out.println(outerSqlstat);
        SqlRepository.selectAll(outerSqlstat);
    }
    
}
