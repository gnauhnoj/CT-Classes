import java.sql.*;
import java.util.*;

import static java.lang.System.err;

/**
 * Created by jhh11 on 10/18/14.
 */
public class helpers {
    // query to confirm if the node is found in the graph
    private static boolean NodeExists (Statement stmt, Integer id) throws Exception {
        String query = "SELECT 1 FROM Nodes WHERE FromNode = " + id +
                " union SELECT 1 FROM Nodes WHERE ToNode = " + id;
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(query);
        } finally {
            Boolean result = rs.next();
            try { if (rs != null) rs.close(); } catch (Exception e) {};
            return result;
        }
    }
    // query which returns an arraylist of all neighbors to a given node
    private static ArrayList<Integer> findNeighbors (Integer id) throws Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<Integer> list = new ArrayList<Integer>();

        try {
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE Network");

            rs = stmt.executeQuery("select ToNode node from Nodes where FromNode = " + id +
                    " union select FromNode node from Nodes where ToNode = " + id);
            while (rs.next()) {
                list.add(rs.getInt("node"));
            }
        }
        catch (SQLException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(rs, stmt, con);
        }
        return list;
    }
    // query which returns an arraylist of all unique nodes in the network
    private static ArrayList<Integer> getUniques() throws Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<Integer> list = new ArrayList<Integer>();

        try {
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE Network");

            rs = stmt.executeQuery("select * from unodes");
            while (rs.next()) {
                list.add(rs.getInt("node"));
            }
        }
        catch (SQLException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
        finally {
            JDBCutils.closeConnection(rs, stmt, con);
        }
        return list;
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
                try {
                    rs =  stmt.executeQuery(query);
                    while (rs.next()) {
                        int count = rs.getInt("count");
                        System.out.println("Neighbour Count: " + count);
                    }
                } finally {
                    try { if (rs != null) rs.close(); } catch (Exception e) {};
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

                    try {
                        rs = stmt.executeQuery(query);
                        while (rs.next()) {
                            Integer ToNode = rs.getInt("ToNode");
                            if (!h.contains(ToNode)) {
                                h.add(ToNode);
                                q.add(ToNode);
                                count++;
                            }
                        }
                    } finally {
                        try { if (rs != null) rs.close(); } catch (Exception e) {};
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
        int maxDepth = 0;
        ResultSet rs = null;
        try {
            ArrayList<Integer> list = getUniques();

            // for each node
            // use BFS to calculate the maximum shortest path for the network
            for (Integer id: list) {
                int depth = 0;
                Queue<Integer[]> q = new LinkedList<Integer[]>();
                Set<Integer> h = new HashSet<Integer>();
                Integer[] node = {id, 0};
                h.add(id);
                q.add(node);

                while (!q.isEmpty()) {
                    Integer[] curr = q.remove();
                    rs = stmt.executeQuery("select ToNode node from Nodes where FromNode = " + curr[0] +
                            " union select FromNode node from Nodes where ToNode = " + curr[0]);
                    while (rs.next()) {
                        Integer t = rs.getInt("node");
                        if (!h.contains(t)) {
                            h.add(t);
                            int newDepth = curr[1] + 1;
                            depth = (depth < newDepth) ? newDepth : depth;
                            Integer[] newNode = {t, newDepth};
                            q.add(newNode);
                        }
                    }
                }
                // collect the maximum shortest path in the network
                maxDepth = (maxDepth < depth) ? depth : maxDepth;
            }
        } finally {
            System.out.println("Network Diameter: " + maxDepth);
            try { if (rs != null) rs.close(); } catch (Exception e) {};
        }
        return stmt;
    }
}
