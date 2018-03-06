
package researchproject.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import researchproject.Parser.Helper;
import researchproject.models.Triple;

/**
 *
 * @author Iskandar
 */
public class GlobalSchemaParser {
    
    public static String getGlobalTriples(String query)
    {  
        Pattern patternStatement = Pattern.compile("[^#]\\?([^{}(#)<>]*)\\.",Pattern.CASE_INSENSITIVE);
        Matcher matchStatement = patternStatement.matcher(query);
        
       // List<String> result = new ArrayList<>();
        if(matchStatement.find())
        {
            String selectPart = matchStatement.group(0);
            String triples =  selectPart + " ";
            String prefix = GetPrefixes(query);
            return selectPart;
            //result.add(triples);
        }

       return null;        
    }  
     
    public static List<String> getSchema(String query)
    {  
        Pattern patternStatement = Pattern.compile("[^#]\\?([^{}(#)<>]*)\\.",Pattern.CASE_INSENSITIVE);
        Matcher matchStatement = patternStatement.matcher(query);
        
        List<String> result = new ArrayList<>();
        while(matchStatement.find())
        {
            String selectPart = matchStatement.group(0);
            String triples =  selectPart + " ";
            String prefix = GetPrefixes(query);
            result.add( prefix + " CONSTRUCT{ "+ triples+ " } WHERE { "+ triples+ " }");
        }

       return result;        
    }
    public static List<String> getVariablesQuery(String query)
    {  
        Pattern patternStatement = Pattern.compile("[^#]\\?([^{}(#)<>]*)\\.",Pattern.CASE_INSENSITIVE);
        Matcher matchStatement = patternStatement.matcher(query);
        
        List<String> result = new ArrayList<>();
        while(matchStatement.find())
        {
            String selectPart = matchStatement.group(0);
            String triples =  selectPart + " ";
            String prefix = GetPrefixes(query);
            result.add( prefix + " SELECT *  WHERE { "+ triples+ " }");
        }

       return result;        
    } 
    public static String GetPrefixes(String query)
    {        
        String prefixRegexString = Pattern.quote("prefix") + "(.*?)" + Pattern.quote("Select") ;
        
        String subQuery = "";
  
        //get All Prefixes
        Pattern patternPrefic = Pattern.compile(prefixRegexString);
        Matcher matchPrefix = patternPrefic.matcher(query);
        while (matchPrefix.find()) {
            subQuery = matchPrefix.group(0).substring(0, matchPrefix.group(0).length() - 6);
        }   
        
        return subQuery;
    }
       
    public static Map<String,String> GetVariables(List<String> variables)
    {  
        Map<String,String> resultSet = new HashMap<>();  
        
  
        for(String variable :  variables)
        {
            variable = variable.trim();
            int closeTagIndex = 0;
            while(closeTagIndex >= 0)
            {
                variable = variable.substring(closeTagIndex);
                closeTagIndex = Helper.findClosingParentheses(variable);
                if(closeTagIndex > 0)
                {
                    String selectStatement = variable.substring(1, closeTagIndex);//.replaceAll(" #", "");
                    String key = GetPair(selectStatement, "?", " ");
                    String value = replaceNodeVars(GetPair(selectStatement, "<", ">"));
                    //selectStatement = selectStatement.substring(selectStatement.indexOf("Select"));
                    //String fullStatement = prefix + " " + selectStatement;
                    resultSet.put(key,value);
                }
            }
        }
        return resultSet;        
    }
    
    public static String replaceNodeVars(String text)
    {
        if(text.contains("http://example.org/globalschema"))
        {            
            return "?"+text.substring(text.indexOf("#")+1, text.length());
        }
        return text;
    }
    public static String GetPair(String query, String openningTag, String closingTag)
    {        
        String prefixRegexString = Pattern.quote(openningTag) + "(.*?)" + Pattern.quote(closingTag) ;
        
        String subQuery = "";
  
        //get All Prefixes
        Pattern patternPrefic = Pattern.compile(prefixRegexString);
        Matcher matchPrefix = patternPrefic.matcher(query);
        if (matchPrefix.find()) {
           subQuery = matchPrefix.group(0).substring(1, matchPrefix.group(0).length() - 1);
           return subQuery;
        }   
        
        return subQuery;
    }
    
    public static List<String> getLsmt(List<String> pairs)
    { 
        List<String> returnResult = new ArrayList<>();
         
        String prefixRegexString = Pattern.quote("<") + "(.*?)" + Pattern.quote(">") ;
        
        String subQuery = "";
        for(String pair : pairs)
        {
            //get All Prefixes
            Pattern patternPrefic = Pattern.compile(prefixRegexString);
            Matcher matchPrefix = patternPrefic.matcher(pair);
            if (matchPrefix.find()) {
               subQuery = matchPrefix.group(0).substring(1, matchPrefix.group(0).length() - 1);
               returnResult.add(subQuery);
            }
        }   
        
        return returnResult;
    }
    public static List<Triple> getTriples(List<String> pairs)
    { 
        List<Triple> returnResult = new ArrayList<>();
         
        String prefixRegexString = Pattern.quote("=") + "(.*?)" + Pattern.quote(")") ;
        
        for(String pair : pairs)
        {
            //get All Prefixes
            Pattern patternPrefic = Pattern.compile(prefixRegexString);
            Matcher matchPrefix = patternPrefic.matcher(pair);
            while(matchPrefix.find()) {
               String s = matchPrefix.group(0).substring(1, matchPrefix.group(0).length() - 1);
               s = s.replace(">", "");
               s = s.replace("<", "").trim();
               
               matchPrefix.find();
               String p = matchPrefix.group(0).substring(1, matchPrefix.group(0).length() - 1);
               p = p.replace(">", "");
               p = p.replace("<", "").trim();
               matchPrefix.find();
               String o = matchPrefix.group(0).substring(1, matchPrefix.group(0).length() - 1);
               o = o.replace(">", "");
               o = o.replace("<", "").trim();
               returnResult.add(new Triple(s,p,o));
            } 
        }
        
        return returnResult;
    }
}
