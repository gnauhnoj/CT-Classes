import java.sql.*;
import java.util.*;

import static java.lang.System.err;

/**
 * Created by jhh11 on 10/18/14.
 */
public class helpers {
    // count the number of neighbors to a node
    public static Statement NeighbourCount (Statement stmt, Integer id) throws Exception{
        if (id == null) {
            err.format("Usage: java NetworkAnalysis NeighbourCount <input>%n");
        } else {
            if (!subHelpers.NodeExists(stmt, id)) {
                System.out.println("-1");
            } else {
                ResultSet rs = null;
                // count distinct neighbors to a node
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
    // determine the reachability of a node through breadth first search of all neighbors in a directed graph
    public static Statement ReachabilityCount (Statement stmt, Integer id) throws Exception{
        if (id == null) {
            err.format("Usage: java NetworkAnalysis ReachabilityCount <input>%n");
        } else {
            // breadth first search following a directed path from the given node
            // counts how many unique nodes are accessed
            Queue<Integer> q = new LinkedList<Integer>();
            Set<Integer> h = new HashSet<Integer>();
            int count = 0;
            h.add(id);
            q.add(id);

            while (!q.isEmpty()) {
                Integer curr = q.remove();
                if (count == 0 && !subHelpers.NodeExists(stmt, curr)) {
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
    // method to find cliques of size k in a network
    public static Statement DiscoverCliques (Statement stmt, Integer k) throws Exception{
        if (k == null) {
            err.format("Usage: java NetworkAnalysis DiscoverCliques <input>%n");
        } else {
            ResultSet rs = null;
            try {
                stmt = subHelpers.createNeighborTable(stmt);
                stmt = subHelpers.createCliqueTable(stmt, k);
                Integer limit = k-1;
                // find nodes with at least k-1 neighbors
                rs = stmt.executeQuery("select FromNode, count(toNode) count from neighbors group by + " +
                        "fromNode having count >= " + limit + " order by FromNode");
                // for each of these nodes search for possible cliques
                while (rs.next()) {
                    Integer curr = rs.getInt("FromNode");
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    subHelpers.findClique(curr, k, list);
                }
            } finally {
                try { if (rs != null) rs.close(); } catch (Exception e) {};
            }

        }
        return stmt;
    }
    // method to find the Network diameter
    public static Statement NetworkDiameter (Statement stmt) throws Exception{
        int maxDepth = 0;
        ResultSet rs = null;
        try {
            ArrayList<Integer> list = subHelpers.getUniques();
            // for each node - use BFS to calculate the maximum shortest path for the network
            for (Integer id: list) {
                int depth = 0;
                Queue<Integer[]> q = new LinkedList<Integer[]>();
                // potential optimization: store this hash in a database instead?
                Set<Integer> h = new HashSet<Integer>();
                Integer[] node = {id, 0};
                h.add(id);
                q.add(node);

                while (!q.isEmpty()) {
                    Integer[] curr = q.remove();
                    rs = stmt.executeQuery("select ToNode node from Nodes where FromNode = " + curr[0] +
                            " union all select FromNode node from Nodes where ToNode = " + curr[0]);
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
