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
//            System.out.print(e);
        }
        stmt.executeUpdate("CREATE DATABASE " + db);
        return stmt;
    }
    private static Statement createTable(Statement stmt, String table, String schemaQuery) throws Exception {
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

            stmt.executeUpdate("create index fromNode on nodes (fromNode)");
            stmt.executeUpdate("create index toNode on nodes (toNode)");

            stmt.executeUpdate("CREATE TABLE unodes AS " +
                            "select fromNode node from nodes " +
                            "union " +
                            "select toNode node from nodes");

            stmt.executeUpdate("create index node on unodes (node)");

        }
        catch (SQLException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(rs, stmt, con);
        }
    }
}
