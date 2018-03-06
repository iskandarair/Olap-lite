/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.Parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author HP
 */
public class EntailmentParser {
    
    public static String GetBasicQuery(String query)
    {
        //Pattern pattern = Pattern.compile("\\?([a-zA-Z0-9\\ <\\/&@:;=.?-]+)> \\.",Pattern.CASE_INSENSITIVE);
        Pattern pattern = Pattern.compile("[\\.{] \\?([a-zA-Z0-9\\ <\\/&@:;=.?-]+)> \\.",Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        String outerquery = null;
        int startIndex = 0;
        while(match.find()) {
            outerquery = match.group(0).substring(match.group(0).indexOf("?"));
            //outerquery = outerquery.substring(2);
           //System.out.println(match.groupCount() + " found result: " + match.group(0));
            String union =  GetUri(outerquery);
            int length = outerquery.length();
            startIndex = query.indexOf(outerquery,startIndex + length);
            query = query.substring(0,(startIndex))+ " { " +query.substring(startIndex,(startIndex+length)) + union +  query.substring((startIndex+length));
            
        }
        System.out.println("Generated UNION: "+ query);
        return query;  
    }   
        public static String GetUri(String query)
    {
        Pattern pattern = Pattern.compile("<(.*?)>",Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        String finding = null;
        if(match.find()) {
            finding = match.group(0);
            String result = "} UNION { " +finding+" <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?temp . ?product a ?temp .} ";
            return result;
      
        }
        return null;  
    }  
}
