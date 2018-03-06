/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.Parser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import researchproject.SqlService.SqlRepository;

/**
 *
 * @author HP
 */
public class OuterClassParser {
    public static String buildOuterQuery(String query, Map<String, List<String>> columns, int queryIndex)
    {
        String table = "Table_Query" + queryIndex + "_table1"; 
        
        String selectPart = getOuterQuery(query);
        String outerPart = Helper.separateColumns(outerQueryToSql(selectPart,columns).trim());

        String clausePart = getOuterSuffix(query);
        String suffixPart = outerQueryToSql(clausePart,columns);
        
        if(suffixPart.contains("desc"))
        {
            int index = suffixPart.indexOf("desc(");
            if(index > -1)
            {
                suffixPart = suffixPart.replace("desc(", "(");
                int parenthesesIndex = Helper.findClosingParentheses(suffixPart);
                suffixPart = suffixPart.substring(0, parenthesesIndex+1) + " desc " + suffixPart.substring(parenthesesIndex+1);
            }
        }
        suffixPart = getColumnPart(suffixPart);
        
        String tableJoin = "";
        String viewTable = "";
        List<String> matchColumns = new ArrayList<String>();
        String addedTables = "";
        for(Map.Entry<String,List<String>> entry : columns.entrySet()) {
                String tableName = entry.getKey();
                
                List<String> columnNames = entry.getValue();
                for(String column : columnNames)
                {
                    for(String existingColumns : matchColumns)
                    {
                        if(existingColumns.contains("."+ column))
                        {
                            String existingColumnTableName = existingColumns.substring(0,existingColumns.indexOf(".") );
                            if(!addedTables.contains(table))
                            {
                                tableJoin += " INNER JOIN " + tableName + " ON " + existingColumns + " = " + tableName + "." + column; 
                                addedTables += table;
                            }
                            else if(!addedTables.contains(existingColumnTableName))
                            {
                                tableJoin += " INNER JOIN " + existingColumnTableName + " ON " + existingColumns + " = " + tableName + "." + column; 
                                addedTables += existingColumnTableName;
                            }
                            
                        }
                    }
                    matchColumns.add(tableName + "." + column);
                }
            }
            if(addedTables.length() == 0)
            {
                return ReplaceTableWithView(outerPart, suffixPart,columns, queryIndex);
            }
            while(outerPart.contains("(1.0*"))
            {
                outerPart = outerPart.replace("(1.0*", "1.0*");
            }
            String outerQuery = outerPart + " FROM " + table + " "+ tableJoin + suffixPart;

        return outerQuery;
    }
    
    private static String ReplaceTableWithView(String outerQuery,String suffix,  Map<String, List<String>> columns, int queryIndex)
    {        
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String viewName = "Query"+queryIndex +"viewTable_"+ timestamp.getTime();
                String viewTable = "CREATE VIEW " + viewName + " AS SELECT ";
                String tables = " FROM ";
                int index = 1;
                for(Map.Entry<String,List<String>> entry : columns.entrySet()) {
                    String tableName = entry.getKey();
                    String comma = index >1 ? ", " : " ";
                    tables += comma + tableName;
                    List<String> columnNames = entry.getValue(); 
                    for(String column : columnNames)
                    {  
                        comma = index >1 ? ", " : " ";
                        viewTable += comma + tableName+"."+column;
                        index++;
                    }  
                }
                viewTable += tables;
                SqlRepository.exectureStatement(viewTable);
                String outerPart = outerQuery.replaceAll("[(]{2}", "(").replaceAll("[(]avg", "avg");
                suffix = suffix.replaceAll("desc", "desc,");
                
                
                outerQuery = outerPart + " FROM " + viewName + " "+ suffix;
                outerQuery = outerQuery.replaceAll("Table_Query\\d_table\\d.","").replaceAll(" [)e] ", " ");
                System.out.println(outerQuery);
                return outerQuery;
    }
    private static String columnsWithAliases(String sql, Map<String, List<String>> columns)
    {
        for(Map.Entry<String,List<String>> entry : columns.entrySet()) {
                String tableName = entry.getKey();
                
                List<String> columnNames = entry.getValue();
                //System.out.println(entry.getKey() + ": " +  entry.getValue().toString());
                for(String column : columnNames)
                {
                    if(sql.contains(column))
                    {
                        int columnIndex = sql.indexOf(column);
                        String preChar = sql.substring(columnIndex-1,columnIndex );
                        if(!preChar.contains("."))
                        {
                            sql = sql.substring(0, columnIndex) + tableName +"." + sql.substring(columnIndex);
                        }
                    }                    
                }
            }
            //System.out.println("?? "+ sql);
            
        return sql;
    }
    private static String outerQueryToSql(String selectPart,Map<String, List<String>> columns)
    {
        String statement = sparqlToSql(selectPart);
        
        int aliasStartIndex = statement.indexOf("As ");
        int aliasEndIndex = statement.indexOf(")",aliasStartIndex);
        if(aliasStartIndex > 0 &&  aliasEndIndex >0)
        {
            statement = statement.substring(0,(aliasStartIndex+3)) + '"' +  statement.substring((aliasStartIndex+3),aliasEndIndex) + '"' + statement.substring(aliasEndIndex+1);
        }
        //System.out.println(statement);
        //System.out.println("==== ===");
        
        String sql = columnsWithAliases(statement, columns);
        // return selectPart;
        return sql;
    }
    private static String SuffixToSQL(String suffix)
    {
        String statement = sparqlToSql(suffix);
        return null;
    }
    
