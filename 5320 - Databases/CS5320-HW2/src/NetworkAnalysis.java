/**
 * Created by jhh11 on 10/17/14.
 */

import java.sql.*;
import static java.lang.System.out;
import static java.lang.System.err;


public class NetworkAnalysis {
    public static Statement NeighbourCount (Statement stmt, Integer id) throws Exception{
        if (id == null) {
            err.format("Usage: java NetworkAnalysis NeighbourCount <input>%n");
        } else {
            ResultSet rs = null;
            String query = "select count(*) count from " +
                    "(select distinct ToNode as Node from roadNodes " +
                    "where FromNode = " + id + ") neighbors";

            rs =  stmt.executeQuery(query);
            while (rs.next()) {
                int count = rs.getInt("count");
                if (count == 0) {
                    System.out.println("-1");
                } else {
                    System.out.println("Neighbour count: " + count);
                }
            }
        }
        return stmt;
    }

    public static void main (String[] args) {
        if (args.length < 1) {
            err.format("Usage: java NetworkAnalysis <classname> <input>%n");
            return;
        }

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        Integer input = null;

        String db = "roadNetwork";
        String table = "roadNodes";

        String func = args[0];
        if (args.length > 1) {
            input = Integer.parseInt(args[1]);
        }

        try {
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE " + db);

            // refactor to call if i can figure out how
            if (func.equals("NeighbourCount")) {
                stmt = NeighbourCount(stmt, input);
            }
        }
        catch (SQLException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(rs, stmt, con);
        }
    }
}
