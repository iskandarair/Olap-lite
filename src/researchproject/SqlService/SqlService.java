/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.SqlService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import researchproject.Parser.Helper;
import researchproject.mapping.JsonObjectParser;
 
public class SqlService {
        public static List<String> createTable(JSONObject jObject, String table)
    {         
        List<String> returnResult = new ArrayList<>();
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> map= JsonObjectParser.getColumns(jObject);
        
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            HashMap<String,String> nameAndType = Helper.getColumnNameAndType(entry.getKey(),entry.getValue());
            result.add(nameAndType);
            //System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        // String table = "Table_Query" + queryIndex + "_table" + tableIndex;
        String createSql = "DROP TABLE IF EXISTS " + table + "; CREATE TABLE " +table;
        createSql += " (id  PRIMARY KEY";
        String paramStatement = "";
        for(Map<String,String> param : result)
        {
            for ( String key : param.keySet() ) {
                paramStatement += ", " + key + " " + param.get(key);                
                returnResult.add(key);
            }
        }
        createSql += paramStatement + ");";
        System.out.println("CREATE TABLE " + table);
        System.out.println(createSql);
        SqlRepository.runStatement(createSql);
        return returnResult;
    }
}
