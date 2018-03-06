/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.Parser;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
     public static int findClosingParentheses(String queryPart) {
        int closePos = queryPart.indexOf("(");
        int counter = 1;
        char[] text = queryPart.toCharArray();
        while (counter > 0) {
            char c = text[++closePos];
            if (c == '(') {
                counter++;
            }
            else if (c == ')') {
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
     public static int findClosingBracket(String queryPart) {
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
     
   public static String separateColumns(String query)
   {
       // Table_Query[0-9]+_table[0-9]+.[a-z]* 
        //System.out.println(query);
        String regEx =  "Table_Query\\d_table\\d.\\w+ (?!As)";
        Pattern pattern = Pattern.compile(regEx);
        Matcher match = pattern.matcher(query);
        String outerquery = null;
        while(match.find()) {
            //System.out.println("COLUMN: " +match.group(0));
            String column = match.group(0);
            int ab = query.indexOf(column) + column.length();
            query = query.substring(0,ab) + ", " + query.substring(ab);
            //System.out.println("!!!!!!"+query);
        }
        //System.out.println("   FINISHED   ");
        return query; 
        
       //return null;
   }
   
   public static String separateOrdering(String query)
   {
       // Table_Query[0-9]+_table[0-9]+.[a-z]* 
        //System.out.println(query);
        String regEx =  "[(| ]Table_Query\\d_table\\d.\\w+[) ] (?:desc)?";
        Pattern pattern = Pattern.compile(regEx);
        Matcher match = pattern.matcher(query);
        while(match.find()) {
           // System.out.println("COLUMN: " +match.group(0));
            String column = match.group(0);
            int ab = query.indexOf(column) + column.length();
            query = query.substring(0,ab) + ", " + query.substring(ab);
            //System.out.println(query);
        }
        //System.out.println("   FINISHED   ");
        return query; 
        
       //return null;
   }
   
   public static HashMap<String, String> getColumnNameAndType(String columnName, String value)
   {
        if(value != null)
        {
            if(isNumeric(value))
            {
                value = "INTEGER";
            }
            else if(value.contains(".") && !value.contains("/"))
            {
                // float
                value = "DECIMAL(15,5)";
            }
            else 
            {
                value = "VARCHAR(255)";
            }            
        }
        if (columnName != null && value != null)
        {
            HashMap<String, String> result = new HashMap<String,String>();
            result.put(columnName, value);
            return result;
        }
        return null;
    }
   
    private static boolean isNumeric(String s) {  
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
    }
    
    public static String GetValueText(String value)
    {
        if(value != null)
        {
            if(isNumeric(value))
            {
                return value;// "INTEGER";
            }
            else if(value.contains(".") && !value.contains("/"))
            {
                return value;
            }
            else 
            {
                return '"'+ value+ '"';// = "VARCHAR(255)";
            }            
        }
        return value;
    }
    
    public static String RemoveDoubleSpaces(String text)
    {
        while(text.contains("  "))
            text = text.replaceAll("  ", " ");
        return text;
    }
}
