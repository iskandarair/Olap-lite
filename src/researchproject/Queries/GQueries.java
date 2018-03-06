/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.Queries;

import java.util.ArrayList;

/**
 *
 * @author Iskandar
 */
public class GQueries {
    private static final  ArrayList queries = new ArrayList();
    
    static  {
      queries.add("prefix ex: <http://example.org/federation#>prefix xsd: <http://www.w3.org/2001/XMLSchema#> prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix qb: <http://purl.org/linked-data/cube#>  prefix qb4o: <http://purl.org/olap#>  prefix gs: <http://example.org/globalschema#> Select ?vendor (xsd:float(?belowAvg)/?offerCount As ?cheapExpensiveRatio) { { Select ?vendor (count(?offer) As ?belowAvg) { { { Select ?vendor ?product ?price ?offer WHERE { ?offer rdf:type qb:Observation . ?offer qb:DataSet gs:OfferDataset . ?offer ex:product ?product . ?offer ex:vendor ?vendor . ?offer ex:productPrice ?price . ?product rdf:type <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType19> . } } { Select ?product (avg(xsd:float(str(?price))) As ?avgPrice) { ?offer rdf:type qb:Observation . ?offer qb:DataSet gs:OfferDataset . ?offer ex:product ?product . ?offer ex:vendor ?vendor . ?offer ex:productPrice ?price . ?product rdf:type <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType19> . } Group By ?product } } FILTER (xsd:float(str(?price)) < ?avgPrice) } Group By ?vendor } { Select ?vendor (count(?offer) As ?offerCount) { ?offer rdf:type qb:Observation . ?offer qb:DataSet gs:OfferDataset . ?offer ex:product ?product . ?offer ex:vendor ?vendor . ?product a <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType19> . } Group By ?vendor } } Order by desc(xsd:float(?belowAvg)/?offerCount) ?vendor limit 10");
      queries.add("prefix skos: <http://www.w3.org/2004/02/skos/core#> prefix bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> prefix ex: <http://example.org/federation#> prefix xsd: <http://www.w3.org/2001/XMLSchema#> prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix qb: <http://purl.org/linked-data/cube#>  prefix qb4o: <http://purl.org/olap#>  prefix gs: <http://example.org/globalschema#> Select ?product { { Select ?product (count(?offer) As ?offerCount) { ?offer rdf:type qb:Observation . ?offer qb:DataSet gs:OfferDataset . ?offer ex:product ?product . ?offer ex:vendor ?vendor . ?vendor rdf:type bsbm:Vendor . ?vendor skos:broader ?city . ?city skos:broader ?country . ?product a <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType19> . FILTER(?country!=<http://downlode.org/rdf/iso-3166/countries#UK>) } Group By ?product Order By desc(?offerCount) } } Limit 1000");
    }
    
    
    public static String getQuery(int queryId){
        return queries.get(queryId).toString();
    }
}
