import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jhh11 on 10/22/14.
 */
public class subHelpers {
    // query to confirm if the node is found in the graph
    protected static boolean NodeExists (Statement stmt, Integer id) throws Exception {
        String query = "SELECT 1 FROM Nodes WHERE FromNode = " + id +
                " union all SELECT 1 FROM Nodes WHERE ToNode = " + id;
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(query);
        } finally {
            Boolean result = rs.next();
            try { if (rs != null) rs.close(); } catch (Exception e) {};
            return result;
        }
    }
    // recursive function to identify cliques with the root of id
    // takes in a current node, target clique size, and a list of previously visited nodes
    protected static void findClique (Integer id, Integer k, ArrayList<Integer> list) throws Exception {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = JDBCutils.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE Network");
            PreparedStatement ps = null;

            // at size 0, fetch all neighbors greater than the node
            if (list.size() == 0) {
                ps = con.prepareStatement("select toNode from neighbors " +
                        "where fromNode = ? AND toNode > ?");
                ps.setInt(1, id);
                ps.setInt(2, id);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ArrayList<Integer> newList = new ArrayList<Integer>(list);
                    Integer curr = rs.getInt("toNode");
                    // add current node to the list
                    newList.add(id);
                    // if clique size is satisfied print valid cliques
                    if (newList.size() == k) {
                        verifyClique(stmt, k, newList);
                        return;
                    } else {
                        // recursively call to continue searching
                        findClique(curr, k, newList);
                    }
                }
            } else {
                // search for nodes
                ps = con.prepareStatement("select fromNode from neighbors " +
                        "where fromNode = ? and toNode = ?");
                ps.setInt(1, id);
                Boolean res = true;

                // check to make sure current node is connected to all previously visited nodes
                for (Integer i : list) {
                    ps.setInt(2, i);
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        res = false;
                    }
                }

                // if the node is connected, add it to the list of previously visited nodes
                if (res) {
                    ArrayList<Integer> newList = new ArrayList<Integer>(list);
                    newList.add(id);
                    // if clique size is satisfied print valid clique
                    if (newList.size() == k) {
                        System.out.println(newList);
                        return;
                    } else {
                        // retrieve all neighbors and make recursive call
                        rs = stmt.executeQuery("select toNode from neighbors where fromNode = " +
                                id + " and toNode > " + id);
                        while (rs.next()) {
                            Integer curr = rs.getInt("toNode");
                            if (!newList.contains(curr)) {
                                findClique(curr, k, newList);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCutils.closeConnection(rs, stmt, con);
        }
        return;
    }
    // query which returns an arraylist of all unique nodes in the network
    // NOTE: unideal implementation because we have to write the arraylist (additional O(N) overhead)
    // alternative implementation, keep track of row and retrieve by so we don't need to load it into memory
    protected static ArrayList<Integer> getUniques () throws Exception {
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
    // creates a Neighbor Table which creates an directed version (where edges are reflected) of an undirected graph
    protected static Statement createNeighborTable (Statement stmt) throws Exception {
        try {
            stmt.executeUpdate("USE Network");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS neighbors AS " +
                    "select * from nodes " +
                    "union all " +
                    "select toNode fromNode, fromNode toNode from nodes");

            stmt.executeUpdate("create index fromNode on neighbors (fromNode)");
            stmt.executeUpdate("create index toNode on neighbors (toNode)");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return stmt;
        }
    }
    // creates a table for each DiscoverCliques call which keeps track of identified cliques
    protected static Statement createCliqueTable (Statement stmt, Integer k) throws Exception {
        try {
            stmt.executeUpdate("USE Network");
            try {
                stmt.executeUpdate("DROP Table clique" + k);
            } catch(Exception e){
            }
            stmt.executeUpdate("CREATE TABLE clique" + k +
                    " (Clique VARCHAR(255) NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return stmt;
        }
    }
    // checks our clique table for a clique, printing and inserting if it wasn't previously found
    protected static void verifyClique (Statement stmt, Integer k, ArrayList<Integer> list) throws Exception {
        String clique = list.toString();
        ResultSet rs = stmt.executeQuery("Select * from clique" + k + " where clique = '" + clique + "'");

        if (rs.next()) {
            return;
        } else {
            stmt.executeUpdate("INSERT INTO clique" + k +
                    " VALUES ('" + clique + "')");
            System.out.println(clique);
        }
    }
}
