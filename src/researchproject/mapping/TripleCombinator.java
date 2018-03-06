/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import researchproject.Parser.Helper;
import researchproject.models.Triple;
/**
 *
 * @author Iskandar
 */
public class TripleCombinator {
 
    private static String[][] data;
    
    public static String[][] getAllCombinations(int binarysize)
    {
        String combinations = new String(new char[binarysize]).replace("\0", "1");
        int decimalValue = Integer.parseInt(combinations, 2);
        
        data = new String[decimalValue][binarysize]; 
        for(int i= decimalValue; i> 0; i--)
        {
            String binaryValue = Integer.toString(i,2);
            binaryValue = new String(new char[binarysize - binaryValue.length()]).replace("\0", "0") + binaryValue;
            String[] result = binaryValue.split("");
            data[i-1] = result;
        }
        return data;
       
    }
    public static List<Triple> getTriples(String[] combination, List<Triple> objects)
    {
        List<Triple> triples = new ArrayList<Triple>();
        
        for(int i = 0; combination.length > i; i++)
        {
            if(combination[i].equalsIgnoreCase("1"))
            triples.add(objects.get(i));
        }
        return triples;
    }
    
    public static String localizedQuery(List<Triple> triples)
    {
        String prefixed = "prefix gs: <http://example.org/globalschema#> prefix ls: <http://example.org/localschema#> prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> prefix ex: <http://example.org/federation#> prefix fs: <http://spinrdf.org/spin#> prefix sp: <http://spinrdf.org/sp#> prefix qb: <http://purl.org/linked-data/cube#> prefix skos: <http://www.w3.org/2004/02/skos/core#>";
                
        String select = " SELECT DISTINCT ?lsmt WHERE { ?llist1 sp:elements ?lsmt. ?glist1 fs:schemaMatch ?llist1. ";
        int id = 0;
        for(Triple triple : triples)
        {
            id++;
            String gsmt="?gsmt"+id;
            String nt = " ?nn" +id;
            String tripleSet =  nt +" sp:subject " + triple.getSubject() + ". " + 
                                nt +" sp:predicate " +triple.getPredicate() + ". " + 
                                nt + " sp:object " + triple.getObject() + ". ";
            
            String result = " ?glist1 sp:elements " + gsmt  +"." +
                              gsmt + " fs:pattern " + nt   +"."
                              + tripleSet;
            select += result;
        }
        return prefixed + select + " }";
    }
    public static String BuildLSchemaRequest(String lsmt)
    {
        String prefix = "prefix gs: <http://example.org/globalschema#> prefix ls: <http://example.org/localschema#> prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> prefix ex: <http://example.org/federation#> prefix fs: <http://spinrdf.org/spin#> prefix sp: <http://spinrdf.org/sp#> prefix qb: <http://purl.org/linked-data/cube#> prefix skos: <http://www.w3.org/2004/02/skos/core#>";
        String query = " SELECT DISTINCT ?s ?p ?o WHERE { <"+lsmt+"> fs:pattern ?nn. ?nn sp:subject ?s. ?nn sp:predicate ?p. ?nn sp:object ?o. }";
        return (prefix + query);
    }
    
    public static String buildlocalizedRequest(List<Triple> triples)
    {
        String result = "";
        for (Triple triple : triples) {
            result += replaceNodeVars(triple.getSubject()) + " " + removeAngelesFromRdfType(triple.getPredicate()) + " " + replaceNodeVars(triple.getObject()) + " . ";
        }
        System.out.println(result);
        return result;
    }
    public static String removeAngelesFromRdfType(String text)
    {
        if(text.contains("<rdf:type>"))
        {
            text = text.replaceAll("<rdf:type>", "rdf:type");
        }
        return text;
    }
    public static String replaceNodeVars(String text)
    {
        if(text.contains("http://example.org/localschema"))
        {            
            return "?"+text.substring(text.indexOf("#")+1, text.length()-1);
        }
        return text;
    }
    
 
 
}
