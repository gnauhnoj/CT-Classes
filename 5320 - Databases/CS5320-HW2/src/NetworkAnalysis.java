/**
 * Created by jhh11 on 10/17/14.
 */

import java.sql.*;
import java.util.Hashtable;
import static java.lang.System.err;


public class NetworkAnalysis {
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

            if (func.equals("NeighbourCount")) {
                stmt = helpers.NeighbourCount(stmt, input);
            } else if (func.equals("ReachabilityCount")) {
                stmt = helpers.ReachabilityCount(stmt, input);

            } else {
                err.format("Usage: NetworkAnalysis <classname> not found");
            }
        }
        catch (SQLException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(rs, stmt, con);
        }
    }
}
