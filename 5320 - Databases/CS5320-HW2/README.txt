CS 5320 F14 :  Assignment 2

Name: Jonathan Huang

netid: jhh283

Primary dataset used: http://snap.stanford.edu/data/p2p-Gnutella08.html (an undirected dataset)

Syntax (from the directory with the compiled .class files):
- java CreateDB <file> (where <file> is the path to the file)
- java NetworkAnalysis NeighbourCount <id>
- java NetworkAnalysis ReachabilityCount <id> (directed)
- java NetworkAnalysis DiscoverCliques <k> (undirected)
- java NetworkAnalysis NetworkDiameter (undirected)

Note:
- The project directory assumes that ConnectorJ is an external library included with your JDK
- An error is thrown (does NOT impact the run) when executing DiscoverCliques multiple times in a row after the database has already been initialized . This error can be ignored and originates from the creation of an index that already exists (did not have time to fix).
- My Graph Diameter implementation is extremely inefficient (doesn't complete run in a reasonable amount of time for the dataset used). I have brainstormed some possible methods of optimization below.
- I apologize in advance if the code is difficult to follow. I did not have as much time as I would have liked to clean it up. Please feel free to let me know if you have questions.

Optimizations:
1. Neighbour Count: The primary optimization in this case was to modify our initial table after loading in our dataset to include indexes on both columns. This actually did improve runtime as the retrieval of specific rows was significantly faster.

2. Reachability Count: The optimization attempted in this case was to again index the values in our table of nodes. As we are performing several sequential database selects, the addition of indexes speeds up the repeated queries of the implemented breadth first search.

Another approach that was not implemented would be to store the list of nodes that have been "reached" in a separate table instead of using a in-memory Hash Table as currently implemented.

3. Discover cliques: Several optimizations are used for this method. By limiting our initial search of nodes to nodes with at least k-1 neighbors,  we significantly reduce the starting set of nodes. This method also creates an indexed Neighbor Table to reduce the number of unions needed to determine two-way relationships. I also implemented a separate table to keep track of "seen" cliques to reduce the in-memory load and used prepared statements for frequent queries. I believe that these improvements did create a faster runtime.

Some additional non-implemented optimizations are: to build the Neighbor Table on each run with the neighbors with k-1 nodes.

4. Graph diameter:  Took advantage of the undirected assumption and used a union all when searching for neighbors. Built an indexed table of unique nodes as an entry point for the BFS used to calculate graph diameter. Because my implementation is extremely slow, it's unclear whether these improvements did anything.

Additional non-implemented optimizations: use the neighbor table used in #3 instead of performing separate queries, keep track of visited nodes in a new on-disk table instead of the in-memory hashset, instead of keeping track of unique nodes in memory, perform constant database accesses.
