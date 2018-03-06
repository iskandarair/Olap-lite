/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import researchproject.Parser.Helper;
import researchproject.models.Triple;

/**
 *
 * @author Iskandar
 */
public class JsonObjectParser {
        
    public static List<Triple> getTriples(JSONObject jObject)
    { 
        List<Triple> triples = new ArrayList<>();
        try
        {
            JSONArray arr = jObject.getJSONObject("results").getJSONArray("bindings");
            for (int i = 0; i < arr.length(); i++)
            {
                String subject = arr.getJSONObject(i).getJSONObject("s").getString("value");
                String predicate = arr.getJSONObject(i).getJSONObject("p").getString("value");
                String object = arr.getJSONObject(i).getJSONObject("o").getString("value");
                Triple triple = new Triple(subject,predicate,object);
                triples.add(triple);
                System.out.println("subject:" + subject + " predicate " + predicate + " object " + object);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception occured: Message " + ex.getMessage());
        }
        return triples;
    }
    
    public static List<String> getResults(JSONObject jObject, String variableName)
    { 
        List<String> returnResult = new ArrayList<>();
        try
        {
            JSONArray arr = jObject.getJSONObject("results").getJSONArray("bindings");
            for (int i = 0; i < arr.length(); i++)
            {
                String lsmt = arr.getJSONObject(i).getJSONObject(variableName).getString("value");
                returnResult.add(lsmt);
                System.out.println("lsmt:" +lsmt);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception occured: Message " + ex.getMessage());
        }
        return returnResult;
    }  
    
    public static Map<String,String> getVariables(JSONObject jObject)
    { 
        Map<String,String> returnResult = new HashMap<>();
        try
        {
            JSONArray arr = jObject.getJSONObject("results").getJSONArray("bindings");
            for (int i = 0; i < arr.length(); i++)
            {
                Iterator<?> keys = arr.getJSONObject(i).keys();
                while(keys.hasNext())
                {
                    String key = (String) keys.next();
                    String value = arr.getJSONObject(i).getJSONObject(key).getString("value");
                    value  = makeVariable(value);
                   System.out.println("key:" +key + " value " + value);
                    returnResult.put(key,value);
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception occured: Message " + ex.getMessage());
        }
        return returnResult;
    } 
    
        public static Map<String,String> getVariables2(JSONObject jObject)
    { 
        Map<String,String> returnResult = new HashMap<>();
        try
        {
            JSONArray arr = jObject.getJSONObject("results").getJSONArray("bindings");
            for (int i = 0; i < arr.length(); i++)
            {
                Iterator<?> keys = arr.getJSONObject(i).keys();
                while(keys.hasNext())
                {
                    String key = (String) keys.next();
                    String value = arr.getJSONObject(i).getJSONObject(key).getString("value");
                    value  = makeVariable(value);
                   System.out.println("key:" +key + " value " + value);
                    returnResult.put(key,value);
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception occured: Message " + ex.getMessage());
        }
        return returnResult;
    } 
    public static String makeVariable(String text)
    {
        return "?"+text.substring(text.indexOf("#")+1, text.length());
    }
    
    public static Map<String,String> getColumns(JSONObject jObject)
    { 
        Map<String,String> result = new HashMap<>();
        try
        {
            JSONArray arr = jObject.getJSONObject("head").getJSONArray("vars");
            
            for (int i = 0; i < arr.length(); i++)
            {
                String name =  arr.getString(i);
                String value =jObject.getJSONObject("results").getJSONArray("bindings").getJSONObject(0).getJSONObject(name).getString("value");
                result.put(name,value);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception occured: Message " + ex.getMessage());
        }
        return result;//returnResult;
    }  
       
    public static String populateTable(String tableName, JSONObject jObject)
    {
        String bulkInsert = "";
        Map<String,String> columns =  getColumns(jObject);
        List<Triple> triples = new ArrayList<>();
        
        try
        {
            int j = 0;
            JSONArray arr = jObject.getJSONObject("results").getJSONArray("bindings");
            for (int i = 0; i < arr.length(); i++)
            {                
                List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
                for (Map.Entry<String, String> entry : columns.entrySet())
                {
                    String value = arr.getJSONObject(i).getJSONObject(entry.getKey()).getString("value");
                    HashMap<String,String> columnValuePair = new HashMap<>();
                    columnValuePair.put(entry.getKey(), value);
                    result.add(columnValuePair);
                }
                String insertSql = "INSERT INTO "+ tableName + " ( ";
                String columnValues = " VALUES (";
                //String paramStatement = "";
                for(HashMap<String,String> param : result)
                {
                    for ( String key : param.keySet() ) {
                        insertSql += key + ", ";
                        columnValues += Helper.GetValueText(param.get(key)) + ", ";
                    }
                }
                insertSql = insertSql.substring(0,insertSql.length() -2) + ")"+ columnValues.substring(0, columnValues.length() -2) + ");";
                insertSql = 
                bulkInsert += insertSql;
                ++j;
            }
            System.out.println("BULD INSERT : " + bulkInsert);
            System.out.println(bulkInsert);
        }
        catch(Exception ex)
        {
            System.out.println("Exception occured: Message " + ex.getMessage());
        }        
        return bulkInsert;
        
    }

}
