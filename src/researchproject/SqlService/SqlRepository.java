
package researchproject.SqlService;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlRepository {
    
    //private static String url = "jdbc:sqlite:" + System.getProperty("user.dir") +"/bsbm.db";
    private static String url = "jdbc:sqlite::memory:";
    private static Connection _conn; 
 
    public static void Initialize()
    {
        try {
           _conn = DriverManager.getConnection(url);
           System.out.println("db connection initialized successfully " + _conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void Close()
    {
        try 
        {
            _conn.close();
        }
        catch(Exception ex)
        {
          System.out.println("Error occured while closing connection " + ex.toString());
        }
    }
    
    public static void createNewDatabase() {   
        try {
            if (_conn != null) {
                DatabaseMetaData meta = _conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                //_conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private Connection connect() {
        // SQLite connection string
      //  String url = "jdbc:sqlite:C://sqlite/db/test.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public static void exectureStatement(String sql){
        
        try (
             Statement stmt  = _conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql);)
        {
            System.out.println("SQL executed successfully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void runStatement(String sql) {
      Statement stmt = null;
      
      try {
         //Class.forName("org.sqlite.JDBC");
         
         //System.out.println("Opened database successfully");
         stmt = _conn.createStatement();
         stmt.executeUpdate(sql);
         stmt.close();
        //_conn.close();
      // System.out.println("Table created successfully");
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         //System.exit(0); 
      }
   }
    
   public static void selectAll(String sql){
        
        try (Statement stmt  = _conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println();
            for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(" || ");
                    String columnValue = rs.getString(i);
                    System.out.print(rsmd.getColumnName(i));
            }
            System.out.println();
            System.out.println();
            int position = 1;
           while (rs.next()) {
                System.out.print(position +" - ");
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.print(" || ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + " ");
                }
                System.out.println("");
                position++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
