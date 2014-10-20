import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import static java.lang.System.err;

/**
 * Created by jhh11 on 10/18/14.
 */
public class helpers {
    private static boolean NodeExists (Statement stmt, Integer id) throws Exception {
        String query = "SELECT 1 FROM roadNodes WHERE FromNode = " + id +
                " union SELECT 1 FROM roadNodes WHERE ToNode = " + id;

        ResultSet rs = null;
        rs = stmt.executeQuery(query);
        return rs.next();
    }
    public static Statement NeighbourCount (Statement stmt, Integer id) throws Exception{
        if (id == null) {
            err.format("Usage: java NetworkAnalysis NeighbourCount <input>%n");
        } else {
            if (!NodeExists(stmt, id)) {
                System.out.println("-1");
            } else {
                ResultSet rs = null;
                String query = "select count(*) count from (select ToNode node from roadNodes where FromNode = " + id +
                        " union select FromNode node from roadNodes where ToNode = " + id + " ) nodes";

                rs =  stmt.executeQuery(query);
                while (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("Neighbour Count: " + count);
                }
            }
        }
        return stmt;
    }
    public static Statement ReachabilityCount (Statement stmt, Integer id) throws Exception{
        if (id == null) {
            err.format("Usage: java NetworkAnalysis NeighbourCount <input>%n");
        } else {
            Queue<Integer> q = new LinkedList<Integer>();
            Set<Integer> h = new HashSet<Integer>();
            int count = 0;
            h.add(id);
            q.add(id);

            while (!q.isEmpty()) {
                Integer curr = q.remove();
                if (count == 0 && !NodeExists(stmt, curr)) {
                    count = -1;
                } else {
                    ResultSet rs = null;
                    String query = "select ToNode from roadNodes where FromNode = " + curr;
                    rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        Integer ToNode = rs.getInt("ToNode");
                        if (!h.contains(ToNode)) {
                            h.add(ToNode);
                            q.add(ToNode);
                            count++;
                        }
                    }
                }
            }
            System.out.println("Reachability Count: " + count);
        }
        return stmt;
    }
}
