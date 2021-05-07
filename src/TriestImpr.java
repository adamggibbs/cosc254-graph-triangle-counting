import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class TriestImpr implements DataStreamAlgo {
    
    // instance variables
    int samsize;
    int time;
    double triangles;
    LinkedHashMap<Integer, LinkedHashSet<Integer>> vertex_neighbors;
    ArrayList<Edge> edges;

    /*
     * Constructor.
     * The parameter samsize denotes the size of the sample, i.e., the number of
     * edges that the algorithm can store.
     */
	public TriestImpr(int samsize){

        // initialize inputted sample size
        this.samsize = samsize;
        
        // initialize other instance variables
        time = 0;
        triangles = 0;
        vertex_neighbors = new LinkedHashMap<Integer, LinkedHashSet<Integer>>();
        edges = new ArrayList<>();
	}

	public void handleEdge(Edge edge){

        // if time is less than sample size,
        // count triangle from incoming edge, add edge
        // otherwise determine whether to add edge
        // if edge is added, 
        // remove a random edge from sample and,
        // decrement triangles from removed edge and,
        // increment triangles from added edge
        if(time <= samsize){
            int new_triangles = countTriangles(edge);
            triangles += new_triangles;
            addEdge(edge);
        } else{

            int new_triangles = countTriangles(edge);

            double nt = Math.max(1, nt_t(time));

            triangles += nt*new_triangles;

            if(flipCoin(time)){
                Edge randEdge = getRandomEdge();
                removeEdge(randEdge);
                addEdge(edge);
            }
        }

        time++;

    } // handleEdge()

	public int getEstimate(){ 

        return (int)triangles;
        
    } // getEstimate()

    /*
     * HELPER METHODS
     * flipcoin(): simulate coin flip, returns boolean
     * addEdge(): adds edge (and inverse) and its vertices to sample
     * removeEdge(): removes edge (and inverse) and its vertices from sample
     * getRandomEdge(): gets randomly chosen edge in sample, returns Edge
     * countTriangles(): counts how many triangles that edge is a part of, returns int
     * nt_t(): calculates the value of nt(t) to update triangles, returns double
     */
    private boolean flipCoin(int t){

        double bias = (double)samsize / t;
        return Math.random() < bias;

    } //flipCoin()

    private void addEdge(Edge edge){

        // add edge to ArrayList
        edges.add(edge);

        // if first vertex is not in HashMap, 
        // add it and add second vertex as a neighbor
        // otherwise just add second vertex as a neighbor
        if(vertex_neighbors.containsKey(edge.u)){
            vertex_neighbors.get(edge.u).add(edge.v);
        } else {
            vertex_neighbors.put(edge.u, new LinkedHashSet<Integer>());
            vertex_neighbors.get(edge.u).add(edge.v);
        }

        // if second vertex is not in HashMap, 
        // add it and add first vertex as a neighbor
        // otherwise just add first vertex as a neighbor
        if(vertex_neighbors.containsKey(edge.v)){
            vertex_neighbors.get(edge.v).add(edge.u);
        } else {
            vertex_neighbors.put(edge.v, new LinkedHashSet<Integer>());
            vertex_neighbors.get(edge.v).add(edge.u);
        }

    } // addEdge()

    private void removeEdge(Edge edge){

        // remove second vertex as a neighbor of the first
        vertex_neighbors.get(edge.u).remove(edge.v);
        // if first vertex now has no neighbors, remove it from HashMap
        if(vertex_neighbors.get(edge.u).isEmpty()){
            vertex_neighbors.remove(edge.u);
        }

        // if edge is not a self loop,
        // remove first vertex as a neighbor of the second
        // if second vertex now has no neighbors, remove it from HashMap
        if(edge.u != edge.v){
            vertex_neighbors.get(edge.v).remove(edge.u);
            if(vertex_neighbors.get(edge.v).isEmpty()){
                vertex_neighbors.remove(edge.v);
            }
        }
        

    } // removeEdge()

    private Edge getRandomEdge(){

        // get random index of ArrayList
        int index = (int)(Math.random()*edges.size());
        // get the random edge to remove
        Edge randEdge = edges.get(index);
        // remove edge from ArrayList
        edges.remove(index);

        return randEdge;
    
    } // getRandomEdge()

    private int countTriangles(Edge edge){

        // if edge is a self loop, it cannot form a triangle
        if(edge.u == edge.v){
            return 0;
        }


        int triangles = 0;

        // get neighbors of each vertex of edge
        LinkedHashSet<Integer> u_neighbors = vertex_neighbors.get(edge.u);
        LinkedHashSet<Integer> v_neighbors = vertex_neighbors.get(edge.v);

        // increment triangle for each neighbor shared between verteces in the edge
        if(u_neighbors != null && v_neighbors != null){
            for (int neighbor : u_neighbors) {

                if(v_neighbors.contains(neighbor)){
                    triangles++;
                }
                
            }
        }

        return triangles;

    } // countTriangles()

    private double nt_t(int t){

        double nt = (double)(t-1) / samsize;
        nt *= (double)(t-2) / (samsize-1);

        return nt;
    
    } // nt_t()
}
