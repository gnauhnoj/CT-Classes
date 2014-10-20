/**
 * Created by jhh11 on 10/13/14.
 */

import java.sql.*;

public class CreateDB {

    // really un DRY
    private static Statement createSQLDatabase(Statement stmt, String db) throws Exception {
        try {
            stmt.executeUpdate("DROP DATABASE " + db);
        } catch(Exception e){
            System.out.print(e);
        }
        stmt.executeUpdate("CREATE DATABASE " + db);
        return stmt;
    }
    private static Statement createTable(Statement stmt, String table, String schemaQuery) throws Exception {
//        try {
//            stmt.executeUpdate("DROP TABLE " + table);
//        } catch(Exception e){
//            System.out.print("table already dropped");
//        }
        stmt.executeUpdate(schemaQuery);
        return stmt;
    }

    public static void main (String[] args) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        String db = "Network";
        String table = "Nodes";
        String schemaQuery = "CREATE TABLE " + table +
                        " (FromNode int NOT NULL, " +
                        " ToNode int NOT NULL)";

        // filename input needs to be like "/Users/jhh11/Copy/CT/5320\ -\ Databases/CS5320-HW2/lib/roadNet-CA.txt"
        String filename = args[0];

        try {
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt = createSQLDatabase(stmt, db);

            stmt.executeUpdate("USE " + db);
            stmt = createTable(stmt, table, schemaQuery);

            // load data
            stmt.executeUpdate("LOAD DATA INFILE \"" + filename + "\" INTO TABLE "
                    + table + " FIELDS TERMINATED BY '\\t' " +
                    "LINES TERMINATED BY '\\n' " +
                    "IGNORE 4 LINES");

            stmt.executeUpdate("CREATE TABLE Undirected AS (select t1.fromNode, t1.toNode from " +
                     "Nodes t1 join Nodes t2 on t1.fromNode = t2.toNode)");
        }
        catch (SQLException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(rs, stmt, con);
        }
    }
}
