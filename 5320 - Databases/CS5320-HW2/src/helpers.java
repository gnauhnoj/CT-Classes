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
        String query = "SELECT 1 FROM Nodes WHERE FromNode = " + id +
                " union SELECT 1 FROM Nodes WHERE ToNode = " + id;

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
                String query = "select count(*) count from (select ToNode node from Nodes where FromNode = " + id +
                        " union select FromNode node from Nodes where ToNode = " + id + " ) nodes";

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
            err.format("Usage: java NetworkAnalysis ReachabilityCount <input>%n");
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
                    String query = "select ToNode from Nodes where FromNode = " + curr;
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
    public static Statement DiscoverCliques (Statement stmt, Integer k) throws Exception{

        // select t1.fromNode, t1.toNode from roadNodes t1 join roadNodes t2 on t1.fromNode = t2.toNode;  make undirected
        if (k == null) {
            err.format("Usage: java NetworkAnalysis DiscoverCliques <input>%n");
        } else {

        }
        return stmt;
    }

    public static Statement NetworkDiameter (Statement stmt) throws Exception{
        // get all combinations of from/to
        // for each combination, find shortest path
        // initialize variable = 0
        // to find shortest path: bfs from start out lookin for node
        // once found, compare to variable, assign if greater
        // return last one

//        select distinct t1.fromNode, t2.toNode
//        from Nodes t1
//        inner join
//        Nodes t2
//        ON t1.fromNode < t2.toNode
//        where t1.fromNode != t2.toNode;

        return stmt;
    }

}
