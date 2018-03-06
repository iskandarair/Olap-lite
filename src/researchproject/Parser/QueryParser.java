/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author HP
 */
public class QueryParser {
    
    public static List<String> GetInnerQuery(String query)
    {        
        List<String> resultSet = new ArrayList<String>();
        String prefixRegexString = Pattern.quote("prefix") + "(.*?)" + Pattern.quote("Select") ;
        
        String subQuery = "";
  
        //get All Prefixes
        Pattern patternPrefic = Pattern.compile(prefixRegexString);
        Matcher matchPrefix = patternPrefic.matcher(query);
        while (matchPrefix.find()) {
            subQuery = matchPrefix.group(0).substring(0, matchPrefix.group(0).length() - 6);
        }   
        
        resultSet = BuildInnerStatement(query, subQuery);       
      //  if(resultSet.isEmpty())
      // {
      //     resultSet.add(query);
      //  }
      
        return resultSet;
    }
    public static List<String> BuildInnerStatement(String query, String prefix)
    {       
        if(!prefix.contains("prefix xsd:"))
        {
            prefix = "prefix xsd: <http://www.w3.org/2001/XMLSchema#> " +prefix;
        }
        List<String> resultSet = new ArrayList<String>();  
        
        // pattern1 and pattern2 are String objects
        String statementRegexString =  Pattern.quote("{ select") + "(.*?)" + Pattern.quote("limit") + "(.*?)" ;
        Pattern patternStatement = Pattern.compile(statementRegexString,Pattern.CASE_INSENSITIVE);
        Matcher matchStatement = patternStatement.matcher(query);
        
        if(matchStatement.find()) {
            String selectPart = matchStatement.group(0);
            
            int closeTagIndex = 0;
            String queryPart = selectPart;
            while(closeTagIndex >= 0)
            {
                queryPart = queryPart.substring(closeTagIndex);
                closeTagIndex = Helper.findClosingBracket(queryPart);
                if(closeTagIndex > 0)
                {
                    String selectStatement = queryPart.substring(1, closeTagIndex).replaceAll(" #", "");
                    selectStatement = selectStatement.substring(selectStatement.indexOf("Select"));
                    String fullStatement = prefix + " " + selectStatement;
                    resultSet.add(fullStatement);
                }
            }
        }
        
        return resultSet;        
    }
    //please remove after making sure that findClosingBracket works fine!!!!
    public static int findClosingParen(String queryPart) {
        int closePos = queryPart.indexOf("{");
        int counter = 1;
        char[] text = queryPart.toCharArray();
        while (counter > 0) {
            char c = text[++closePos];
            if (c == '{') {
                counter++;
            }
            else if (c == '}') {
                counter--;
            }
        }
        if(closePos > 0)
        {
            return closePos;
        }
        else 
        {
            return -1;
        }
    }   
            
}