    private static String sparqlToSql(String selectPart)
    {        
        while(selectPart.contains("xsd:float"))
        {
            selectPart = selectPart.replace("xsd:float","1.0*");
        }
        while(selectPart.contains("?"))
        {
             selectPart = selectPart.replace("?","");       
        }
        return selectPart;
    }
    private  static String getOuterQuery(String query)
    {        
        // pattern1 and pattern2 are String objects
        String statementRegexString =  Pattern.quote(" select") + "(.*?)" + Pattern.quote(" {");
        Pattern patternStatement = Pattern.compile(statementRegexString,Pattern.CASE_INSENSITIVE);
        Matcher matchStatement = patternStatement.matcher(query);
        String outerquery = null;
        if(matchStatement.find()) {
            outerquery = matchStatement.group(0).substring(0, (matchStatement.group(0).length()-1));
            //System.out.println("OUTER QUERY " +outerquery);
        }
        return outerquery;    
    }

    private  static String getOuterSuffix(String query)
    {
        int lastBracketIndex = Helper.findClosingBracket(query);
        if(lastBracketIndex > 0)
        {
            String suffix = query.substring(lastBracketIndex+1);
            //System.out.println("Suffix " + suffix);
            return suffix;    
        }     
        return null;
    }
    
    private static String getColumnPart(String query)
    {                
        // pattern1 and pattern2 are String objects
        String regEx =  Pattern.quote("By ") + "(.*?)" + Pattern.quote(" limit");
        Pattern pattern = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        String outerquery = null;
        if(match.find()) {
            outerquery = match.group(0).substring(2, (match.group(0).length()-6));
            String updatedSuffix = addCommasSuffix(outerquery.replaceAll("\\s+$", ""));
            int startIndex = query.indexOf(outerquery);
            int length = updatedSuffix.length();
            outerquery = query.substring(0, startIndex) +updatedSuffix + " "+ query.substring((startIndex+ length -1));
        }
        if(outerquery == null)
        {
            return query;
        }
        System.out.println(outerquery);
        return outerquery;  
    }

    private static String addCommasSuffix(String query)
    {
        Pattern pattern = Pattern.compile("[^*][ (/]Table_Query\\d_table\\d.\\w+[) ](?: desc)?(?:(?! \\*))",Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(query);
        String outerquery = null;
        while(match.find()) {
            outerquery = match.group(0);
            int startIndex = query.indexOf(outerquery);
            int length = outerquery.length();
            query = query.substring(0,(startIndex+length)) + ',' +  query.substring((startIndex+length));
      
        }
        return query;  
    }    
    
}
